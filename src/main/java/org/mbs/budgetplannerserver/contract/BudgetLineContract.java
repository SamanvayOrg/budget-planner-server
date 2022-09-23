package org.mbs.budgetplannerserver.contract;

import java.math.BigDecimal;

public class BudgetLineContract {
    private Long id;
    private String code;
    private String name;
    private String functionCode;
    private String detailedHeadCode;
    private BigDecimal budgetedAmount;
    private BigDecimal actuals;
    private BigDecimal eightMonthsActuals;
    private BigDecimal fourMonthsProbables;
    private BigDecimal currentYear8MonthsActuals;
    private BigDecimal currentYear4MonthsProbables;
    private BigDecimal currentYearBudgetedAmount;

    private BigDecimal currentYearActuals;
    private BigDecimal previousYearActuals;
    private BigDecimal yearMinus1Actuals;
    private BigDecimal yearMinus2Actuals;
    private BigDecimal displayOrder;
    private String majorHeadGroup;
    private BigDecimal majorHeadGroupDisplayOrder;
    private String majorHead;
    private String minorHead;
    private Boolean isEligibleForDeletion;
    private Boolean isVoided;
    private String category;
    private String functionGroupCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getDetailedHeadCode() {
        return detailedHeadCode;
    }

    public void setDetailedHeadCode(String detailedHeadCode) {
        this.detailedHeadCode = detailedHeadCode;
    }

    public BigDecimal getBudgetedAmount() {
        return budgetedAmount;
    }

    public void setBudgetedAmount(BigDecimal budgetedAmount) {
        this.budgetedAmount = budgetedAmount;
    }

    public BigDecimal getCurrentYear8MonthsActuals() {
        return currentYear8MonthsActuals;
    }

    public void setCurrentYear8MonthsActuals(BigDecimal currentYear8MonthsActuals) {
        this.currentYear8MonthsActuals = currentYear8MonthsActuals;
    }

    public BigDecimal getCurrentYear4MonthsProbables() {
        return currentYear4MonthsProbables;
    }

    public void setCurrentYear4MonthsProbables(BigDecimal currentYear4MonthsProbables) {
        this.currentYear4MonthsProbables = currentYear4MonthsProbables;
    }

    public BigDecimal getPreviousYearActuals() {
        return previousYearActuals;
    }

    public void setPreviousYearActuals(BigDecimal previousYearActuals) {
        this.previousYearActuals = previousYearActuals;
    }

    public BigDecimal getYearMinus1Actuals() {
        return yearMinus1Actuals;
    }

    public void setYearMinus1Actuals(BigDecimal yearMinus1Actuals) {
        this.yearMinus1Actuals = yearMinus1Actuals;
    }

    public BigDecimal getYearMinus2Actuals() {
        return yearMinus2Actuals;
    }

    public void setYearMinus2Actuals(BigDecimal yearMinus2Actuals) {
        this.yearMinus2Actuals = yearMinus2Actuals;
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

    public Boolean getEligibleForDeletion() {
        return isEligibleForDeletion;
    }

    public void setEligibleForDeletion(Boolean eligibleForDeletion) {
        isEligibleForDeletion = eligibleForDeletion;
    }

    public Boolean getVoided() {
        return isVoided;
    }

    public void setVoided(Boolean voided) {
        isVoided = voided;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public BigDecimal getCurrentYearBudgetedAmount() {
        return currentYearBudgetedAmount;
    }

    public void setCurrentYearBudgetedAmount(BigDecimal currentYearBudgetedAmount) {
        this.currentYearBudgetedAmount = currentYearBudgetedAmount;
    }

    public BigDecimal getActuals() {
        return actuals;
    }

    public void setActuals(BigDecimal actuals) {
        this.actuals = actuals;
    }

    public BigDecimal getCurrentYearActuals() {
        return currentYearActuals;
    }

    public void setCurrentYearActuals(BigDecimal currentYearActuals) {
        this.currentYearActuals = currentYearActuals;
    }

    public BigDecimal getEightMonthsActuals() {
        return eightMonthsActuals;
    }

    public void setEightMonthsActuals(BigDecimal eightMonthsActuals) {
        this.eightMonthsActuals = eightMonthsActuals;
    }

    public BigDecimal getFourMonthsProbables() {
        return fourMonthsProbables;
    }

    public void setFourMonthsProbables(BigDecimal fourMonthsProbables) {
        this.fourMonthsProbables = fourMonthsProbables;
    }

    public String getFunctionGroupCategory() {
        return functionGroupCategory;
    }

    public void setFunctionGroupCategory(String functionGroupCategory) {
        this.functionGroupCategory = functionGroupCategory;
    }
}
