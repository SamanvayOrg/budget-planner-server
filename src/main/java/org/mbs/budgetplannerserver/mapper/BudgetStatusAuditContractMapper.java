package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.BudgetStatusAuditContract;
import org.mbs.budgetplannerserver.domain.BudgetStatus;
import org.mbs.budgetplannerserver.domain.BudgetStatusAudit;

import java.util.ArrayList;
import java.util.List;

public class BudgetStatusAuditContractMapper {
    public Iterable<BudgetStatusAuditContract> map(Iterable<BudgetStatusAudit> budgetStatusAudits) {
        List<BudgetStatusAuditContract> budgetStatusAuditContract = new ArrayList<>();
        budgetStatusAudits.forEach(e -> {
            budgetStatusAuditContract.add(fromBudgetStatusAudit(e));
        });
        return budgetStatusAuditContract;
    }

    public BudgetStatusAuditContract fromBudgetStatusAudit(BudgetStatusAudit budgetStatusAudit) {
        BudgetStatusAuditContract budgetStatusAuditContract = new BudgetStatusAuditContract();
        budgetStatusAuditContract.setUserContract(new UserContractMapper().fromUser(budgetStatusAudit.getUser()));
        // prevBudgetStatus is null by design for a budget's first-ever status entry
        // (BudgetService.createBudgetInternal passes null as "previous" on creation) —
        // calling .toString() unconditionally NPEs on every freshly created budget.
        BudgetStatus prevBudgetStatus = budgetStatusAudit.getPrevBudgetStatus();
        budgetStatusAuditContract.setPrevBudgetStatus(prevBudgetStatus == null ? null : prevBudgetStatus.toString());
        budgetStatusAuditContract.setCurrentBudgetStatus(budgetStatusAudit.getCurrentBudgetStatus().toString());
        budgetStatusAuditContract.setCreatedAt(budgetStatusAudit.getCreatedAt());
        budgetStatusAuditContract.setAllowedNextBudgetStatuses(budgetStatusAudit.getCurrentBudgetStatus().allowedTransitionValues());
        return budgetStatusAuditContract;
    }
}
