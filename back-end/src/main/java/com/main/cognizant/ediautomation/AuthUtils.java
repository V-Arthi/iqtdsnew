package com.main.cognizant.ediautomation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.main.cognizant.libs.MapReader;

public class AuthUtils {

	private static final Logger log = LogManager.getLogger(AuthUtils.class);

	private static String authPrefix = null;
	private static int authCounter = 0;
	// private final static String templatefile = "278_Template.properties";
	private static final String templatefile = "src" + File.separator + "test" + File.separator + "resources"
			+ File.separator + "ediautomation" + File.separator + "278_Template.properties";

	private final static List<String> segOrder = Arrays.asList(new String[] { "HEAD_ISA,GS,ST,BHT", "2000A_HL",
			"2010A_NM1", "2000B_HL", "2010B_NM1,REFZH,REFSY,REF1J,REFG5,REFN5,REFN7,N3,N4", "2000C_HL",
			"2010C_NM1,REF6P,REFF6,REFHJ,REFIG,REFNQ,REFSY,REFN6,N3,N4,DMG,INS", "2000D_HL", "2010D_NM1",
			"2000E_HL,TRN,UM,REFBB,REFNT,DTP439,DTPABC,DTP431,DTPAAH,DTP435,DTP096,HI,HSD,CRC,CR1,CR2,CR5,CR6,PWK,MSG",
			"2010EA_NM1,REFZH,REFSY,REF1J,REF1G,REFN5,REFN7,N3,N4,PER,PRV", "2010EB_NM1,N3,N4", "2010EC_NM1,REF,DTP",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4",
			"2000F_HL,TRN,UM,REF,DTP,SV1,SV2,SV3,TOO,HSD,PWK,MSG", "2010F_NM1,N3,N4", "HEAD_SE,GE,IEA" });

	private final static List<String> required = Arrays.asList(new String[] { "2000A_HL", "2010A_NM1", "2000B_HL",
			"2010B_NM1", "2000C_HL", "2010C_NM1", "2000E_HL,TRN,UM" });

	public static EDIFile generate278(VENDORS vendor) {
		EDIFile ret = null;
		Properties prop = new Properties();

		try {
			// prop.load(AuthUtils.class.getResourceAsStream(templatefile));
			prop.load(new FileInputStream(templatefile));
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> segOrder2 = segOrder.stream()
				.flatMap(e -> Arrays.asList(
						StringUtils.replaceAll(e, ",", "," + StringUtils.substringBefore(e, "_") + "_").split(","))
						.stream())
				.collect(Collectors.toList());

		List<String> lines = segOrder2.stream().map(e -> prop.getProperty(e + "_" + vendor.code(), prop.getProperty(e)))
				.collect(Collectors.toList());

		lines.forEach(System.out::println);

		ret = EDIFile.getInstance(lines);
		return ret;
	}

	public static String getUniqueAuthNumber() {
		if (authPrefix == null) {
			LocalDateTime now = LocalDateTime.now();
			authPrefix = now.format(DateTimeFormatter.ofPattern("YYMMDDhhmm"));
		}
		authCounter++;
		return String.format("AUTO%s%04d", authPrefix, authCounter);
	}

	public static Map<String, String> getAuthDetails(List<EDILine> auth, Map<String, String> readMap) {
		HashMap<String, String> ret = new HashMap<>();

		// get number of service lines
		readMap.keySet().stream().filter(e -> StringUtils.endsWith(e, "**")).forEach(e -> {
			String value = Integer.toString(EDIFile.getCount(auth, StringUtils.removeEnd(e, "**")));
			if (!StringUtils.isEmpty(value))
				ret.put(readMap.get(e), value);
		});
		// get header level details
		readMap.keySet().stream().filter(e -> !StringUtils.endsWith(e, "*")).forEach(e -> {
			log.debug(String.format("Processing Map entry :%s = %s", e, readMap.get(e)));
			String value = EDIFile.getField(auth, e);
			if (!StringUtils.isEmpty(value))
				ret.put(readMap.get(e), value);
		});

		// get detail line level details
		readMap.keySet().stream().filter(e -> StringUtils.endsWith(e, "*")).filter(e -> !StringUtils.endsWith(e, "**"))
				.forEach(e -> {
					for (int i = 1; i <= Integer.parseInt(ret.get("NO_OF_SERVICELINES")); i++) {
						String value = EDIFile.getField(auth, StringUtils.replace(e, "*", "@" + i));
						if (!StringUtils.isEmpty(value))
							ret.put(readMap.get(e) + "@" + i, value);
					}
				});

		return ret;
	}

	public static void apply(List<EDILine> claim, Map<String, String> map, Map<String, String> data) {
		map.keySet().stream().forEach(e -> {
			if (data.containsKey(e)) {
				String value = data.get(e);
				String e278field = map.get(e);
				EDIFile.updateField(claim, e278field, value);
			}
		});
	}

	public static void fixAuthLines(List<EDILine> auth) {

		// We need all the mandatory lines to be part of the output
		List<String> req2 = required.stream()
				.flatMap(e -> Arrays.asList(
						StringUtils.replaceAll(e, ",", "," + StringUtils.substringBefore(e, "_") + "_").split(","))
						.stream())
				.collect(Collectors.toList());
		auth.stream().filter(e -> req2.contains(e.getLoopId() + "_" + e.getSegmentId())).forEach(EDILine::setModified);

		// Dep HL needed if any child level loops is to be included in the output

		if (auth.stream().filter(e -> "2010D".equals(e.getLoopId())).filter(e -> e.isModified()).findAny().isPresent())
			auth.stream().filter(e -> "2000D".equals(e.getLoopId()) && "HL".equals(e.getSegmentId()))
					.forEach(EDILine::setModified);

		// Service line HL need to added for each service line included in the output
		int n = (int) auth.stream()
				.filter(e -> "2000F".equals(e.getLoopId()) && StringUtils.startsWith(e.getSegmentId(), "SV"))
				.filter(e -> e.isModified()).count();
		List<EDILine> allhls = auth.stream().filter(e -> "HL".equals(e.getSegmentId())).collect(Collectors.toList());
		int mc = (int) auth.stream().filter(e -> "HL".equals(e.getSegmentId())).filter(EDILine::isModified).count();
		int j = mc;

		for (int i = 0; i < allhls.size(); i++) {
			EDILine hl = allhls.get(i);
			if (hl.isModified())
				j--;
			else if (j == 0 && n > 0) {
				hl.setElement(1, Integer.toString(i)); // HL01
				hl.setElement(2, Integer.toString(mc)); // HL02
				n--;
			}

		}
	}

	public static Map<String, String> map837To278(Map<String, String> clm, MapReader mr) {
		Map<String, String> ret = new HashMap<>();

		Map<String, String> t1 = mr.getMap("DIRECT");
		t1.forEach((k, v) -> {
			if (clm.containsKey(k)) {
				ret.put(v, clm.get(k));
			}
		});

		Map<String, String> t2 = mr.getMap("PROV1");
		t2.forEach((k, v) -> {
			if (StringUtils.startsWith(k, "$")) {
				ret.put(v, StringUtils.removeStart(k, "$"));
			} else if (clm.containsKey(k)) {
				ret.put(v, clm.get(k));
			}
		});

		Map<String, String> t9 = mr.getMap("LINELEVEL");
		int n = Integer.parseInt(clm.get("NO_OF_SERVICELINES"));
		t9.forEach((k, v) -> {
			for (int i = 1; i <= n; i++) {
				if (clm.containsKey(k + "@" + i)) {
					ret.put(v + "@" + i, clm.get(k + "@" + i));
				}
			}

		});

		return ret;
	}

}
