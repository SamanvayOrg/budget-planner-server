package org.mbs.budgetplannerserver.domain;

public class PreviousYearBudgets {
    private Budget yearMinus1;
    private Budget yearMinus2;
    private Budget yearMinus3;
    private Budget yearMinus4;

    public PreviousYearBudgets(Budget yearMinus1, Budget yearMinus2, Budget yearMinus3, Budget yearMinus4) {
        this.yearMinus1 = yearMinus1;
        this.yearMinus2 = yearMinus2;
        this.yearMinus3 = yearMinus3;
        this.yearMinus4 = yearMinus4;
    }

    public BudgetLine[] getBudgetLinesMatching(BudgetLine budgetLine) {
        return new BudgetLine[]
                {
                        nullSafeGetBudgetLine(yearMinus1, budgetLine),
                        nullSafeGetBudgetLine(yearMinus2, budgetLine),
                        nullSafeGetBudgetLine(yearMinus3, budgetLine),
                        nullSafeGetBudgetLine(yearMinus4, budgetLine)
                };
    }

    private BudgetLine nullSafeGetBudgetLine(Budget budget, BudgetLine budgetLine) {
        if (budget != null) {
            return budget.getBudgetLineMatching(budgetLine);
        }

        return null;
    }
}
