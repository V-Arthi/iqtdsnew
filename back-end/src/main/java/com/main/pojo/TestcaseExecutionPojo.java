package com.main.pojo;

import java.util.List;

public class TestcaseExecutionPojo {

	private List<Long> tests;
	private String env;
	private String user;
	/**
	 * @param tests
	 * @param env
	 * @param user
	 */
	public TestcaseExecutionPojo(List<Long> tests, String env, String user) {
		super();
		this.tests = tests;
		this.env = env;
		this.user = user;
	}
	/**
	 * @return the tests
	 */
	public List<Long> getTests() {
		return tests;
	}
	/**
	 * @param tests the tests to set
	 */
	public void setTests(List<Long> tests) {
		this.tests = tests;
	}
	/**
	 * @return the env
	 */
	public String getEnv() {
		return env;
	}
	/**
	 * @param env the env to set
	 */
	public void setEnv(String env) {
		this.env = env;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
}
