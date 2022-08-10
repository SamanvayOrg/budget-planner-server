package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.MunicipalityContract;
import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.mapper.MunicipalityContractMapper;
import org.mbs.budgetplannerserver.mapper.UserContractMapper;
import org.mbs.budgetplannerserver.service.MunicipalityService;
import org.mbs.budgetplannerserver.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class MunicipalityController {
    private final MunicipalityService municipalityService;
    private final UserService userService;

    public MunicipalityController(MunicipalityService municipalityService, UserService userService) {
        this.municipalityService = municipalityService;
        this.userService = userService;
    }

    @RequestMapping(value = "/api/municipality", method = GET)
    @PreAuthorize("hasAuthority('read')") // ✨ 👈 New line ✨
    public MunicipalityContract getMunicipality() {
        return new MunicipalityContractMapper().fromMunicipality(userService.getMunicipality());
    }

    @RequestMapping(value = "/api/municipality", method = POST)
    @PreAuthorize("hasAuthority('superAdmin')") // ✨ 👈 New line ✨
    public MunicipalityContract createMunicipality(@RequestBody MunicipalityContract municipalityContract) {
        return new MunicipalityContractMapper().fromMunicipality(municipalityService.create(municipalityContract));
    }

    @RequestMapping(value = "/api/municipality/{id}/adminUser", method = POST)
    @PreAuthorize("hasAuthority('superAdmin')") // ✨ 👈 New line ✨
    public UserContract createMunicipalityAdminUser(@PathVariable Long id, @RequestBody UserContract userContract) {
        userContract.setMunicipalityId(id);
        userContract.setAdmin(true);
        return new UserContractMapper().fromUser(userService.create(userContract));
    }

    @RequestMapping(value = "/api/municipality/adminUser/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('superAdmin')") // ✨ 👈 New line ✨
    public UserContract deleteMunicipalityAdminUser(@PathVariable Long id) {
        if(!userService.getUser(id).getAdmin()) {
            throw new AccessDeniedException("SuperAdmin user can only delete Admin users of other Municipalities");
        }
        if(id != userService.getUser().getId()) {
            throw new AccessDeniedException("SuperAdmin user can not delete himself");
        }
        return new UserContractMapper().fromUser(userService.delete(id));
    }

    @RequestMapping(value = "/api/municipality/admins", method = GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('superAdmin')") // ✨ 👈 New line ✨
    public Iterable<UserContract> getAllMunicipalityAdmins() {
        return new UserContractMapper().map(userService.getAllMunicipalityAdmins());
    }

    @RequestMapping(value = "/api/municipality/{id}", method = PUT)
    @PreAuthorize("hasAuthority('write')") // ✨ 👈 New line ✨
    public MunicipalityContract updateMunicipality(@PathVariable Long id, @RequestBody MunicipalityContract municipalityContract) {
        if(!id.equals(userService.getMunicipality().getId())) {
            throw new AccessDeniedException("Admin user can only update his own municipality");
        }
        return new MunicipalityContractMapper().fromMunicipality(municipalityService.update(municipalityContract));
    }

    @RequestMapping(value = "/api/municipality/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('superAdmin')") // ✨ 👈 New line ✨
    public MunicipalityContract deleteMunicipality(@PathVariable Long id) {
        return new MunicipalityContractMapper().fromMunicipality(municipalityService.delete(id));
    }

    @RequestMapping(value = "/api/allMunicipalities", method = GET)
    @PreAuthorize("hasAnyAuthority('superAdmin')")
    public Iterable<MunicipalityContract> getAllMunicipalities(){
        return new MunicipalityContractMapper().map(municipalityService.getAllMunicipalities());
    }
}
