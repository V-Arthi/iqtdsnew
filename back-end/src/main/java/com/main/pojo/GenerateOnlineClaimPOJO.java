package com.main.pojo;

import java.util.ArrayList;
import java.util.List;

import com.main.models.ClaimFilter;
import com.main.models.Overrides;

public class GenerateOnlineClaimPOJO {

	private long runID;

	private String env;
	
	private String targetEnv;
	
	private String claimID;

	private String subscriberID;

	private String providerID;

	private List<Overrides> overrides = new ArrayList<Overrides>();

	private List<ClaimFilter> claimFilters = new ArrayList<ClaimFilter>();

	public GenerateOnlineClaimPOJO() {

	}

	public GenerateOnlineClaimPOJO(long runID, String env, String targetEnv, String claimID, String subsriberID, String providerID,
			List<Overrides> overrides) {
		this.runID = runID;
		this.env = env;
		this.targetEnv=targetEnv;
		this.claimID = claimID;
		this.subscriberID = subsriberID;
		this.providerID = providerID;
		this.overrides = overrides;
	}

	public String getTargetEnv() {
		return targetEnv;
	}

	public void setTargetEnv(String targetEnv) {
		this.targetEnv = targetEnv;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getClaimID() {
		return claimID;
	}

	public void setClaimID(String claimID) {
		this.claimID = claimID;
	}

	public String getSubscriberID() {
		return subscriberID;
	}

	public void setSubscriberID(String subscriberID) {
		this.subscriberID = subscriberID;
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public List<Overrides> getOverrides() {
		return overrides;
	}

	public void setOverrides(List<Overrides> overrides) {
		this.overrides = overrides;
	}

	public long getRunID() {
		return runID;
	}

	public void setRunID(long runID) {
		this.runID = runID;
	}

	public List<ClaimFilter> getClaimFilters() {
		return claimFilters;
	}

	public void setClaimFilters(List<ClaimFilter> claimFilters) {
		this.claimFilters = claimFilters;
	}

	@Override
	public String toString() {
		return "GenerateOnlineClaimPOJO [runID=" + runID + ", env=" + env + ", targetEnv=" + targetEnv + ", claimID="
				+ claimID + ", subscriberID=" + subscriberID + ", providerID=" + providerID + ", overrides=" + overrides
				+ ", claimFilters=" + claimFilters + "]";
	}

}
