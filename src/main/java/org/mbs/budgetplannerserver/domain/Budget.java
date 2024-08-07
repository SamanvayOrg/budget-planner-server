package org.mbs.budgetplannerserver.domain;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "budget", uniqueConstraints = {
		@UniqueConstraint(name = "UniqueFYAndMunicipality", columnNames = { "financialYear", "municipality_id"})
})
@BatchSize(size = 100)
@SQLDelete(sql = "UPDATE budget SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class Budget extends BaseModel {
	private int financialYear;
	private BigDecimal openingBalance;
	private BigDecimal closingBalance;
	private Long population;

	@ManyToOne(targetEntity = Municipality.class)
	@JoinColumn(name = "municipality_id")
	private Municipality municipality;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "budget")
	private Set<BudgetLine> budgetLines;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "budget")
	@OrderBy("createdAt desc")
	private List<BudgetStatusAudit> budgetStatusAudits;

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

	public Set<BudgetLine> getBudgetLines() {
		return budgetLines;
	}

	public Set<BudgetLineDetail> getSelfBudgetLineDetails() {
		return getBudgetLines().stream().map(BudgetLineDetail::forBudgetLine).collect(Collectors.toSet());
	}

	public Set<BudgetLineDetail> getUniqueBudgetLineDetails() {
		HashSet<BudgetLineDetail> budgetLineDetails = new HashSet<>();
		budgetLineDetails.addAll(getSelfBudgetLineDetails());
		budgetLineDetails.addAll(getPreviousYearBudgets().getUniqueBudgetLineDetails());
		return budgetLineDetails;
	}

	public void setBudgetLines(Set<BudgetLine> budgetLineItems) {
		this.budgetLines = budgetLineItems;
		this.budgetLines.forEach(budgetLine -> budgetLine.setBudget(this));
	}

	public void addBudgetLine(BudgetLine budgetLine) {
		this.getBudgetLines().add(budgetLine);
		budgetLine.setBudget(this);
	}

	public void removeBudgetLine(BudgetLine budgetLine) {
		if(budgetLine != null) {
			this.getBudgetLines().remove(budgetLine);
			budgetLine.setVoided(true);
		}
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

	public BudgetLine matchingBudgetLine(BudgetLineDetail budgetLineDetail) {
		Optional<BudgetLine> matchingLine = getBudgetLines().stream().filter(line -> budgetLineDetail.matches(line)).findFirst();
		return matchingLine.orElse(null);
	}

	public void setBudgetStatusAudits(List<BudgetStatusAudit> budgetStatusAudits) {
		this.budgetStatusAudits = budgetStatusAudits;
		this.budgetStatusAudits.forEach(bsa -> bsa.setBudget(this));
	}

	public List<BudgetStatusAudit> getBudgetStatusAudits() {
		return budgetStatusAudits;
	}

	public BudgetStatusAudit getLatestBudgetStatusAudit() {
		return budgetStatusAudits.stream().findFirst().orElseThrow(EntityNotFoundException::new);
	}
}
