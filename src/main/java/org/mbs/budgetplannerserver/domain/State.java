package org.mbs.budgetplannerserver.domain;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "state")
public class State extends BaseModel {

	public State(Long id) {
		super(id);
	}

	private String name;

	public State() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
