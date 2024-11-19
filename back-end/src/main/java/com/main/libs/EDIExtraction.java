package com.main.libs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.main.cognizant.dbutils.QueryManager;
import com.main.cognizant.dbutils.QueryManager.Query;
import com.main.db.utils.Record;
import com.main.db.utils.Recordset;
import com.main.thirdpartyfunctions.CommonFunctions;
import com.main.thirdpartyfunctions.FacetsDataSupplier;

public class EDIExtraction {

	private FacetsDataSupplier facetsDS;

	private String claimID;
	private String claimX12 = null;
	private String filenameConvention = null;
	boolean extracted = false;
	boolean hipaaFound = false;
	private List<LogMessage> logs;

	public EDIExtraction(String claimID, FacetsDataSupplier facetsDS) {
		this.claimID = claimID;
		this.facetsDS = facetsDS;
		logs = new ArrayList<LogMessage>();
		System.out.println("Extraction in progress for :" + claimID);
		extract();

	}

	public String getClaimID() {
		return claimID;
	}

	public String getclaimX12() {
		return claimX12;
	}

	public String getFileName() {
		return filenameConvention;
	}

	public boolean isExtracted() {
		return hipaaFound && extracted;
	}

	public List<LogMessage> getLogs() {
		return logs;
	}

	private void extract() {

		QueryManager qm = QueryManager.getInstance();

		Query qryCLRN = qm.setQuery("CLRN_" + claimID,
				"SELECT CLRN_IDENTIFIER FROM FACETS.CMC_CLRN_REF_NO WHERE CLRN_MCTR_VALUE ='D9' AND CLCL_ID='" + claimID
						+ "'");
		Record r = facetsDS.getRecord(qryCLRN);
		String clrn = r == null ? "" : r.getValue("CLRN_IDENTIFIER");

		Query basicClaimInfo = qm.setQuery("BasicClaimInfo_" + claimID,
				"SELECT CLCL_ID,CLCL_PA_ACCT_NO,TO_CHAR(CLCL_RECD_DT,'YYYYMMDD') AS RECD_DT FROM FACETS.CMC_CLCL_CLAIM CLCL WHERE CLCL_ID='"
						+ claimID + "'");
		basicClaimInfo.bind("ClaimID", claimID);
		Record clRecord = facetsDS.getRecord(basicClaimInfo);

		if (clRecord == null) {
			logs.add(new LogMessage("No Claim detail found in core table for claim " + claimID, Status.INFO));
			System.err.println("No Claim detail found in core table for claim  " + claimID);
			return;
		}

		String patControlNum = clRecord.getValue("CLCL_PA_ACCT_NO");
		String recdDate = clRecord.getValue("RECD_DT");

		CommonFunctions comFunc = new CommonFunctions();

		Query HGQry = clrn.contentEquals("")
				? qm.setQuery("HIPAA_WO_CLRN", comFunc.getQuery("queries" + File.separator + "qryHG_WithoutCLRN.sql"))
				: qm.setQuery("HIPAA_WITH_CLRN", comFunc.getQuery("queries" + File.separator + "qryHG_WithCLRN.sql"));

		HGQry.bind("ClaimReceivedDate", recdDate);
		HGQry.bind("PatientControlNumber", patControlNum);
		HGQry.bind("CLRN_Number", clrn);

		// System.out.println("HIPAA Query \n"+HGQry);

		Recordset rs = facetsDS.getRecodsetHG(HGQry);

		if (rs == null || rs.getRecordCount() == 0 || rs.getRecordCount() > 100) {
			logs.add(new LogMessage("Claim detail not found in HIPAA table for claim  " + claimID, Status.FAIL));
			return;
		} else {
			hipaaFound = true;
			logs.add(new LogMessage("Claim detail found in HIPAA table for claim  " + claimID, Status.PASS));
		}

		Record firstRec = rs.getRecord(1);
		String dohn_ckn = firstRec.getValue("DOHD_CKN");

		Recordset clRs = rs.getSubsetAsRecordset("DOHD_CKN", dohn_ckn);

		String claimTxt = "";

		for (Record clR : clRs) {
			for (int i = 1; i <= 7; i++)
				claimTxt += clR.getValue("DOTS_TEXT" + i);
		}

		claimTxt = claimTxt.trim();

		if (claimTxt.length() > 0) {
			extracted = true;
			logs.add(new LogMessage("EDI extracted from HIPAA table for claim  " + claimID, Status.PASS));
		} else {
			logs.add(new LogMessage("Claim detail could not be extracted from HIPAA table for claim  " + claimID,
					Status.FAIL));
			return;
		}

		String senderID = "";
		String receiverID = "";
		String transVersionID = "";

		if (dohn_ckn.length() > 0) {

			Query HGXWALK_Qry = qm.setQuery("HIPAA_CROSSWALK_" + dohn_ckn,
					"SELECT XGSS_GS_02,XGSS_GS_03,XGSS_GS_08 FROM FHG.FHG_XGSS_GS_SEG_X WHERE DOHD_CKN=<DOHN_CKN_VALUE>");
			HGXWALK_Qry.bind("DOHN_CKN_VALUE", dohn_ckn);
			Record xRec = facetsDS.getRecordHG(HGXWALK_Qry);
			senderID = xRec.getValue("XGSS_GS_02");
			receiverID = xRec.getValue("XGSS_GS_03");
			transVersionID = xRec.getValue("XGSS_GS_08");
		}

		for (String segLine : claimTxt.split("~")) {
			if (StringUtils.startsWith(segLine, "CLM*")) {
				String tobeReplaced = segLine.split("\\*")[1];
				claimTxt = claimTxt.replace(segLine, segLine.replace(tobeReplaced, claimID));
			}
		}

		claimX12 = claimTxt;
		filenameConvention = Util.getEDIFileName(senderID, receiverID, transVersionID).replace("#UNIQUEID#", claimID);

	}
}
