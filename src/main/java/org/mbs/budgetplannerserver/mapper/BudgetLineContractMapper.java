package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.BudgetLineContract;
import org.mbs.budgetplannerserver.domain.*;
import org.mbs.budgetplannerserver.domain.code.DetailedHead;
import org.mbs.budgetplannerserver.domain.code.MajorHead;
import org.mbs.budgetplannerserver.domain.code.MajorHeadGroup;
import org.mbs.budgetplannerserver.domain.code.MinorHead;

import java.math.BigDecimal;

import static org.mbs.budgetplannerserver.domain.AmountType.*;
import static org.mbs.budgetplannerserver.domain.PreviousYears.PREV_YEAR;
import static org.mbs.budgetplannerserver.domain.PreviousYears.PREV_YEAR_MINUS_1;

public class BudgetLineContractMapper {

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
        budgetLineContract.setDisplayOrder(budgetLine.getDisplayOrder());
        budgetLineContract.setMinorHead(budgetLine.getMinorHead().getName());
        budgetLineContract.setMinorHeadCategory(budgetLine.getMinorHead().getCategory());
        budgetLineContract.setMajorHead(budgetLine.getMajorHead().getName());
        budgetLineContract.setMajorHeadDisplayOrder(budgetLine.getMajorHead().getDisplayOrder());
        budgetLineContract.setMajorHeadGroup(budgetLine.getMajorHeadGroup().getName());
        budgetLineContract.setMajorHeadGroupDisplayOrder(budgetLine.getMajorHeadGroup().getDisplayOrder());

        budgetLineContract.setBudgetedAmount(budgetLine.getBudgetedAmount());
        budgetLineContract.setActuals(budgetLine.getActualAmount());
        budgetLineContract.setEightMonthsActuals(budgetLine.getEightMonthActualAmount());
        budgetLineContract.setFourMonthsProbables(budgetLine.getFourMonthProbableAmount());

        budgetLineContract.setCurrentYearBudgetedAmount(budgetLine.getPreviousBudgeted(PREV_YEAR));
        budgetLineContract.setCurrentYearActuals(budgetLine.getPreviousActuals(PREV_YEAR));
        budgetLineContract.setCurrentYear8MonthsActuals(budgetLine.getPreviousEightMonthActuals(PREV_YEAR));
        budgetLineContract.setCurrentYear4MonthsProbables(budgetLine.getPreviousFourMonthProbables(PREV_YEAR));

        budgetLineContract.setPreviousYearActuals(budgetLine.getPreviousActuals(PreviousYears.PREV_YEAR_MINUS_1));
        budgetLineContract.setYearMinus1Actuals(budgetLine.getPreviousActuals(PreviousYears.PREV_YEAR_MINUS_2));
        budgetLineContract.setYearMinus2Actuals(budgetLine.getPreviousActuals(PreviousYears.PREV_YEAR_MINUS_3));

        budgetLineContract.setVoided(false);
        budgetLineContract.setEligibleForDeletion(budgetLine.canBeDeleted());
        return budgetLineContract;
    }

    public BudgetLine updateActuals(BudgetLine lineToBeUpdated, BudgetLineContract budgetLineContract) {
        lineToBeUpdated.setActualAmount(budgetLineContract.getPreviousYearActuals());
        return lineToBeUpdated;
    }

    public BudgetLine updateEstimates(BudgetLine lineToBeUpdated, BudgetLineContract budgetLineContract) {
        lineToBeUpdated.setEightMonthActualAmount(budgetLineContract.getCurrentYear8MonthsActuals());
        lineToBeUpdated.setFourMonthProbableAmount(budgetLineContract.getCurrentYear4MonthsProbables());
        return lineToBeUpdated;
    }

    public BudgetLine updateBudgeted(BudgetLine lineToBeUpdated, BudgetLineContract budgetLineContract) {
        lineToBeUpdated.setBudgetedAmount(budgetLineContract.getBudgetedAmount());
        return lineToBeUpdated;
    }

}
