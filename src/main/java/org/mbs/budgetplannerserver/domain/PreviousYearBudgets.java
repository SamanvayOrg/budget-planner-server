package org.mbs.budgetplannerserver.domain;

import java.util.HashSet;
import java.util.Set;

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

    public BudgetLine[] getBudgetLinesMatching(BudgetLineDetail budgetLineDetail) {
        return new BudgetLine[]
                {
                        yearMinus1.getBudgetLineMatching(budgetLineDetail),
                        yearMinus2.getBudgetLineMatching(budgetLineDetail),
                        yearMinus3.getBudgetLineMatching(budgetLineDetail),
                        yearMinus4.getBudgetLineMatching(budgetLineDetail)
                };
    }

    public Set<BudgetLineDetail> getUniqueBudgetLineDetails() {
        HashSet<BudgetLineDetail> budgetLineDetails = new HashSet<>();
        budgetLineDetails.addAll(yearMinus1.getSelfBudgetLineDetails());
        budgetLineDetails.addAll(yearMinus2.getSelfBudgetLineDetails());
        budgetLineDetails.addAll(yearMinus3.getSelfBudgetLineDetails());
        budgetLineDetails.addAll(yearMinus4.getSelfBudgetLineDetails());

        return budgetLineDetails;
    }
}
