package org.mbs.budgetplannerserver.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "city_class")
public class CityClass extends BaseModel {

	private String name;

	public CityClass(Long id) {
		super(id);
	}

	public CityClass() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
