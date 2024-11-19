package com.main.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "testcase")
public class Testcase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "identificationid")
	private long identificationid;

	@Column(name = "validationid")
	private long validationid;

	@Column(name = "user")
	private String user;

	public Testcase() {
	}

	/**
	 * @param name
	 * @param identificationid
	 * @param validationid
	 * @param user
	 */
	public Testcase(String name, long identificationid, long validationid, String user) {
		super();
		this.name = name;
		this.identificationid = identificationid;
		this.validationid = validationid;
		this.user = user;

	}

	/**
	 * @return the id
	 */
	public long getid() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setid(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the identificationid
	 */
	public long getidentificationid() {
		return identificationid;
	}

	/**
	 * @param identificationid the identificationid to set
	 */
	public void setidentificationid(long identificationid) {
		this.identificationid = identificationid;
	}

	/**
	 * @return the validationid
	 */
	public long getValidationid() {
		return validationid;
	}

	/**
	 * @param validationid the validationid to set
	 */
	public void setValidationid(long validationid) {
		this.validationid = validationid;
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
