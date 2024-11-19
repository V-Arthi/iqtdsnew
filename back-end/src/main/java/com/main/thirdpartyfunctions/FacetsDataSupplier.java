package com.main.thirdpartyfunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.main.cognizant.dbutils.DBParameters;
import com.main.cognizant.dbutils.DBType;
import com.main.cognizant.dbutils.Database;
import com.main.cognizant.dbutils.DatabaseManager;
import com.main.cognizant.dbutils.QueryManager;
import com.main.cognizant.dbutils.QueryManager.Query;
import com.main.db.utils.Record;
import com.main.db.utils.Recordset;
import com.main.libs.Settings;

public class FacetsDataSupplier {

	private static final Logger log = LoggerFactory.getLogger(FacetsDataSupplier.class);

	private String env;
	private Database db, db_HIPAA;
	private static Properties props;
	static {
		try {
			props = Settings.getApplicationProperties();
		} catch (Exception e) {
			log.error("Error while reading application.properties file from facets Data Supplier");
			e.printStackTrace();
		}

	}

	public FacetsDataSupplier(String env) {
		this.env = env.toUpperCase();
	}

	public void init() {
		log.error("ENV: " + env);
		String host = props.getProperty(env + ".FACETS.Host");
		String port = props.getProperty(env + ".FACETS.Port");
		String serv = props.getProperty(env + ".FACETS.ServiceName");
		String username = props.getProperty(env + ".FACETS.UserName");
		String pass = props.getProperty(env + ".FACETS.Password");
		DBParameters dbParams = new DBParameters(DBType.ORACLE, host, port, serv, username, pass, env + ".FACETS");
		db = DatabaseManager.getInstance().getDatabase(dbParams);
	}

	public void initHG() {
		
		String host = props.getProperty(env + ".HIPAA.Host");
		String port = props.getProperty(env + ".HIPAA.Port");
		String serv = props.getProperty(env + ".HIPAA.ServiceName");
		String username = props.getProperty(env + ".HIPAA.UserName");
		String pass = props.getProperty(env + ".HIPAA.Password");
		DBParameters dbParams = new DBParameters(DBType.ORACLE, host, port, serv, username, pass, env + ".HIPAA");
		db_HIPAA = DatabaseManager.getInstance().getDatabase(dbParams);
	}
	
	public String getDbExceptionMessage() {
		return db.connectionExceptionMessage();
	}

	public Recordset getRecordset(Query query) {
		return db.getRecordSet(query);
	}

	public Record getRecord(Query query) {
		return db.getRecord(query);
	}

	public Recordset getRecodsetHG(Query query) {
		return db_HIPAA.getRecordSet(query);
	}

	public Record getRecordHG(Query query) {
		return db_HIPAA.getRecord(query);
	}

	public Recordset getMemberRecord(String memberID) {
		QueryManager qm = QueryManager.getInstance();
		Query qry = qm.getQueryFromResource("memberRecord");
		if (qry == null)
			return null;
		String sbsbID = StringUtils.substringBefore(memberID, "-").trim();
		String suffix = StringUtils.substringAfter(memberID, "-").trim();
		if (suffix.length() < 2)
			suffix = "0" + suffix;

		qry.bind("SubscriberID", sbsbID);
		qry.bind("Suffix", suffix);
		return db.getRecordSet(qry, 5);
	}

	public Recordset getProviderRecord(String providerID) {
		QueryManager qm = QueryManager.getInstance();
		Query qry = qm.getQueryFromResource("providerRecord");
		if (qry == null)
			return null;
		qry.bind("ProviderID", providerID);
		return db.getRecordSet(qry, 5);
	}

	public void close() {
		if (db.isDatabaseConnected())
			db.closeDbConnection();
		if (db_HIPAA != null && db_HIPAA.isDatabaseConnected())
			db_HIPAA.closeDbConnection();
	}
	
	public ResultSet getResultset(Query query) {
		return db.getResultset(query);
	}

	public ArrayList<String> getColumnValues(ResultSet rs) {
		ArrayList<String> columnvalue = new ArrayList<String>();
		try {
			rs.beforeFirst();
			while (rs.next()) {
				columnvalue.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columnvalue;
	}
	
	public int getResultSetSize(ResultSet rs) throws SQLException {
		rs.last();
		return rs.getRow();
	}
}
