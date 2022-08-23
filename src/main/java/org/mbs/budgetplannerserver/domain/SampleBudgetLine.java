package org.mbs.budgetplannerserver.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.mbs.budgetplannerserver.domain.code.DetailedHead;
import org.mbs.budgetplannerserver.domain.code.Function;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "sample_budget_line")
@SQLDelete(sql = "UPDATE sample_budget_line SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class SampleBudgetLine  extends BaseModel{

    @ManyToOne(targetEntity = State.class)
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne(targetEntity = Function.class)
    @JoinColumn(name = "function_id")
    private Function function;

    @ManyToOne(targetEntity = DetailedHead.class)
    @JoinColumn(name = "detailed_head_id")
    private DetailedHead detailedHead;

    private String name;

    private BigDecimal displayOrder;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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

    public BigDecimal getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(BigDecimal displayOrder) {
        this.displayOrder = displayOrder;
    }

    public BudgetLine toBudgetLine() {
        BudgetLine budgetLine = new BudgetLine();

        budgetLine.setName(getName());
        budgetLine.setFunction(getFunction());
        budgetLine.setDetailedHead(getDetailedHead());
        budgetLine.setDisplayOrder(getDisplayOrder());
        budgetLine.setVoided(false);
        return budgetLine;
    }
}