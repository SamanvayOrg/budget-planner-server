package org.mbs.budgetplannerserver.contract;

import java.math.BigDecimal;

public class BudgetLineContract {
    private String code;
    private String name;
    private BigDecimal plannedAmount;
    private BigDecimal revisedAmount;
    private BigDecimal actualAmount;
    private BigDecimal yearMinus1ActualAmount;
    private BigDecimal yearMinus2ActualAmount;
    private BigDecimal yearMinus3ActualAmount;
    private BigDecimal displayOrder;
    private String majorHeadGroup;
    private BigDecimal majorHeadGroupDisplayOrder;
    private String majorHead;
    private String minorHead;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public BigDecimal getYearMinus1ActualAmount() {
        return yearMinus1ActualAmount;
    }

    public void setYearMinus1ActualAmount(BigDecimal yearMinus1ActualAmount) {
        this.yearMinus1ActualAmount = yearMinus1ActualAmount;
    }

    public BigDecimal getYearMinus2ActualAmount() {
        return yearMinus2ActualAmount;
    }

    public void setYearMinus2ActualAmount(BigDecimal yearMinus2ActualAmount) {
        this.yearMinus2ActualAmount = yearMinus2ActualAmount;
    }

    public BigDecimal getYearMinus3ActualAmount() {
        return yearMinus3ActualAmount;
    }

    public void setYearMinus3ActualAmount(BigDecimal yearMinus3ActualAmount) {
        this.yearMinus3ActualAmount = yearMinus3ActualAmount;
    }

    public BigDecimal getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(BigDecimal displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getMajorHeadGroup() {
        return majorHeadGroup;
    }

    public void setMajorHeadGroup(String majorHeadGroup) {
        this.majorHeadGroup = majorHeadGroup;
    }

    public BigDecimal getMajorHeadGroupDisplayOrder() {
        return majorHeadGroupDisplayOrder;
    }

    public void setMajorHeadGroupDisplayOrder(BigDecimal majorHeadGroupDisplayOrder) {
        this.majorHeadGroupDisplayOrder = majorHeadGroupDisplayOrder;
    }

    public String getMajorHead() {
        return majorHead;
    }

    public void setMajorHead(String majorHead) {
        this.majorHead = majorHead;
    }

    public String getMinorHead() {
        return minorHead;
    }

    public void setMinorHead(String minorHead) {
        this.minorHead = minorHead;
    }
}
