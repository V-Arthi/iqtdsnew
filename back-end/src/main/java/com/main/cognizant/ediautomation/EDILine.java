package com.main.cognizant.ediautomation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class EDILine {

	private static String delim = "*";
	private static String grpdelim = ":";
	private String loopId;
	private String segId;
	private String uniqueSegId;
	private String line;
	private String[] parts;
	private boolean dirty;

	public EDILine(String line) {
		this.loopId = null;
		this.segId = StringUtils.substringBefore(line, delim);
		this.line = StringUtils.substringAfter(line, delim);
		this.uniqueSegId = null;
		this.dirty = false;
	}

	public static void overrideDelimiter(String dlm) {
		delim = dlm;
	}

	public String getLoopId() {
		return loopId;
	}

	public String getSegmentId() {
		return segId;
	}

	public String getUniqueSegmentId() {
		return (uniqueSegId == null) ? segId : uniqueSegId;
	}

	public boolean isModified() {
		return dirty;
	}

	public void setModified() {
		dirty = true;
	}

	public void setLoopId(String lpid) {
		this.loopId = lpid;
	}

	public void setUniqueSegmentId(String segid) {
		this.uniqueSegId = segid;
	}

	public String[] getElements() {
		return (dirty && parts != null) ? parts : StringUtils.splitByWholeSeparatorPreserveAllTokens(line, delim);
	}

	public List<String> getElementsMapAsList() {
		List<String> ret = new ArrayList<String>();
		String[] parts = getElements();
		for (int i = 0; i < parts.length; i++)
			ret.add(String.format("%s_%s%02d=%s", loopId, segId, i + 1, parts[i]));
		return ret;
	}

	public String toString() {
		return loopId + "|" + segId + "|"
				+ StringUtils.appendIfMissing(line, System.lineSeparator(), System.lineSeparator());
	}

	public String getX12Line() {
		String ret = StringUtils.removeEnd(segId + delim + ((dirty && parts != null) ? getJoinedLine(parts) : line),
				delim);
		if (StringUtils.startsWith(segId, "HI"))
			ret = fixExcessGroupField(ret);
		while (StringUtils.endsWith(ret, delim))
			ret = StringUtils.removeEnd(ret, delim);
		return ret;
	}

	public String getElement(int i) {
		return getElements()[i - 1];
	}

	public String getElement(String elementId) {
		String ret = null;
		boolean isGrpElement = StringUtils.contains(elementId, "-");
		String[] ele = getElements();
		String[] subEle = null;
		int idx, subidx;
		if (isGrpElement) {
			String temp = StringUtils.removeStart(elementId, getUniqueSegmentId());
			idx = Integer.valueOf(StringUtils.substringBefore(temp, "-"));
			subidx = Integer.valueOf(StringUtils.substringAfter(temp, "-"));
			if (idx <= ele.length) {
				ret = ele[idx - 1];
				subEle = StringUtils.splitByWholeSeparatorPreserveAllTokens(ret, grpdelim);
				if (subidx <= subEle.length) {
					ret = subEle[subidx - 1];
				} else {
					ret = null;
				}
			}
		} else {
//			System.out.println("Element ID [" + elementId + "]");
//			System.out.println("Unique Segment ID [" + getUniqueSegmentId() + "]");
			idx = Integer.valueOf(StringUtils.removeStart(elementId, getUniqueSegmentId()));
			if (idx <= ele.length) {
				ret = ele[idx - 1];
			}
		}

		return ret;
	}

	public boolean setElement(int i, String value) {
		if (!dirty) {
			parts = getElements();
		}
		if (i <= parts.length) {
			dirty = true;
			parts[i - 1] = value;
			return true;
		}
		return false;
	}

	public boolean setElement(String elementId, String value) {
		if (!dirty) {
			parts = getElements();
		}

		boolean isGrpElement = StringUtils.contains(elementId, "-");
		String[] subEle = null;
		int idx, subidx;
		if (isGrpElement) {
			String temp = StringUtils.removeStart(elementId, getUniqueSegmentId());
			idx = Integer.valueOf(StringUtils.substringBefore(temp, "-"));
			subidx = Integer.valueOf(StringUtils.substringAfter(temp, "-"));
			if (idx <= parts.length) {
				subEle = StringUtils.splitByWholeSeparatorPreserveAllTokens(parts[idx - 1], grpdelim);
				if (subidx <= subEle.length) {
					subEle[subidx - 1] = value;
					parts[idx - 1] = getJoinedGroupField(subEle);
					dirty = true;
					return true;
				}

			}
		} else {
			idx = Integer.valueOf(StringUtils.removeStart(elementId, getUniqueSegmentId()));
			if (idx <= parts.length) {
				parts[idx - 1] = value;
				dirty = true;
				return true;
			}
		}

		return false;

	}

	private static String getJoinedLine(String[] parts) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (; i < parts.length - 1; i++) {
			sb.append(parts[i] + delim);
		}
		sb.append(parts[i]);
		return sb.toString();
	}

	private static String getJoinedGroupField(String[] parts) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (; i < parts.length - 1; i++) {
			sb.append(parts[i] + grpdelim);
		}
		sb.append(parts[i]);
		return sb.toString();
	}

	private static String fixExcessGroupField(String x12line) {
		String[] ps = StringUtils.splitByWholeSeparatorPreserveAllTokens(x12line, delim);
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (; i <= ps.length - 1; i++) {
			if (ps[i].contains(grpdelim)) {
				if (!StringUtils.isEmpty(StringUtils.substringAfter(ps[i], grpdelim)))
					sb.append(ps[i] + delim);
			} else {
				sb.append(ps[i] + delim);
			}
		}
		// sb.append(ps[i]);
		return sb.toString();

	}
}
