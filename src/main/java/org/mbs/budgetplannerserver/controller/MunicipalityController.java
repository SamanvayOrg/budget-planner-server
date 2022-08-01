package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.MunicipalityContract;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.mapper.MunicipalityContractMapper;
import org.mbs.budgetplannerserver.service.MunicipalityService;
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
    public MunicipalityContract getMunicipality() {
        return new MunicipalityContractMapper().map(municipalityService.getMunicipality());
    }

    @RequestMapping(value = "/api/municipality", method = POST)
    public void updateMunicipality(@RequestBody Municipality municipality) {
        municipalityService.save(municipality);
    }
}
