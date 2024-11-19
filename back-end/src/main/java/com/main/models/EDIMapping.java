package com.main.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "edi_mapping")
public class EDIMapping {
	@Id
	@Column(name = "field_name")
	private String fieldName;

	@Column(name = "segment_ref")
	private String segmentRef;

	@Column(name = "is_strict")
	private boolean isStrict;

	@Column(name = "is_value_mockup_eligible")
	private boolean valueOverridable;

	public EDIMapping() {
	}

	public EDIMapping(String fieldName, String segmentRef) {
		super();
		this.fieldName = fieldName;
		this.segmentRef = segmentRef;

	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSegmentRef() {
		return segmentRef;
	}

	public void setSegmentRef(String segmentRef) {
		this.segmentRef = segmentRef;
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

	@Override
	public String toString() {
		return "EDIMapping [fieldName=" + fieldName + ", segmentRef=" + segmentRef + ", isStrict=" + isStrict
				+ ", valueOverridable=" + valueOverridable + "]";
	}
}
