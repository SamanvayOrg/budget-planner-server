package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.BudgetContract;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.Year;
import org.mbs.budgetplannerserver.mapper.BudgetContractMapper;
import org.mbs.budgetplannerserver.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class BudgetController {
	private final BudgetService budgetService;

	public BudgetController(BudgetService budgetService) {
		this.budgetService = budgetService;
	}

	@RequestMapping(value = "/api/budget", method = GET)
	@ResponseBody
	public BudgetContract getBudgetByYear(@RequestParam("year") Integer year) {
		Budget budget = budgetService.getBudgetForFinancialYear(new Year(year).getYear());
		assertBudgetPresent(budget);
		return new BudgetContractMapper().map(budget);
	}

	private void assertBudgetPresent(Budget budget) {
		if (budget == null) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "entity not found"
			);
		}
	}

	@RequestMapping(value = "/api/budget", method = POST)
	public void create(@RequestParam("year") Integer year) {
		budgetService.createBudget(year);
	}

	@RequestMapping(value = "/api/budget/current", method = GET)
	public BudgetContract currentBudget() {
		Budget budget = budgetService.getCurrentBudget();
		assertBudgetPresent(budget);
		return new BudgetContractMapper().map(budget);
	}

	@RequestMapping(value = "/api/budgets", method = GET)
	public List<BudgetContract> allBudgets() {
		List<Budget> budgets = budgetService.getAllBudgets();
		return budgets.stream().map(budget -> new BudgetContractMapper().map(budget)).collect(Collectors.toList());
	}
}
