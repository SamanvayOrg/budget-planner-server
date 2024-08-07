package org.mbs.budgetplannerserver.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.mbs.budgetplannerserver.domain.code.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mbs.budgetplannerserver.domain.AmountType.*;
import static org.mbs.budgetplannerserver.domain.PreviousYears.PREV_YEAR;
import static org.mbs.budgetplannerserver.domain.PreviousYears.PREV_YEAR_MINUS_1;

@Entity
@Table(name = "budget_line")
@SQLDelete(sql = "UPDATE budget_line SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class BudgetLine extends BaseModel{
    @ManyToOne(targetEntity = Budget.class)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne(targetEntity = Function.class)
    @JoinColumn(name = "function_id")
    private Function function;

    @ManyToOne(targetEntity = DetailedHead.class)
    @JoinColumn(name = "detailed_head_id")
    private DetailedHead detailedHead;

    private String name;

    private BigDecimal budgetedAmount;
    private BigDecimal revisedAmount;
    private BigDecimal actualAmount;
    private BigDecimal eightMonthActualAmount;
    private BigDecimal fourMonthProbableAmount;

    private BigDecimal displayOrder;

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public DetailedHead getDetailedHead() {
        return detailedHead;
    }

    public void setDetailedHead(DetailedHead detailedHead) {
        this.detailedHead = detailedHead;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudgetedAmount() {
        return budgetedAmount;
    }

    public void setBudgetedAmount(BigDecimal plannedAmount) {
        this.budgetedAmount = plannedAmount;
    }

    public BigDecimal getRevisedAmount() {
        return revisedAmount;
    }

    public void setRevisedAmount(BigDecimal revisedAmount) {
        this.revisedAmount = revisedAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getEightMonthActualAmount() {
        return eightMonthActualAmount;
    }

    public void setEightMonthActualAmount(BigDecimal eightMonthActualAmount) {
        this.eightMonthActualAmount = eightMonthActualAmount;
    }

    public BigDecimal getFourMonthProbableAmount() {
        return fourMonthProbableAmount;
    }

    public void setFourMonthProbableAmount(BigDecimal fourMonthProbableAmount) {
        this.fourMonthProbableAmount = fourMonthProbableAmount;
    }

    public BigDecimal getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(BigDecimal order) {
        this.displayOrder = order;
    }


    public boolean canBeDeleted(AmountType amountType) {
        switch(amountType) {
            case ESTIMATES: return nullOrZero(getBudgetedAmount());
            case ACTUALS: return isAllNullOrZero(Arrays.asList(getBudgetedAmount(), getFourMonthProbableAmount(), getEightMonthActualAmount()));
            default: return true;
        }
    }

    private static boolean isAllNullOrZero(List<BigDecimal> amounts) {
        return amounts.stream().allMatch(BudgetLine::nullOrZero);
    }

    private static boolean nullOrZero(BigDecimal amount) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public String getFullCode() {
        return getFunction().getFullCode() + "-" + getDetailedHead().getFullCode();
    }

    public BigDecimal getPreviousActuals(int index) {
        BudgetLine matchingBudgetLine = matchingBudgetLine(index);
        return matchingBudgetLine == null ? null : matchingBudgetLine.getActualAmount();
    }

    public BigDecimal getPreviousFourMonthProbables(int index) {
        BudgetLine matchingBudgetLine = matchingBudgetLine(index);
        return matchingBudgetLine == null ? null : matchingBudgetLine.getFourMonthProbableAmount();
    }

    public BigDecimal getPreviousBudgeted(int index) {
        BudgetLine matchingBudgetLine = matchingBudgetLine(index);
        return matchingBudgetLine == null ? null : matchingBudgetLine.getBudgetedAmount();
    }

    public MinorHead getMinorHead() {
        return getDetailedHead().getMinorHead();
    }

    public MajorHead getMajorHead() {
        return getDetailedHead().getMajorHead();
    }

    public MajorHeadGroup getMajorHeadGroup() {
        return getDetailedHead().getMajorHeadGroup();
    }

    public BigDecimal getPreviousEightMonthActuals(int index) {
        BudgetLine matchingBudgetLine = matchingBudgetLine(index);
        return matchingBudgetLine == null ? null : matchingBudgetLine.getEightMonthActualAmount();
    }

    public BudgetLine matchingBudgetLine(int index) {
        return matchingPreviousBudgetLines()[index];
    }

    public BudgetLine[] matchingPreviousBudgetLines() {
        return getBudget().getPreviousYearBudgets().getBudgetLinesMatching(BudgetLineDetail.forBudgetLine(this));
    }

    public boolean canBeDeleted() {
        BudgetLine currentYearBudgetLine = matchingBudgetLine(PREV_YEAR);
        BudgetLine previousYearBudgetLine = matchingBudgetLine(PREV_YEAR_MINUS_1);

        return (currentYearBudgetLine == null || currentYearBudgetLine.canBeDeleted(ESTIMATES)) &&
                (previousYearBudgetLine == null || previousYearBudgetLine.canBeDeleted(ACTUALS)) &&
                canBeDeleted(BUDGETED);
    }
}
