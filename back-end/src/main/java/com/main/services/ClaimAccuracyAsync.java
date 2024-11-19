package com.main.services;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.main.cognizant.dbutils.QueryManager;
import com.main.cognizant.dbutils.QueryManager.Query;
import com.main.db.utils.Recordset;
import com.main.models.ClaimAccuracy;
import com.main.models.ClaimAccuracyConditions;
import com.main.models.ClaimAccuracyResult;
import com.main.repositories.ClaimAccuracyConditionsRepository;
import com.main.repositories.ClaimAccuracyRepository;
import com.main.thirdpartyfunctions.FacetsDataSupplier;

@Service
public class ClaimAccuracyAsync {

	private static final Logger log = LoggerFactory.getLogger(ClaimAccuracyAsync.class);
	
	@Autowired
	private ClaimAccuracyRepository claimAccuracyRepo;

	@Autowired
	private ClaimAccuracyConditionsRepository conditionsRepo;

	@Async
	public void ClaimAccuracy(ClaimAccuracy data) {

		data.setStatus("Validating");
		claimAccuracyRepo.save(data);
		Boolean errorflag = false;
		FacetsDataSupplier facetsDS;
		try {

			List<String> claimIds = data.getClaimIds();

			QueryManager qryManager = QueryManager.getInstance();
			facetsDS = new FacetsDataSupplier(data.getEnv());
			facetsDS.init();
			List<ClaimAccuracyResult> results = new ArrayList<ClaimAccuracyResult>();

			for (String claimId : claimIds) {
				System.out.println(claimId);
				claimId = claimId.trim();
				String verificationQry = "SELECT CLCL_ID FROM FACETS.CMC_CLCL_CLAIM WHERE CLCL_ID IN ('{claimid}')"
						.replace(("{claimid}").toLowerCase(), claimId);
				Query query = qryManager.setQuery(String.format("cl_val_%s", claimId), verificationQry);
				Recordset rs1 = facetsDS.getRecordset(query);
				if (rs1 == null) {
					ClaimAccuracyResult claimValidationResultStatusRow = new ClaimAccuracyResult(claimId, "Claim in DB",
							"Claim to be in DB", "Claim not found in DB", "fail");
					errorflag = setResults(claimValidationResultStatusRow, results, data, "Error", errorflag);
				} else {
					for (ClaimAccuracyConditions conditionresults : conditionsRepo.findAllActive()) {
						System.out.println(conditionresults.getQuery());

						String validationQry = conditionresults.getQuery().replace(("{claimid}").toLowerCase(),
								claimId);
						Query qry = qryManager.setQuery(String.format("cl_val_%s", claimId), validationQry);
						ResultSet rs = facetsDS.getResultset(qry);

						if (conditionresults.getCondition().contains("Claim Status")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Data to be in DB", "Data not found",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							} else {
								ArrayList<String> columnvalue = facetsDS.getColumnValues(rs);
								for (String col : columnvalue) {
									if (col.equals("15")) {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(), "02",
												"Status 15 -  Claim has an error, must be fixed and moved to status 01 followed by running Payment Job (CKMM) to move to Status 02",
												"fail");
									} else if (col.equals("11")) {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(), "02",
												"Status 11  - Claim has open pends, resolve it systematically to moved to status 01 followed by running Payment Job (CKMM) to move to Status 02",
												"fail");
									} else if (col.equals("01")) {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(), "02",
												"Status 01 , Claim is pending for payment job to be run, wait for the payment batch CKMM job to be completed to have the claim moved to status 02",
												"fail");
									} else if (col.equals("02")) {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(), "02", "02", "pass");
										status = "Completed";
									}else {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(), "02",
												"Status "+col+" , Claim is not status 02",
												"fail");
									}

									errorflag = setResults(claimValidationResultStatusRow, results, data, status,
											errorflag);
								}
							}
						}
						if (conditionresults.getCondition().contains("an active member")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Data to be in DB", "Data not found",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							} else {
								ArrayList<String> columnvalue = facetsDS.getColumnValues(rs);
								ArrayList<String> values = new ArrayList<String>(Arrays.asList("S23", "S24", "SM", "SO",
										"ST", "BH3", "BL0", "5JA", "5JB", "344", "345", "349", "ZGQ"));
								for (String col : columnvalue) {
									if (values.contains(col)) {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(),
												"Not in ('S23','S24', 'SM','SO','ST','BH3','BL0','5JA','5JB','344','345','349','ZGQ')",
												col + " - Claim processed with a terminated member, reprocess with an active member",
												"fail");
									} else {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(),
												"Not in ('S23','S24', 'SM','SO','ST','BH3','BL0','5JA','5JB','344','345','349','ZGQ')",
												col + " - Claim processed with a active member", "pass");
										status = "Completed";
									}
									errorflag = setResults(claimValidationResultStatusRow, results, data, status,
											errorflag);
								}
							}
						}
						
						if (conditionresults.getCondition().contains("ESI Vendor PENDS")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "No WM_VP_ESI warning message", "No warning message",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							} else {
								ArrayList<String> columnvalue = facetsDS.getColumnValues(rs);
								for (String col : columnvalue) {
									if (col.equals("WM_VP_ESI")) {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(),
												"No WM_VP_ESI warning message",
												col + " - ESI Vendor PENDS for CNY LOBs must be resolved systematically to be eligible for sequential PENDS",
												"fail");
									}else {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(),
												"No WM_VP_ESI warning message",
												col , "pass");
										status = "Completed";
									}
									errorflag = setResults(claimValidationResultStatusRow, results, data, status,
											errorflag);
								}
							}
						}
						
						if (conditionresults.getCondition().contains("FHN PEND")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "No FHN Vendor pend warning message", "No warning message",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								ArrayList<String> columnvalue = facetsDS.getColumnValues(rs);
								ArrayList<String> values = new ArrayList<String>(Arrays.asList("WM_VP_FHN_GHI", "WM_VP_FHN_HIP" , "WM_VP_FHN_CCI" , "WM_VP_FHN_ASO"));
								for (String col : columnvalue) {
									if (values.contains(col)) {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(),
												"No FHN Vendor pend warning message",
												col + " - FHN Vendor pend is overridden manually on provider with CNY address, reprocess the claim to allow vendor pend to be resolved systematically",
												"fail");
									}else {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(),
												"No FHN Vendor pend warning message",
												col , "pass");
										status = "Completed";
									}
									errorflag = setResults(claimValidationResultStatusRow, results, data, status,
											errorflag);
								}
							}
						}
						
						if (conditionresults.getCondition().contains("Professional Inpatient services")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Professional Inpatient services should be billed with Inpatient POS", "No procedure code for POS 21",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								ArrayList<String> columnvalue = facetsDS.getColumnValues(rs);
								ArrayList<String> values = new ArrayList<String>(Arrays.asList("99221", "99222","99223","99231","99232","99233","99234","99235","99236","99237","99238","99239"));
								for (String col : columnvalue) {
									if (values.contains(col)) {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(),
												"Professional Inpatient services should be billed with Inpatient POS",
												col + " - Procedure code is incompatible with 'Place of Service'. Re-create the claim with POS 21",
												"fail");
									}else {
										claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
												conditionresults.getCondition(),
												"Professional Inpatient services should be billed with Inpatient POS",
												col , "pass");
										status = "Completed";
									}
									errorflag = setResults(claimValidationResultStatusRow, results, data, status,
											errorflag);
								}
							}
						}
						
						if (conditionresults.getCondition().contains("Acupuncture")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Acupuncture revenue codes should not be billed with Inpatient Facility Bill Type", "No value for Bill type 11 and 12",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Acupuncture revenue codes should not be billed with Inpatient Facility Bill Type", "'Acupuncture revenue codes should not be billed with Inpatient Facility Bill Type'. Re-create a claim with correct Bill Type",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Anesthesia")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Anesthesia claims need to have more than one unit billed", "No value for Anesthesia claims query",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Anesthesia claims need to have more than one unit billed", "Anesthesia claims need to have more than one unit billed. Re-create the claim with correct units",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Drug codes")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Claims with Drug codes need to have an appropriate NDC code billed", "No value for Drug codes without appropriate NDC code",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Claims with Drug codes need to have an appropriate NDC code billed", "Claims with Drug codes need to have an appropriate NDC code billed. Re-create the claim with Drug and NDC code combination.",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Radiology procedure codes")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Radiology Procedure code should not be billed with POS 81", "No value for Radiology Procedure code with POS 81",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Radiology Procedure code should not be billed with POS 81", "Radiology Procedure code should not be billed with POS 81. Re-create the claim with appropriate POS",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Valid COB")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "If Member has COB, then Valid COB data must be entered", "No COB data mismatch on the claim",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "If Member has COB, then Valid COB data must be entered", "COB data mismatch on the claim. Ensure Patient liability is greater than Zero & COB total allowed amount is equal to sum of paid+ded+copay+coinsurance",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Med SUPP product")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Valid data must be entered on Medicare Supplemental Screen", "No Med Supp data mismatch on the claim",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Valid data must be entered on Medicare Supplemental Screen", "Med Supp data mismatch on the claim. Please ensure Patient liability is greater than Zero & Med Supp total allowed amount must be equal to sum of paid+ded+copay+coinsurance",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Workflow pends")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Workflow pends should not be resolved manually", "No Workflow PEND manually bypassed",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Workflow pends should not be resolved manually", "Workflow PEND manually bypassed the user, re-create a claim and allow it to process systematically",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Facility Inpatient claims with TOB")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Facility Inpatient claims must have a 'Room and Board Revenue code'", "No Facility Inpatient claims without 'Room and Board Revenue code'",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Facility Inpatient claims must have a 'Room and Board Revenue code'", "Facility Inpatient claims must have a 'Room and Board Revenue code'. Re-create a claim by adding R&B revenue codes",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Member names should not be Alphanumeric")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Member names should not be Alphanumeric", "Member names is not Alphanumeric",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Member names should not be Alphanumeric", "Member name has numeric values. Please change the member and reprocess the claim",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Client ID should be 03 and 04 for CCI")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Client ID should be 03 and 04 for CCI", "Client ID is not 03 and 04",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Client ID should be 03 and 04 for CCI", "Client ID is incorrect for a CCI Claim. Please choose a group with client ID as 03 or 04 and resubmit the claim",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Claims lines with same DOS+CPT and one line applied with CDD override")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Claims lines with same DOS+CPT and one line applied with CDD override", "No Duplicate denial is overridden",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Claims lines with same DOS+CPT and one line applied with CDD override", "Duplicate denial is overridden. Please reprocess claims with different dates of service lines.",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Inpatient claims should not have same FROM & TO Date of service")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Inpatient claims should not have same FROM & TO Date of service", "Inpatient claims have different FROM & TO Date of service",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Inpatient claims should not have same FROM & TO Date of service", "Inpatient claim is submitted with same Dates of Service. Please resubmit claim with min. 1 day span. ",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Claim with multiple lines of different POS")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Claim with multiple lines of different POS", "No Single claim has multiple POS",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Claim with multiple lines of different POS", "Single claim has multiple POS. Please submit a claim with same Place of service in all claim lines. ",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("CCI Professional claims should have Taxonomy code to get appropriate pricing")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "CCI Professional claims should have Taxonomy code to get appropriate pricing", "No taxanomy code issue",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "CCI Professional claims should have Taxonomy code to get appropriate pricing", "CCI professional claim is submitted without Taxonomy code, Recommendation is to submit with Taxonomy code for appropriate pricing.",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("invalid COB member selection")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Valid COB member selection", "valid COB member selection",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Valid COB member selection", "Claim is submitted with Invalid COB Type \"A- Auto\", Recommendation is to submit with Correct COB Type.",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("DOD (Date of Death) on the Medicare claims")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Correct Date of Death on the Medicare claims", "Correct Date of Death on the Medicare claims",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Correct Date of Death on the Medicare claims", "Medicare Claim with Date of Death Occurrence code 55 is submitted with a Incorrect date. Submit claim with Date of Death within Claim Span Period",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Valid alternate payee details used on the claim")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Valid alternate payee details used on the claim", "Valid alternate payee details used on the claim",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Valid alternate payee details used on the claim", "Early Intervention (EIP)/Medicaid Reclamation (MREC) claims are payable to \"Alternative Payee\". Please resubmit claim with Alternate Payee details.",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						
						if (conditionresults.getCondition().contains("Default provider assignment, claim should have assigned to correct Queue")) {
							ClaimAccuracyResult claimValidationResultStatusRow = null;
							String status = "Error";
							if (facetsDS.getResultSetSize(rs) <= 0) {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Default provider assignment claim should have assigned to correct Queue", "Default provider assignment claim is assigned to correct Queue",
										"pass");
								status = "Completed";
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}else {
								claimValidationResultStatusRow = new ClaimAccuracyResult(claimId,
										conditionresults.getCondition(), "Default provider assignment claim should have assigned to correct Queue", "Claim has not been assigned to correct queue for the Default provider assignment, please review claim and report a defect to Facets SI.\r\n"
												+ "\r\n"
												+ "Default provider ID -200000000007  - Should assigned to ClaimsOps team\r\n"
												+ "Default provider ID -200000000008  - Should assigned to PFO team\r\n"
												+ "Default provider ID -200000000006  - Should assigned to PFO team\r\n"
												+ "Default provider ID -200000000002  - Should assigned to PFO team",
										"fail");
								errorflag = setResults(claimValidationResultStatusRow, results, data, status,
										errorflag);
							}
						}
						// Add Condition here
						rs.close();
					}
				}
			}
			facetsDS.close();
		} catch (Exception e) {
			data.setStatus("Error: " + e.getMessage());
			log.error("Error", e);
		}
		if (data.getStatus().equals("Validating") || errorflag) {
			data.setStatus("Error");
		}
		claimAccuracyRepo.saveAndFlush(data);

	}

	private Boolean setResults(ClaimAccuracyResult claimValidationResultStatusRow, List<ClaimAccuracyResult> results,
			com.main.models.ClaimAccuracy data, String status, Boolean errorflag) {
		results.add(claimValidationResultStatusRow);
		data.setResult(results);
		data.setStatus(status);
		claimAccuracyRepo.saveAndFlush(data);
		if (status.equals("Error")) {
			errorflag = !errorflag ? true : errorflag;
		}
		return errorflag;
	}

}
