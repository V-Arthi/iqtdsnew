package com.main.cognizant.ediautomation;

public enum VENDORS {

	CARECORE("CARECORE"), ICORE("ICORE"), NIA("NIA"), ORTHONET("ORTHONET"), PALLADIAN("PLDN"), VALUEOPTIONS("VALOPT"),
	ACP("ACP"), HCP("HCP");

	String vendorcode;

	VENDORS(String vendor) {
		vendorcode = vendor;
	}

	public String code() {

		return vendorcode;
	}

}
