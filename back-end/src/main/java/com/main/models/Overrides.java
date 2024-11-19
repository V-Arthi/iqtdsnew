package com.main.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "run_extract_overrides")
public class Overrides {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "edimap_id_x12")
	private EDIMappingX12Standard ediMap;

//	@OneToOne(cascade = CascadeType.MERGE)
//	@JoinColumn(name = "edimap_id") //edimap_id_old
//	private EDIMapping ediMap;   //ediMapOld
	
	@Column(name = "txn_set")
	private String txnNumber;

	@Column(name = "value")
	private String value;

	public Overrides() {
	}

//	public Overrides(long id, EDIMapping ediMap, String txnNumber, String value) {
//		super();
//		this.id = id;
//		this.ediMap = ediMap;
//		this.txnNumber = txnNumber;
//		this.value = value;
//	}
	public Overrides(long id, EDIMappingX12Standard ediMap, String txnNumber, String value) {
		super();
		this.id = id;
		this.ediMap = ediMap;
		this.txnNumber = txnNumber;
		this.value = value;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public EDIMappingX12Standard getEdiMap() {
		return ediMap;
	}

	public void setEdiMap(EDIMappingX12Standard ediMap) {
		this.ediMap = ediMap;
	}
//	public EDIMapping getEdiMap() {
//		return ediMap;
//	}
//
//	public void setEdiMap(EDIMapping ediMap) {
//		this.ediMap = ediMap;
//	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTxnNumber() {
		return txnNumber;
	}

	public void setTxnNumber(String txnNumber) {
		this.txnNumber = txnNumber;
	}

	@Override
	public String toString() {
		return "Overrides [id=" + id + ", ediMap=" + ediMap + ", txn=" + txnNumber + ", value=" + value + "]";
	}

}
