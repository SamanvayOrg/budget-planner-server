package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.BudgetStatusAudit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetStatusAuditRepository extends CrudRepository<BudgetStatusAudit, Long> {
    Optional<BudgetStatusAudit> findFirstByBudgetOrderByCreatedAtDesc(Budget budget);
}


