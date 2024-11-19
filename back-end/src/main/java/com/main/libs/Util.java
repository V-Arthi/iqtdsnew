package com.main.libs;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;

public class Util {
	private static final String dateFormat = "MMddYYYY";
	private static final String timeFormat = "HHmmssaa";

	public static String getTimeStamp() {
		SimpleDateFormat dtf = new SimpleDateFormat(dateFormat + "_" + timeFormat);
		return dtf.format(new Date());
	}

	public static String getDateTimeInFormat(String format) {
		SimpleDateFormat dtf = new SimpleDateFormat(format);
		return dtf.format(new Date());
	}

	public static String getDuration(Date start, Date end) {
		return getDuration(Duration.between(start.toInstant(), end.toInstant()));
	}

	public static String getDateInFormat(Date date, String format) {
		String ret = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			if (date == null)
				return null;
			ret = sdf.format(date);
		} catch (Exception e) {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			ret = sdf.format(new Date());
		}
		return ret;
	}

	public static String getDuration(Duration duration) {
		long hrs = duration.toHours();
		long mins = duration.toMinutes();
		long mins_ = mins - (hrs * 60);
		long secs = duration.getSeconds();
		long secs_ = secs - (mins * 60);
		return String.format("%s hours : %s mins : %s seconds", String.valueOf(hrs), String.valueOf(mins_),
				String.valueOf(secs_));
	}

	public static String getAsSQLValue(String val) {
		return "'" + Arrays.asList(val.split("\\,")).stream().map(s -> s.trim()).collect(Collectors.joining("', '"))
				+ "'";
	}

	public static String getEDIFileName(String senderID, String receiverID, String transVersionID) {
		String fileType = transVersionID.equalsIgnoreCase("005010X222A1") ? "837P"
				: (transVersionID.equalsIgnoreCase("005010X223A2") ? "837I" : "837D");

		return getEDIFileNameOfType(senderID, receiverID, fileType);

	}

	public static String getEDIFileNameOfType(String senderID, String receiverID, String fileType) {
		String defaultFileName = "FACETS_CCI_" + fileType + "_CCI_" + senderID + "_" + receiverID
				+ ".#YYYYMMDD#.#UNIQUEID#_daily." + fileType;
		String fileNameConventionFor = String.format("%s|%s|%s", senderID, receiverID, fileType);
		String fileName = Settings.getFileNameSettings(fileType).getProperty(fileNameConventionFor);

		// System.out.println("Finding file name convention for file type "+fileType+ "
		// with combination "+fileNameConventionFor);

		String name = fileName == null ? defaultFileName : fileName;
		name = name.replaceAll("#YYYYMMDD#", Util.getDateTimeInFormat("YYYYMMdd")).replaceAll("#UNIQUEID#",
				getDateTimeInFormat("hhmmssms"));

		return name.replace(" ", "");
	}

	public static String getDateInFormat(String date, String format, int plusOrMinusDays) {
		SimpleDateFormat dtf = new SimpleDateFormat(format);
		String changed = date;
		try {
			Date given = dtf.parse(date);
			// System.out.println("what is given:"+given);
			Date driven = DateUtils.addDays(given, plusOrMinusDays);
			// System.out.println("what is driven:"+driven);
			changed = dtf.format(driven);
		} catch (ParseException e) {
			System.out.println("incorrect date format");
		}

		return changed;
	}

	public static String getDateInFormat(String date, String srcFormat, String destFormat) {
		SimpleDateFormat srcDateFormat = new SimpleDateFormat(srcFormat);
		SimpleDateFormat destDateFormat = new SimpleDateFormat(destFormat);
		String changed = "99991231";
		try {
			Date given = srcDateFormat.parse(date);
			changed = destDateFormat.format(given);
		} catch (ParseException e) {
			System.out.println("Incorrect date format");
		}
		return changed;
	}

	public static String getJenkinsBuildID() {
		return System.getenv("BUILD_NUMBER") != null ? System.getenv("BUILD_NUMBER") : "LOCAL";
	}

	public static int getRandomNumber(int min, int max) {
		return new Random().nextInt(max - min) + min;
	}

	public static String getInstantAsString(Instant instant) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss").withLocale(Locale.US)
				.withZone(ZoneId.systemDefault());
		return formatter.format(instant);

	}

	public static void extractOralceWalletFromResourceAndCopyToParentDirectory() throws Exception {

		Path target = Paths.get("C://Automation", "wallet");

		File targetFile = target.toFile();
		targetFile.mkdirs();

		URL walletLocation = Util.class.getClassLoader().getResource("wallet");
		File walletDir = new File(walletLocation.toURI());
		File[] allFiles = walletDir.listFiles();

		for (File f : allFiles) {
			Path dest = Paths.get(target.toString(), f.getName());
			// Path newFile = Files.createFile(Paths.get(target.toString(),f.getName()));
			InputStream ips = Util.class.getClassLoader().getResourceAsStream("wallet" + File.separator + f.getName());
			if (ips != null) {
				Files.copy(ips, dest, StandardCopyOption.REPLACE_EXISTING);

			}

		}

		System.setProperty("wallet_location", target.toString());
	}

}
