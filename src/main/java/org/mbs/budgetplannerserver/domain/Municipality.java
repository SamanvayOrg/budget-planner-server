package org.mbs.budgetplannerserver.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "municipality", uniqueConstraints = {
		@UniqueConstraint(name = "UniqueNameAndState", columnNames = { "name", "state_id"})
})
@SQLDelete(sql = "UPDATE municipality SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class Municipality extends BaseModel{

	private String name;

	@ManyToOne(targetEntity = State.class)
	@JoinColumn(name = "state_id")
	private State state;

	@ManyToOne(targetEntity = CityClass.class)
	@JoinColumn(name = "class_id")
	private CityClass cityClass;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getCityClass() {
		return cityClass.getName();
	}

	public void setCityClass(CityClass cityClass) {
		this.cityClass = cityClass;
	}
}
