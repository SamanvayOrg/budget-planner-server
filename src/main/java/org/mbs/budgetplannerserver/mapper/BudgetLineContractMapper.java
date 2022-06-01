package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.BudgetLineContract;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.BudgetLine;
import org.mbs.budgetplannerserver.domain.PreviousYearBudgets;
import org.mbs.budgetplannerserver.domain.code.DetailedHead;
import org.mbs.budgetplannerserver.domain.code.MajorHead;
import org.mbs.budgetplannerserver.domain.code.MajorHeadGroup;
import org.mbs.budgetplannerserver.domain.code.MinorHead;

import java.math.BigDecimal;

public class BudgetLineContractMapper {

    public BudgetLineContract map(BudgetLine budgetLine, Budget budget) {
        BudgetLineContract budgetLineContract = new BudgetLineContract();
        budgetLineContract.setName(budgetLine.getDetailedHead().getName());
        budgetLineContract.setCode(budgetLine.getFullCode());
        budgetLineContract.setPlannedAmount(budgetLine.getPlannedAmount());
        budgetLineContract.setRevisedAmount(budgetLine.getRevisedAmount());
        budgetLineContract.setActualAmount(budgetLine.getActualAmount());
        budgetLineContract.setDisplayOrder(budgetLine.getDisplayOrder());
        DetailedHead detailedHead = budgetLine.getDetailedHead();
        MinorHead minorHead = detailedHead.getMinorHead();
        budgetLineContract.setMinorHead(minorHead.getName());
        MajorHead majorHead = minorHead.getMajorHead();
        budgetLineContract.setMajorHead(majorHead.getName());
        MajorHeadGroup majorHeadGroup = majorHead.getMajorHeadGroup();
        budgetLineContract.setMajorHeadGroup(majorHeadGroup.getName());

        PreviousYearBudgets previousYearBudgets = budget.getPreviousYearBudgets();
        BudgetLine[] budgetLinesMatching = previousYearBudgets.getBudgetLinesMatching(budgetLine);
        budgetLineContract.setYearMinus1ActualAmount(actualAmount(budgetLinesMatching, 0));
        budgetLineContract.setYearMinus2ActualAmount(actualAmount(budgetLinesMatching, 1));
        budgetLineContract.setYearMinus3ActualAmount(actualAmount(budgetLinesMatching, 2));

        return budgetLineContract;
    }

    private BigDecimal actualAmount(BudgetLine[] budgetLinesMatching, int index) {
        BudgetLine matching = budgetLinesMatching[index];
        return matching == null? BigDecimal.valueOf(0.0) : matching.getActualAmount();
    }

}
