package org.mbs.budgetplannerserver.domain;

import com.fasterxml.jackson.annotation.JsonRawValue;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "state")
@SQLDelete(sql = "UPDATE state SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class State extends BaseModel {

	public State(Long id) {
		super(id);
	}

	@Column(unique = true)
	private String name;

	public State() {
		super();
	}

	@Column(name = "languages", columnDefinition = "json")
	@JsonRawValue
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
