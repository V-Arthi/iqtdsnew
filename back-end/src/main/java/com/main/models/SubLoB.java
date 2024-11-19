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
@Table(name = "tbl_sub_lob")
public class SubLoB {

	@Id
	@Column(name = "sub_lob")
	private String subLoB;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "sub_lob", referencedColumnName = "sub_lob")
	private List<TestCondition> testConditions;

	public SubLoB() {
	}

	public SubLoB(String subLoB) {
		super();
		this.subLoB = subLoB;
	}

	public String getSubLoB() {
		return subLoB;
	}

	public void setSubLoB(String subLoB) {
		this.subLoB = subLoB;
	}

	public List<TestCondition> getTestConditions() {
		return testConditions;
	}

	public void setTestConditions(List<TestCondition> testConditions) {
		this.testConditions = testConditions;
	}

}
