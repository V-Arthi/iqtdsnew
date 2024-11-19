package com.main.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "claim_accuracy_conditions")
public class ClaimAccuracyConditions {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "condition_name", columnDefinition = "longtext")
	private String condition;

	@Column(name = "query", columnDefinition = "longtext")
	private String query;

	@Column(name = "active")
	private boolean active;

	public ClaimAccuracyConditions() {
	}

	public ClaimAccuracyConditions(String condition, String query) {
		super();
		this.condition = condition;
		this.query = query;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "ClaimAccuracyConditions [condition=" + condition + ", query=" + query +", active=" + active + "]";
	}
}
