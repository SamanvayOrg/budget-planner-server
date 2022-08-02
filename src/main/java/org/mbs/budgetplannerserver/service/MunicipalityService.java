package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.contract.MunicipalityContract;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.CityClassRepository;
import org.mbs.budgetplannerserver.repository.MunicipalityRepository;
import org.mbs.budgetplannerserver.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class MunicipalityService {
    private final UserService userService;
    private final MunicipalityRepository municipalityRepository;
    private final StateRepository stateRepository;
    private final CityClassRepository cityClassRepository;

    @Autowired
    public MunicipalityService(UserService userService, MunicipalityRepository municipalityRepository,
                               StateRepository stateRepository, CityClassRepository cityClassRepository) {
        this.userService = userService;
        this.municipalityRepository = municipalityRepository;
        this.stateRepository = stateRepository;
        this.cityClassRepository = cityClassRepository;
    }

    public Municipality getMunicipality() {
        User user = userService.getUser();
        return user.getMunicipality();
    }

    public Municipality getMunicipality(Long municipalityId) {
        return municipalityRepository.findById(municipalityId).orElseThrow(EntityNotFoundException::new);
    }

    public Municipality create(MunicipalityContract municipalityContract) {
        Municipality municipality = new Municipality();
        municipality.setName(municipalityContract.getName());
        municipality.setPopulation(municipalityContract.getPopulation());
        municipality.setState(stateRepository.findByName(municipalityContract.getState())
                .orElseThrow(EntityNotFoundException::new));
        municipality.setCityClass(cityClassRepository.findByName(municipalityContract.getCityClass())
                .orElseThrow(EntityNotFoundException::new));
        return save(municipality);
    }

    public Municipality update(MunicipalityContract municipalityContract) {
        Municipality municipality = getMunicipality(municipalityContract.getId());
        municipality.setName(municipalityContract.getName());
        municipality.setPopulation(municipalityContract.getPopulation());
        return save(municipality);
    }

    private Municipality save(Municipality municipality) {
        return municipalityRepository.save(municipality);
    }
}
