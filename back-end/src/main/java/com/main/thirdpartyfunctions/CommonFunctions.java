package com.main.thirdpartyfunctions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.main.interfaces.Filter;

public class CommonFunctions {

	public String getQuery(String qryFileName) {
		String content = "";
		try {
			content = new String(
					Files.readAllBytes(Paths.get(Claims.class.getClassLoader().getResource(qryFileName).toURI())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public String transformConditions(List<Filter> filters) {

		List<String> conditions = new ArrayList<String>();
		for (Filter f : filters) {

			String value = f.getValue();

			if (StringUtils.equalsIgnoreCase(f.getOperator(), "IN")) {
				value = "('" + value.replaceAll(",", "','") + "')";
				conditions.add(String.format(" AND %s %s %s", f.getField(), f.getOperator(), value));
			} else if (StringUtils.equalsIgnoreCase(f.getOperator(), "like") && StringUtils.containsNone(value, "%")) {
				value = "'%" + value + "%'";
				conditions.add(String.format(" AND %s %s %s", f.getField(), f.getOperator(), value));
			} else if (StringUtils.equalsIgnoreCase(f.getOperator(), "substr")) {
				String[] intermediate=value.split("in");
				value = "(" + f.getField() + "," + intermediate[0]+ ") IN ('" + intermediate[1].replaceAll(",", "','") + "')";
				conditions.add(String.format(" AND %s%s", f.getOperator(), value));
			} else if (StringUtils.endsWithIgnoreCase(f.getField(), "_DT")) {
				value = String.format("TO_DATE('%s','MM/DD/YYYY')", value);
				conditions.add(String.format(" AND %s %s %s", f.getField(), f.getOperator(), value));
			} else {
				value = "'" + value + "'";
				conditions.add(String.format(" AND %s %s %s", f.getField(), f.getOperator(), value));
			}


		}

		return conditions.stream().collect(Collectors.joining());
	}
}
