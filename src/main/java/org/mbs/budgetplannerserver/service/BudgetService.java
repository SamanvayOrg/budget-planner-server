package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.PreviousYearBudgets;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    private UserService userService;
    private BudgetRepository budgetRepository;

    @Autowired
    public BudgetService(UserService userService, BudgetRepository budgetRepository) {
        this.userService = userService;
        this.budgetRepository = budgetRepository;
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
}
