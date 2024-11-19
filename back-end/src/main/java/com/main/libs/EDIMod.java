package com.main.libs;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.main.cognizant.dbutils.QueryManager.Query;
import com.main.cognizant.ediautomation.EDIFile;
import com.main.db.utils.Record;
import com.main.db.utils.Recordset;
import com.main.libs.DBConnector;
import com.main.libs.LogMessage;
import com.main.libs.Status;
import com.main.libs.Util;

public class EDIMod {
	private EDIFile edi;
	private String fileName;
	private String fileType;

	private DBConnector connection;
	private Query memberQuery, providerQuery;

	private HashMap<String, Boolean> sectionUpdates;

	private static final String ediMappingFieldHeader = "DBFieldName";

	private String outputDirectory = System.getProperty("user.dir") + File.separator + "EDI Updated";

	public EDIMod(String fullFileName) {
		this.fileName = outputDirectory + File.separator + Paths.get(fullFileName).getFileName().toString();
		this.edi = EDIFile.getInstance(fullFileName);
		this.fileType = edi.getFileType();
		sectionUpdates = new HashMap<String, Boolean>();
		sectionUpdates.put("member", false);
		sectionUpdates.put("provider", false);
		// edi.printFile();
	}

	public void setConnection(DBConnector connection) {
		this.connection = connection;
	}

	public void setMemberQuery(Query qry) {
		this.memberQuery = qry;
	}

	public void setProviderQuery(Query qry) {
		this.providerQuery = qry;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
		fileName = outputDirectory + File.separator + Paths.get(fileName).getFileName().toString();
		prepOutputDir();
	}

	private void prepOutputDir() {
		new File(outputDirectory).mkdirs();
	}

	public List<LogMessage> dosUpdate(String Dos) {
		List<LogMessage> logs = new ArrayList<LogMessage>();
		Record r = Mapping.EDIMapping.getSubsetAsRecordset(ediMappingFieldHeader, "DoS").getRecord(1);
		String ref = r.getValue(fileType);
		String fieldName = r.getValue("FieldName");
		// System.out.println("dos segments :"+ref);

		for (String el : ref.split(",")) {
			el = el.trim();
			for (int txnId = 1; txnId <= edi.getTransactionCount(); txnId++) {
				String elValue = EDIFile.getField(edi.getTransaction(txnId).getEDILines(), el);
				// System.out.println("Reference:"+el+" value :"+elValue);
				if (elValue != null) {
					// handling date range
					if (StringUtils.contains(elValue, "-")) {
						String toDate = Util.getDateInFormat(Dos, "yyyyMMdd", 2);
						// System.out.println("to date:"+toDate);
						logs.addAll(updateEDIValue(el, Dos + "-" + toDate, fieldName, true));
					} else {
						logs.addAll(updateEDIValue(el, Dos, fieldName, true));
					}
				}
			}
		}

		return logs;
	}

	public List<LogMessage> receivedDateUpdate(String recdDate) {
		List<LogMessage> logs = new ArrayList<LogMessage>();
		Record r = Mapping.EDIMapping.getSubsetAsRecordset(ediMappingFieldHeader, "Recd_Date").getRecord(1);
		String ref = r.getValue(fileType);
		String fieldName = r.getValue("FieldName");
		System.out.println(String.format("%s[%s] is to be updated with value %s", fieldName, ref, recdDate));
		boolean result = edi.getBHT().setElement(ref, recdDate);
		if (result) {
			logs.add(new LogMessage(String.format("%s[%s] is updated with value %s", fieldName, ref, recdDate),
					Status.PASS));
		} else {
			logs.add(new LogMessage(String.format("%s[%s] is not updated with value %s", fieldName, ref, recdDate),
					Status.WARNING));
		}
		edi.saveAs(fileName);
		return logs;
	}

	public List<LogMessage> updatePatAccNo(String patAccNo) {
		Record r = Mapping.EDIMapping.getSubsetAsRecordset(ediMappingFieldHeader, "Pat_Acc_Num").getRecord(1);
		String ref = r.getValue(fileType);
		String fieldName = r.getValue("FieldName");
		return updateEDIValue(ref, patAccNo, fieldName, true);
	}

	public List<LogMessage> updateEDIFile(List<String> fields, List<String> values) {
		List<LogMessage> logs = new ArrayList<LogMessage>();

		for (int i = 0; i < fields.size(); i++) {
			Record r = Mapping.EDIMapping.getSubsetAsRecordset(ediMappingFieldHeader, fields.get(i)).getRecord(1);
			if (r == null) {
				logs.add(new LogMessage("EDI Mapping not found for field " + fields.get(i), Status.WARNING));
				return logs;
			}
			String refs = r.getValue(fileType);
			String fieldName = r.getValue("FieldName");
			String value = values.size() > i ? values.get(i) : "";
			boolean isStrict = r.getValue("isStrict").equalsIgnoreCase("yes");
			if (StringUtils.contains(refs, ":"))
				value = StringUtils.substringAfter(refs, ":");

			if (StringUtils.startsWith(refs, "#")) {
				logs.addAll(callSectionUpdate(StringUtils.substringAfter(refs, "#")));
				continue;
			}

			if (value.length() > 0)
				logs.addAll(updateEDIValue(refs, value, fieldName, isStrict));
		}

		return logs;
	}

	private List<LogMessage> callSectionUpdate(String sectionName) {
		System.out.println("calling section udpate for section " + sectionName);
		List<LogMessage> logs = new ArrayList<LogMessage>();
		if (StringUtils.startsWithIgnoreCase(sectionName, "member")) {
			logs.addAll(memberUpdate());
		} else if (StringUtils.startsWithIgnoreCase(sectionName, "provider")) {
			logs.addAll(providerUpdate());
		}

		return logs;

	}

	public List<LogMessage> memberUpdate() {
		List<LogMessage> logs = new ArrayList<LogMessage>();
		if (sectionUpdates.get("member"))
			return Arrays.asList(new LogMessage("member update already called once", Status.INFO));
		sectionUpdates.replace("member", true);
		Recordset rs = connection.getDB().getRecordSet(memberQuery, 100);
		Record dbRecord = rs == null ? null : rs.getRecord(Util.getRandomNumber(1, rs.getRecordCount()));
		Recordset mapping = Mapping.MemberEDIMaping;

		if (mapping == null)
			logs.add(new LogMessage("member mapping not found", Status.WARNING));

		if (dbRecord == null)
			logs.add(
					new LogMessage("member record not found in db for the query[" + memberQuery + "]", Status.WARNING));

		if (mapping == null || dbRecord == null)
			return logs;
		rs.clear();

		logs.addAll(updateSection("Member", dbRecord, mapping));
		return logs;
	}

	public List<LogMessage> providerUpdate() {
		List<LogMessage> logs = new ArrayList<LogMessage>();
		if (sectionUpdates.get("provider"))
			return Arrays.asList(new LogMessage("provider update already called once", Status.INFO));
		sectionUpdates.replace("provider", true);
		Recordset rs = connection.getDB().getRecordSet(providerQuery, 100);
		Record dbRecord = rs == null ? null : rs.getRecord(Util.getRandomNumber(1, rs.getRecordCount()));
		Recordset mapping = Mapping.ProviderEDIMapping;

		if (mapping == null)
			logs.add(new LogMessage("provider mapping not found", Status.WARNING));

		if (dbRecord == null)
			logs.add(new LogMessage("provider record not found in db for the query[" + providerQuery + "]",
					Status.WARNING));

		if (mapping == null || dbRecord == null)
			return logs;
		rs.clear();
		logs.addAll(updateSection("Provider", dbRecord, mapping));
		return logs;

	}

	private List<LogMessage> updateSection(String sectionName, Record dbRecord, Recordset mapping) {
		List<LogMessage> logs = new ArrayList<LogMessage>();

		for (Record r : mapping) {
			String fieldName = r.getValue("FieldName");
			String refs = r.getValue(fileType);
			String value = StringUtils.startsWith(fieldName, "Constant") ? r.getValue("Value")
					: dbRecord.getValue(StringUtils.substringAfterLast(r.getValue("Value"), "."));
			boolean isStrict = r.getValue("isStrict").equalsIgnoreCase("yes");

			// provider
			if (fileType.equalsIgnoreCase("837P") && StringUtils.startsWith(refs, "2010AA_")) {
				String[] entityName = value.split(",");
				if (StringUtils.endsWith(refs, "NM103")) {
					value = entityName[0].trim(); // last name
				} else if (StringUtils.endsWith(refs, "NM104")) {
					value = entityName.length > 1 ? entityName[1].trim().split(" ")[0].trim() : ""; // firstname
				} else if (StringUtils.endsWith(refs, "NM105")) {
					value = "";
					String firstName = entityName.length > 1 ? entityName[1].trim() : "";
					if (firstName.length() > 1 && firstName.contains(" ")) {
						String middleInitial = firstName.split(" ")[1].trim().replace(".", "");
						value = middleInitial;
					}
				}
			} else if (fileType.equalsIgnoreCase("837I") && StringUtils.startsWith(refs, "2010AA_")) {
				if (StringUtils.endsWith(refs, "NM104") || StringUtils.endsWith(refs, "NM105")) {
					value = "";
				}
			}

			logs.addAll(updateEDIValue(refs, value, sectionName + "." + fieldName, isStrict));
		}
		return logs;
	}

	private List<LogMessage> updateEDIValue(String refs, String value, String fieldName, boolean isStrict) {
		List<LogMessage> logs = new ArrayList<LogMessage>();
		String refsArr[] = refs.split(",");
		for (int x = 0; x < refsArr.length; x++) {
			String ref = refsArr[x].trim();

			/*
			 * this or that handling
			 */
			if (StringUtils.contains(ref, "~")) {
				orLoop: for (String r : ref.split("~")) {
					String rValue;
					for (int txn = 1; txn <= edi.getTransactionCount(); txn++) {
						rValue = EDIFile.getField(edi.getTransaction(txn).getEDILines(), r);
						if (rValue != null) {
							ref = r;
							break orLoop;
						}

					}
				}
			}

			if (StringUtils.contains(ref, "~")) {
				ref = StringUtils.substringBefore(ref, "~");

			}

			value = StringUtils.contains(value, ",") ? StringUtils.substringBefore(value, ",") : value;

			System.out.println(String.format("%s[%s] is to be updated with value %s", fieldName, ref, value));
			for (int i = 1; i <= edi.getTransactionCount(); i++) {
				String existing_Value = EDIFile.getField(edi.getTransaction(i).getEDILines(), ref);

				if (EDIFile.updateField(edi.getTransaction(i).getEDILines(), ref, value.trim())) {
					logs.add(new LogMessage(
							String.format("%s[%s] is updated with value %s at txn %d [previous value was '%s'] ",
									fieldName, ref, value, i, existing_Value),
							Status.PASS));
				} else {
					logs.add(new LogMessage(
							String.format("%s[%s] is not updated with value %s at txn %d ", fieldName, ref, value, i),
							isStrict ? Status.WARNING : Status.INFO));
				}
			}
		}
		edi.saveAs(fileName);
		return logs;
	}

	public String getEDIContent() {
		return edi.getX12Content();
	}

	public void flushFile() {
		if (new File(fileName).exists())
			new File(fileName).delete();

		edi.fixGE();
		edi.fixFileHeaders();
		edi.saveAs(fileName);
		edi = null;
	}
}
