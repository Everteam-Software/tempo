package org.intalio.tempo.workflow.tmsb4p.query;

public interface QueryOperator {
	public static final String EQUALS = "=";
	public static final String NOT_EQUALS = "<>";
	public static final String LESS_THAN = "<";
	public static final String GREATER_THAN = ">";
	public static final String LESS_EQUALS = "<=";
	public static final String GREATER_EQUALS = ">=";

	// extends operator
	public static final String NOT_EQUALS2 = "!=";
	public static final String IN = "in";
	public static final String NOT_IN = "not in";
	public static final String IS_NULL = "is null";
	public static final String IS_NOT_NULL = "is not null";	
}
