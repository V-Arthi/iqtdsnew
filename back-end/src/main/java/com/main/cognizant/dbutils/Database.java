package com.main.cognizant.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.main.cognizant.dbutils.QueryManager.Query;
import com.main.db.utils.Record;
import com.main.db.utils.Recordset;
import com.main.libs.Settings;

public class Database {

	private static Properties props;

	private static final Logger log = LoggerFactory.getLogger(Database.class);

	private static final Set<String> hoc3dbs = Sets.newHashSet("faembsia", "faembsib", "faembut1", "faembpr1",
			"faembpp1", "faembps1", "hgembsia", "hgembsib", "hgembut1", "hgembpr1", "hgembpp1", "hgembps1");

	static {
		try {
			props = Settings.getApplicationProperties();
		} catch (Exception e) {
			log.error("Error while reading application.properties file from facets Data Supplier");
			e.printStackTrace();
		}

	}

	Connection con;
	String conExceptionMessage;

	public Database(DBParameters params) {
		String status = "closed";

		try {
			String dbURL = null;
			if (params.getType() == DBType.ORACLE) {
				System.setProperty("oracle.jdbc.fanEnabled", "false");
				if (ishoc3(params.getServiceName())) {
					log.info("Trying to connect to hoc3 database:" + params.getServiceName());

					System.setProperty("oracle.net.tns_admin",
							props.getProperty("oracle.net.tns_admin", "C:\\tools\\Wallet"));
					System.setProperty("oracle.net.wallet_location",
							props.getProperty("oracle.net.wallet_location", "C:\\tools\\Wallet"));

					dbURL = String.format(
							"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCPS)(HOST=%s)(PORT=%s)))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=%s)))",
							params.getHost(), params.getPort(), params.getServiceName().toLowerCase());

				} else {
					dbURL = String.format(
							"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=%s)(PORT=%s)))(CONNECT_DATA=(SERVICE_NAME=%s)))",
							params.getHost(), params.getPort(), params.getServiceName());
				}
			} else if (params.getType() == DBType.MS_SQL_SERVER) {
				dbURL = String.format("jdbc:sqlserver://%s;databaseName=%s;", params.getHost(),
						params.getDatabaseName());
			} else if (params.getType() == DBType.MYSQL) {
				dbURL = String.format("jdbc:mysql://%s:%s/%s", params.getHost(), params.getPort(),
						params.getDatabaseName());
			}

			Class.forName(params.getType().getClassName());
			con = DriverManager.getConnection(dbURL, params.getUsername(), params.getPassword());
			status = con.isClosed() ? "closed" : "open";

		} catch (Exception e) {
			conExceptionMessage = e.getMessage();
			log.error("Failed!", e);
		}
		log.info(String.format("The database [%s] connection status is %s", params.getAliasName(), status));
	}

	public boolean isDatabaseConnected() {
		boolean ret = false;
		try {
			ret = con != null && !con.isClosed();
		} catch (SQLException e) {
			log.error("Failed!", e);
		}
		return ret;
	}
	
	public String connectionExceptionMessage() {
		return isDatabaseConnected() ? null : conExceptionMessage;
	}

	public Record getRecord(Query query) {
		Recordset recordset = runQuery(query.toString(), 1);
		Record ret = null;
		if (recordset != null)
			ret = recordset.getRecord(1);
		return ret;
	}

	public Recordset getRecordSet(Query query, long limit) {
		return runQuery(query.toString(), limit);
	}

	public Recordset getRecordSet(Query query) {
		return runQuery(query.toString(), -1);
	}

	private Recordset runQuery(String query, long recNo) {
//		System.out.println(query);
		Recordset recordSet = null;
		try {
//			log.info(String.format("Running query \n [%s] \n to fecth [%d] records",
//					query.replaceAll(System.lineSeparator(), " "), recNo));
			Statement stmt = con.createStatement();

			/* query timeout 60 minutes */
			stmt.setQueryTimeout(60 * 60);

			ResultSet rs = stmt.executeQuery(query);
			long recCount = 0;
			while (rs.next()) {
				if (recordSet == null) {
					ArrayList<String> fieldName = new ArrayList<String>();
					for (int colNo = 1; colNo <= rs.getMetaData().getColumnCount(); colNo++) {
						fieldName.add(rs.getMetaData().getColumnName(colNo));
					}
					recordSet = new Recordset(fieldName);
				}
				ArrayList<String> data = new ArrayList<String>();
				for (int colNo = 1; colNo <= rs.getMetaData().getColumnCount(); colNo++) {
					data.add(rs.getString(colNo));
				}
				recordSet.add(data);
				recCount++;
				if (recNo > 0 && recCount >= recNo)
					break;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			log.error("Failed!", e);
		}
		if (recordSet != null)
			log.debug(String.format("Records returned from query [%d]", recordSet.getRecordCount()));
		else
			log.debug(String.format("Records returned from query [%d]", 0));

		return recordSet;
	}

	public void closeDbConnection() {
		try {
			if (isDatabaseConnected())
				con.close();
		} catch (SQLException e) {
			log.error("Failed!", e);
		}
	}

	public int executeNonQuery(Query query) {
		int affectedRows = 0;
		try {
			PreparedStatement pstmt = con.prepareStatement(query.toString());
			affectedRows = pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			log.error("Failed!", e);
		}
		return affectedRows;
	}

	private boolean ishoc3(String dbname) {
		return hoc3dbs.contains(dbname.toLowerCase());
	}

	public ResultSet getResultset(Query query) {
		ResultSet rs = null;
		try {
			PreparedStatement s = con.prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = s.executeQuery();
		} catch (Exception e) {
			log.error("Failed!", e);
			e.printStackTrace();
		}
		return rs;
	}
}
