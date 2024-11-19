package com.main.pojo;

public class ExtractEDIPOJO {

	private String id;

	private String env;

	private long runId;

	public ExtractEDIPOJO() {
	}

	public ExtractEDIPOJO(String id, String env, long runId) {
		this.id = id;
		this.env = env;
		this.runId = runId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public long getRunId() {
		return runId;
	}

	public void setRunId(long runId) {
		this.runId = runId;
	}

	@Override
	public String toString() {
		return "ExtractEDIPOJO [id=" + id + ", env=" + env + "]";
	}

}
