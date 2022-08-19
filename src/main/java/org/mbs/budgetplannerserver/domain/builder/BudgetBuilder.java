package org.mbs.budgetplannerserver.domain.builder;

import org.mbs.budgetplannerserver.domain.*;
import org.mbs.budgetplannerserver.repository.SampleBudgetLineRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BudgetBuilder {
    private final Budget budget;

    public BudgetBuilder() {
        this.budget = new Budget();
        this.budget.setBudgetLines(new HashSet<>());
    }

    public BudgetBuilder withOpeningBalance(BigDecimal openingBalance) {
        budget.setOpeningBalance(openingBalance);
        return this;
    }

    public BudgetBuilder withClosingBalance(BigDecimal closingBalance) {
        budget.setClosingBalance(closingBalance);
        return this;
    }

    public BudgetBuilder withFinancialYear(int year) {
        budget.setFinancialYear(year);
        return this;
    }

    public BudgetBuilder withPopulation(Long population) {
        budget.setPopulation(population);
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

    public BudgetBuilder withBudgetStatusAudit(BudgetStatusAudit budgetStatusAudit) {
        budget.setBudgetStatusAudits(Arrays.asList(budgetStatusAudit));
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
