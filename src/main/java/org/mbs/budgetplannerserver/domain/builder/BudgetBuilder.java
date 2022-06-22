package org.mbs.budgetplannerserver.domain.builder;

import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.BudgetLine;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.SampleBudgetLineRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BudgetBuilder {
    private final Budget budget;

    public BudgetBuilder() {
        this.budget = new Budget();
        this.budget.setBudgetLines(new HashSet<>());
    }

    public BudgetBuilder withFinancialYear(int year) {
        budget.setFinancialYear(year);
        return this;
    }

    public BudgetBuilder forUser(User user) {
        budget.setMunicipality(user.getMunicipality());
        return this;
    }

    public BudgetBuilder withBudgetLines(Set<BudgetLine> budgetLines) {
        budget.setBudgetLines(budgetLines);
        return this;
    }

    public Budget build() {
        return budget;
    }

    public BudgetBuilder withSampleBudgetLines(SampleBudgetLineRepository sampleBudgetLineRepository, User user) {
        return withBudgetLines(sampleBudgetLineRepository
                .findAllByState(user.getState())
                .stream()
                .map(sampleBudgetLine -> sampleBudgetLine.toBudgetLine())
                .collect(Collectors.toSet()));
    }
}
