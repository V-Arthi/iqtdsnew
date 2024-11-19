package com.main.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.main.libs.Util;

@Entity
@Table(name = "jenkins_run")
public class JenkinsRun {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "build_id")
	private long buildID;

	@Column(name = "build_url")
	private String buildURL;

	private String env;

	@Lob
	@Column(name = "logs")
	private String logs;

	@Column(name = "input_id")
	private String inputDataID;

	@Column(name = "data_id")
	private String createdDataID;

	@Column(name = "created_on", updatable = false)
	private Date createdOn;

	public JenkinsRun(long buildID, String buildURL, String inputDataID, String env) {

		this.buildID = buildID;
		this.buildURL = buildURL;
		this.inputDataID = inputDataID;
		this.env = env;

	}

	public JenkinsRun() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBuildID() {
		return buildID;
	}

	public void setBuildID(long buildID) {
		this.buildID = buildID;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getBuildURL() {
		return buildURL;
	}

	public void setBuildURL(String buildURL) {
		this.buildURL = buildURL;
	}

	public String getInputDataID() {
		return inputDataID;
	}

	public void setInputDataID(String inputDataID) {
		this.inputDataID = inputDataID;
	}

	public String getCreatedDataID() {
		return createdDataID;
	}

	public void setCreatedDataID(String createdDataID) {
		this.createdDataID = createdDataID;
	}

	public String getLogs() {
		return logs;
	}

	public void setLogs(String logs) {
		this.logs = logs;
	}

	public String getCreatedOn() {
		return Util.getDateInFormat(createdOn, "MM-dd-yyyy");
	}

}
