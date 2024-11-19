package com.main.pojo;

public class TestConditionPOJO {
	private final long testId;
	private final String testName;

	public TestConditionPOJO(long testId, String testName) {
		this.testId = testId;
		this.testName = testName;
	}

	public long getTestId() {
		return testId;
	}

	public String getTestName() {
		return testName;
	}

}
