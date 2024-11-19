package com.main.pojo;

public class JobPOJO {

	private long id;
	private String status;
	private String createdOn;

	public JobPOJO(long id, String status, String createdOn) {
		this.id = id;
		this.status = status;
		this.createdOn = createdOn;

	}

	public JobPOJO() {
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

}
