package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.CityClass;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityClassRepository extends CrudRepository<CityClass, Long> {
    Optional<CityClass> findByName(String cityClassName);
}

