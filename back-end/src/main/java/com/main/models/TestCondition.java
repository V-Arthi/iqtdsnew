package com.main.models;

import java.util.ArrayList;
import java.util.List;

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

@Entity
@Table(name = "test_conditions")
public class TestCondition {

	@Id
	@Column(name = "test_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long testId;

	@Column(name = "br_name")
	private String brName = " ";

	@Column(name = "test_name")
	private String testName;

	@Column(name = "claim_input_type", nullable = false)
	private String claimInputType;

	@Column(name = "claim_type", nullable = false)
	private String claimType;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "test_id", referencedColumnName = "test_id")
	private List<ClaimFilter> claimFilters = new ArrayList<ClaimFilter>();

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "test_id", referencedColumnName = "test_id")
	private List<MemberFilter> memberFilters = new ArrayList<MemberFilter>();

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "test_id", referencedColumnName = "test_id")
	private List<ProviderFilter> providerFilters = new ArrayList<ProviderFilter>();

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "test_id", referencedColumnName = "test_id")
	private List<Execution> runs = new ArrayList<Execution>();

	@Column(name = "tags")
	private String tags;

	@Column(name = "provider_type", nullable = false)
	private String providerType;

	

	public TestCondition() {
	}

	public TestCondition(String brName, String testName, String claimInputType, String claimType, String providerType,
			String tags) {
		super();
		this.brName = brName;
		this.testName = testName;
		this.claimInputType = claimInputType;
		this.claimType = claimType;
		this.providerType = providerType;
		this.tags = tags;
		
	}

	public long getTestId() {
		return testId;
	}

	public void setTestId(long testId) {
		this.testId = testId;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getClaimInputType() {
		return claimInputType;
	}

	public void setClaimInputType(String claimInputType) {
		this.claimInputType = claimInputType;
	}

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public List<ClaimFilter> getClaimFilters() {
		return claimFilters;
	}

	public void setClaimFilters(List<ClaimFilter> claimFilters) {
		this.claimFilters = claimFilters;
	}

	public List<MemberFilter> getMemberFilters() {
		return memberFilters;
	}

	public void setMemberFilters(List<MemberFilter> memberFilters) {
		this.memberFilters = memberFilters;
	}

	public List<ProviderFilter> getProviderFilters() {
		return providerFilters;
	}

	public void setProviderFilters(List<ProviderFilter> providerFilters) {
		this.providerFilters = providerFilters;
	}

	public String getProviderType() {
		return providerType;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}



}
