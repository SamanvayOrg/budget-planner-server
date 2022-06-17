package org.mbs.budgetplannerserver.domain;

import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "budget")
@BatchSize(size = 100)
public class Budget extends BaseModel {
	private int financialYear;

	@ManyToOne(targetEntity = Municipality.class)
	@JoinColumn(name = "municipality_id")
	private Municipality municipality;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "budget")
	private Set<BudgetLine> budgetLines;

	@Transient
	private PreviousYearBudgets previousYearBudgets = new PreviousYearBudgets(null, null, null, null);

	public void setMunicipality(Municipality municipality) {
		this.municipality = municipality;
	}

	public Municipality getMunicipality() {
		return municipality;
	}

	public int getFinancialYear() {
		return financialYear;
	}

	public String getFinancialYearString() {
		return String.valueOf(financialYear) + "-" + String.valueOf(financialYear + 1).substring(2);
	}

	public void setFinancialYear(int year) {
		this.financialYear = year;
	}

	public Set<BudgetLine> getBudgetLines() {
		return budgetLines;
	}

	public List<BudgetLine> getBudgetLinesOrdered() {
		return getBudgetLines().stream().sorted(Comparator.comparing(BudgetLine::getDisplayOrder)).collect(Collectors.toList());
	}

	public void setBudgetLines(Set<BudgetLine> budgetLineItems) {
		this.budgetLines = budgetLineItems;
	}

	public PreviousYearBudgets getPreviousYearBudgets() {
		return previousYearBudgets;
	}
	public void setPreviousYearBudgets(PreviousYearBudgets previousYearBudgets) {
		this.previousYearBudgets = previousYearBudgets;
	}

	public BudgetLine getBudgetLineMatching(BudgetLine budgetLine) {
		Optional<BudgetLine> matchingLine = getBudgetLines().stream().filter(line -> line.matches(budgetLine)).findFirst();
		return matchingLine.orElse(null);
	}

	public List<String> possibleCodes() {
		return this.getBudgetLines().stream().map(budgetLine -> budgetLine.getFullCode()).collect(Collectors.toList());
	}
}
