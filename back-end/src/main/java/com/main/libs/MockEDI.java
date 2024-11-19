package com.main.libs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.x12.Loop;
import com.main.cognizant.ediautomation.EDIFile;
import com.main.db.utils.Recordset;
import com.main.models.EDIMapping;
import com.main.models.EDIMappingX12Standard;
import com.main.models.Overrides;
import com.main.thirdpartyfunctions.FacetsDataSupplier;

public class MockEDI {

	private EDIFile ediFile;
	
	private Loop loop;
	
	private List<Overrides> overrrides = new ArrayList<Overrides>();

	private List<String> logs = new ArrayList<String>();

	public MockEDI(String ediData) {
		this.ediFile = EDIFile.getInstance(Arrays.asList(ediData.split("~")));
	}

	public String getEdiData() {
		ediFile.fixFileHeaders();
		ediFile.fixDTPs();
		ediFile.fixGE();

		return ediFile.getX12Content().trim();
	}

	public void setOverrides(List<Overrides> overrides) {
		this.overrrides = overrides;
	}

	public void runOverrides(Loop loop) {
		overrrides.forEach(o -> {
			applyOverride(loop,o);
		});
	}

//	private void applyOverride(Overrides override) {
//		String fieldName = override.getEdiMap().getFieldName();
//		String segmentRef = override.getEdiMap().getSegmentRef();
//		String txn = override.getTxnNumber();
//		String value = override.getValue();
//		if (StringUtils.contains(fieldName.toLowerCase(), "date")) {
//			value = Util.getDateInFormat(value, "MM/dd/yyyy", "yyyyMMdd");
//		}
//		updateEDISegmentValue(fieldName, segmentRef, txn, value);
//	}

//	public MockEDI doMemberUpdate(String memberID, String memEnv, List<EDIMapping> memberMapping) {
//		FacetsDataSupplier fd = new FacetsDataSupplier(memEnv);
//		fd.init();
//		Recordset rs = fd.getMemberRecord(memberID);
//
//		if (rs == null) {
//			logs.add("No member record found in environment :" + memEnv + " for member ID " + memberID);
//			return this;
//		}
//
//		for (EDIMapping em : memberMapping) {
//			String fieldName = em.getFieldName();
//			String segmentRef = em.getSegmentRef();
//			String value = rs.getRecord(1).getValue(fieldName);
//
//			updateEDISegmentValue(fieldName, segmentRef, null, value);
//		}
//		fd.close();
//		return this;
//	}

//	public MockEDI doProviderUpdate(String providerID, String provEnv, List<EDIMapping> providerMapping) {
//		String fileType = ediFile.getFileType();
//		FacetsDataSupplier fd = new FacetsDataSupplier(provEnv);
//		fd.init();
//		Recordset rs = fd.getProviderRecord(providerID);
//
//		if (rs == null) {
//			logs.add("No provider record found in environment :" + provEnv + " for provider ID " + providerID);
//			return this;
//		}
//
//		for (EDIMapping em : providerMapping) {
//			String fieldName = em.getFieldName();
//			String segmentRef = em.getSegmentRef();
//			String value = rs.getRecord(1).getValue(fieldName);
//
//			if (value == null)
//				continue;
//
//			if (fileType.equalsIgnoreCase("837P") && StringUtils.startsWith(segmentRef, "2010AA_")) {
//				String[] entityName = value.split(",");
//				if (StringUtils.endsWith(segmentRef, "NM103")) {
//					value = entityName[0].trim(); // last name
//				} else if (StringUtils.endsWith(segmentRef, "NM104")) {
//					value = entityName.length > 1 ? entityName[1].trim().split(" ")[0].trim() : ""; // firstname
//				} else if (StringUtils.endsWith(segmentRef, "NM105")) {
//					value = "";
//					String firstName = entityName.length > 1 ? entityName[1].trim() : "";
//					if (firstName.length() > 1 && firstName.contains(" ")) {
//						String middleInitial = firstName.split(" ")[1].trim().replace(".", "");
//						value = middleInitial;
//					}
//				}
//			} else if (fileType.equalsIgnoreCase("837I") && StringUtils.startsWith(segmentRef, "2010AA_")) {
//				if (StringUtils.endsWith(segmentRef, "NM104") || StringUtils.endsWith(segmentRef, "NM105")) {
//					value = "";
//				}
//			}
//			updateEDISegmentValue(fieldName, segmentRef, null, value);
//
//		}
//		fd.close();
//		return this;
//	}

//	public MockEDI doPatientAcctNumChange(String patAcctNo, EDIMapping map) {
//		updateEDISegmentValue(map.getFieldName(), map.getSegmentRef(), null, patAcctNo);
//		return this;
//	}

	public MockEDI doRecievedDateUpdate(String receivedDate, EDIMapping map) {
		String prevValue = ediFile.getBHT().getElement(map.getSegmentRef());
		boolean isUpdated = ediFile.getBHT().setElement(map.getSegmentRef(), receivedDate);
		logs.add(String.format(
				"%s[%s] is " + (isUpdated ? "updated" : "not updated ") + " with value '%s' [previous value was '%s']",
				map.getFieldName(), map.getSegmentRef(), receivedDate, prevValue));
		return this;
	}

//	private void updateEDISegmentValue(String fieldName, String segmentRef, String txn, String value) {
//		String refsArr[] = segmentRef.split(",");
//		for (int x = 0; x < refsArr.length; x++) {
//			String ref = refsArr[x].trim();
//
//			/*
//			 * this or that handling
//			 */
//			if (StringUtils.contains(ref, "~")) {
//				orLoop: for (String r : ref.split("~")) {
//					String rValue;
//					if (NumberUtils.isCreatable(txn)) {
//						rValue = EDIFile.getField(ediFile.getTransaction(Integer.parseInt(txn)).getEDILines(), r);
//						if (rValue != null) {
//							ref = r;
//							break orLoop;
//						}
//					} else {
//						for (int t = 1; t <= ediFile.getTransactionCount(); t++) {
//							rValue = EDIFile.getField(ediFile.getTransaction(t).getEDILines(), r);
//							if (rValue != null) {
//								ref = r;
//								break orLoop;
//							}
//
//						}
//					}
//				}
//			}
//
//			if (StringUtils.contains(ref, "~")) {
//				ref = StringUtils.substringBefore(ref, "~");
//
//			}
//
//			value = StringUtils.contains(value, ",") ? StringUtils.substringBefore(value, ",") : value;
//
//			// System.out.println(String.format("%s[%s] is to be updated with value %s",
//			// fieldName,ref,value));
//			if (NumberUtils.isCreatable(txn)) {
//				String existing_Value = EDIFile.getField(ediFile.getTransaction(Integer.parseInt(txn)).getEDILines(),
//						ref);
//				if (EDIFile.updateField(ediFile.getTransaction(Integer.parseInt(txn)).getEDILines(), ref,
//						value.trim())) {
//					logs.add(String.format("%s[%s] is updated with value %s at txn %d [previous value was '%s'] ",
//							fieldName, ref, value, Integer.parseInt(txn), existing_Value));
//				} else {
//					logs.add(String.format("%s[%s] is not updated with value %s at txn %d [previous value was '%s']",
//							fieldName, ref, value, Integer.parseInt(txn), existing_Value));
//				}
//			} else {
//				for (int i = 1; i <= ediFile.getTransactionCount(); i++) {
//					String existing_Value = EDIFile.getField(ediFile.getTransaction(i).getEDILines(), ref);
//
//					if (existing_Value == null)
//						continue;
//
//					if (existing_Value.contains("-") && !value.contains("-")) {
//						value = String.format("%s-%s", value, value);
//					}
//
//					if (EDIFile.updateField(ediFile.getTransaction(i).getEDILines(), ref, value.trim())) {
//						logs.add(String.format("%s[%s] is updated with value %s at txn %d [previous value was '%s'] ",
//								fieldName, ref, value, i, existing_Value));
//					} else {
//						logs.add(
//								String.format("%s[%s] is not updated with value %s at txn %d [previous value was '%s']",
//										fieldName, ref, value, i, existing_Value));
//					}
//				}
//			}
//		}
//	}

	public String getLogs() {
		return logs.stream().collect(Collectors.joining("\n"));
	}

	public String getFileName() {
		String senderID = ediFile.getISA().getElement(6);
		String receiverID = ediFile.getISA().getElement(8);
		return Util.getEDIFileNameOfType(senderID, receiverID, ediFile.getFileType());
	}


	public MockEDI doMemberUpdate(String memberID, String memEnv, List<EDIMappingX12Standard> memberMapping,
			Loop loop) {
		FacetsDataSupplier fd = new FacetsDataSupplier(memEnv);
		fd.init();
		Recordset rs = fd.getMemberRecord(memberID);

		if (rs == null) {
			logs.add("No member record found in environment :" + memEnv + " for member ID " + memberID);
			return this;
		}

		for (EDIMappingX12Standard em : memberMapping) {
			String fieldName = em.getFieldName();
			String loopId = em.getLoop();
			int loopRepeat = em.getLoopRepeat();
			String segment = em.getSegment();
			int segmentRepeat = em.getSegmentRepeat();
			String element = em.getElement();
			int subElement = em.getSubElement();
			String value = rs.getRecord(1).getValue(fieldName);
			updateEDIFileContents(loop, fieldName, loopId, loopRepeat, segment, segmentRepeat, element, subElement,
					value,null);
	
		}
		fd.close();
		return this;
	}

	private void updateEDIFileContents(Loop loop,String fieldName, String loopId, int loopRepeat, 
			String segment, int segmentRepeat,
		String element, int subElement, String value,String txn) {
	
	try{
		String prevValue=loop.getLoop(loopId, loopRepeat-1).getSegment(segment, segmentRepeat-1).getElement(element).getValue();
		System.out.println(fieldName+" "+prevValue);
		loop.getLoop(loopId, loopRepeat-1).getSegment(segment, segmentRepeat-1).getElement(element).setValue(value);
		String curValue=loop.getLoop(loopId, loopRepeat-1).getSegment(segment, segmentRepeat-1).getElement(element).getValue();
		System.out.println(fieldName+" "+curValue);
		boolean isUpdated= value.equals(curValue);
		if(txn==null)
			txn="1";
		
		logs.add(String.format(
				"%s[%s-%s-%s-%s-%s-%s] is " + (isUpdated ? "updated" : "not updated ") + " with value '%s' at txn %s "+"[previous value was '%s']",
				fieldName, loopId,loopRepeat,segment,segmentRepeat,element,subElement,value,txn,prevValue));
		setEdiX12Data(loop);
	}
	catch(Exception e) {
		
		logs.add(String.format(
				"%s[%s-%s-%s-%s-%s-%s] is not present in the EDI Template ",
				fieldName, loopId,loopRepeat,segment,segmentRepeat,element,subElement,value));
		
	}
	}
	
	private void setEdiX12Data(Loop loop) {
		this.loop=loop;
	}
	public String getEdiX12data() {
		return loop.toString();
	}
	private void applyOverride(Loop loop,Overrides override) {
		String fieldName = override.getEdiMap().getFieldName();
		String loopId = override.getEdiMap().getLoop();
		int loopRepeat = override.getEdiMap().getLoopRepeat();
		String segment = override.getEdiMap().getSegment();
		int segmentRepeat = override.getEdiMap().getSegmentRepeat();
		String element = override.getEdiMap().getElement();
		int subElement = override.getEdiMap().getSubElement();
		String txn = override.getTxnNumber();
		String value = override.getValue();
		if (StringUtils.contains(fieldName.toLowerCase(), "date")) {
			value = Util.getDateInFormat(value, "MM/dd/yyyy", "yyyyMMdd");
		}
//		updateEDISegmentValue(fieldName, segmentRef, txn, value);
		updateEDIFileContents(loop, fieldName, loopId, loopRepeat, segment, segmentRepeat, element, subElement,
				value,txn);
	}

	
	
	public MockEDI doProviderUpdate(String providerID, String provEnv, List<EDIMappingX12Standard> providerMapping,Loop loop) {
		FacetsDataSupplier fd = new FacetsDataSupplier(provEnv);
		fd.init();
		Recordset rs = fd.getProviderRecord(providerID);

		if (rs == null) {
			logs.add("No provider record found in environment :" + provEnv + " for provider ID " + providerID);
			return this;
		}

		for (EDIMappingX12Standard em : providerMapping) {
			String fieldName = em.getFieldName();
			String loopId = em.getLoop();
			int loopRepeat = em.getLoopRepeat();
			String segment = em.getSegment();
			int segmentRepeat = em.getSegmentRepeat();
			String element = em.getElement();
			int subElement = em.getSubElement();
			String value = rs.getRecord(1).getValue(fieldName);
			updateEDIFileContents(loop, fieldName, loopId, loopRepeat, segment, segmentRepeat, element, subElement,
					value,null);
		}
		fd.close();
		return this;
	}
	public MockEDI doPatientAcctNumChange(String patAcctNo, EDIMappingX12Standard map, Loop loop) {
		updateEDIFileContents(loop, map.getFieldName(), map.getLoop(), map.getLoopRepeat(),
				map.getSegment(), map.getSegmentRepeat(), map.getElement(), map.getSubElement(),
				patAcctNo,null);
		return this;
	}
	public MockEDI doRecievedDateUpdate(String receivedDate, EDIMappingX12Standard map,Loop loop) {
		updateEDIFileContents(loop, map.getFieldName(), map.getLoop(), map.getLoopRepeat(),
				map.getSegment(), map.getSegmentRepeat(), map.getElement(), map.getSubElement(),
				receivedDate,null);
		return this;
	}

}