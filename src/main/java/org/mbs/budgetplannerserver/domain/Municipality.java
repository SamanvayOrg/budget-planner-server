package org.mbs.budgetplannerserver.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "municipality")
public class Municipality extends BaseModel{

	private String name;

	@ManyToOne(targetEntity = State.class)
	@JoinColumn(name = "state_id")
	private State state;

	private Long population;

	public String getName() {
		return name;
	}

	public Long getPopulation() {
		return population;
	}

	public void setPopulation(Long population) {
		this.population = population;
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
}
