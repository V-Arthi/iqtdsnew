package com.main.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.main.interfaces.Validation;

@Entity
@Table(name = "claim_validation_results")
public class ClaimValidationResult implements Validation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String claimId;
	private String fieldName;
	private String expected;
	private String actual;
	private String result;

	public ClaimValidationResult() {

	}

	public ClaimValidationResult(long id, String claimId, String fieldName, String expected, String actual,
			String result) {
		this.id = id;
		this.claimId = claimId;
		this.fieldName = fieldName;
		this.expected = expected;
		this.actual = actual;
		this.result = result;
	}

	public ClaimValidationResult(String claimId, String fieldName, String expected, String actual, String result) {

		this.claimId = claimId;
		this.fieldName = fieldName;
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

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public String getExpected() {
		return expected;
	}

	@Override
	public String getActual() {
		return actual;
	}

	@Override
	public String getResult() {
		return result;
	}

	@Override
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;

	}

	@Override
	public void setExpected(String expected) {
		this.expected = expected;

	}

	@Override
	public void setActual(String actual) {
		this.actual = actual;
	}

	@Override
	public void setResult(String result) {
		this.result = result;
	}

}
