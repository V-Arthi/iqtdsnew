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
@Table(name = "claim_accuracy")
public class ClaimAccuracy {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "claimIds", columnDefinition = "longtext")
	private String claimIds;

	private String env;

	private String status;
	
	private String username;

	@Column(name = "created_on", updatable = false)
	private Instant createdOn = Instant.now();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "claim_accuracy_id", referencedColumnName = "id")
	private List<ClaimAccuracyResult> results = new ArrayList<ClaimAccuracyResult>();

	public ClaimAccuracy(long id, List<String> claimIds, String env, String username) {
		this.id = id;
		this.claimIds = claimIds.stream().collect(Collectors.joining(","));
		this.env = env;
		this.username = username;
	}

	public ClaimAccuracy(List<String> claimIds, String env, String username) {

		this.claimIds = claimIds.stream().collect(Collectors.joining(","));
		this.env = env;
		this.username = username;
	}

	public ClaimAccuracy() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public List<ClaimAccuracyResult> getResult() {
		return results;
	}

	public void setResult(List<ClaimAccuracyResult> result) {
		this.results = result;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

}
