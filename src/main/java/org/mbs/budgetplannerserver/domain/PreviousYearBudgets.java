package org.mbs.budgetplannerserver.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreviousYearBudgets {
    private final Budget[] previousYearBudgets;

    public PreviousYearBudgets(Budget yearMinus1, Budget yearMinus2, Budget yearMinus3, Budget yearMinus4) {
        previousYearBudgets = new Budget[]{yearMinus1, yearMinus2, yearMinus3, yearMinus4};
    }

    public BudgetLine[] getBudgetLinesMatching(BudgetLineDetail budgetLineDetail) {
        return new BudgetLine[]
                {
                        previousYearBudgets[0].matchingBudgetLine(budgetLineDetail),
                        previousYearBudgets[1].matchingBudgetLine(budgetLineDetail),
                        previousYearBudgets[2].matchingBudgetLine(budgetLineDetail),
                        previousYearBudgets[3].matchingBudgetLine(budgetLineDetail)
                };
    }

    public Set<BudgetLineDetail> getUniqueBudgetLineDetails() {
        HashSet<BudgetLineDetail> budgetLineDetails = new HashSet<>();
        Arrays.stream(previousYearBudgets).forEach(budget -> {
            budgetLineDetails.addAll(budget.getSelfBudgetLineDetails());
        });
        return budgetLineDetails;
    }

    public Budget getBudgetLineForYear(int minus) {
        assert minus < 4 && minus > 0;
        return previousYearBudgets[minus - 1];
    }
}
