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
@Table(name = "data_creations_edi")
public class DataCreationEDIFile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Lob
	@Column(name = "overrides")
	private String overrides;

	@Column(name = "member_id")
	private String memberID;

	@Column(name = "provider_id")
	private String providerID;

	@Column(name = "data_created_on")
	private Date dataCreatedOn;

	@Column(name = "file_name")
	private String fileName;

	@Lob
	@Column(name = "file_content")
	private String fileContent;

	@Lob
	@Column(name = "file_mockup_logs")
	private String mockupLogs;

	@Column(name = "unique_identification_string")
	private String uniqueIdentifier;

	public DataCreationEDIFile() {
		this.dataCreatedOn = new Date();
	}

	public DataCreationEDIFile(String overrides) {
		super();
		this.overrides = overrides;
		this.dataCreatedOn = new Date();

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOverrides() {
		return overrides;
	}

	public void setOverrides(String overrides) {
		this.overrides = overrides;
	}

	public String getMemberID() {
		return memberID;
	}

	public void setMemberID(String memberID) {
		this.memberID = memberID;
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public String getDataCreatedOn() {

		return Util.getDateInFormat(dataCreatedOn, "MM/dd/yyyy");
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public String getMockupLogs() {
		return mockupLogs;
	}

	public void setMockupLogs(String mockupLogs) {
		this.mockupLogs = mockupLogs;
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	@Override
	public String toString() {

		return "DataCreationEDIFile [id=" + id + ", overrides=" + overrides + ", memberID=" + memberID + ", providerID="
				+ providerID + ", dataCreatedOn=" + Util.getDateInFormat(dataCreatedOn, "MM/dd/yyyy") + ", fileName="
				+ fileName + ", fileContent=" + fileContent + ", mockupLogs=" + mockupLogs + "]";
	}

}
