package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.repository.BudgetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class BudgetController {
	private BudgetsRepository budgetsRepository;

	@Autowired
	public BudgetController(BudgetsRepository budgetsRepository) {
		this.budgetsRepository = budgetsRepository;
	}

	@GetMapping("/budgets")
	public Iterable<Budget> getBudgets() {
		return budgetsRepository.findAll();
	}


	@RequestMapping(value = "/budget", method = GET)
	@ResponseBody
	public Budget getBudgetByYear(@RequestParam("year") Optional<String> year) {
		return budgetsRepository.findByYear(year);
	}


	@GetMapping("/budgetd")
	public Iterable<Budget> getBudgetByYear() {
		return null;
	}
}
