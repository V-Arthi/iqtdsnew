package com.main.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "edi_mapping_x12_standard")
public class EDIMappingX12Standard {
	@Id
	@Column(name = "field_name")
	private String fieldName;

	@Column(name = "claim_type")
	private String claimType;
	
	@Column(name = "loop_id")
	private String loop;
	
	@Column(name = "loop_repeat")
	private int loopRepeat;
	
	@Column(name = "segment")
	private String segment;
	
	@Column(name = "segment_repeat")
	private int segmentRepeat;
	
	@Column(name = "element")
	private String element;
	
	@Column(name = "sub_element")
	private int subElement;

	@Column(name = "is_strict")
	private boolean isStrict;
	
	@Column(name = "is_value_mockup_eligible")
	private boolean valueOverridable;
	
	public EDIMappingX12Standard() {
		
	}
	public EDIMappingX12Standard(String fieldName, String claimType, String loop, int loopRepeat, String segment,
			int segmentRepeat, String element, int subElement) {
		super();
		this.fieldName = fieldName;
		this.claimType = claimType;
		this.loop = loop;
		this.loopRepeat = loopRepeat;
		this.segment = segment;
		this.segmentRepeat = segmentRepeat;
		this.element = element;
		this.subElement = subElement;
	}

	@Override
	public String toString() {
		return "EDIMappingX12Standard [fieldName=" + fieldName + ", claimType=" + claimType + ", loop=" + loop
				+ ", loopRepeat=" + loopRepeat + ", segment=" + segment + ", segmentRepeat=" + segmentRepeat
				+ ", elemement=" + element + ", subElement=" + subElement + ", isStrict=" + isStrict
				+ ", valueOverridable=" + valueOverridable + "]";
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public String getLoop() {
		return loop;
	}

	public void setLoop(String loop) {
		this.loop = loop;
	}

	public int getLoopRepeat() {
		return loopRepeat;
	}

	public void setLoopRepeat(int loopRepeat) {
		this.loopRepeat = loopRepeat;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public int getSegmentRepeat() {
		return segmentRepeat;
	}

	public void setSegmentRepeat(int segmentRepeat) {
		this.segmentRepeat = segmentRepeat;
	}

	public String getElement() {
		return element;
	}

	public void setElemement(String element) {
		this.element = element;
	}

	public int getSubElement() {
		return subElement;
	}

	public void setSubElement(int subElement) {
		this.subElement = subElement;
	}

	public boolean isStrict() {
		return isStrict;
	}

	public void setStrict(boolean isStrict) {
		this.isStrict = isStrict;
	}

	public boolean isValueOverridable() {
		return valueOverridable;
	}

	public void setValueOverridable(boolean valueOverridable) {
		this.valueOverridable = valueOverridable;
	}


	
}
