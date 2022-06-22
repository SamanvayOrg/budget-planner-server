package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.BudgetContract;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.Year;
import org.mbs.budgetplannerserver.mapper.BudgetContractMapper;
import org.mbs.budgetplannerserver.service.BudgetLineService;
import org.mbs.budgetplannerserver.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class BudgetController {
	private final BudgetService budgetService;
	private final BudgetLineService budgetLineService;

	public BudgetController(BudgetService budgetService, BudgetLineService budgetLineService) {
		this.budgetService = budgetService;
		this.budgetLineService = budgetLineService;
	}

	@RequestMapping(value = "/api/budget", method = GET)
	@ResponseBody
	public BudgetContract getBudgetByYear(@RequestParam("year") Integer year) {
		Budget budget = budgetService.getBudgetForFinancialYear(new Year(year).getYear()).orElseThrow(NOT_FOUND());
		return new BudgetContractMapper().map(budget);
	}

	@RequestMapping(value = "/api/budget", method = POST)
	public void create(@RequestParam("year") Integer year) {
		budgetService.getOrCreate(year, 0, true);
	}

	@RequestMapping(value = "/api/budget/actuals", method = POST)
	public void updateActuals(@RequestBody BudgetContract budgetContract) {
		Budget budget = budgetService.getOrCreate(Integer.parseInt(budgetContract.getBudgetYear().substring(0, 4)), 2, false);

		budgetService.save(new BudgetContractMapper().withUpdatedActuals(budgetContract, budget, budgetLineService));
	}

	@RequestMapping(value = "/api/budget/estimates", method = POST)
	public void updateEstimates(@RequestBody BudgetContract budgetContract) {
		Budget budget = budgetService.getOrCreate(Integer.parseInt(budgetContract.getBudgetYear().substring(0, 4)), 1, false);

		budgetService.save(new BudgetContractMapper().withUpdatedEstimates(budgetContract, budget, budgetLineService));
	}

	@RequestMapping(value = "/api/budget/budgeted", method = POST)
	public void updateBudgeted(@RequestBody BudgetContract budgetContract) {
		Budget budget = budgetService.findById(budgetContract.getId()).orElseThrow(NOT_FOUND());
		budgetService.save(new BudgetContractMapper().withUpdatedBudgeted(budgetContract, budget, budgetLineService));
	}

	@RequestMapping(value = "/api/budget/current", method = GET)
	public BudgetContract currentBudget() {
		Budget budget = budgetService.getCurrentBudget().orElseThrow(NOT_FOUND());
		return new BudgetContractMapper().map(budget);
	}

	@RequestMapping(value = "/api/budgets", method = GET)
	public List<BudgetContract> allBudgets() {
		List<Budget> budgets = budgetService.getAllBudgets();
		return budgets.stream().map(budget -> new BudgetContractMapper().map(budget)).collect(Collectors.toList());
	}

	private Supplier<ResponseStatusException> NOT_FOUND() {
		return () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
	}
}
