package org.mbs.budgetplannerserver.service;

import org.junit.jupiter.api.Test;
import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.UserRepository;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// A role lives in Auth0, which is what actually decides a user's privileges; the database
// only records it. Updating one without the other is how a demoted administrator keeps
// administrator access, so these pin that the two move together.
class UserServiceUpdateTest {

    private final MunicipalityService municipalityService = mock(MunicipalityService.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final Auth0Service auth0Service = mock(Auth0Service.class);
    private final Environment environment = mock(Environment.class);

    private final UserService userService =
            new UserService(municipalityService, userRepository, auth0Service, environment);

    private static final ResponseEntity<String> OK = new ResponseEntity<>("{}", HttpStatus.OK);

    @Test
    public void demotingAnAdminRemovesTheAdminRoleInAuth0() {
        User existing = user(UserService.ADMIN_USER_ROLE, true);
        stubRepository(existing);
        when(auth0Service.getRoleIdByName(UserService.ADMIN_USER_ROLE)).thenReturn("rol_admin");
        when(auth0Service.getRoleIdByName(UserService.REGULAR_USER_ROLE)).thenReturn("rol_regular");
        when(auth0Service.removeRole(any(), any())).thenReturn(OK);
        when(auth0Service.assignRole(any(), any())).thenReturn(OK);

        User updated = userService.update(1L, contract("Demoted", false, null));

        verify(auth0Service).removeRole(eq(existing), eq(java.util.List.of("rol_admin")));
        verify(auth0Service).assignRole(eq(existing), eq(java.util.List.of("rol_regular")));
        assertEquals(UserService.REGULAR_USER_ROLE, updated.getRole());
        assertFalse(updated.getAdmin(), "the stored flag must follow the role");
    }

    // The old role must go before the new one arrives. Adding first would leave a user
    // holding both if the removal then failed, which for a demotion means keeping exactly
    // the privileges being removed.
    @Test
    public void aFailureToRemoveTheOldRoleAbortsTheUpdate() {
        User existing = user(UserService.ADMIN_USER_ROLE, true);
        stubRepository(existing);
        when(auth0Service.getRoleIdByName(anyString())).thenReturn("rol_x");
        when(auth0Service.removeRole(any(), any()))
                .thenReturn(new ResponseEntity<>("nope", HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(RuntimeException.class, () -> userService.update(1L, contract("Demoted", false, null)));

        verify(auth0Service, never()).assignRole(any(), any());
        verify(userRepository, never()).save(any());
    }

    // The existing update screen sends only the admin flag. Deriving the role from that
    // alone would turn a Read-only user into an accountant the moment anyone corrected
    // their name.
    @Test
    public void editingOnlyTheNameLeavesAReadOnlyUserReadOnly() {
        User existing = user(UserService.READ_ONLY_ROLE, false);
        stubRepository(existing);

        User updated = userService.update(1L, contract("New Name", false, null));

        verify(auth0Service, never()).removeRole(any(), any());
        verify(auth0Service, never()).assignRole(any(), any());
        assertEquals(UserService.READ_ONLY_ROLE, updated.getRole());
        assertEquals("New Name", updated.getName());
    }

    @Test
    public void anExplicitRoleChangeIsApplied() {
        User existing = user(UserService.REGULAR_USER_ROLE, false);
        stubRepository(existing);
        when(auth0Service.getRoleIdByName(UserService.REGULAR_USER_ROLE)).thenReturn("rol_regular");
        when(auth0Service.getRoleIdByName(UserService.READ_ONLY_ROLE)).thenReturn("rol_readonly");
        when(auth0Service.removeRole(any(), any())).thenReturn(OK);
        when(auth0Service.assignRole(any(), any())).thenReturn(OK);

        User updated = userService.update(1L, contract("Same Name", false, UserService.READ_ONLY_ROLE));

        verify(auth0Service).removeRole(eq(existing), eq(java.util.List.of("rol_regular")));
        verify(auth0Service).assignRole(eq(existing), eq(java.util.List.of("rol_readonly")));
        assertEquals(UserService.READ_ONLY_ROLE, updated.getRole());
    }

    // Deleting only voided the row; Auth0 kept the account and its roles, so the person
    // could still sign in holding the privileges just taken from them.
    @Test
    public void deletingAUserRevokesEveryRoleTheyHoldInAuth0() {
        User existing = user(UserService.ADMIN_USER_ROLE, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(auth0Service.roleIdsOf(existing)).thenReturn(java.util.List.of("rol_admin", "rol_extra"));
        when(auth0Service.removeRole(any(), any())).thenReturn(OK);

        userService.delete(1L);

        // Both roles, including one this application never assigned — a role added by hand
        // in the Auth0 dashboard must not survive the deletion.
        verify(auth0Service).removeRole(eq(existing), eq(java.util.List.of("rol_admin", "rol_extra")));
        verify(userRepository).delete(existing);
    }

    @Test
    public void aFailureToRevokeInAuth0LeavesTheUserInPlace() {
        User existing = user(UserService.ADMIN_USER_ROLE, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(auth0Service.roleIdsOf(existing)).thenReturn(java.util.List.of("rol_admin"));
        when(auth0Service.removeRole(any(), any()))
                .thenReturn(new ResponseEntity<>("nope", HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(RuntimeException.class, () -> userService.delete(1L));

        // Voiding the row while the account keeps its roles would be the worst outcome:
        // gone from the list, still able to sign in.
        verify(userRepository, never()).delete(any(User.class));
    }

    private void stubRepository(User existing) {
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    }

    private User user(String role, boolean admin) {
        Municipality municipality = new Municipality();
        municipality.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setName("Existing");
        user.setEmail("existing@example.test");
        user.setUserName("auth0|existing");
        user.setRole(role);
        user.setAdmin(admin);
        user.setMunicipality(municipality);
        return user;
    }

    private UserContract contract(String name, boolean admin, String role) {
        UserContract contract = new UserContract();
        contract.setName(name);
        contract.setAdmin(admin);
        contract.setRole(role);
        contract.setMunicipalityId(1L);
        return contract;
    }
}
