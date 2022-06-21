package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.service.MunicipalityService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class MunicipalityController {
    private MunicipalityService municipalityService;

    public MunicipalityController(MunicipalityService municipalityService) {
        this.municipalityService = municipalityService;
    }

    @RequestMapping(value = "/api/municipality", method = GET)
    public Municipality getMunicipality() {
        return municipalityService.getMunicipality();
    }
}
