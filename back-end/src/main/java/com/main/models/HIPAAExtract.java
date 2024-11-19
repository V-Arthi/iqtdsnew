package com.main.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "hipaa_extracts")
public class HIPAAExtract {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "data_id")
	private String dataId;

	@Column(name = "env")
	private String env;

	@Column(name = "data_type")
	private String dataType;

	@Lob
	@Column(name = "extract_logs")
	private String extractLogs;

	@Lob
	@Column(name = "extract_data")
	private String extractData;

	@Column(name = "file_name")
	private String fileName;

	public HIPAAExtract() {
	}

	public HIPAAExtract(String dataId, String env, String dataType, String extractLogs, String extractData,
			String fileName) {
		super();

		this.dataId = dataId;
		this.env = env;
		this.dataType = dataType;
		this.extractLogs = extractLogs;
		this.extractData = extractData;
		this.fileName = fileName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getExtractLogs() {
		return extractLogs;
	}

	public void setExtractLogs(String extractLogs) {
		this.extractLogs = extractLogs;
	}

	public String getExtractData() {
		return extractData;
	}

	public void setExtractData(String extractData) {
		this.extractData = extractData;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
