package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.BudgetContract;
import org.mbs.budgetplannerserver.contract.BudgetLineContract;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.BudgetLine;
import org.mbs.budgetplannerserver.domain.BudgetLineDetail;
import org.mbs.budgetplannerserver.service.BudgetLineService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BudgetContractMapper {
    public BudgetContract map(Budget budget) {
        BudgetContract budgetContract = new BudgetContract();
        budgetContract.setOpeningBalance(budget.getOpeningBalance());
        budgetContract.setClosingBalance(budget.getClosingBalance());
        budgetContract.setPopulation(budget.getPopulation());
        budgetContract.setBudgetYear(budget.getFinancialYearString());
        budgetContract.setId(budget.getId());
        Set<BudgetLineDetail> budgetLineDetails = budget.getUniqueBudgetLineDetails();
        BudgetLineContractMapper budgetLineContractMapper = new BudgetLineContractMapper();
        List<BudgetLineContract> contractLines = budgetLineDetails
                .stream()
                .map(budgetLineDetail -> budgetLineContractMapper.map(budgetLineDetail, budget))
                .filter(budgetLine -> budgetLine != null)
                .collect(Collectors.toList());
        budgetContract.setBudgetLines(contractLines);

        return budgetContract;
    }

    public Budget withUpdatedActuals(BudgetContract budgetContract, Budget budget, BudgetLineService budgetLineService) {
        budgetContract
                .getBudgetLines()
                .stream()
                .forEach(budgetlineContract -> budget.addBudgetLine(updateActuals(budgetlineContract, budget, budgetLineService)));

        return budget;
    }

    public Budget withUpdatedEstimates(BudgetContract budgetContract, Budget budget, BudgetLineService budgetLineService) {
        budgetContract
                .getBudgetLines()
                .stream()
                .forEach(budgetlineContract -> budget.addBudgetLine(updateEstimates(budgetlineContract, budget, budgetLineService)));

        return budget;
    }

    public Budget withUpdatedBudgeted(BudgetContract budgetContract, Budget budget, BudgetLineService budgetLineService) {
        computeClosingBalance(budgetContract, budget);
        budgetContract
                .getBudgetLines()
                .stream()
                .forEach(budgetlineContract -> budget.addBudgetLine(updateBudgeted(budgetlineContract, budget, budgetLineService)));

        return budget;
    }

    private void computeClosingBalance(BudgetContract budgetContract, Budget budget) {
        BigDecimal closingBalance = budget.getOpeningBalance();
       closingBalance = closingBalance.add(budgetContract.getBudgetLines().stream()
                .filter(bc -> bc.getMajorHeadGroup().equals("Revenue Receipt"))
                .filter(bc -> bc.getBudgetedAmount() != null)
                .map(BudgetLineContract::getBudgetedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        closingBalance = closingBalance.add(budgetContract.getBudgetLines().stream()
                .filter(bc -> bc.getMajorHeadGroup().equals("Assets"))
                .filter(bc -> bc.getBudgetedAmount() != null)
                .map(BudgetLineContract::getBudgetedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        closingBalance = closingBalance.subtract(budgetContract.getBudgetLines().stream()
                .filter(bc -> bc.getMajorHeadGroup().equals("Expenses"))
                .filter(bc -> bc.getBudgetedAmount() != null)
                .map(BudgetLineContract::getBudgetedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        closingBalance = closingBalance.subtract(budgetContract.getBudgetLines().stream()
                .filter(bc -> bc.getMajorHeadGroup().equals("Liability"))
                .filter(bc -> bc.getBudgetedAmount() != null)
                .map(BudgetLineContract::getBudgetedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        budget.setClosingBalance(closingBalance);
    }

    private BudgetLine updateBudgeted(BudgetLineContract budgetLineContract, Budget budget, BudgetLineService budgetLineService) {
        BudgetLine budgetLine = getOrCreateBudgetLine(budgetLineContract, budget, budgetLineService);

        return new BudgetLineContractMapper().updateBudgeted(budgetLine, budgetLineContract);
    }

    private BudgetLine getOrCreateBudgetLine(BudgetLineContract budgetLineContract, Budget budget, BudgetLineService budgetLineService) {
        BudgetLine budgetLine = budget.matchingBudgetLine(budgetLineDetail(budgetLineContract));
        if (budgetLine == null) {
            budgetLine = budgetLineService.createBudgetLine(budgetLineContract.getFunctionCode(), budgetLineContract.getDetailedHeadCode(), budgetLineContract.getName());
        }
        return budgetLine;
    }

    private BudgetLine updateEstimates(BudgetLineContract budgetLineContract, Budget budget, BudgetLineService budgetLineService) {
        BudgetLine budgetLine = getOrCreateBudgetLine(budgetLineContract, budget, budgetLineService);

        return new BudgetLineContractMapper().updateEstimates(budgetLine, budgetLineContract);
    }

    private BudgetLine updateActuals(BudgetLineContract budgetLineContract, Budget budget, BudgetLineService budgetLineService) {
        BudgetLine budgetLine = getOrCreateBudgetLine(budgetLineContract, budget, budgetLineService);

        return new BudgetLineContractMapper().updateActuals(budgetLine, budgetLineContract);
    }

    private BudgetLineDetail budgetLineDetail(BudgetLineContract budgetLineContract) {
        return new BudgetLineDetail(budgetLineContract.getFunctionCode(), budgetLineContract.getDetailedHeadCode(), budgetLineContract.getName());
    }
}
