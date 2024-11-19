package com.main.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_lob")
public class LoB {

	@Id
	@Column(name = "lob")
	private String lob;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "lob", referencedColumnName = "lob")
	private List<TestCondition> testConditions;

	public LoB() {
	}

	public LoB(String lob) {
		super();
		this.lob = lob;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	public List<TestCondition> getTestConditions() {
		return testConditions;
	}

	public void setTestConditions(List<TestCondition> testConditions) {
		this.testConditions = testConditions;
	}

}
