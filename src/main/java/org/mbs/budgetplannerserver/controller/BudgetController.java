package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.BudgetContract;
import org.mbs.budgetplannerserver.domain.Budgets;
import org.mbs.budgetplannerserver.repository.BudgetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class BudgetController {
	private BudgetsRepository budgetsRepository;

	@Autowired
	public BudgetController(BudgetsRepository budgetsRepository) {
		this.budgetsRepository = budgetsRepository;
	}

	@GetMapping("/allbudgets")
	public Iterable<Budgets> getBudgets() {
		return budgetsRepository.findAll();
	}


//	@GetMapping(path = {"/budget/{data}"})
//	public Optional<Budgets> budget(@PathVariable(required = false, name = "data") String data) {
//		return budgetsRepository.findById(Long.valueOf(data));
//	}

	@CrossOrigin
	@RequestMapping(value = "/budgets", method = GET)
	@ResponseBody
	public Budgets getBudgetByYear(@RequestParam("year") Optional<String> year) {
		return budgetsRepository.findByYear(year);
	}


//	@GetMapping("/budgetd")
//	public Iterable<Budgets> getBudgetByYear() {
//		return null;
//	}

	@CrossOrigin
	@RequestMapping(value = "/budget", method = GET)
	@ResponseBody
	public BudgetContract getBudget(@RequestParam("id") Optional<String> budgetId) {
		return BudgetContract.dummy();
	}


}
