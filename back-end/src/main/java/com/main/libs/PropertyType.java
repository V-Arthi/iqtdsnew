package com.main.libs;

public enum PropertyType {

	DatabaseProperties("DB.properties"), DriverProperties("Driver.properties"),
	TestManagementProperties("TestManagement.properties"), UserDefinedProperties("User.properties"),
	FileNameProperty_837I("837I_FileNames.properties"), FileNameProperty_837P("837P_FileNames.properties");

	private String value;

	PropertyType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
