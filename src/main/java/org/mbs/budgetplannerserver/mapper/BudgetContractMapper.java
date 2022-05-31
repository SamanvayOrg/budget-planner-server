package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.BudgetContract;
import org.mbs.budgetplannerserver.contract.BudgetLineContract;
import org.mbs.budgetplannerserver.domain.Budget;

import java.util.List;
import java.util.stream.Collectors;

public class BudgetContractMapper {
    public BudgetContract map(Budget budget) {
        BudgetContract budgetContract = new BudgetContract();
        budgetContract.setBudgetYear(budget.getFinancialYearString());
        List<BudgetLineContract> budgetLines = budget.getBudgetLinesOrdered().stream().map(budgetLine -> new BudgetLineContractMapper().map(budgetLine, budget)).collect(Collectors.toList());
        budgetContract.setBudgetLines(budgetLines);

        return budgetContract;
    }
}
