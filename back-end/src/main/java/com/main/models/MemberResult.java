package com.main.models;

import java.io.Serializable;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.main.db.utils.Record;

@Entity
@Table(name = "member_results")
public class MemberResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Lob
	@Column(name = "data", nullable = false)
	private String data;

	public MemberResult() {
	}

	public MemberResult(String data) {
		this.data = data;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LinkedHashMap<String, String> getData() {
		return new Record(data).asOrderedHashMap();
	}

	public void setData(String data) {
		this.data = data;
	}

}
