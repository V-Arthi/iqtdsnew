package com.main.interfaces;

public interface Validation {

	public String getFieldName();

	public String getExpected();

	public String getActual();

	public String getResult();

	public void setFieldName(String fieldName);

	public void setExpected(String expected);

	public void setActual(String actual);

	public void setResult(String result);

}
