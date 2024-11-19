package com.main.models;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.main.libs.Util;

@Entity
@Table(name = "jobs")
public class JobInternal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "job_id")
	private List<Execution> runs;

	private String status;

	@Column(name = "created_on", updatable = false)
	private Instant createdOn;

	public JobInternal() {
	}

	public JobInternal(List<Execution> runs) {
		super();
		this.runs = runs;
		this.createdOn = Calendar.getInstance(TimeZone.getTimeZone("EST")).toInstant();
		this.status = "Submitted";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Execution> getRuns() {
		return runs/*
					 * .stream().filter(r->r.getTestCondition()!=null).collect(Collectors.toList())
					 */;
	}

	public void setRuns(List<Execution> runs) {
		this.runs = runs;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedOn() {
		return Util.getInstantAsString(createdOn);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
