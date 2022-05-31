package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.Year;
import org.mbs.budgetplannerserver.service.BudgetService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class BudgetController {
	private BudgetService budgetService;

	public BudgetController(BudgetService budgetService) {
		this.budgetService = budgetService;
	}

	@GetMapping("/budgets")
	public Iterable<Budget> getBudgets() {
		return Arrays.asList();
	}


	@RequestMapping(value = "/budget", method = GET)
	@ResponseBody
	public Budget getBudgetByYear(@RequestParam("year") Integer year) {
		return budgetService.getBudgetForFinancialYear(new Year(year).getYear());
	}
}
