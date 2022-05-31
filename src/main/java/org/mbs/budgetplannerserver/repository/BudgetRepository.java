package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends CrudRepository<Budget, Long> {

    Budget findById(Optional<String> year);

    Budget findByMunicipalityAndFinancialYear(Municipality municipality, int financialYear);
}
