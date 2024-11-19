package com.main.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class MetaXWalk {
	@Id
	@Column(name = "user_field_name")
	private String userDefinedFieldName;

	@Column(name = "meta_type")
	private String metaType;

	@Column(name = "db_field_name")
	private String dbFieldName;

	public MetaXWalk() {
		super();
	}

	public MetaXWalk(String userDefinedFieldName, String metaType, String dbFieldName) {
		super();
		this.userDefinedFieldName = userDefinedFieldName;
		this.metaType = metaType;
		this.dbFieldName = dbFieldName;
	}

	public String getUserDefinedFieldName() {
		return userDefinedFieldName;
	}

	public void setUserDefinedFieldName(String userDefinedFieldName) {
		this.userDefinedFieldName = userDefinedFieldName;
	}

	public String getMetaType() {
		return metaType;
	}

	public void setMetaType(String metaType) {
		this.metaType = metaType;
	}

	public String getDbFieldName() {
		return dbFieldName;
	}

	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dbFieldName == null) ? 0 : dbFieldName.hashCode());
		result = prime * result + ((metaType == null) ? 0 : metaType.hashCode());
		result = prime * result + ((userDefinedFieldName == null) ? 0 : userDefinedFieldName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaXWalk other = (MetaXWalk) obj;
		if (dbFieldName == null) {
			if (other.dbFieldName != null)
				return false;
		} else if (!dbFieldName.equals(other.dbFieldName))
			return false;
		if (metaType == null) {
			if (other.metaType != null)
				return false;
		} else if (!metaType.equals(other.metaType))
			return false;
		if (userDefinedFieldName == null) {
			if (other.userDefinedFieldName != null)
				return false;
		} else if (!userDefinedFieldName.equals(other.userDefinedFieldName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MetaXWalk [userDefinedFieldName=" + userDefinedFieldName + ", metaType=" + metaType + ", dbFieldName="
				+ dbFieldName + "]";
	}

}
