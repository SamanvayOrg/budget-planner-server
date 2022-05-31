package org.mbs.budgetplannerserver.domain;

import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "budget")
@BatchSize(size = 100)
public class Budget extends BaseModel {
	private int financialYear;

	@ManyToOne(targetEntity = Municipality.class)
	@JoinColumn(name = "municipality_id")
	private Municipality municipality;


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "budget")
	private Set<BudgetLine> budgetLineItems;

	public void setMunicipality(Municipality municipality) {
		this.municipality = municipality;
	}

	public Municipality getMunicipality() {
		return municipality;
	}

	public int getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(int year) {
		this.financialYear = year;
	}

	public Set<BudgetLine> getBudgetLineItems() {
		return budgetLineItems;
	}

	public void setBudgetLineItems(Set<BudgetLine> budgetLineItems) {
		this.budgetLineItems = budgetLineItems;
	}
}
