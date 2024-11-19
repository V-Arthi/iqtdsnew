package com.main.thirdpartyfunctions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.main.interfaces.Filter;
import com.main.models.ProviderFilter;

public class Providers extends CommonFunctions {

	private static final String qryFileNameIn = "queries" + File.separator + "provider_par_optimized.sql";
	private static final String qryFileNameOut = "queries" + File.separator + "provider_all_optimized.sql";

	public Providers() {
		super();
	}

	public String attachWhereCondition(List<ProviderFilter> filters, int limit, String type) {

		String qry = getQuery(type.equalsIgnoreCase("in") ? qryFileNameIn : qryFileNameOut);

		List<Filter> genFilters = new ArrayList<Filter>();
		genFilters.addAll(filters);

		String condition = transformConditions(genFilters);

		if (filters.size() > 0) {
			if (!StringUtils.contains(qry.toUpperCase(), "WHERE") || StringUtils.endsWithIgnoreCase(qry, "where")) {
				condition = " WHERE " + StringUtils.substringAfter(condition, "AND");
			}
		}

		return qry + condition + " FETCH NEXT " + limit + " ROWS ONLY";
	}

}