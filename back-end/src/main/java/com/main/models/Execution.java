package com.main.models;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.main.libs.Util;

@Entity
@Table(name = "runs")
public class Execution {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "test_id")
	private TestCondition testCondition;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "job_id")
	private JobInternal jobInternal;

	private String env;

	private String status;

	@Column(name = "created_on", updatable = false)
	private Instant createdOn;

	@Column(name = "record_length_asked")
	private int recordLength;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "run_id")
	private List<ClaimResult> claimResults;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "run_id")
	private List<MemberResult> memberResults;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "run_id")
	private List<ProviderResult> providerResults;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "run_id")
	private List<JenkinsRun> jenkinsRuns;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "run_id")
	private List<RunExtractOverrideMapping> runExtMap;

	public Execution(TestCondition testCondition, String env, int recordLength) {
		super();
		this.env = env;
		this.testCondition = testCondition;
		this.status = "Submitted";
		this.recordLength = recordLength;
		this.createdOn = Calendar.getInstance(TimeZone.getTimeZone("EST")).toInstant();
	}

	public Execution() {
		this.createdOn = Calendar.getInstance(TimeZone.getTimeZone("EST")).toInstant();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TestCondition getTestCondition() {
		return testCondition;
	}

	public void setTestCondition(TestCondition testCondition) {
		this.testCondition = testCondition;
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

	public void setCreatedOn(Instant createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedOn() {
		return Util.getInstantAsString(createdOn);
	}

	public int getRecordLength() {
		return recordLength;
	}

	public void setRecordLength(int recordLength) {
		this.recordLength = recordLength;
	}

	public List<ClaimResult> getClaimResults() {
		return claimResults;
	}

	public void setClaimResults(List<ClaimResult> claimResults) {
		this.claimResults = claimResults;
	}

	public List<MemberResult> getMemberResults() {
		return memberResults;
	}

	public void setMemberResults(List<MemberResult> memberResults) {
		this.memberResults = memberResults;
	}

	public List<ProviderResult> getProviderResults() {
		return providerResults;
	}

	public void setProviderResults(List<ProviderResult> providerResults) {
		this.providerResults = providerResults;
	}

	public List<JenkinsRun> getJenkinsRuns() {
		return jenkinsRuns;
	}

	public void setJenkinsRuns(List<JenkinsRun> jenkinsRuns) {
		this.jenkinsRuns = jenkinsRuns;
	}

	public List<RunExtractOverrideMapping> getRunExtMap() {
		return runExtMap;
	}

	public void setRunExtMap(List<RunExtractOverrideMapping> runExtMap) {
		this.runExtMap = runExtMap;
	}

}
