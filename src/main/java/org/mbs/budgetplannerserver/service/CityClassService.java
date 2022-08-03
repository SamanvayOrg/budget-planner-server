package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.CityClass;
import org.mbs.budgetplannerserver.repository.CityClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
@Service
public class CityClassService {
    private final CityClassRepository cityClassRepository;

    @Autowired
    public CityClassService(CityClassRepository cityClassRepository) {
        this.cityClassRepository = cityClassRepository;
    }

    @Transactional
    public Iterable<CityClass> getCityClass() {
        return cityClassRepository.findAll();
    }
}
