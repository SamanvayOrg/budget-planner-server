package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.Budgets;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetsRepository extends CrudRepository<Budgets, Long> {

	Budgets findById(Optional<String> year);

	Budgets findByYear(Optional<String> year);
}
