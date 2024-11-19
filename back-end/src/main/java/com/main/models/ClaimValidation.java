package com.main.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.main.libs.Util;

@Entity
@Table(name = "claim_validations")
public class ClaimValidation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "test_id", nullable = false)
	private long testId;

	private String claimIds;

	private String env;

	private String status;

	@Column(name = "created_on", updatable = false)
	private Instant createdOn = Instant.now();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "claim_validation_id", referencedColumnName = "id")
	private List<ClaimValidationResult> results = new ArrayList<ClaimValidationResult>();

	public ClaimValidation(long id, long testId, List<String> claimIds, String env) {

		this.id = id;
		this.testId = testId;
		this.claimIds = claimIds.stream().collect(Collectors.joining(","));
		this.env = env;
	}

	public ClaimValidation(long testId, List<String> claimIds, String env) {

		this.testId = testId;
		this.claimIds = claimIds.stream().collect(Collectors.joining(","));
		this.env = env;
	}

	public ClaimValidation() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTestId() {
		return testId;
	}

	public void setTestId(long testId) {
		this.testId = testId;
	}

	public List<String> getClaimIds() {
		return Arrays.asList(claimIds.split(","));
	}

	public void setClaimIds(List<String> claimIds) {
		this.claimIds = claimIds.stream().collect(Collectors.joining(","));
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedOn() {
		return createdOn == null ? Util.getInstantAsString(Instant.now()) : Util.getInstantAsString(createdOn);
	}

	public List<ClaimValidationResult> getResult() {
		return results;
	}

	public void setResult(List<ClaimValidationResult> result) {
		this.results = result;
	}

}
