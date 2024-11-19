package com.main.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class RunExtractOverrideMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "extract_id")
	private HIPAAExtract extract;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "run_extract_id")
	private List<Overrides> overrides;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "run_extract_id")
	private List<DataCreationEDIFile> ediFiles;

	public RunExtractOverrideMapping() {
	}

	public RunExtractOverrideMapping(HIPAAExtract extract) {
		super();
		this.extract = extract;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public HIPAAExtract getExtract() {
		return extract;
	}

	public void setExtract(HIPAAExtract extract) {
		this.extract = extract;
	}

	public List<Overrides> getOverrides() {
		return overrides;
	}

	public void setOverrides(List<Overrides> overrides) {
		this.overrides = overrides;
	}

	public List<DataCreationEDIFile> getEdiFiles() {
		return ediFiles;
	}

	public void setEdiFiles(List<DataCreationEDIFile> ediFiles) {
		this.ediFiles = ediFiles;
	}

}
