package com.main.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.main.interfaces.Filter;

@Entity
@Table(name = "claim_filters")
public class ClaimFilter implements Filter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "field_name", nullable = false)
	private String field;

	@Column(name = "operand_exp", nullable = false)
	private String operator;

	@Column(name = "field_value", nullable = false)
	private String value;

	public ClaimFilter() {
	}

	public ClaimFilter(String field, String operandExpression, String value) {
		super();
		this.field = field;
		this.operator = operandExpression;
		this.value = value;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getField() {
		return field;
	}

	@Override
	public void setField(String field) {
		this.field = field;
	}

	@Override
	public String getOperator() {
		return operator;
	}

	@Override
	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("Field:%s|Operator:%s|Value:%s", field, operator, value);
	}

}
