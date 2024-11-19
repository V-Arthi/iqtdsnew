package com.main.cognizant.dbutils;

public class DBParameters {
	private String databaseName, host, port, serviceName, username, password, aliasName;
	private DBType type;

	public String getDatabaseName() {
		return databaseName;
	}

	public DBType getType() {
		return type;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getAliasName() {
		return aliasName;
	}

	public DBParameters(DBType type, String host, String port, String databaseOrServiceName, String userName,
			String password, String aliasName) {
		this.type = type;
		this.host = host;
		this.port = type == DBType.MS_SQL_SERVER ? null : port;
		this.serviceName = type == DBType.ORACLE ? databaseOrServiceName : null;
		this.databaseName = type == DBType.ORACLE ? null : databaseOrServiceName;
		this.username = userName;
		this.password = password;
		this.aliasName = aliasName == null ? String.format("%s-%s", type.getName(), databaseOrServiceName) : aliasName;
	}

}
