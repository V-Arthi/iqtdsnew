package com.main.cognizant.dbutils;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {

	private static DatabaseManager instance;
	private Map<String, Database> dbMap;

	private DatabaseManager() {
		dbMap = new HashMap<String, Database>();
	}

	public static DatabaseManager getInstance() {
		if (instance == null)
			instance = new DatabaseManager();
		return instance;
	}

	public Database getDatabase(DBParameters dbParams) {
		Database ret = null;

		if (!dbMap.containsKey(dbParams.getAliasName())) {
			dbMap.put(dbParams.getAliasName(), new Database(dbParams));
		}

		if (!dbMap.get(dbParams.getAliasName()).isDatabaseConnected()) {
			dbMap.replace(dbParams.getAliasName(), new Database(dbParams));
		}

		ret = dbMap.get(dbParams.getAliasName());

		return ret;
	}

	public static void dispose() {
		if (instance != null) {
			for (String key : instance.dbMap.keySet()) {
				instance.dbMap.get(key).closeDbConnection();
			}
		}
	}

}
