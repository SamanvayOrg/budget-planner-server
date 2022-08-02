package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.MunicipalityContract;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.mapper.MunicipalityContractMapper;
import org.mbs.budgetplannerserver.service.MunicipalityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class MunicipalityController {
    private final MunicipalityService municipalityService;

    public MunicipalityController(MunicipalityService municipalityService) {
        this.municipalityService = municipalityService;
    }

    @RequestMapping(value = "/api/municipality", method = GET)
    @PreAuthorize("hasAuthority('read')") // ✨ 👈 New line ✨
    public MunicipalityContract getMunicipality() {
        return new MunicipalityContractMapper().map(municipalityService.getMunicipality());
    }

    @RequestMapping(value = "/api/municipality", method = POST)
    @PreAuthorize("hasAuthority('write')") // ✨ 👈 New line ✨
    public MunicipalityContract updateMunicipality(@RequestBody MunicipalityContract municipalityContract) {
        Municipality municipality = municipalityService.getMunicipality();
        municipality.setName(municipalityContract.getName());
        municipality.setPopulation(municipalityContract.getPopulation());
        return new MunicipalityContractMapper().map(municipalityService.save(municipality));
    }
}
