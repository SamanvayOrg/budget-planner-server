package org.mbs.budgetplannerserver.contract;

import java.util.ArrayList;
import java.util.List;

public class BudgetContract {
	private String budgetYear;
	private List<BudgetLineContract> budgetLines = new ArrayList<>();

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
