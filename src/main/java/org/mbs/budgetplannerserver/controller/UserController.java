package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.mapper.UserContractMapper;
import org.mbs.budgetplannerserver.service.MunicipalityService;
import org.mbs.budgetplannerserver.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class UserController {
    private static final String SUPER_ADMIN_AUTHORITY = "superAdmin";

    private final UserService userService;
    private final MunicipalityService municipalityService;

    public UserController(UserService userService, MunicipalityService municipalityService) {
        this.userService = userService;
        this.municipalityService = municipalityService;
    }

    @RequestMapping(value = "/api/users", method = GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('admin')")
    public Iterable<UserContract> getAllUsers() {
        return new UserContractMapper().map(userService.getAllUsers());
    }

    @RequestMapping(value = "/api/user", method = POST)
    @PreAuthorize("hasAuthority('admin')")
    public UserContract createUser(@RequestBody UserContract userContract) {
        if(!userContract.getMunicipalityId().equals(userService.getMunicipality().getId())) {
            throw new AccessDeniedException("Admin user can only create users in his own municipality");
        }
        if (isRequestingAdminPrivilege(userContract) && !currentUserIsSuperAdmin()) {
            throw new AccessDeniedException(
                    "A Municipality Admin can only create regular users. Creating another Admin requires a Super Admin.");
        }
        return new UserContractMapper().fromUser(userService.create(userContract));
    }

    @RequestMapping(value = "/api/user/{id}", method = PUT)
    @PreAuthorize("hasAuthority('admin')")
    public UserContract updateUser(@PathVariable Long id, @RequestBody UserContract userContract) {
        User existingUser = userService.getUser(id);
        if(!existingUser.getMunicipality().getId().equals(userService.getMunicipality().getId())) {
            throw new AccessDeniedException("Admin user can only update users in his own municipality");
        }
        // Only a Super Admin may promote to Admin; demotion and other edits are unrestricted.
        boolean becomesAdmin = UserService.ADMIN_USER_ROLE.equals(
                UserService.roleNameForUpdate(userContract, existingUser));
        boolean isPromotion = becomesAdmin && !Boolean.TRUE.equals(existingUser.getAdmin());
        if (isPromotion && !currentUserIsSuperAdmin()) {
            throw new AccessDeniedException(
                    "A Municipality Admin cannot promote a user to Admin. That requires a Super Admin.");
        }
        return new UserContractMapper().fromUser(userService.update(id, userContract));
    }

    // Resolve via the same rule the service applies, so the check cannot be bypassed through the role field.
    private boolean isRequestingAdminPrivilege(UserContract userContract) {
        return UserService.ADMIN_USER_ROLE.equals(UserService.roleNameFor(userContract));
    }

    private boolean currentUserIsSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> SUPER_ADMIN_AUTHORITY.equals(authority.getAuthority()));
    }

    @RequestMapping(value = "/api/user/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('admin')")
    public UserContract deleteUser(@PathVariable Long id) {
        if(!userService.getUser(id).getMunicipality().getId().equals(userService.getMunicipality().getId())) {
            throw new AccessDeniedException("Admin user can only delete users in his own municipality");
        }
        if(id.equals(userService.getUser().getId())) {
            throw new AccessDeniedException("Admin user can not delete himself");
        }
        return new UserContractMapper().fromUser(userService.delete(id));
    }

    @RequestMapping(value = "/api/user/changePassword", method = POST)
    @PreAuthorize("hasAuthority('write')")
    public UserContract changeUserPassword() {
        return new UserContractMapper().fromUser(userService.sendChangePasswordEmail(userService.getUser()));
    }

    @RequestMapping(value = "/api/user",method = GET)
    @PreAuthorize("hasAuthority('read')")
    public UserContract getUser(){
        return new UserContractMapper().fromUser(userService.getUser());
    }
}
