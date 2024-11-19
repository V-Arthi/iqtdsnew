package com.main.cognizant.ediautomation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EDIFile {

	private static final Logger log = LogManager.getLogger(EDIFile.class);

	private static String line_delimiter = "~";
	private static final Map<String, String> editrantypes = EDIFile.initTranTypes();
	private static boolean is835 = false;
	private String sysline_sepetator;
	private EDILine isa;
	private EDILine gs;
	private EDILine st;
	private EDILine bht;
	private List<EDITransaction> ts;
	private EDILine se;
	private EDILine ge;
	private EDILine iea;
	private IG ig;
	private String fileType;
	// private int transactionSegmentCount;
	private String lpID;

	private EDIFile() {

	}

	public static HashMap<String, String> initTranTypes() {
		HashMap<String, String> map = new HashMap<>();
		map.put("005010X222A1", "837P");
		map.put("005010X222A2", "837I");
		map.put("005010X298", "837P");
		map.put("005010X223A1", "837P");
		map.put("005010X223A2", "837I");
		map.put("005010X217", "278");
		map.put("005010X221", "835");
		map.put("005010X221A1", "835");
		map.put("005010X224A2", "837D");
		return map;
	}

	public static EDIFile getInstance(String fullfilename) {
		EDIFile instance = new EDIFile();

		instance.loadFile(fullfilename);
		instance.sysline_sepetator = System.lineSeparator();
		return instance;
	}

	public static EDIFile get835InstanceStarPipe(String fullfilename) {
		is835 = true;
//		EDILine.overrideDelimiter("|");

		EDIFile instance = getInstance(fullfilename);
		instance.line_delimiter = "|";
		return instance;
	}

	public static EDIFile get835InstancePipeTilt(String fullfilename) {
		is835 = true;
		EDILine.overrideDelimiter("|");

		EDIFile instance = getInstance(fullfilename);
		return instance;
	}

	public static EDIFile getInstance(List<String> edilines) {
		EDIFile instance = new EDIFile();

		instance.loadLines(edilines);
		instance.sysline_sepetator = System.lineSeparator();
		return instance;
	}

	/*
	 * public int getTransactionSegmentCount() { return transactionSegmentCount; }
	 */
	public EDILine getISA() {
		return isa;
	}

	public EDILine getGS() {
		return gs;
	}

	public EDILine getST() {
		return st;
	}

	public EDILine getBHT() {
		return bht;
	}

	public EDILine getSE() {
		return se;
	}

	public EDILine getGE() {
		return ge;
	}

	public EDILine getIEA() {
		return iea;
	}

	public void printFile() {
		System.out.print(isa);
		System.out.print(gs);

		ts.stream().forEach(System.out::print);

		System.out.print(ge);
		System.out.print(iea);
	}

	public String getX12Content() {
		StringBuilder sb = new StringBuilder();
		sb.append(isa.getX12Line());
		sb.append(line_delimiter + sysline_sepetator);
		sb.append(gs.getX12Line());
		sb.append(line_delimiter + sysline_sepetator);
		ts.forEach(t -> sb.append(t.getX12Transaction()));
		sb.append(ge.getX12Line());
		sb.append(line_delimiter + sysline_sepetator);
		sb.append(iea.getX12Line());
		sb.append(line_delimiter + sysline_sepetator);
		return sb.toString();
	}

	public String getFileType() {
		return fileType;
	}

	public int getTransactionCount() {
		return ts.size();
	}

	public EDITransaction getTransaction(int i) {

		if (i > 0 && i <= ts.size()) {
			return ts.get(i - 1);
		} else {
			log.error(String.format("Incorrect Transaction number [%d] available transaction are [%d to %d]", i, 0,
					ts.size()));
			return null;
		}

	}

	public void swapSenderReceivers() {

		String sender = isa.getElement(6);
		String receiver = isa.getElement(8);

		isa.setElement(8, sender);
		isa.setElement(6, receiver);

		sender = gs.getElement(2);
		receiver = gs.getElement(3);

		gs.setElement(3, sender);
		gs.setElement(2, receiver);

	}

	public void fixFileHeaders() {
		LocalDateTime now = LocalDateTime.now();
		String ymd = now.format(DateTimeFormatter.ofPattern("yyMMdd"));
		String cymd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String hhmm = now.format(DateTimeFormatter.ofPattern("hhmm"));
		String id = Long.toString(Long.parseLong(now.format(DateTimeFormatter.ofPattern("yymdHHmssyy"))) % 999999999L);

		isa.setElement(9, ymd); // ISA09
		isa.setElement(10, hhmm);
		isa.setElement(13, id);
		gs.setElement(4, cymd); // GS04
		gs.setElement(5, hhmm);
		gs.setElement(6, id);
		ge.setElement(2, id);
		iea.setElement(2, id);

	}

	public void fixGE() {
		ge.setElement(1, Integer.toString(getTransactionCount()));
	}

	public void fixDTPs() {
		for (EDITransaction txn : ts) {
			txn.getEDILines().stream().filter(e -> StringUtils.startsWith(e.getUniqueSegmentId(), "DTP"))
					.filter(e -> StringUtils.contains(e.getElement(3), "-"))
					.filter(e -> StringUtils.equals(e.getElement(2), "D8")).forEach(e -> e.setElement(2, "RD8"));

			txn.getEDILines().stream().filter(e -> StringUtils.startsWith(e.getUniqueSegmentId(), "DTP"))
					.filter(e -> !StringUtils.contains(e.getElement(3), "-"))
					.filter(e -> StringUtils.equals(e.getElement(2), "RD8")).forEach(e -> e.setElement(2, "D8"));
		}
	}

	public void saveAs(String fullfilename) {

		try {
			BufferedWriter bw = Files.newBufferedWriter(Paths.get(fullfilename), StandardOpenOption.CREATE);
			saveAs(bw);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void saveAs(BufferedWriter bw) {

		try {
			bw.append(isa.getX12Line());
			bw.append(line_delimiter + sysline_sepetator);
			bw.append(gs.getX12Line());
			bw.append(line_delimiter + sysline_sepetator);
			for (EDITransaction tn : ts)
				bw.append(tn.getX12Transaction());
			bw.append(ge.getX12Line());
			bw.append(line_delimiter + sysline_sepetator);
			bw.append(iea.getX12Line());
			bw.append(line_delimiter/* +sysline_sepetator */);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void loadLines(List<String> lines) {
		lines.forEach(e -> processLine(e));
	}

	private void loadFile(String fullfilename) {

		log.info(String.format("Loading the EDI file %s", fullfilename));

		try (Stream<String> stream = Files.lines(Paths.get(fullfilename))) {

			stream.flatMap(e -> Arrays.asList(StringUtils.split(e, line_delimiter)).stream())
					.forEach(e -> processLine(e));

		} catch (IOException e) {
			log.error(String.format("Unable to open the edi file [%s]", fullfilename), e);
			e.printStackTrace();
		}

		log.info(String.format("Number of Transactions found %d", ts.size()));
		if (fileType.startsWith("837"))
			ts.stream().forEach(e -> log.info(String.format("Number of Claims found in Tranaction %d -> %d  ",
					ts.indexOf(e) + 1, e.getClaimNumbers().size())));

	}

	private void processLine(String line) {

		EDILine el = new EDILine(line);
		EDITransaction t;
		String cLpID;

		log.info(line);

//	System.out.println(el.getSegmentId());			
		switch (el.getSegmentId()) {

		case "ISA":
			if (isa == null) {
				isa = el;
			} else {
				log.error("More than one ISA segment detected");
			}
			break;
		case "GS":
			if (gs == null) {
				gs = el;
				String gs08 = el.getElements()[7];
				fileType = editrantypes.get(gs08);
				log.debug(String.format("GS08 is [%s] Hence Transaction Type is %s ", gs08, fileType));
				ig = IG.getInstance(fileType);

			} else {
				log.error("more than one GS segment detected");
			}
			break;
		case "GE":
			if (ge == null) {
				ge = el;
			} else {
				log.error("More than one GE segment detected");
			}
			break;

		case "IEA":
			if (iea == null) {
				iea = el;

			} else {
				log.error("More than one IEA segment detected");
			}
			break;

		case "ST":
			if (ts == null)
				ts = new ArrayList<>();
			t = new EDITransaction();
			t.st = el;
			ts.add(t);
			st = el;
			break;

		case "SE":
			t = ts.get(ts.size() - 1);
			t.se = el;
			if (fileType.startsWith("837"))
				t.loadClaimMap();
			if (fileType.startsWith("278"))
				t.loadAuthkey();
			if (fileType.startsWith("835"))
				t.loadClaimPaymentMap();
			se = el;
			break;

		case "BHT":
			t = ts.get(ts.size() - 1);
			t.bht = el;
			bht = el;
			break;

		default:
			t = ts.get(ts.size() - 1);
			t.lines.add(el);
			cLpID = ig.findLoopId(el);
			log.debug("Possible LoopIDs:" + cLpID);
			if (cLpID == null) {
				if (lpID != null)
					cLpID = lpID;
			} else if (lpID != null && StringUtils.contains(cLpID, "$")) {
				String[] temp = StringUtils.split(cLpID, "$");
				int minLpDist = 9999, lpDist;

				/*
				 * for (int j = 0; j < temp.length; j++) { lpDist = getLoopDistance(lpID,
				 * temp[j]); if (lpDist < minLpDist) { minLpDist = lpDist; cLpID = temp[j]; } }
				 */

				int[] lpDists = getLoopDistances(lpID, temp);
				int[] sorted = ArrayUtils.clone(lpDists);
				Arrays.sort(sorted);
				// log.debug("All loop dists: "+ Arrays.toString(lpDists));
				// log.debug("Sorted loop dists" + Arrays.toString(sorted));
				if (sorted[0] == sorted[1]) {
					minLpDist = sorted[0];
					for (int k = 0; k < temp.length; k++) {
						if (lpDists[k] == minLpDist) {
							if (lpID.length() > 4 && temp[k].length() > 4) {
								if (lpID.charAt(4) == temp[k].charAt(4))
									cLpID = temp[k];
							}
						}
					}
				} else {
					cLpID = temp[ArrayUtils.indexOf(lpDists, sorted[0])];
				}
			}
			log.debug("Finalized LoopIDs:" + cLpID);
			el.setLoopId(cLpID);
			el.setUniqueSegmentId(ig.findUniqueSegmentId(el));
			lpID = cLpID;
			log.debug(String.format("LoopID:%s SegmentId:%s UniqueSegmentId:%s", el.getLoopId(), el.getSegmentId(),
					el.getUniqueSegmentId()));
		}

	}

	private static int getLoopDistance(String lp0, String lp1) {
		int ret = Math.abs(Integer.valueOf(lp0.substring(0, 4)) - Integer.valueOf(lp1.substring(0, 4)));
		return ret;
	}

	private static int[] getLoopDistances(String lp0, String[] lp1) {
		int[] ret = new int[lp1.length];
		for (int i = 0; i < lp1.length; i++)
			ret[i] = Math.abs(Integer.valueOf(lp0.substring(0, 4)) - Integer.valueOf(lp1[i].substring(0, 4)));
		return ret;
	}

	public class EDITransaction {

		private EDILine st;
		private EDILine bht;
		private List<EDILine> lines;
		private EDILine se;
		private List<String> clmMapKey;
		private List<Integer[]> clmMapIdx;

		private List<String> clmPaymentMapKey;
		private List<Integer[]> clmPaymentMapIdx;

		private String autKey; // only 1 auth can be present inside ST/SE pack, hence array not needed (like
								// claims)
		private List<String> claimID835;

		private EDITransaction() {
			lines = new ArrayList<>();
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(st);
			sb.append(bht);
			lines.forEach(e -> sb.append(e));
			sb.append(se);
			return sb.toString();
		}

		public EDILine getST() {
			return st;
		}

		public EDILine getBHT() {
			return bht;
		}

		public EDILine getSE() {
			return se;
		}

		public String getX12Transaction() {
			StringBuilder sb = new StringBuilder();
			sb.append(st.getX12Line());
			sb.append(line_delimiter + sysline_sepetator);
			sb.append(bht.getX12Line());
			sb.append(line_delimiter + sysline_sepetator);
			lines.forEach(e -> {
//				if(e.isModified()) {
				sb.append(e.getX12Line());
				sb.append(line_delimiter + sysline_sepetator);
//					}
			});
			sb.append(se.getX12Line());
			sb.append(line_delimiter + sysline_sepetator);
			return sb.toString();
		}

		public EDITransaction trancateTransactionLines() {
			lines.removeIf(e -> !e.isModified());
			return this;
		}

		public EDITransaction fixTransactionST_SE(String txnid, EDILine gs) {
			st.setElement("ST02", txnid);
			se.setElement("SE01", Integer.toString(lines.size() + 3)); // +3 for ST,BHT and SE
			se.setElement("SE02", txnid);
			bht.setElement("BHT04", gs.getElement("GS04"));
			bht.setElement("BHT05", gs.getElement("GS05"));
			return this;
		}

		public List<EDILine> getEDILines() {
			return lines;
		}

		public String[] getElements(String loop, String seg) {
			List<EDILine> ret = lines.stream()
					.filter(e -> StringUtils.equals(e.getLoopId(), loop) && StringUtils.equals(e.getSegmentId(), seg))
					.collect(Collectors.toList());

			if (ret == null || ret.size() == 0) {
				log.error(String.format("No EDI line found matching loop[%s] Segment[%s]", loop, seg));
				return null;
			} else if (ret.size() > 1) {
				log.warn(String.format(
						"%3d EDI line found found matching loop[%s] Segment[%s], returning the first mathcing record",
						ret.size(), loop, seg));

			}
			return ret.get(0).getElements();

		}

		public List<EDILine> getClaim(String clmnumber) {
			List<EDILine> ret = null;

			if (clmMapKey.contains(clmnumber)) {
				Integer[] idx = clmMapIdx.get(clmMapKey.indexOf(clmnumber));
				ret = new ArrayList<>();
				for (int i = 0; i < idx.length; i = i + 2) {
					ret.addAll(lines.subList(idx[i], idx[i + 1] + 1));
				}

			} else {
				log.error(String.format("Claim number[%s] not found in the file", clmnumber));
				log.debug(String.format("Available Claims are %s", clmMapKey));
			}

			return ret;
		}

		public List<EDILine> getClaimPayment(String clmnumber) {
			List<EDILine> ret = null;

			if (clmPaymentMapKey.contains(clmnumber)) {
				Integer[] idx = clmPaymentMapIdx.get(clmPaymentMapKey.indexOf(clmnumber));
				ret = new ArrayList<>();
				for (int i = 0; i < idx.length; i = i + 2) {
					ret.addAll(lines.subList(idx[i], idx[i + 1] + 1));
				}

			} else {
				log.error(String.format("Claim payment number[%s] not found in the file", clmnumber));
				log.debug(String.format("Available Claims payment are %s", clmPaymentMapKey));
			}

			return ret;
		}

		public List<String> getClaimNumbers() {
			return clmMapKey;
		}

		public List<String> getClaimPaymentNumbers() {
			return clmPaymentMapKey;
		}

		private void loadClaimMap() {
			List<EDILine> ret = lines.stream().filter(
					e -> StringUtils.equals(e.getSegmentId(), "HL") || StringUtils.equals(e.getSegmentId(), "CLM"))
					.collect(Collectors.toList());

			EDILine el;
			clmMapKey = new ArrayList<>();
			clmMapIdx = new ArrayList<>();
			String clmKey;
			Integer[] clmIdx;
			int s2000A = -1, e2000A = -1, s2000B = -1, e2000B = -1, s2000C = -1, s2300 = -1;

			for (int i = 0; i < ret.size(); i++) {
				el = ret.get(i);
				switch (el.getLoopId()) {
				case "2000A":
					s2000A = lines.indexOf(el);
					e2000A = -1;
					s2000B = -1;
					e2000B = -1;
					s2000C = -1;
					s2300 = -1;
					break;
				case "2000B":
					s2000B = lines.indexOf(el);
					e2000B = -1;
					if (e2000A < 0)
						e2000A = s2000B - 1;
					s2000C = -1;
					s2300 = -1;
					break;
				case "2000C":
					s2000C = lines.indexOf(el);
					if (e2000B < 0)
						e2000B = s2000C - 1;
					s2300 = -1;
					break;
				case "2300":
					clmKey = el.getElement(1);
					s2300 = lines.indexOf(el);
					clmMapKey.add(clmKey);
					clmIdx = new Integer[6];
					clmIdx[0] = s2000A;
					clmIdx[1] = e2000A;
					clmIdx[2] = s2000B;
					clmIdx[3] = s2000C > 0 ? e2000B : s2300 - 1;
					clmIdx[4] = s2000C > 0 ? s2000C : s2300;
					clmIdx[5] = i + 1 >= ret.size() ? lines.size() - 1 : lines.indexOf(ret.get(i + 1)) - 1;
					clmMapIdx.add(clmIdx);
					break;
				}

			}

		}

		private void loadClaimPaymentMap() {
			List<EDILine> ret = lines.stream().filter(
					e -> StringUtils.equals(e.getSegmentId(), "N1") || StringUtils.equals(e.getSegmentId(), "CLP"))
					.collect(Collectors.toList());

			EDILine el;
			clmPaymentMapKey = new ArrayList<>();
			clmPaymentMapIdx = new ArrayList<>();
			String clmPaymentKey;
			Integer[] clmPaymentIdx;
			int s1000A = -1, e1000A = -1, s1000B = -1, e1000B = -1, s2100 = -1;

			for (int i = 0; i < ret.size(); i++) {
				el = ret.get(i);
				switch (el.getLoopId()) {
				case "1000A":
					s1000A = lines.indexOf(el);
					e1000A = -1;
					s1000B = -1;
					e1000B = -1;
					s2100 = -1;
					break;
				case "1000B":
					s1000B = lines.indexOf(el);
					e1000B = -1;
					if (e1000A < 0)
						e1000A = s1000B - 1;
					s2100 = -1;
					break;
				case "2100":
					clmPaymentKey = el.getElement(7);
					s2100 = lines.indexOf(el);
					if (e1000B < 0)
						e1000B = s2100 - 1;
					clmPaymentMapKey.add(clmPaymentKey);
					clmPaymentIdx = new Integer[6];
					clmPaymentIdx[0] = s1000A;
					clmPaymentIdx[1] = e1000A;
					clmPaymentIdx[2] = s1000B;
					clmPaymentIdx[3] = e1000B;
					clmPaymentIdx[4] = s2100;
					clmPaymentIdx[5] = i + 1 >= ret.size() ? lines.size() - 1 : lines.indexOf(ret.get(i + 1)) - 1;
					clmPaymentMapIdx.add(clmPaymentIdx);
					break;
				}

			}

		}

		private void loadAuthkey() {
			List<EDILine> ret = lines.stream().filter(e -> StringUtils.equals(e.getSegmentId(), "TRN"))
					.collect(Collectors.toList());
			autKey = ret.get(0).getElement("TRN02");
		}

		public List<EDILine> getAuth() {
			return lines;
		}

		public List<EDILine> getMatchingLines(String loopid) {
			return lines.stream().filter(e -> StringUtils.equals(e.getLoopId(), loopid)).collect(Collectors.toList());
		}
	}

	public static boolean updateField(List<EDILine> lines, String segRef, String value) {
		return updateField(lines, StringUtils.substringBefore(segRef, "_"), StringUtils.substringAfter(segRef, "_"),
				value);

	}

	public static boolean updateField(List<EDILine> lines, String loopid, String fieldid, String value) {
		boolean ret = false;
		log.debug(String.format("Updating %s - %s with value %s", loopid, fieldid, value));
		boolean skip = StringUtils.contains(fieldid, "@");
		int reqItr = 1;
		int skippedItr = 0;
		if (skip) {
			reqItr = Integer.parseInt(StringUtils.substringAfter(fieldid, "@"));
			fieldid = StringUtils.substringBefore(fieldid, "@");
		}
		for (EDILine el : lines) {
			if (StringUtils.equals(el.getLoopId(), loopid)
					&& StringUtils.startsWith(fieldid, el.getUniqueSegmentId())) {
				log.debug(String.format("Matching line for %s - %s found : %s", loopid, fieldid, el.toString()));
				if (!skip || reqItr == skippedItr + 1) {
					ret = el.setElement(fieldid, value);
					log.debug("Update successfull");
					// break;
				}
				skippedItr++;
			}
		}
		return ret;
	}

	public static String getField(List<EDILine> lines, String segRef) {
		return getField(lines, StringUtils.substringBefore(segRef, "_"), StringUtils.substringAfter(segRef, "_"));

	}

	public static String getField(List<EDILine> lines, String loopid, String fieldid) {
		String ret = null;
		boolean skip = StringUtils.contains(fieldid, "@");
		int reqItr = 1;
		int skippedItr = 0;
		if (skip) {
			reqItr = Integer.parseInt(StringUtils.substringAfter(fieldid, "@"));
			fieldid = StringUtils.substringBefore(fieldid, "@");
		}
		for (EDILine el : lines) {
			if (StringUtils.equals(el.getLoopId(), loopid)
					&& StringUtils.startsWith(fieldid, el.getUniqueSegmentId())) {
				// System.out.println("Fetching the field: "+ loopid+fieldid);
				// System.out.println("Edi line:" + el.getX12Line());
				if (!skip || reqItr == skippedItr + 1) {
					log.debug(String.format("Selected EDILine [%s] for Pulling element %s ", el, fieldid));
					ret = el.getElement(fieldid);
					log.debug(String.format("Pulling element %s - Value [%s]", fieldid, ret));
					break;
				}
				skippedItr++;
			}
		}
		return ret;
	}

	public static int getCount(List<EDILine> lines, String segReg) {

		return getCount(lines, StringUtils.substringBefore(segReg, "_"), StringUtils.substringAfter(segReg, "_"));

	}

	public static int getCount(List<EDILine> lines, String loopid, String segid) {

		return (int) lines.stream().filter(el -> StringUtils.equals(el.getLoopId(), loopid)
				&& StringUtils.startsWith(segid, el.getUniqueSegmentId())).count();

	}

	public static void mockFile(List<EDILine> lines, Map<String, String> edimap, Map<String, String> data) {

		int nols = Integer.parseInt(data.getOrDefault("NO_OF_SERVICELINES", "1"));
		// get header level details
		edimap.keySet().stream().filter(e -> !StringUtils.endsWith(e, "*")).forEach(e -> {
			// System.out.println("Updating:" + edimap.get(e));
			if (data.containsKey(edimap.get(e)))
				EDIFile.updateField(lines, e, data.get(edimap.get(e)));
		});

		// get detail line level details
		edimap.keySet().stream().filter(e -> StringUtils.endsWith(e, "*")).filter(e -> !StringUtils.endsWith(e, "**"))
				.forEach(e -> {
					for (int i = 1; i <= nols; i++) {
						if (data.containsKey(edimap.get(e) + "@" + i))
							EDIFile.updateField(lines, StringUtils.replace(e, "*", "@" + i),
									data.get(edimap.get(e) + "@" + i));
					}
				});
	}

	public static void addLineAfter(EDILine line, List<EDILine> lines, String loopid, String segmentid) {
		boolean found = false;
		int idx = -1;
		for (int i = 0; i < lines.size(); i++) {
			EDILine el = lines.get(i);
			if (StringUtils.equals(el.getLoopId(), loopid)
					&& (segmentid == null || StringUtils.startsWith(el.getUniqueSegmentId(), segmentid)))
				found = true;
			if (found) {
				if ((getLoopDistance(loopid, el.getLoopId()) > 0)
						|| (segmentid != null && !StringUtils.startsWith(el.getUniqueSegmentId(), segmentid))) {
					idx = i;
					break;
				}
			}
		}

		if (found) {
			if (idx < 0) {
				lines.add(line);
			} else {
				lines.add(idx, line);
			}
		}
	}

	public static void addLineBefore(EDILine line, List<EDILine> lines, String loopid, String segmentid) {
		addLineBefore(line, lines, loopid, segmentid, 1);
	}

	public static void addLineBefore(EDILine line, List<EDILine> lines, String loopid, String segmentid, int itr) {
		boolean found = false;
		int idx = -1;
		for (int i = 0; i < lines.size(); i++) {
			EDILine el = lines.get(i);
			if (StringUtils.equals(el.getLoopId(), loopid)
					&& (segmentid == null || StringUtils.startsWith(el.getUniqueSegmentId(), segmentid))) {
				found = true;
				if (--itr == 0) {
					idx = i;
					break;
				}
			}
		}

		if (found) {
			if (idx < 0) {
				lines.add(line);
			} else {
				lines.add(idx, line);
			}
		}
	}

}
