package org.mbs.budgetplannerserver.contract;

import java.util.ArrayList;
import java.util.List;

public class BudgetContract {
	private Long id;
	private String budgetYear;
	private List<BudgetLineContract> budgetLines = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getBudgetYear() {
		return budgetYear;
	}

	public void setBudgetYear(String budgetYear) {
		this.budgetYear = budgetYear;
	}

	public List<BudgetLineContract> getBudgetLines() {
		return budgetLines;
	}

	public void setBudgetLines(List<BudgetLineContract> budgetLines) {
		this.budgetLines = budgetLines;
	}
}
