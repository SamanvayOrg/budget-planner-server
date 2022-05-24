package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.Budget;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetsRepository extends CrudRepository<Budget, Long> {

		Budget findById(Optional<String> year);

	Budget findByYear(Optional<String> year);
}
