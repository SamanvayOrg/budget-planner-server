package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.domain.Budgets;
import org.mbs.budgetplannerserver.repository.BudgetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class BudgetController {
	private BudgetsRepository budgetsRepository;

	@Autowired
	public BudgetController(BudgetsRepository budgetsRepository) {
		this.budgetsRepository = budgetsRepository;
	}

	@GetMapping("/budgets")
	public Iterable<Budgets> getBudgets() {
		return budgetsRepository.findAll();
	}


//	@GetMapping(path = {"/budget/{data}"})
//	public Optional<Budgets> budget(@PathVariable(required = false, name = "data") String data) {
//		return budgetsRepository.findById(Long.valueOf(data));
//	}


	@RequestMapping(value = "/budget", method = GET)
	@ResponseBody
	public Budgets getBudgetByYear(@RequestParam("year") Optional<String> year) {
		return budgetsRepository.findByYear(year);
	}


	@GetMapping("/budgetd")
	public Iterable<Budgets> getBudgetByYear() {
		return null;
	}
}
