package com.main.models;

import java.time.Instant;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "testcaseexecution")
public class TestcaseExecution {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String tests;
	private String env;
	private String status;
	private String user;
	@Column(name = "created_on")
	@CreationTimestamp
	private Instant createdOn;
	@Column(name = "updated_on")
	@UpdateTimestamp
	private LocalDateTime updatedOn;
	private String jobID;
	@Column(name = "paaccno", columnDefinition = "longtext")
	private String paaccno;
	
	public TestcaseExecution() {
	}

	
	/**
	 * @param tests
	 * @param env
	 * @param status
	 * @param user
	 */
	public TestcaseExecution(String tests, String env, String status, String user) {
		super();
		this.tests = tests;
		this.env = env;
		this.status = status;
		this.setUser(user);
	}


	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the tests
	 */
	public String getTests() {
		return tests;
	}

	/**
	 * @param tests the tests to set
	 */
	public void setTests(String tests) {
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
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


	/**
	 * @return the jobID
	 */
	public String getJobID() {
		return jobID;
	}


	/**
	 * @param jobID the jobID to set
	 */
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}


	/**
	 * @return the paaccno
	 */
	public String getPaaccno() {
		return paaccno;
	}


	/**
	 * @param paaccno the paaccno to set
	 */
	public void setPaaccno(String paaccno) {
		this.paaccno = paaccno;
	}

}
