package org.mbs.budgetplannerserver.controller;

import org.junit.jupiter.api.Test;
import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.service.MunicipalityService;
import org.mbs.budgetplannerserver.service.UserService;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
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
