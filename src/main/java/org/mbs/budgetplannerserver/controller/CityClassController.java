package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.CityClassContract;
import org.mbs.budgetplannerserver.mapper.CityClassContractMapper;
import org.mbs.budgetplannerserver.service.CityClassService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
@RestController
public class CityClassController {
    private final CityClassService cityClassService;

    public CityClassController(CityClassService cityClassService) {
        this.cityClassService = cityClassService;
    }

    @RequestMapping(value = "/api/class", method = GET)
    public Iterable<CityClassContract> getAllCities() {
        return new CityClassContractMapper().map(cityClassService.getCityClass());
    }


}

