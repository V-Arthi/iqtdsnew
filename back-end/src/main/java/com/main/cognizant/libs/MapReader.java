package com.main.cognizant.libs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.main.db.utils.Recordset;

public class MapReader {

	private static final Logger log = LogManager.getLogger(MapReader.class);
	private static Map<String, MapReader> instanceMap = new HashMap<>();

	// private static MapReader instance = null;

	private Map<String, Map<String, String>> map;

	private MapReader(String filepath, String sheetname) {
		map = new HashMap<>();
		/*
		 * ExcelReader er = new ExcelReader(filepath); er.selectSheet(sheetname);
		 * Recordset rs = er.getRecordSet();
		 */
		Recordset rs = null;
		List<String> header = rs.getHeader();
		String type = header.get(0);
		String lhs = header.get(1);
		String rhs = header.get(2);
		List<String> mtypes = rs.getUniqueValuesAsList(type);
		for (String mtype : mtypes) {
			Recordset rs2 = rs.getSubsetAsRecordset(type, mtype);
			Map<String, String> data = new HashMap<>();
			for (int i = 1; i <= rs2.getRecordCount(); i++) {
				data.put(rs2.getValue(i, lhs), rs2.getValue(i, rhs));
			}
			map.put(mtype, data);
		}

		// er.closeWorkbook();
	}

	public static MapReader getInstance(String fullfilename, String mapname) {

		if (instanceMap.containsKey(mapname)) {
			log.warn(String.format("%s is already initalized with a data filename, hence ingnoring the file [%s]",
					mapname, fullfilename));
		} else {
			instanceMap.put(mapname, new MapReader(fullfilename, mapname));
		}
		return instanceMap.get(mapname);
	}

	public static MapReader getInstance(String mapname) {
		MapReader ret = null;
		if (instanceMap.containsKey(mapname)) {
			ret = instanceMap.get(mapname);
		} else {
			log.error(String.format("%s is not initalized with a data filename", mapname));
		}
		return ret;
	}

	public Map<String, String> getMap(String mtype) {
		Map<String, String> ret = null;
		if (!map.containsKey(mtype)) {
			log.error(String.format("%s is not a valid MapType, available types are %s", mtype, map.keySet()));
		} else {
			ret = map.get(mtype);
		}
		return ret;
	}

	public String getMappedValue(String mtype, String lhs) {
		String ret = null;
		if (!map.containsKey(mtype)) {
			log.error(String.format("%s is not a valid MapType, available types are %s", mtype, map.keySet()));
		} else {
			ret = map.get(mtype).get(lhs);
		}
		return ret;
	}
}
