package com.main.cognizant.dbutils;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueryManager {

	private static final Logger log = LogManager.getLogger(QueryManager.class);

	private static QueryManager instance;
	private String qryrepo;
	private Map<String, String> qryMap;

	private QueryManager() {
		qryrepo = Paths.get(System.getProperty("user.dir"), "Queries").toString();
		qryMap = new HashMap<String, String>();
	}

	public static QueryManager getInstance() {
		if (instance == null)
			instance = new QueryManager();
		return instance;
	}

	public Query getQuery(String qryName) {
		String qrystmt = null;
		Path p = null;

		if (qryMap.containsKey(qryName))
			qrystmt = qryMap.get(qryName);

		if (qrystmt == null && !qryName.contains(".")) {
			Stream<String> lines = getQueryFromTextFile(qryName);
			if (lines != null) {
				qrystmt = lines.map(e -> StringUtils.removeStart(e.trim(), " "))
						.filter(e -> !StringUtils.startsWith(e, "--")).collect(Collectors.joining(" "));
				qryMap.put(qryName, qrystmt);
			}

		}

		if (qrystmt == null) {
			String shtname = null;
			if (qryName.contains(".")) {
				shtname = StringUtils.substringBefore(qryName, ".");
				qryName = StringUtils.substringAfter(qryName, ".");
			} else {
				shtname = "Master";
			}

			qryMap.put(qryName, qrystmt);

		}

		if (StringUtils.isEmpty(qrystmt)) {
			log.error(String.format("Query [%s] not found", qryName));
			return null;
		}
		return new Query(qrystmt);
	}

	public Query setQuery(String qryName, String qry) {
		/*
		 * String qrystmt = null; if (!qryMap.containsKey(qryName)) {
		 * qryMap.put(qryName, qry); } qrystmt = qryMap.get(qryName);
		 */
		return new Query(qry);
	}

	public Query getQueryFromResource(String qryName) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		String content = "";
		try {
			// content = new
			// String(Files.readAllBytes(Paths.get(Claims.class.getClassLoader().getResource("queries"+File.separator+qryName+".sql").toURI())));
			content = new String(Files.readAllBytes(
					Paths.get(loader.getResource("queries" + File.separator + qryName + ".sql").toURI())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.length() > 0 ? new Query(content) : null;
	}

	private Stream<String> getQueryFromTextFile(String qryName) {
		Path p = Paths.get(qryrepo, qryName + ".txt");
		Stream<String> lines = null;
		if (p.toFile().exists()) {
			try {
				BufferedReader br = Files.newBufferedReader(p);
				lines = br.lines();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return lines;
	}

	private Stream<String> getQueryFromTextFileCustomLocation(String qryrepo, String qryName) {
		Path p = Paths.get(qryrepo, qryName + ".txt");
		Stream<String> lines = null;
		if (p.toFile().exists()) {
			try {
				BufferedReader br = Files.newBufferedReader(p);
				lines = br.lines();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return lines;
	}

	public class Query {
		String qry = "";

		private Query(String query) {
			qry = query.trim();

			if (StringUtils.endsWith(qry, " and ")) {
				qry = StringUtils.substringBeforeLast(qry, " and");
			} else if (StringUtils.endsWith(qry, " AND ")) {
				qry = StringUtils.substringBeforeLast(qry, " AND");
			} else if (StringUtils.endsWith(qry, " or ")) {
				qry = StringUtils.substringBeforeLast(qry, " or");
			} else if (StringUtils.endsWith(qry, " OR ")) {
				qry = StringUtils.substringBeforeLast(qry, " OR");
			}

		}

		public Query newQuery() {
			return new Query(qry);
		}

		public void toUpperCase() {
			qry.toUpperCase();
		}

		public void bind(String key, String value) {

			qry = StringUtils.replace(qry, "<" + key.trim() + ">", "'" + value + "'");
			qry = StringUtils.replace(qry, "[" + key.trim() + "]", value);
		}

		public String toString() {
			return qry;
		}

		public void replace(String string, String stringToBeReplaced) {
			qry = qry.replaceAll(string, stringToBeReplaced);
		}

	}

}
