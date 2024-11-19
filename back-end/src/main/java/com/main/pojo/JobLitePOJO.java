package com.main.pojo;

import java.util.List;
import java.util.stream.Collectors;

public class JobLitePOJO {

	private long id;
	private String status;
	private String createdOn;
	private String env;
	private String testID;

	public JobLitePOJO(long id, String status, String createdOn, List<String> env, List<String> testID) {
		this.id = id;
		this.status = status;
		this.createdOn = createdOn;
		this.env = env.stream().distinct().collect(Collectors.joining(","));
		this.testID = testID.stream().collect(Collectors.joining(","));
	}

	public JobLitePOJO() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getTestID() {
		return testID;
	}

	public void setTestID(String testID) {
		this.testID = testID;
	}

}
