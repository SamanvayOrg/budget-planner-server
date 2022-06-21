package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.BudgetLineContract;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.BudgetLine;
import org.mbs.budgetplannerserver.domain.BudgetLineDetail;
import org.mbs.budgetplannerserver.domain.PreviousYearBudgets;
import org.mbs.budgetplannerserver.domain.code.DetailedHead;
import org.mbs.budgetplannerserver.domain.code.MajorHead;
import org.mbs.budgetplannerserver.domain.code.MajorHeadGroup;
import org.mbs.budgetplannerserver.domain.code.MinorHead;

import java.math.BigDecimal;

public class BudgetLineContractMapper {

    public BudgetLineContract map(BudgetLineDetail budgetLineDetail, Budget budget) {
        BudgetLine budgetLine = budget.getBudgetLineMatching(budgetLineDetail);
        BudgetLineContract budgetLineContract = new BudgetLineContract();
        budgetLineContract.setId(budgetLine.getId());
        budgetLineContract.setName(budgetLine.getName());
        budgetLineContract.setCode(budgetLine.getFullCode());
        budgetLineContract.setBudgetedAmount(budgetLine.getBudgetedAmount());
        budgetLineContract.setDisplayOrder(budgetLine.getDisplayOrder());
        DetailedHead detailedHead = budgetLine.getDetailedHead();
        MinorHead minorHead = detailedHead.getMinorHead();
        budgetLineContract.setMinorHead(minorHead.getName());
        MajorHead majorHead = minorHead.getMajorHead();
        budgetLineContract.setMajorHead(majorHead.getName());
        MajorHeadGroup majorHeadGroup = majorHead.getMajorHeadGroup();
        budgetLineContract.setMajorHeadGroup(majorHeadGroup.getName());
        budgetLineContract.setMajorHeadGroupDisplayOrder(majorHeadGroup.getDisplayOrder());

        PreviousYearBudgets previousYearBudgets = budget.getPreviousYearBudgets();
        BudgetLine[] budgetLinesMatching = previousYearBudgets.getBudgetLinesMatching(budgetLineDetail);

        BudgetLine currentYearBudgetLine = budgetLinesMatching[0];
        if (currentYearBudgetLine != null) {
            budgetLineContract.setCurrentYear8MonthsActuals(currentYearBudgetLine.getEightMonthActualAmount());
            budgetLineContract.setCurrentYear4MonthsProbables(currentYearBudgetLine.getFourMonthProbableAmount());
        }
        budgetLineContract.setPreviousYearActuals(actualAmount(budgetLinesMatching, 1));
        budgetLineContract.setYearMinus1Actuals(actualAmount(budgetLinesMatching, 2));
        budgetLineContract.setYearMinus2Actuals(actualAmount(budgetLinesMatching, 3));

        return budgetLineContract;
    }

    private BigDecimal actualAmount(BudgetLine[] budgetLinesMatching, int index) {
        BudgetLine matching = budgetLinesMatching[index];
        return matching == null? null : matching.getActualAmount();
    }

}
