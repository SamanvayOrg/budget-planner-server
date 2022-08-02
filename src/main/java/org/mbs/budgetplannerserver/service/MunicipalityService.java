package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.MunicipalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class MunicipalityService {
    private final UserService userService;
    public MunicipalityRepository municipalityRepository;

    @Autowired
    public MunicipalityService(UserService userService) {
        this.userService = userService;
    }

    public Municipality getMunicipality() {
        User user = userService.getUser();
        return user.getMunicipality();
    }

    public Municipality getMunicipality(Long municipalityId) {
        return municipalityRepository.findById(municipalityId).orElseThrow(EntityNotFoundException::new);
    }

    public Municipality save(Municipality municipality) {
        return municipalityRepository.save(municipality);
    }
}
