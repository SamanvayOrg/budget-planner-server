package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.MunicipalityContract;
import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.mapper.MunicipalityContractMapper;
import org.mbs.budgetplannerserver.mapper.UserContractMapper;
import org.mbs.budgetplannerserver.service.MunicipalityService;
import org.mbs.budgetplannerserver.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return new MunicipalityContractMapper().map(municipalityService.getMunicipality());
    }

    @RequestMapping(value = "/api/municipality", method = POST)
    @PreAuthorize("hasAuthority('superAdmin')") // ✨ 👈 New line ✨
    public MunicipalityContract createMunicipality(@RequestBody MunicipalityContract municipalityContract) {
        return new MunicipalityContractMapper().map(municipalityService.create(municipalityContract));
    }

    @RequestMapping(value = "/api/municipality/{id}/adminUser", method = POST)
    @PreAuthorize("hasAuthority('superAdmin')") // ✨ 👈 New line ✨
    public UserContract createMunicipalityAdminUser(@PathVariable Long id, @RequestBody UserContract userContract) {
        userContract.setMunicipalityId(id);
        userContract.setAdmin(true);
        return new UserContractMapper().fromUser(userService.create(userContract));
    }


    @RequestMapping(value = "/api/municipality/{id}", method = PUT)
    @PreAuthorize("hasAuthority('admin')") // ✨ 👈 New line ✨
    public MunicipalityContract updateMunicipality(@PathVariable Long id, @RequestBody MunicipalityContract municipalityContract) {
        if(!id.equals(municipalityService.getMunicipality().getId())) {
            throw new AccessDeniedException("Admin user can only update his own municipality");
        }
        return new MunicipalityContractMapper().map(municipalityService.update(municipalityContract));
    }
}
