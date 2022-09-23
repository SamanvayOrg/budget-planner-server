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

import static org.mbs.budgetplannerserver.domain.AmountType.*;

public class BudgetLineContractMapper {

    public static final int PREV_YEAR_MINUS_TWO = 3;
    public static final int PREV_YEAR_MINUS_ONE = 2;
    public static final int PREV_YEAR = 1;
    public static final int CURRENT_YEAR = 0;

    public BudgetLineContract map(BudgetLineDetail budgetLineDetail, Budget budget) {
        BudgetLine budgetLine = budget.matchingBudgetLine(budgetLineDetail);
        if (budgetLine == null) {//TODO check if this exception avoidance is correct
            return null;
        }
        BudgetLineContract budgetLineContract = new BudgetLineContract();
        budgetLineContract.setId(budgetLine.getId());
        budgetLineContract.setName(budgetLine.getName());
        budgetLineContract.setFunctionCode(budgetLine.getFunction().getFullCode());
        budgetLineContract.setFunctionGroupCategory(budgetLine.getFunction().getFunctionGroup().getCategory());
        budgetLineContract.setDetailedHeadCode(budgetLine.getDetailedHead().getFullCode());
        budgetLineContract.setCode(budgetLine.getFullCode());
        budgetLineContract.setBudgetedAmount(budgetLine.getBudgetedAmount());
        budgetLineContract.setActuals(budgetLine.getActualAmount());
        budgetLineContract.setEightMonthsActuals(budgetLine.getEightMonthActualAmount());
        budgetLineContract.setFourMonthsProbables(budgetLine.getFourMonthProbableAmount());
        budgetLineContract.setDisplayOrder(budgetLine.getDisplayOrder());
        DetailedHead detailedHead = budgetLine.getDetailedHead();
        MinorHead minorHead = detailedHead.getMinorHead();
        budgetLineContract.setMinorHead(minorHead.getName());
        budgetLineContract.setMinorHeadCategory(minorHead.getCategory());
        MajorHead majorHead = minorHead.getMajorHead();
        budgetLineContract.setMajorHead(majorHead.getName());
        MajorHeadGroup majorHeadGroup = majorHead.getMajorHeadGroup();
        budgetLineContract.setMajorHeadGroup(majorHeadGroup.getName());
        budgetLineContract.setMajorHeadGroupDisplayOrder(majorHeadGroup.getDisplayOrder());

        PreviousYearBudgets previousYearBudgets = budget.getPreviousYearBudgets();
        BudgetLine[] budgetLinesMatching = previousYearBudgets.getBudgetLinesMatching(budgetLineDetail);

        BudgetLine currentYearBudgetLine = budgetLinesMatching[CURRENT_YEAR];
        BudgetLine previousYearBudgetLine = budgetLinesMatching[PREV_YEAR];
        if (currentYearBudgetLine != null) {
            budgetLineContract.setCurrentYear8MonthsActuals(currentYearBudgetLine.getEightMonthActualAmount());
            budgetLineContract.setCurrentYear4MonthsProbables(currentYearBudgetLine.getFourMonthProbableAmount());
            budgetLineContract.setCurrentYearBudgetedAmount(currentYearBudgetLine.getBudgetedAmount());
        }
        budgetLineContract.setCurrentYearActuals(actualAmount(budgetLinesMatching, CURRENT_YEAR));
        budgetLineContract.setPreviousYearActuals(actualAmount(budgetLinesMatching, PREV_YEAR));
        budgetLineContract.setYearMinus1Actuals(actualAmount(budgetLinesMatching, PREV_YEAR_MINUS_ONE));
        budgetLineContract.setYearMinus2Actuals(actualAmount(budgetLinesMatching, PREV_YEAR_MINUS_TWO));
        budgetLineContract.setVoided(false);
        budgetLineContract.setEligibleForDeletion(evaluateBudgetLineEligibilityForDeletion(budgetLine, currentYearBudgetLine, previousYearBudgetLine));
        return budgetLineContract;
    }

    public boolean evaluateBudgetLineEligibilityForDeletion(BudgetLine budgetLine, BudgetLine currentYearBudgetLine, BudgetLine previousYearBudgetLine) {
        return (currentYearBudgetLine == null || currentYearBudgetLine.canBeDeleted(ESTIMATES)) &&
                (previousYearBudgetLine == null || previousYearBudgetLine.canBeDeleted(ACTUALS)) &&
                (budgetLine == null || budgetLine.canBeDeleted(BUDGETED));
    }

    private BigDecimal actualAmount(BudgetLine[] budgetLinesMatching, int index) {
        BudgetLine matching = budgetLinesMatching[index];
        return matching == null ? null : matching.getActualAmount();
    }

    public BudgetLine updateActuals(BudgetLine lineToBeUpdated, BudgetLineContract budgetLineContract) {
        BudgetLine budgetLine = lineToBeUpdated;
        lineToBeUpdated.setActualAmount(budgetLineContract.getPreviousYearActuals());
        return budgetLine;
    }

    public BudgetLine updateEstimates(BudgetLine lineToBeUpdated, BudgetLineContract budgetLineContract) {
        BudgetLine budgetLine = lineToBeUpdated;
        lineToBeUpdated.setEightMonthActualAmount(budgetLineContract.getCurrentYear8MonthsActuals());
        lineToBeUpdated.setFourMonthProbableAmount(budgetLineContract.getCurrentYear4MonthsProbables());
        return budgetLine;
    }

    public BudgetLine updateBudgeted(BudgetLine lineToBeUpdated, BudgetLineContract budgetLineContract) {
        BudgetLine budgetLine = lineToBeUpdated;
        lineToBeUpdated.setBudgetedAmount(budgetLineContract.getBudgetedAmount());
        return budgetLine;
    }

}
