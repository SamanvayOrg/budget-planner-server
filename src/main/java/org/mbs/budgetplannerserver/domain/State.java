package org.mbs.budgetplannerserver.domain;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "state")
public class State extends BaseModel {

	public State(Long id) {
		super(id);
	}

	public State() {
		super();
	}
}
