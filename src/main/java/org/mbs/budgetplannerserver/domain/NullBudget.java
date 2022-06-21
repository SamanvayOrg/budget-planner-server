package org.mbs.budgetplannerserver.domain;

import java.util.Collections;
import java.util.Set;

public class NullBudget extends Budget{

    @Override
    public Municipality getMunicipality() {
        return new Municipality();
    }

    @Override
    public int getFinancialYear() {
        return 1990;
    }

    @Override
    public String getFinancialYearString() {
        return "";
    }

    @Override
    public Set<BudgetLine> getBudgetLines() {
        return Collections.emptySet();
    }

    @Override
    public PreviousYearBudgets getPreviousYearBudgets() {
        return new PreviousYearBudgets(new NullBudget(), new NullBudget(), new NullBudget(), new NullBudget());
    }
}
