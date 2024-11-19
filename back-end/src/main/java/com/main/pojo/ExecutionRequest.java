package com.main.pojo;

public class ExecutionRequest {
	private Long testId;
	private String env;
	private int recordLength;

	public ExecutionRequest(Long testId, String env, int recordLength) {
		this.testId = testId;
		this.env = env;
		this.recordLength = recordLength;
	}

	public ExecutionRequest() {
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public int getRecordLength() {
		return recordLength;
	}

	public void setRecordLength(int recordLength) {
		this.recordLength = recordLength;
	}

}
