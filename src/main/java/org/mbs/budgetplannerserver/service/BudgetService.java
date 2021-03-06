package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.*;
import org.mbs.budgetplannerserver.domain.builder.BudgetBuilder;
import org.mbs.budgetplannerserver.repository.BudgetRepository;
import org.mbs.budgetplannerserver.repository.SampleBudgetLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final UserService userService;
    private final BudgetRepository budgetRepository;
    private final SampleBudgetLineRepository sampleBudgetLineRepository;

    @Autowired
    public BudgetService(UserService userService, BudgetRepository budgetRepository, SampleBudgetLineRepository sampleBudgetLineRepository) {
        this.userService = userService;
        this.budgetRepository = budgetRepository;
        this.sampleBudgetLineRepository = sampleBudgetLineRepository;
    }

    public Optional<Budget> getBudgetForFinancialYear(int year) {
        User user = userService.getUser();
        Budget budget = budgetRepository.findByMunicipalityAndFinancialYear(user.getMunicipality(), year);
        return Optional.ofNullable(withPreviousBudgets(user, budget));
    }

    public Budget getOrCreate(int year, int minus, boolean withBudgeLines) {
        User user = userService.getUser();
        Budget budget = budgetRepository.findByMunicipalityAndFinancialYear(user.getMunicipality(), year - minus);
        return budget == null ? createBudgetInternal(year - minus, user, withBudgeLines) : budget;
    }

    public Optional<Budget> getCurrentBudget() {
        return getBudgetForFinancialYear(Year.currentYear());
    }

    public List<Budget> getAllBudgets() {
        User user = userService.getUser();
        return budgetRepository.findByMunicipality(user.getMunicipality());
    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Optional<Budget> findById(Long id) {
        User user = userService.getUser();
        Budget budget = budgetRepository.findByMunicipalityAndId(user.getMunicipality(), id);
        return Optional.ofNullable(withPreviousBudgets(user, budget));
    }

    private Budget createBudgetInternal(int year, User user, boolean withBudgetLines) {
        BudgetBuilder budgetBuilder = new BudgetBuilder()
                .withFinancialYear(year)
                .forUser(user);
        if (withBudgetLines) {
            budgetBuilder.withSampleBudgetLines(sampleBudgetLineRepository, user);
        }

        Budget budget = budgetBuilder.build();
        budgetRepository.save(budget);

        return budget;
    }

    private Budget withPreviousBudgets(User user, Budget budget) {
        if (budget == null) {
            return null;
        }
        int financialYear = budget.getFinancialYear();
        List<Budget> previousBudgets = budgetRepository.findByMunicipalityAndFinancialYearBetweenOrderByFinancialYearDesc(user.getMunicipality(), financialYear - 5, financialYear - 1);
        PreviousYearBudgets previousYearBudgets = new PreviousYearBudgets(
                findForYear(previousBudgets, financialYear - 1),
                findForYear(previousBudgets, financialYear - 2),
                findForYear(previousBudgets, financialYear - 3),
                findForYear(previousBudgets, financialYear - 4));
        budget.setPreviousYearBudgets(previousYearBudgets);
        return budget;
    }

    private Budget findForYear(List<Budget> budgets, int year) {
        return budgets.stream().filter(budget -> budget.getFinancialYear() == year).findFirst().orElse(new NullBudget());
    }
}
