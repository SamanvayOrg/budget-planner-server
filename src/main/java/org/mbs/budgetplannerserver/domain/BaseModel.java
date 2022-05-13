package org.mbs.budgetplannerserver.domain;

import javax.persistence.*;

@MappedSuperclass
public class BaseModel {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	@Id
	private Long id;

	public BaseModel(Long id) {
		this.id = id;
	}

	public BaseModel() {
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
