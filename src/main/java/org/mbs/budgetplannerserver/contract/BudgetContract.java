package org.mbs.budgetplannerserver.contract;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BudgetContract {
	private Long id;
	private String budgetYear;
	private BigDecimal openingBalance;
	private BigDecimal closingBalance;
	private Long population;
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

	public BigDecimal getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(BigDecimal openingBalance) {
		this.openingBalance = openingBalance;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public Long getPopulation() {
		return population;
	}

	public void setPopulation(Long population) {
		this.population = population;
	}

	public List<BudgetLineContract> getBudgetLines() {
		return budgetLines;
	}

	public void setBudgetLines(List<BudgetLineContract> budgetLines) {
		this.budgetLines = budgetLines;
	}
}
