package com.main.interfaces;

public interface Filter {

	String getField();

	void setField(String field);

	String getOperator();

	void setOperator(String operator);

	String getValue();

	void setValue(String value);
}
