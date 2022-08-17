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

    public static final int PREV_YEAR_MINUS_TWO = 3;
    public static final int PREV_YEAR_MINUS_ONE = 2;
    public static final int PREV_YEAR = 1;
    public static final int CURRENT_YEAR = 0;
    private static final int INT_CONST_ZERO = 0;

    public BudgetLineContract map(BudgetLineDetail budgetLineDetail, Budget budget) {
        BudgetLine budgetLine = budget.matchingBudgetLine(budgetLineDetail);
        if(budgetLine == null) {//TODO check if this exception avoidance is correct
            return null;
        }
        BudgetLineContract budgetLineContract = new BudgetLineContract();
        budgetLineContract.setId(budgetLine.getId());
        budgetLineContract.setName(budgetLine.getName());
        budgetLineContract.setFunctionCode(budgetLine.getFunction().getFullCode());
        budgetLineContract.setDetailedHeadCode(budgetLine.getDetailedHead().getFullCode());
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

        BudgetLine currentYearBudgetLine = budgetLinesMatching[CURRENT_YEAR];
        if (currentYearBudgetLine != null) {
            budgetLineContract.setCurrentYear8MonthsActuals(currentYearBudgetLine.getEightMonthActualAmount());
            budgetLineContract.setCurrentYear4MonthsProbables(currentYearBudgetLine.getFourMonthProbableAmount());
        }
        budgetLineContract.setPreviousYearActuals(actualAmount(budgetLinesMatching, PREV_YEAR));
        budgetLineContract.setYearMinus1Actuals(actualAmount(budgetLinesMatching, PREV_YEAR_MINUS_ONE));
        budgetLineContract.setYearMinus2Actuals(actualAmount(budgetLinesMatching, PREV_YEAR_MINUS_TWO));
        budgetLineContract.setVoided(false);
        budgetLineContract.setEligibleForDeletion(evaluateBudgetLineEligibilityForDeletion(budgetLineContract, budgetLinesMatching));
        return budgetLineContract;
    }

    public boolean evaluateBudgetLineEligibilityForDeletion(BudgetLineContract budgetLineContract, BudgetLine[] budgetLinesMatching) {
        BigDecimal prevYearMinusOneBudgeted = budgetedAmount(budgetLinesMatching, PREV_YEAR_MINUS_ONE);
        BigDecimal prevYearMinusOneProbables = probableAmount(budgetLinesMatching, PREV_YEAR_MINUS_ONE);

        BigDecimal prevYearBudgeted = budgetedAmount(budgetLinesMatching, PREV_YEAR);
        BigDecimal prevYearActual = actualAmount(budgetLinesMatching, PREV_YEAR);
        BigDecimal prevYearEightMonthActual = eightMonthActualAmount(budgetLinesMatching, PREV_YEAR);

        BigDecimal currentYearProbables = probableAmount(budgetLinesMatching, CURRENT_YEAR);
        BigDecimal currentYearActual = actualAmount(budgetLinesMatching, CURRENT_YEAR);
        BigDecimal currentYearEightMonthActual = eightMonthActualAmount(budgetLinesMatching, CURRENT_YEAR);

        return (true
            &&(prevYearMinusOneProbables == null || prevYearMinusOneProbables.compareTo(BigDecimal.ZERO) == INT_CONST_ZERO)
            &&(prevYearMinusOneBudgeted == null || prevYearMinusOneBudgeted.compareTo(BigDecimal.ZERO) == INT_CONST_ZERO)
                &&(prevYearBudgeted == null || prevYearBudgeted.compareTo(BigDecimal.ZERO) == INT_CONST_ZERO)
                &&(prevYearActual == null || prevYearActual.compareTo(BigDecimal.ZERO) == INT_CONST_ZERO)
                &&(prevYearEightMonthActual == null || prevYearEightMonthActual.compareTo(BigDecimal.ZERO) == INT_CONST_ZERO)
                    &&(currentYearProbables == null || currentYearProbables.compareTo(BigDecimal.ZERO) == INT_CONST_ZERO)
                    &&(currentYearActual == null || currentYearActual.compareTo(BigDecimal.ZERO) == INT_CONST_ZERO)
                    &&(currentYearEightMonthActual == null || currentYearEightMonthActual.compareTo(BigDecimal.ZERO) == INT_CONST_ZERO));

    }

    private BigDecimal actualAmount(BudgetLine[] budgetLinesMatching, int index) {
        BudgetLine matching = budgetLinesMatching[index];
        return matching == null? null : matching.getActualAmount();
    }

    private BigDecimal budgetedAmount(BudgetLine[] budgetLinesMatching, int index) {
        BudgetLine matching = budgetLinesMatching[index];
        return matching == null? null : matching.getBudgetedAmount();
    }

    private BigDecimal eightMonthActualAmount(BudgetLine[] budgetLinesMatching, int index) {
        BudgetLine matching = budgetLinesMatching[index];
        return matching == null? null : matching.getEightMonthActualAmount();
    }

    private BigDecimal probableAmount(BudgetLine[] budgetLinesMatching, int index) {
        BudgetLine matching = budgetLinesMatching[index];
        return matching == null? null : matching.getFourMonthProbableAmount();
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
