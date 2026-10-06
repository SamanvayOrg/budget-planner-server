package org.mbs.budgetplannerserver.controller;

import org.junit.jupiter.api.Test;
import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.CityClass;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.State;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.service.MunicipalityService;
import org.mbs.budgetplannerserver.service.UserService;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// The create-an-administrator endpoint must force both the role and the flag, since an explicit role wins.
class MunicipalityControllerTest {

    private final MunicipalityService municipalityService = mock(MunicipalityService.class);
    private final UserService userService = mock(UserService.class);
    private final MunicipalityController controller = new MunicipalityController(municipalityService, userService);

    @Test
    public void createMunicipalityAdminUserForcesTheAdminRoleEvenIfTheBodyAsksForAnother() {
        when(userService.create(org.mockito.ArgumentMatchers.any())).thenReturn(adminUser());

        UserContract requestedReadOnly = new UserContract();
        requestedReadOnly.setName("Fake Admin");
        requestedReadOnly.setEmail("fake.admin@example.test");
        requestedReadOnly.setRole("Read-only");

        controller.createMunicipalityAdminUser(1L, requestedReadOnly);

        ArgumentCaptor<UserContract> sent = ArgumentCaptor.forClass(UserContract.class);
        verify(userService).create(sent.capture());
        assertEquals(UserService.ADMIN_USER_ROLE, sent.getValue().getRole(),
                "the requested role must be overridden with Admin");
        assertEquals(Boolean.TRUE, sent.getValue().getAdmin());
        assertEquals(UserService.ADMIN_USER_ROLE, UserService.roleNameFor(sent.getValue()),
                "resolution must also land on Admin, not on the role the caller asked for");
    }

    @Test
    public void createMunicipalityAdminUserAssignsTheMunicipalityFromThePath() {
        when(userService.create(org.mockito.ArgumentMatchers.any())).thenReturn(adminUser());

        controller.createMunicipalityAdminUser(7L, new UserContract());

        ArgumentCaptor<UserContract> sent = ArgumentCaptor.forClass(UserContract.class);
        verify(userService).create(sent.capture());
        assertEquals(7L, sent.getValue().getMunicipalityId());
    }

    // Users of a deleted municipality cannot be loaded afterwards (their eager join finds
    // nothing behind Municipality's @Where), so they have to go first — and only once the
    // municipality is known to exist, or a typo in the id would void users for nothing.
    @Test
    public void deletingAMunicipalityVoidsItsUsersFirst() {
        Municipality khopoli = municipalityNamed(13L, "Khopoli");
        when(municipalityService.getMunicipality(13L)).thenReturn(khopoli);
        when(municipalityService.delete(13L)).thenReturn(khopoli);

        controller.deleteMunicipality(13L);

        InOrder inOrder = inOrder(userService, municipalityService);
        inOrder.verify(userService).deleteAllInMunicipality(13L);
        inOrder.verify(municipalityService).delete(13L);
    }

    @Test
    public void anUnknownMunicipalityVoidsNoUsers() {
        when(municipalityService.getMunicipality(99L)).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> controller.deleteMunicipality(99L));

        verify(userService, never()).deleteAllInMunicipality(any());
        verify(municipalityService, never()).delete(any());
    }

    private Municipality municipalityNamed(Long id, String name) {
        State state = new State();
        state.setName("Maharashtra");
        CityClass cityClass = new CityClass();
        cityClass.setName("Municipal Council");
        Municipality municipality = new Municipality();
        municipality.setId(id);
        municipality.setName(name);
        municipality.setState(state);
        municipality.setCityClass(cityClass);
        return municipality;
    }

    private User adminUser() {
        Municipality municipality = new Municipality();
        municipality.setId(1L);
        User user = new User();
        user.setName("Fake Admin");
        user.setEmail("fake.admin@example.test");
        user.setAdmin(true);
        user.setRole(UserService.ADMIN_USER_ROLE);
        user.setMunicipality(municipality);
        return user;
    }
}
