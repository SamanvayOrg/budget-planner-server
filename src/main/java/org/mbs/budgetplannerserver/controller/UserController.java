package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.mapper.UserContractMapper;
import org.mbs.budgetplannerserver.service.MunicipalityService;
import org.mbs.budgetplannerserver.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class UserController {
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
        return new UserContractMapper().fromUser(userService.create(userContract));
    }

    @RequestMapping(value = "/api/user/{id}", method = PUT)
    @PreAuthorize("hasAuthority('admin')")
    public UserContract updateUser(@PathVariable Long id, @RequestBody UserContract userContract) {
        if(!userService.getUser(id).getMunicipality().getId().equals(userService.getMunicipality().getId())) {
            throw new AccessDeniedException("Admin user can only update users in his own municipality");
        }
        return new UserContractMapper().fromUser(userService.update(id, userContract));
    }

    @RequestMapping(value = "/api/user/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('admin')")
    public UserContract deleteUser(@PathVariable Long id) {
        if(!userService.getUser(id).getMunicipality().getId().equals(userService.getMunicipality().getId())) {
            throw new AccessDeniedException("Admin user can only delete users in his own municipality");
        }
        if(id != userService.getUser().getId()) {
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
