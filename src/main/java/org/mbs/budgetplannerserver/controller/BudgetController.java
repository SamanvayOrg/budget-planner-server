package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.BudgetContract;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.Year;
import org.mbs.budgetplannerserver.mapper.BudgetContractMapper;
import org.mbs.budgetplannerserver.service.BudgetService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class BudgetController {
	private BudgetService budgetService;

	public BudgetController(BudgetService budgetService) {
		this.budgetService = budgetService;
	}

	@RequestMapping(value = "/budget", method = GET)
	@ResponseBody
	public BudgetContract getBudgetByYear(@RequestParam("year") Integer year) {
		Budget budget = budgetService.getBudgetForFinancialYear(new Year(year).getYear());
		return new BudgetContractMapper().map(budget);
	}
}
