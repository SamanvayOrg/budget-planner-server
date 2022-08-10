package org.mbs.budgetplannerserver.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "city_class")
@SQLDelete(sql = "UPDATE city_class SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class CityClass extends BaseModel {

	@Column(unique = true)
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
