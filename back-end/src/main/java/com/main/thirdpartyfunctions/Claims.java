package com.main.thirdpartyfunctions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.main.interfaces.Filter;
import com.main.models.ClaimFilter;
import com.main.models.TestCondition;

public class Claims extends CommonFunctions {

	private static final String qryFileName = "queries" + File.separator + "claims_optimized.sql";
	private static final String valQryFileName = "queries" + File.separator + "claims_validation.sql";
	private static final String DentalqryFileName = "queries" + File.separator + "dentalclaims_optimized.sql";
	private static final String DentalvalQryFileName = "queries" + File.separator + "dentalclaims_validation.sql";
	private static final String HospitalqryFileName = "queries" + File.separator + "hospitalclaims_optimized.sql";
	private static final String HospitalvalQryFileName = "queries" + File.separator + "hospitalclaims_validation.sql";

	private String qry;

	private String validationQry;

	// private static final String minSelectFields = "CLCL.CLCL_ID AS \"ID\",
	// CLCL.CLCL_CL_TYPE AS \"Type\", CLCL.CLCL_CL_SUB_TYPE AS \"Sub Type\",
	// CLCL.CLCL_RECD_DT AS \"Received Date\", CLCL.PDPD_ID As \"Product ID\",
	// CLCL.CLCL_NTWK_IND AS \"Network Type\", CDML.PSCD_ID AS \"Place Of Service\",
	// CDML.IPCD_ID AS \"Procedure Code\", CLCL.CLCL_TOT_CHG AS \"Total Charge\",
	// CDML.CDML_ALLOW AS \"Allowed Amount\", CLCL.CLCL_PA_PAID_AMT AS \"Paid
	// Amount\"";

	public Claims() {
		qry = getQuery(qryFileName);
		validationQry = getQuery(valQryFileName);
	}

	public Claims(String type) {
		if (type.equals("Dental")) {
			qry = getQuery(DentalqryFileName);
			validationQry = getQuery(DentalvalQryFileName);
		} else if (type.equals("Hospital")) {
			qry = getQuery(HospitalqryFileName);
			validationQry = getQuery(HospitalvalQryFileName);
		} else {
			qry = getQuery(qryFileName);
			validationQry = getQuery(valQryFileName);
		}
	}

	public String getQuery() {
		return qry;
	}

	private void cutOffExtraJoins(List<ClaimFilter> filters) {
		HashSet<String> uniqueTables = new HashSet<String>();
		uniqueTables.add("CLCL");
		uniqueTables.add("CDML");

		for (Filter f : filters) {
			uniqueTables.add(StringUtils.substringBefore(f.getField(), "."));
		}

		List<String> qryLines = Arrays.asList(qry.split("\n"));

		StringBuilder qryBuilder = new StringBuilder();

		if (qryLines.size() > 0) {
			for (String line : qryLines) {
				if (StringUtils.startsWith(line, "JOIN")) {
					String[] line_arr = line.split(" ");
					if (line_arr.length >= 3) {
						String prefix = line_arr[2];
						if (!uniqueTables.contains(prefix))
							continue;
					}
				} else if (StringUtils.startsWith(line, "LEFT")) {
					String[] line_arr = line.split(" ");
					if (line_arr.length >= 4) {
						String prefix = line_arr[3];
						if (!uniqueTables.contains(prefix))
							continue;
					}
				}
				qryBuilder.append(line + " ");

			}
		}

		qry = qryBuilder.toString();
		validationQry = qry;
	}

	private void cutOffDentalExtraJoins(List<ClaimFilter> filters) {
		HashSet<String> uniqueTables = new HashSet<String>();
		uniqueTables.add("CLCL");
		uniqueTables.add("CDDL");

		for (Filter f : filters) {
			uniqueTables.add(StringUtils.substringBefore(f.getField(), "."));
		}

		List<String> qryLines = Arrays.asList(qry.split("\n"));

		StringBuilder qryBuilder = new StringBuilder();

		if (qryLines.size() > 0) {
			for (String line : qryLines) {
				if (StringUtils.startsWith(line, "JOIN")) {
					String[] line_arr = line.split(" ");
					if (line_arr.length >= 3) {
						String prefix = line_arr[2];
						if (!uniqueTables.contains(prefix))
							continue;
					}

				} else if (StringUtils.startsWith(line, "LEFT")) {
					String[] line_arr = line.split(" ");
					if (line_arr.length >= 4) {
						String prefix = line_arr[3];
						if (!uniqueTables.contains(prefix))
							continue;
					}
				}
				qryBuilder.append(line + " ");

			}
		}

		qry = qryBuilder.toString();
		validationQry = qry;
	}

	public String attachWhereConditions(List<ClaimFilter> filters, int limit, boolean isEDI) {

		cutOffExtraJoins(filters);

		List<Filter> genFilters = new ArrayList<Filter>();
		genFilters.addAll(filters);

		String condition = transformConditions(genFilters);

		if (filters.size() > 0) {
			if (!StringUtils.contains(qry.toUpperCase(), "WHERE") || StringUtils.endsWithIgnoreCase(qry, "where")) {
				condition = " WHERE " + StringUtils.substringAfter(condition, "AND");
			}
		}

		if (isEDI)
			condition += " AND CLCL.CLCL_ID NOT LIKE 'P%' AND CLCL.CLCL_ID NOT LIKE 'TD%'";
		validationQry = qry + condition;
		return qry + condition + " FETCH FIRST " + limit + " ROWS ONLY )GROUP BY CLCL_ID";

	}

	public String attachDentalWhereConditions(List<ClaimFilter> filters, int limit, boolean isEDI) {

		cutOffDentalExtraJoins(filters);

		List<Filter> genFilters = new ArrayList<Filter>();
		genFilters.addAll(filters);

		String condition = transformConditions(genFilters);

		if (filters.size() > 0) {
			if (!StringUtils.contains(qry.toUpperCase(), "WHERE") || StringUtils.endsWithIgnoreCase(qry, "where")) {
				condition = " WHERE " + StringUtils.substringAfter(condition, "AND");
			}
		}

		if (isEDI)
			condition += " AND CLCL.CLCL_ID NOT LIKE 'P%' AND CLCL.CLCL_ID NOT LIKE 'TD%'";
		
	
		validationQry = qry + condition;
		return qry + condition + " FETCH FIRST " + limit + " ROWS ONLY )GROUP BY CLCL_ID";

	}

	public String getValidationQuery(List<ClaimFilter> filters, List<String> userFields, String claimId,
			TestCondition test) {
		StringBuilder qryBuilder;
		if (test.getClaimType().equals("Dental")) {
			attachDentalWhereConditions(filters, 1, test.getClaimInputType().equalsIgnoreCase("edi"));
			qryBuilder = new StringBuilder();

			qryBuilder.append("SELECT DISTINCT CONCAT(CONCAT(CLCL.CLCL_ID,'-'),CDDL.CDDL_SEQ_NO) \"CLAIM\", \n");
			qryBuilder.append("CLCL.CLCL_CUR_STS \"Status\", \n");
			qryBuilder.append("CASE WHEN CLCL.CLCL_CUR_STS!='15' THEN 'pass' ELSE 'fail' END \"Status_Res\", \n");

		} else {
			attachWhereConditions(filters, 1, test.getClaimInputType().equalsIgnoreCase("edi"));
			qryBuilder = new StringBuilder();

			qryBuilder.append("SELECT DISTINCT CONCAT(CONCAT(CLCL.CLCL_ID,'-'),CDML.CDML_SEQ_NO) \"CLAIM\", \n");
			qryBuilder.append("CLCL.CLCL_CUR_STS \"Status\", \n");
			qryBuilder.append("CASE WHEN CLCL.CLCL_CUR_STS!='15' THEN 'pass' ELSE 'fail' END \"Status_Res\", \n");

		}

		for (int i = 0; i < filters.size(); i++) {
			ClaimFilter filter = filters.get(i);
			String condition = transformCaseConditions(filter);
			String userFieldName = i < userFields.size() ? userFields.get(i)
					: StringUtils.substringAfter(filter.getField(), ".");

			qryBuilder.append(String.format("%s \"%s\", \n", filter.getField(), userFieldName));
			qryBuilder.append(
					String.format("CASE WHEN %s THEN 'pass' ELSE 'fail' END \"%s_Res\"", condition, userFieldName));

			if (i < filters.size() - 1)
				qryBuilder.append(",");

			qryBuilder.append(" \n");

		}

		qryBuilder.append("FROM FACETS");
		qryBuilder.append(StringUtils.substringAfter(validationQry, "FROM FACETS"));
//		qryBuilder.append(StringUtils.substringAfter(validationQry, "WHERE"));
//		qryBuilder.append(" WHERE CLCL.CLCL_ID='" + claimId + "' \n");
		qryBuilder.append(" AND CLCL.CLCL_ID='" + claimId + "' \n");
		qryBuilder.append("ORDER BY \"CLAIM\" FETCH FIRST 1 ROW ONLY");
		System.out.println("Validation Query---" + qryBuilder.toString());
		return qryBuilder.toString();
	}

	private String transformCaseConditions(Filter f) {
		String conditions = "";
		String value = f.getValue();

		if (StringUtils.equalsIgnoreCase(f.getOperator(), "IN"))
			value = "('" + value.replaceAll(",", "','") + "')";
		else if (StringUtils.equalsIgnoreCase(f.getOperator(), "like") && StringUtils.containsNone(value, "%"))
			value = "%" + value + "%";
		else if (StringUtils.endsWithIgnoreCase(f.getField(), "_DT"))
			value = String.format("TO_DATE('%s','MM/DD/YYYY')", value);
		else
			value = "'" + value + "'";

		conditions = String.format("%s %s %s", f.getField(), f.getOperator(), value);

		return conditions;
	}

}
