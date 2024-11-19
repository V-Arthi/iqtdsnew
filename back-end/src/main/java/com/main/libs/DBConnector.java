package com.main.libs;

import java.util.HashMap;

import com.main.cognizant.dbutils.DBParameters;
import com.main.cognizant.dbutils.DBType;
import com.main.cognizant.dbutils.Database;
import com.main.cognizant.dbutils.DatabaseManager;

public class DBConnector {
	private String env, dbName, key;
	private Settings settings;
	private static HashMap<String, Database> connections = new HashMap<String, Database>();
	private DatabaseManager dbManager = DatabaseManager.getInstance();

	public DBConnector(String env, String dbName) {

		this.env = env;
		this.dbName = dbName;
		this.key = env + "." + dbName;

	}

	public String getDBName() {
		return key;
	}

	private void loadDB() {

		String type = settings.getDBproperty(env, dbName, "Type");
		String host = settings.getDBproperty(env, dbName, "Host");
		String port = settings.getDBproperty(env, dbName, "Port");
		String service = settings.getDBproperty(env, dbName, "ServiceName");
		String user = settings.getDBproperty(env, dbName, "UserName");
		String pass = settings.getDBproperty(env, dbName, "Password");

		DBType dbType = null;
		switch (type.toLowerCase()) {
		case "oracle":
			dbType = DBType.ORACLE;
			break;
		case "mysql":
			dbType = DBType.MYSQL;
			break;
		case "mssql":
			dbType = DBType.MS_SQL_SERVER;
			break;
		default:
			throw new RuntimeException("Invalid db type " + type);
		}

		DBParameters params = new DBParameters(dbType, host, port, service, user, pass, key);

		if (connections.containsKey(key)) {
			connections.replace(key, dbManager.getDatabase(params));
		} else {
			connections.put(key, dbManager.getDatabase(params));
		}
	}

	public Database getDB() {
		if (!connections.containsKey(key) || !connections.get(key).isDatabaseConnected())
			loadDB();
		return connections.get(key);
	}

	public String getServiceName() {
		return settings.getDBproperty(env, dbName, "ServiceName");
	}
}
