package org.mbs.budgetplannerserver.domain;

import java.util.Objects;

//Parameter object
public class BudgetLineDetail {
    private final String functionCode;
    private final String detailedHeadCode;
    private final String name;

    public BudgetLineDetail(BudgetLine budgetLine) {
        functionCode = budgetLine.getFunction().getFullCode();
        detailedHeadCode = budgetLine.getDetailedHead().getFullCode();
        name = budgetLine.getName();
    }

    public boolean matches(BudgetLine budgetLine) {
        return budgetLine.getFunction().getFullCode().equals(functionCode) &&
                budgetLine.getDetailedHead().getFullCode().equals(detailedHeadCode) &&
                budgetLine.getName().equals(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BudgetLineDetail that = (BudgetLineDetail) o;
        return Objects.equals(functionCode, that.functionCode) && Objects.equals(detailedHeadCode, that.detailedHeadCode) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionCode, detailedHeadCode, name);
    }
}
