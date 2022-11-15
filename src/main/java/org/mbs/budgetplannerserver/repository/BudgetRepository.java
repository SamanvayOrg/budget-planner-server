package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends CrudRepository<Budget, Long> {

    Budget findByMunicipalityAndFinancialYear(Municipality municipality, int financialYear);

    List<Budget> findByFinancialYear(int financialYear);

    List<Budget> findByMunicipalityAndFinancialYearBetweenOrderByFinancialYearDesc(Municipality municipality, int startYear, int endYear);

    List<Budget> findByMunicipality(Municipality municipality);

    Budget findByMunicipalityAndId(Municipality municipality, Long id);
}
