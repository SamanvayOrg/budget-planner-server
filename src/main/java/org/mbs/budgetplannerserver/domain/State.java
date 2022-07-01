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
	private String languages;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}
}
