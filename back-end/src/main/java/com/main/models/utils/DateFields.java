package com.main.models.utils;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@MappedSuperclass
@JsonIgnoreProperties(value = { "created_on", "updated_on" }, allowGetters = true)

public abstract class DateFields {

	@CreatedDate
	@Column(name = "created_on", nullable = false, updatable = false)
	private Instant createdOn;

	@LastModifiedDate
	@Column(name = "updated_on", nullable = false)
	private Instant updatedOn;

	public Instant getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Instant createdOn) {
		this.createdOn = createdOn;
	}

	public Instant getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Instant updatedOn) {
		this.updatedOn = updatedOn;
	}

}
