package org.mbs.budgetplannerserver.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "budget")
public class Budget extends BaseModel {


	private String year;

	@ManyToOne(targetEntity = Municipality.class)
	@JoinColumn(name = "municipality_id")
	private Municipality municipality;

	public void setMunicipality(Municipality municipality) {
		this.municipality = municipality;
	}

	public Municipality getMunicipality() {
		return municipality;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getYear() {
		return String.valueOf(year);
	}
}
