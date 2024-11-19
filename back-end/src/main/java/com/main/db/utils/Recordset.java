package com.main.db.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Recordset implements Iterable<Record> {

	private static final Logger log = LogManager.getLogger(Recordset.class);

	private List<String> head;
	private List<List<String>> data;
	private List<HashMap<String, String>> records;

	public Recordset(List<String> head) {
		this.head = new ArrayList<String>();
		this.data = new ArrayList<List<String>>();
		this.records = new ArrayList<HashMap<String, String>>();
		this.head.addAll(head);
	}

	public Recordset(String[] head) {
		this(Arrays.asList(head));
	}

	public List<String> getHeader() {
		return head;
	}

	public boolean add(String[] datarow) {
		return add(Arrays.asList(datarow));
	}

	public boolean add(List<String> datarow) {

		if (head.size() == datarow.size()) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.addAll(datarow);
			this.data.add(temp);
			this.records.add(new Record(head, datarow).asHashMap());
		} else {
			log.error(String.format("Number of elements in header [%d] and data row [%d] do not match", head.size(),
					datarow.size()));
			return false;
		}
		return true;
	}

	public int getRecordCount() {
		return data.size();
	}

	public int getColumnCount() {
		return head.size();
	}

	public List<List<String>> getData() {
		return data;
	}

	public boolean isEmpty() {
		return data.size() == 0;
	}

	public Record getRecord(int recnum) {
		Record ret = null;
		if (recnum < 1 || recnum > data.size()) {
			log.error(String.format("Record number[%d] incorrect, available records are [ %d to %d]", recnum, 1,
					data.size()));
		} else {
			ret = new Record(head, data.get(recnum - 1));
		}
		return ret;
	}

	public String getValue(int recnum, String fieldname) {
		String ret = null;

		int i = head.indexOf(fieldname);
		if (i < 0) {
			log.error(
					String.format("Field [%s] not present in the Recordset, available fields are %s", fieldname, head));
		} else if (recnum < 1 || recnum > data.size()) {
			log.error(String.format("Record number[%d] incorrect, available records are [ %d to %d]", recnum, 1,
					data.size()));
		} else {
			ret = data.get(recnum - 1).get(i);
		}

		return ret;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(head.stream().collect(Collectors.joining("|")));
		sb.append("\n");
		sb.append(
				data.stream().map(e -> e.stream().collect(Collectors.joining("|"))).collect(Collectors.joining("\n")));

		return sb.toString();
	}

	public List<String> getAsString() {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < data.size(); i++) {
			List<String> row = data.get(i);
			List<String> rowTransformed = new ArrayList<String>();
			for (int j = 0; j < row.size(); j++) {
				rowTransformed.add(String.format("%s%s%s", head.get(j), Record.DEFAULT_VALUE_DELIMITER, row.get(j)));
			}
			result.add(rowTransformed.stream().collect(Collectors.joining(Record.DEFAULT_COLUMN_DELIMITER)));

		}
		return result;
	}

	public Recordset getSubsetAsRecordset(List<String> fields, List<String> values) {

		if (fields.size() != values.size()) {
			log.error(String.format("Incorrect number for key [%d] value [%d] pairs specified", fields.size(),
					values.size()));
			return null;
		}
		String[] args = new String[fields.size() * 2];
		for (int i = 0; i < args.length; i = i + 2) {
			args[i] = fields.get(i / 2);
			args[i + 1] = values.get(i / 2);
		}

		return getSubsetAsRecordset(args);
	}

	public Recordset getSubsetAsRecordset(String... arg) {

		if (arg.length == 0) {
			log.error("No filter specified!!!");
			return null;
		}

		if (arg.length % 2 > 0) {
			log.error(String.format("Incorrect number for key value pairs specified %s", Arrays.asList(arg)));
			return null;
		}

		for (int i = 0; i < arg.length; i = i + 2) {
			if (!head.contains(arg[i])) {
				log.error(String.format("Incorrect field name specified [%s], available fields are  %s", arg[i], head));
				return null;
			}
		}

		log.debug(String.format("Looking up recordset for filter condition %s", Arrays.asList(arg)));
		Recordset ret = new Recordset(head);

		data.stream().filter((e) -> {
			boolean found = true;
			for (int i = 0; i < arg.length; i = i + 2) {
				found = found && StringUtils.equals(arg[i + 1], e.get(head.indexOf(arg[i])));

//					log.trace(String.format("Filter: [%s] Current value [%s] Found[%s]",arg[i+1],e.get(head.indexOf(arg[i])),found));
//					System.out.println(String.format("Filter: [%s] Current value [%s] Found[%s]",arg[i+1],e.get(head.indexOf(arg[i])),found));
			}
			return found;
		}).forEach(e -> ret.data.add(e));
//			System.out.println(ret);
		return ret;
	}

	public void clear() {
		data = new ArrayList<List<String>>();
	}

	public List<String> getColumnValuesAsList(String fieldname) {
		final List<String> ret = new ArrayList<>();
		int i = head.indexOf(fieldname);
		if (i < 0) {
			log.error(
					String.format("Field [%s] not present in the Recordset, available fields are %s", fieldname, head));
		} else {
			data.stream().forEach(e -> ret.add(e.get(i)));
		}
		return ret;
	}

	public List<String> getUniqueValuesAsList(String fieldname) {
		final List<String> ret = new ArrayList<>();
		int i = head.indexOf(fieldname);
		if (i < 0) {
			log.error(
					String.format("Field [%s] not present in the Recordset, available fields are %s", fieldname, head));
		} else {
			data.stream().forEach(e -> {
				String temp = e.get(i);
				if (!ret.contains(temp))
					ret.add(temp);
			});
		}
		return ret;
	}

	public int getRecordIdx(Record r) {
		return data.indexOf(r.getData()) + 1;
	}

	public int getRecordIdx(List<String> dat) {
		return data.indexOf(dat) + 1;
	}

	public List<HashMap<String, String>> getRecords() {
		return records;
	}

	@Override
	public Iterator<Record> iterator() {
		// TODO Auto-generated method stub
		return new Iterator<Record>() {
			private int count = data.size();
			private int idx = -1;

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return idx + 1 <= count - 1;
			}

			@Override
			public Record next() {
				// TODO Auto-generated method stub
				idx++;
				return new Record(head, data.get(idx));
			}
		};
	}

}
