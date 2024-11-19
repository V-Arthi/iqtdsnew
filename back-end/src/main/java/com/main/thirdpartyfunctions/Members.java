package com.main.thirdpartyfunctions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.main.interfaces.Filter;
import com.main.models.MemberFilter;

public class Members extends CommonFunctions {

	private static final String qryFileName = "queries" + File.separator + "members_optimized.sql";

	public Members() {
		super();
	}

	public String attachWhereCondition(List<MemberFilter> filters, int limit) {

		String qry = getQuery(qryFileName);

		List<Filter> genericFilters = new ArrayList<Filter>();
		genericFilters.addAll(filters);

		String condition = transformConditions(genericFilters);

		if (filters.size() > 0) {

			if (!StringUtils.contains(qry.toUpperCase(), "WHERE") || StringUtils.endsWithIgnoreCase(qry, "where")) {
				condition = " WHERE " + StringUtils.substringAfter(condition, "AND");
			}

		}

		return qry + condition + " AND SBSB.SBSB_ID LIKE 'K%' ORDER BY SBSB.SBSB_ID,MEME.MEME_SFX FETCH NEXT " + limit
				+ " ROWS ONLY";
	}

}
