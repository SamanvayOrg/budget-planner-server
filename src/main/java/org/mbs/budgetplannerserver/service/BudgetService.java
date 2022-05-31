package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.domain.Year;
import org.mbs.budgetplannerserver.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return budgetRepository.findByMunicipalityAndFinancialYear(user.getMunicipality(), year);
    }
}
