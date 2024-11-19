package com.main.pojo;

public class GenerateEDIPOJO {

	private long runExtractMapID;

	private String memberID;

	private String providerID;
	

	public GenerateEDIPOJO() {
	}

	public GenerateEDIPOJO(long runExtractMapID, String memberID, String providerID) {

		this.runExtractMapID = runExtractMapID;
		this.memberID = memberID;
		this.providerID = providerID;
	}

	public long getRunExtractMapID() {
		return runExtractMapID;
	}

	public void setRunExtractMapID(long runExtractMapID) {
		this.runExtractMapID = runExtractMapID;
	}

	public String getMemberID() {
		return memberID;
	}

	public void setMemberID(String memberID) {
		this.memberID = memberID;
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

}
