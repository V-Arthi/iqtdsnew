package com.main.db.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Record {

	private static final Logger log = LogManager.getLogger(Record.class);

	private List<String> head;
	private List<String> data;

	public static final String DEFAULT_COLUMN_DELIMITER = "|";
	public static final String DEFAULT_VALUE_DELIMITER = "=";

	public Record(List<String> head, List<String> data) {

		if (head.size() == data.size()) {
			this.head = new ArrayList<String>();
			this.data = new ArrayList<String>();
			this.head.addAll(head);
			this.data.addAll(data);
		} else {
			log.error("Number of elements in header and data do not match");
		}
	}

	/**
	 * parse a given String row as Record Object by using DEFAULT_COLUMN_DELIMITER
	 * and DEFAULT_VALUE_DELIMITER
	 * 
	 * @param row
	 */
	public Record(String row) {

		parseRecord(row, DEFAULT_COLUMN_DELIMITER, DEFAULT_VALUE_DELIMITER);
	}

	/**
	 * Constructor method parse a String row to a Record Object -
	 * id=1|name=Lorem|Age=99 - where '|' is columndelimiter and '=' is
	 * valuedelimiter
	 * 
	 * @param row
	 * @param columndelimiter
	 * @param valuedelimiter
	 * @return Record
	 */

	public Record(String row, String columndelimiter, String valuedelimiter) {

		parseRecord(row, columndelimiter, valuedelimiter);

	}

	private void parseRecord(String row, String columndelimiter, String valuedelimiter) {
		if (row.indexOf(columndelimiter) < 0) {
			System.out.format("Error:Given column delimeter['%s'] not found in given row string", columndelimiter);
			log.error(
					String.format("Error:Given column delimeter['%s'] not found in given row string", columndelimiter));
			return;
		}

		head = new ArrayList<String>();
		data = new ArrayList<String>();
		String[] columnArr = row.split("\\" + columndelimiter);
		// System.out.format("given row is: %s\n",row);
		for (int i = 0; i < columnArr.length; i++) {
			String column = columnArr[i];
			if (column.indexOf(valuedelimiter) < 0) {
				System.out.format("SKIP:Given value delimeter['%s'] not found in column data[%s]", valuedelimiter,
						column);
				log.error(String.format("SKIP:Given value delimeter['%s'] not found in column data[%s]", valuedelimiter,
						column));
				continue;
			}
			// System.out.format("column[%d] value:%s\n",i+1,column);
			String[] c = column.split(valuedelimiter);
			// System.out.format("adding '%s' into head & '%s' into data\n",c[0],c[1]);
			head.add(c[0]);
			data.add(c[1]);
		}
	}

	public List<String> getHeader() {
		return head;
	}

	public List<String> getData() {
		return data;
	}

	public int getDataCount() {
		return data.size();
	}

	public String getValue(String fieldname) {
		String ret = null;
		int i = head.indexOf(fieldname);
		if (i < 0) {
			log.error(String.format("Field [%s] not present in the Record, available fields are %s", fieldname, head));
		} else {
			ret = data.get(i);
		}
		return ret;
	}

	public List<String> getSubsetValues(List<String> fieldnames) {
		List<String> ret = null;

		if (!head.containsAll(fieldnames)) {
			log.error(String.format(
					"All requested fields [%s] are not present in the Record header, available fields are %s",
					fieldnames, head));
		} else {
			ret = fieldnames.stream().map(e -> getValue(e)).collect(Collectors.toList());
		}
		return ret;
	}

	public Record getSubsetAsRecord(List<String> fieldnames) {
		return new Record(fieldnames, getSubsetValues(fieldnames));
	}

	public String toString() {
		return head.stream().map((e) -> e + "=" + data.get(head.indexOf(e))).collect(Collectors.joining("|"));
	}

	public String toString(String delimeter) {
		return head.stream().map((e) -> e + "=" + data.get(head.indexOf(e))).collect(Collectors.joining(delimeter));
	}

	public HashMap<String, String> asHashMap() {
		HashMap<String, String> ret = new HashMap<>();
		if (head == null)
			return ret;
		head.stream().forEach(e -> ret.put(e, getValue(e)));
		return ret;
	}

	public LinkedHashMap<String, String> asOrderedHashMap() {
		LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
		if (head == null)
			return ret;
		head.stream().forEach(e -> ret.put(e, getValue(e)));
		return ret;

	}

}
