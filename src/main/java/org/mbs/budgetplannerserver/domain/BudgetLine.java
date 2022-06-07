package org.mbs.budgetplannerserver.domain;

import org.mbs.budgetplannerserver.domain.code.DetailedHead;
import org.mbs.budgetplannerserver.domain.code.Function;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "budget_line")
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

    private BigDecimal plannedAmount;
    private BigDecimal revisedAmount;
    private BigDecimal actualAmount;
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

    public BigDecimal getPlannedAmount() {
        return plannedAmount;
    }

    public void setPlannedAmount(BigDecimal plannedAmount) {
        this.plannedAmount = plannedAmount;
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

    public BigDecimal getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(BigDecimal order) {
        this.displayOrder = order;
    }

    public String getFullCode() {
        return getFunction().getFullCode() + "-" + getDetailedHead().getFullCode();
    }

    public boolean matches(BudgetLine other) {
        return this.getFunction().getFullCode().equals(other.getFunction().getFullCode()) && this.getDetailedHead().getFullCode().equals(other.getDetailedHead().getFullCode());
    }
}
