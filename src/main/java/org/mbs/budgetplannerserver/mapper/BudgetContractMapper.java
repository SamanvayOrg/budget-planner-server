package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.BudgetContract;
import org.mbs.budgetplannerserver.contract.BudgetLineContract;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.BudgetLine;
import org.mbs.budgetplannerserver.domain.BudgetLineDetail;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BudgetContractMapper {
    public BudgetContract map(Budget budget) {
        BudgetContract budgetContract = new BudgetContract();
        budgetContract.setBudgetYear(budget.getFinancialYearString());
        Set<BudgetLineDetail> budgetLineDetails = budget.getUniqueBudgetLineDetails();
        BudgetLineContractMapper budgetLineContractMapper = new BudgetLineContractMapper();
        List<BudgetLineContract> contractLines = budgetLineDetails
                .stream()
                .map(
                budgetLineDetail -> budgetLineContractMapper.map(budgetLineDetail, budget))
                .collect(Collectors.toList());
        budgetContract.setBudgetLines(contractLines);

        return budgetContract;
    }}
