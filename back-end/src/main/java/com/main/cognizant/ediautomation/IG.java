package com.main.cognizant.ediautomation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public class IG {

	private static HashMap<String, IG> instances = new HashMap<String, IG>();

	private List<String> ig = new ArrayList<>();

	private IG(String txnType) {
		// System.out.println("Transaction type is :"+txnType);
		// String
		// filename="src"+File.separator+"test"+File.separator+"resources"+File.separator+"ediautomation"+File.separator+"IG_"+txnType;
		try (Stream<String> stream = new BufferedReader(new InputStreamReader(
				// ClassLoader.getSystemResourceAsStream("IG_" + txnType)
				IG.class.getClassLoader().getResourceAsStream("IG_" + txnType)
		// new FileInputStream(filename)
		)).lines()) {
			ig = stream.flatMap(e -> Arrays.asList(StringUtils.split(e, ",")).stream()).collect(Collectors.toList());
		}
	}

	public static IG getInstance(String txnType) {

		if (!instances.containsKey(txnType)) {
			instances.put(txnType, new IG(txnType));
		}
		return instances.get(txnType);
	}

	public String findLoopId(EDILine el) {
		String seg = el.getSegmentId();
		String ret;

		ret = ig.stream().filter(e -> StringUtils.startsWith(e, seg)).filter(e -> {
			return ((e.split("_").length == 2) ? true
					: StringUtils.equals(
							el.getElement(Integer.valueOf(StringUtils.substring(e, seg.length(), seg.length() + 2))),
							e.split("_")[1]));
		}).map(e -> StringUtils.substringAfterLast(e, "_")).collect(Collectors.joining("$"));

		return StringUtils.isEmpty(ret) ? null : ret;
	}

	public String findUniqueSegmentId(EDILine el) {
		String pfx = el.getLoopId() + "_";

		String ret;
		ret = ig.stream().filter(e -> StringUtils.startsWith(e, pfx + el.getSegmentId()))
				.map(e -> StringUtils.removeStart(e, pfx)).filter(e -> {
					return StringUtils.equals(el.getElement(StringUtils.substringBefore(e, "_")), e.split("_")[1]);
				}).map(e -> StringUtils.substringAfterLast(e, "_")).findFirst().orElse(null);

		return ret;
	}
}
