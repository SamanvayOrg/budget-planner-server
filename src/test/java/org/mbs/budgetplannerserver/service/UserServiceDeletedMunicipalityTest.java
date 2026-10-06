package org.mbs.budgetplannerserver.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// A user whose municipality has been soft-deleted cannot be loaded: the eager join finds
// nothing behind Municipality's @Where filter and the repository throws. In production this
// surfaced as a 500 on every request the account made, with nothing to tell anyone why.
class UserServiceDeletedMunicipalityTest {

    private final MunicipalityService municipalityService = mock(MunicipalityService.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final Auth0Service auth0Service = mock(Auth0Service.class);

    private final UserService userService =
            new UserService(municipalityService, userRepository, auth0Service);

    private static final ResponseEntity<String> OK = new ResponseEntity<>("{}", HttpStatus.OK);

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void anAccountOnADeletedMunicipalityIsRefusedWithAReasonInsteadOfA500() {
        signedInAs("auth0|stranded");
        when(userRepository.findByUserName("auth0|stranded")).thenThrow(
                new JpaObjectRetrievalFailureException(
                        new EntityNotFoundException("Unable to find Municipality with id 13")));

        ResponseStatusException refused = assertThrows(ResponseStatusException.class, userService::getUser);

        assertEquals(HttpStatus.FORBIDDEN, refused.getStatus());
        assertTrue(refused.getReason().contains("municipality"), refused.getReason());
    }

    @Test
    public void anAccountWithNoRecordIsRefused() {
        signedInAs("auth0|unknown");
        when(userRepository.findByUserName("auth0|unknown")).thenReturn(null);

        ResponseStatusException refused = assertThrows(ResponseStatusException.class, userService::getUser);

        assertEquals(HttpStatus.FORBIDDEN, refused.getStatus());
    }

    // The stranded rows in production were left behind by municipality deletions. Deleting a
    // municipality must void its users, and must not be blocked by an account Auth0 has since lost.
    @Test
    public void deletingAMunicipalityVoidsItsUsersEvenWhenAuth0NoLongerKnowsOne() {
        User known = userIn(13L, "auth0|known");
        User gone = userIn(13L, "auth0|gone");
        when(userRepository.findByMunicipalityId(13L)).thenReturn(List.of(known, gone));
        when(auth0Service.roleIdsOf(known)).thenReturn(List.of("rol_admin"));
        when(auth0Service.removeRole(any(), any())).thenReturn(OK);
        when(auth0Service.roleIdsOf(gone)).thenThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, new byte[0], null));

        List<User> deleted = userService.deleteAllInMunicipality(13L);

        verify(auth0Service).removeRole(known, List.of("rol_admin"));
        verify(userRepository).delete(known);
        verify(userRepository).delete(gone);
        assertEquals(2, deleted.size());
    }

    private void signedInAs(String userName) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userName, "n/a", List.of()));
    }

    private User userIn(Long municipalityId, String userName) {
        Municipality municipality = new Municipality();
        municipality.setId(municipalityId);
        User user = new User();
        user.setUserName(userName);
        user.setEmail(userName + "@example.test");
        user.setMunicipality(municipality);
        return user;
    }
}
