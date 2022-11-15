package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.BudgetReportsContract;
import org.mbs.budgetplannerserver.domain.Budget;

import java.util.List;
import java.util.stream.Collectors;

public class BudgetReportsContractMapper {

    public BudgetReportsContract map(Budget budget) {
        BudgetReportsContract budgetReportsContract = new BudgetReportsContract();
        budgetReportsContract.setBudgetId(budget.getId());
        budgetReportsContract.setBudgetStatus(budget.getLatestBudgetStatusAudit().getCurrentBudgetStatus().toString());
        budgetReportsContract.setMunicipalityId(budget.getMunicipality().getId());
        budgetReportsContract.setMunicipalityName(budget.getMunicipality().getName());
        return budgetReportsContract;
    }

    public List<BudgetReportsContract> map(List<Budget> budgets) {
        return budgets.stream().map(this::map).collect(Collectors.toList());
    }
}
