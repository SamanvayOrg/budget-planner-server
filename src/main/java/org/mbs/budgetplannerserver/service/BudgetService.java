package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.PreviousYearBudgets;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.domain.Year;
import org.mbs.budgetplannerserver.repository.BudgetRepository;
import org.mbs.budgetplannerserver.repository.SampleBudgetLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private UserService userService;
    private BudgetRepository budgetRepository;
    private SampleBudgetLineRepository sampleBudgetLineRepository;

    @Autowired
    public BudgetService(UserService userService, BudgetRepository budgetRepository, SampleBudgetLineRepository sampleBudgetLineRepository) {
        this.userService = userService;
        this.budgetRepository = budgetRepository;
        this.sampleBudgetLineRepository = sampleBudgetLineRepository;
    }

    public Budget getBudgetForFinancialYear(int year) {
        User user = userService.getUser();
        Budget budget = budgetRepository.findByMunicipalityAndFinancialYear(user.getMunicipality(), year);
        List<Budget> previousBudgets = budgetRepository.findByMunicipalityAndFinancialYearBetweenOrderByFinancialYearDesc(user.getMunicipality(), year - 4, year - 1);
        PreviousYearBudgets previousYearBudgets = new PreviousYearBudgets(findForYear(previousBudgets, year - 1), findForYear(previousBudgets, year - 2), findForYear(previousBudgets, year - 3));
        budget.setPreviousYearBudgets(previousYearBudgets);

        return budget;
    }

    private Budget findForYear(List<Budget> budgets, int year) {
        return budgets.stream().filter(budget -> budget.getFinancialYear() == year).findFirst().orElse(null);
    }

    public Budget createBudget(int year) {
        User user = userService.getUser();
        Budget budget = budgetRepository.findByMunicipalityAndFinancialYear(user.getMunicipality(), year);
        return budget == null ? createBudgetInternal(year, user) : budget;
    }

    private Budget createBudgetInternal(int year, User user) {
        Budget newBudget = new Budget();
        newBudget.setFinancialYear(year);
        newBudget.setMunicipality(user.getMunicipality());

        newBudget.setBudgetLines(sampleBudgetLineRepository
                .findAllByState(user.getState())
                .stream()
                .map(sampleBudgetLine -> sampleBudgetLine.toBudgetLine(newBudget))
                .collect(Collectors.toSet()));

        budgetRepository.save(newBudget);

        return newBudget;
    }

    public Budget getCurrentBudget() {
        return getBudgetForFinancialYear(Year.currentYear());
    }

    public List<Budget> getAllBudgets() {
        User user = userService.getUser();
        return budgetRepository.findByMunicipality(user.getMunicipality());
    }
}
