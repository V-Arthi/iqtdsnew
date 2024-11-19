package com.main.cognizant.ediautomation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.main.cognizant.ediautomation.EDIFile.EDITransaction;

public class ClaimUtils {

	private static final Logger log = LogManager.getLogger(ClaimUtils.class);

	public static List<List<String>> getMatchingClaims(EDIFile ef) {
		List<List<String>> ret = new ArrayList<>();
		for (int i = 1; i <= ef.getTransactionCount(); i++) {
			System.out.println("ProcessingTransaction :" + i);
			EDITransaction txn = ef.getTransaction(i);
			List<String> claims = ef.getTransaction(i).getClaimNumbers();
			List<String> selected = new ArrayList<>();
			for (String clm : claims) {
				List<EDILine> lines = txn.getClaim(clm);
				Optional<EDILine> temp = lines.stream()
						.filter(e -> StringUtils.equals(e.getLoopId(), "2300")
								&& StringUtils.startsWith("CLM05", e.getSegmentId())
								&& StringUtils.startsWith("21", e.getElement("CLM05-1")))
						.findFirst();
				if (temp.isPresent()) {
					System.out.println("Found IP claim :" + clm);
					selected.add(clm);
				}
			}
			ret.add(selected);
		}
		return ret;
	}

	/*
	 * public static void getClaimDetails(List<EDILine> claim,Map<String,String>
	 * map) { claim.stream().filter(e->StringUtils.equals(e.getLoopId(),"2010BA") ||
	 * StringUtils.equals(e.getLoopId(),"2010CA")).flatMap(e->e.getElementsMapAsList
	 * ().stream()).forEach(e->map.put(StringUtils.substringBefore(e,
	 * "="),StringUtils.substringAfter(e,"=")));
	 * 
	 * }
	 */

	public static Map<String, String> getClaimDetails(List<EDILine> claim, Map<String, String> readMap) {
		HashMap<String, String> ret = new HashMap<>();

		HashMap<String, String> DiagMap = new HashMap<>();
		HashMap<String, Integer> DiagCounter = new HashMap<>();
		// getting Diag codes
		for (EDILine line : claim) {
			if (StringUtils.equals(line.getLoopId(), "2300")
					&& StringUtils.startsWith(line.getUniqueSegmentId(), "HIA")) {
				line.getElementsMapAsList().stream().map(e -> StringUtils.substringAfter(e, "=")).forEach(e -> {
					String qual = StringUtils.substringBefore(e, ":");
					String val = StringUtils.substringAfter(e, ":");
					int n = DiagCounter.getOrDefault(qual, 0) + 1;
					DiagCounter.put(qual, n);
					DiagMap.put(String.format("2300_HI%s%02d-2", qual, n), val);
				});
			}
		}
		log.debug("Diag code Found:" + DiagMap);

		// get header level details
		readMap.keySet().stream().filter(e -> !StringUtils.endsWith(e, "*")).forEach(e -> {
			String value = StringUtils.contains(e, "HIA") ? DiagMap.get(e) : EDIFile.getField(claim, e);
			log.debug(String.format("Pulling element %s - Value [%s]", e, value));
			if (!StringUtils.isEmpty(value))
				ret.put(readMap.get(e), value);
		});

		// get service line level details
		int n = 0;
		String idLine = readMap.keySet().stream().filter(e -> StringUtils.endsWith(e, "**"))
				.map(e -> StringUtils.removeEnd(e, "**")).findFirst().orElse(null);

		if (idLine == null)
			return ret; // if ** not refined, no point in looking for line level details.

		for (EDILine line : claim) {
			if (StringUtils.equals(line.getLoopId(), StringUtils.substringBefore(idLine, "_"))
					&& StringUtils.startsWith(line.getUniqueSegmentId(), StringUtils.substringAfter(idLine, "_"))) {
				n++;
			}
			final int m = n;
			readMap.keySet().stream().filter(e -> StringUtils.endsWith(e, "*"))
					.filter(e -> !StringUtils.endsWith(e, "**"))
					.filter(e -> StringUtils.equals(line.getLoopId(), StringUtils.substringBefore(e, "_")))
					.filter(e -> StringUtils.startsWith(StringUtils.substringAfter(e, "_"), line.getUniqueSegmentId()))
					.forEachOrdered(e -> {
						String value = line.getElement(StringUtils.substringAfter(StringUtils.removeEnd(e, "*"), "_"));
						log.debug(String.format("Pulling element %s - Value [%s]", e, value));
						if (!StringUtils.isEmpty(value))
							ret.put(readMap.get(e) + "@" + m, value);
					});
		}
		ret.put("NO_OF_SERVICELINES", Integer.toString(n));
		/*
		 * //get detail line level details readMap.keySet().stream().filter(e ->
		 * StringUtils.endsWith(e, "*")).filter(e -> !StringUtils.endsWith(e, "**"))
		 * .forEach(e -> { for(int
		 * i=1;i<=Integer.parseInt(ret.get("NO_OF_SERVICELINES"));i++){ String value =
		 * EDIFile.getField(claim, StringUtils.replace(e,"*","@"+i));
		 * 
		 * if(!StringUtils.isEmpty(value)) ret.put(readMap.get(e)+"@"+i,value); } });
		 */

		return ret;
	}

	public static void updateField(List<EDILine> claim, String loopid, String fieldid, String value) {

		for (EDILine el : claim) {
			if (StringUtils.equals(el.getLoopId(), loopid) && StringUtils.startsWith(fieldid, el.getSegmentId())) {
				System.out.println("updating the field: " + loopid + fieldid);

				el.setElement(fieldid, value);
			}
		}

	}

}
