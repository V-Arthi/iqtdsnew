package com.main.libs;

import com.main.db.utils.Recordset;

public class Mapping {

	public static String workbookName;
	public static Recordset EDIMapping, MemberEDIMaping, ProviderEDIMapping;

	private Mapping() {

	}

	/*
	 * public static void loadEDIMapping(String mappingSheet) { ExcelReader er = new
	 * ExcelReader(workbookName);
	 * EDIMapping=er.selectSheet(mappingSheet).getRecordSet(); er.closeWorkbook(); }
	 * 
	 * public static void loadMemberEDIMapping(String mappingSheet) { ExcelReader er
	 * = new ExcelReader(workbookName);
	 * MemberEDIMaping=er.selectSheet(mappingSheet).getRecordSet().
	 * getSubsetAsRecordset("Execute","Yes"); er.closeWorkbook(); }
	 * 
	 * public static void loadProviderEDIMapping(String mappingSheet) { ExcelReader
	 * er = new ExcelReader(workbookName);
	 * ProviderEDIMapping=er.selectSheet(mappingSheet).getRecordSet().
	 * getSubsetAsRecordset("Execute","Yes"); er.closeWorkbook(); }
	 */

}
