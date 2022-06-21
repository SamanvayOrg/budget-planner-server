package org.mbs.budgetplannerserver.domain;

import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
	private PreviousYearBudgets previousYearBudgets;

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
		return financialYear + "-" + String.valueOf(financialYear + 1).substring(2);
	}

	public void setFinancialYear(int year) {
		this.financialYear = year;
	}

	public Set<BudgetLine> getBudgetLines() {
		return budgetLines;
	}

	public Set<BudgetLineDetail> getSelfBudgetLineDetails() {
		return getBudgetLines().stream().map(BudgetLineDetail::new).collect(Collectors.toSet());
	}

	public Set<BudgetLineDetail> getUniqueBudgetLineDetails() {
		HashSet<BudgetLineDetail> budgetLineDetails = new HashSet<>();
		budgetLineDetails.addAll(getSelfBudgetLineDetails());
		budgetLineDetails.addAll(getPreviousYearBudgets().getUniqueBudgetLineDetails());
		return budgetLineDetails;
	}

	public void setBudgetLines(Set<BudgetLine> budgetLineItems) {
		this.budgetLines = budgetLineItems;
	}

	public PreviousYearBudgets getPreviousYearBudgets() {
		if (previousYearBudgets != null) {
			return previousYearBudgets;
		}
		return new PreviousYearBudgets(new NullBudget(), new NullBudget(), new NullBudget(), new NullBudget());
	}
	public void setPreviousYearBudgets(PreviousYearBudgets previousYearBudgets) {
		this.previousYearBudgets = previousYearBudgets;
	}

	public BudgetLine getBudgetLineMatching(BudgetLineDetail budgetLineDetail) {
		Optional<BudgetLine> matchingLine = getBudgetLines().stream().filter(line -> budgetLineDetail.matches(line)).findFirst();
		return matchingLine.orElse(null);
	}
}
