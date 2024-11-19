package com.main.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "claim_accuracy_results")
public class ClaimAccuracyResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String claimId;
	@Column(name = "query", columnDefinition = "longtext")
	private String query;
	private String expected;
	private String actual;
	private String result;

	public ClaimAccuracyResult() {

	}

	public ClaimAccuracyResult(long id, String claimId, String query, String expected, String actual,
			String result) {
		this.id = id;
		this.claimId = claimId;
		this.query = query;
		this.expected = expected;
		this.actual = actual;
		this.result = result;
	}

	public ClaimAccuracyResult(String claimId, String condition, String expected, String actual, String result) {

		this.claimId = claimId;
		this.query = condition;
		this.expected = expected;
		this.actual = actual;
		this.result = result;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getClaimId() {
		return claimId;
	}

	public void setClaimId(String claimId) {
		this.claimId = claimId;
	}

	public String getQuery() {
		return query;
	}

	public String getExpected() {
		return expected;
	}

	public String getActual() {
		return actual;
	}

	public String getResult() {
		return result;
	}

	public void setQuery(String query) {
		this.query = query;

	}

	public void setExpected(String expected) {
		this.expected = expected;

	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
