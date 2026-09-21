package org.mbs.budgetplannerserver.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.service.MunicipalityService;
import org.mbs.budgetplannerserver.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// A Chief Officer (Municipality Admin) may create only regular users — accountants —
// never another Admin. A Super Admin still may, and reaches the update path below through
// the same endpoint, so both sides of the rule need pinning.
class UserControllerTest {

    private static final long MUNICIPALITY_ID = 1L;

    private final UserService userService = mock(UserService.class);
    private final MunicipalityService municipalityService = mock(MunicipalityService.class);
    private final UserController controller = new UserController(userService, municipalityService);

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void chiefOfficerCannotCreateAnotherAdmin() {
        signedInWith("read", "write", "admin");
        when(userService.getMunicipality()).thenReturn(municipality());

        assertThrows(AccessDeniedException.class, () -> controller.createUser(userContract(true)));

        verify(userService, never()).create(any());
    }

    @Test
    public void chiefOfficerCanCreateAnAccountant() {
        signedInWith("read", "write", "admin");
        when(userService.getMunicipality()).thenReturn(municipality());
        when(userService.create(any())).thenReturn(existingUser(false));

        controller.createUser(userContract(false));

        verify(userService).create(any());
    }

    @Test
    public void superAdminMayStillCreateAnAdmin() {
        signedInWith("read", "write", "admin", "superAdmin");
        when(userService.getMunicipality()).thenReturn(municipality());
        when(userService.create(any())).thenReturn(existingUser(true));

        controller.createUser(userContract(true));

        verify(userService).create(any());
    }

    // Without this the create rule is trivially bypassed: create an accountant, then
    // promote them to Admin.
    @Test
    public void chiefOfficerCannotPromoteAnExistingAccountantToAdmin() {
        signedInWith("read", "write", "admin");
        when(userService.getMunicipality()).thenReturn(municipality());
        when(userService.getUser(7L)).thenReturn(existingUser(false));

        assertThrows(AccessDeniedException.class, () -> controller.updateUser(7L, userContract(true)));

        verify(userService, never()).update(any(Long.class), any());
    }

    @Test
    public void chiefOfficerMayStillEditAnAccountantWithoutChangingPrivileges() {
        signedInWith("read", "write", "admin");
        when(userService.getMunicipality()).thenReturn(municipality());
        when(userService.getUser(7L)).thenReturn(existingUser(false));
        when(userService.update(any(Long.class), any())).thenReturn(existingUser(false));

        controller.updateUser(7L, userContract(false));

        verify(userService).update(any(Long.class), any());
    }

    @Test
    public void chiefOfficerCanCreateAReadOnlyUser() {
        signedInWith("read", "write", "admin");
        when(userService.getMunicipality()).thenReturn(municipality());
        when(userService.create(any())).thenReturn(existingUser(false));

        controller.createUser(userContractWithRole("Read-only"));

        verify(userService).create(any());
    }

    // The role field must not become a way around the create rule: asking for the Admin
    // role while passing admin=false has to be rejected exactly like admin=true is.
    @Test
    public void chiefOfficerCannotRequestTheAdminRoleWhileClaimingNotToBeAdmin() {
        signedInWith("read", "write", "admin");
        when(userService.getMunicipality()).thenReturn(municipality());

        assertThrows(AccessDeniedException.class,
                () -> controller.createUser(userContractWithRole("Admin")));

        verify(userService, never()).create(any());
    }

    @Test
    public void anUnknownRoleIsRejectedBeforeAnythingIsCreated() {
        signedInWith("read", "write", "admin");
        when(userService.getMunicipality()).thenReturn(municipality());

        assertThrows(ResponseStatusException.class,
                () -> controller.createUser(userContractWithRole("Auditor")));

        verify(userService, never()).create(any());
    }

    private UserContract userContractWithRole(String role) {
        UserContract contract = userContract(false);
        contract.setRole(role);
        return contract;
    }

    // Ids are above 127 so a reference comparison on boxed Longs cannot pass by accident.
    @Test
    public void adminCannotDeleteHimself() {
        signedInWith("read", "write", "admin");
        when(userService.getUser(500L)).thenReturn(existingUser(true));
        when(userService.getMunicipality()).thenReturn(municipality());
        when(userService.getUser()).thenReturn(userWithId(500L));

        assertThrows(AccessDeniedException.class, () -> controller.deleteUser(500L));

        verify(userService, never()).delete(any(Long.class));
    }

    @Test
    public void adminCanDeleteAnotherUserInHisMunicipality() {
        signedInWith("read", "write", "admin");
        when(userService.getUser(501L)).thenReturn(existingUser(false));
        when(userService.getMunicipality()).thenReturn(municipality());
        when(userService.getUser()).thenReturn(userWithId(500L));
        when(userService.delete(501L)).thenReturn(existingUser(false));

        controller.deleteUser(501L);

        verify(userService).delete(501L);
    }

    private User userWithId(Long id) {
        User user = existingUser(true);
        user.setId(id);
        return user;
    }

    private void signedInWith(String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("local|test", "n/a",
                        Arrays.stream(authorities).map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
    }

    private Municipality municipality() {
        Municipality municipality = new Municipality();
        municipality.setId(MUNICIPALITY_ID);
        return municipality;
    }

    private UserContract userContract(boolean admin) {
        UserContract contract = new UserContract();
        contract.setMunicipalityId(MUNICIPALITY_ID);
        contract.setAdmin(admin);
        contract.setName("Test User");
        contract.setEmail("test@local.test");
        return contract;
    }

    private User existingUser(boolean admin) {
        User user = new User();
        user.setAdmin(admin);
        user.setName("Test User");
        user.setEmail("test@local.test");
        user.setMunicipality(municipality());
        return user;
    }
}
