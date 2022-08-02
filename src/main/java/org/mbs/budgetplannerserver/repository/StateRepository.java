package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.State;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends CrudRepository<State, Long> {
    Optional<State> findByName(String stateName);
}
