package com.main.cognizant.dbutils;

public enum DBType {
	ORACLE("Oracle", "oracle.jdbc.driver.OracleDriver"),
	MS_SQL_SERVER("MSSQLServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
	MYSQL("MySQLServer", "com.mysql.cj.jdbc.Driver");

	private String name, className;

	private DBType(String name, String className) {
		this.name = name;
		this.className = className;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}
}
