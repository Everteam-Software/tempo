package org.intalio.tempo.workflow.tmsb4p.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLStatement {
	private List<String> selectClause = new ArrayList<String>();
	private List<String> fromClause = new ArrayList<String>();
	private List<String> whereClause = new ArrayList<String>();
	private List<String> orderByClause = new ArrayList<String>();
	private Map<String, Object> paraValues = null;
	
	public List<String> getSelectClause() {
		return selectClause;
	}
	public void setSelectClause(List<String> selectClause) {
		this.selectClause = selectClause;
	}
	public List<String> getFromClause() {
		return fromClause;
	}
	public void setFromClause(List<String> fromClause) {
		this.fromClause = fromClause;
	}
	public List<String> getWhereClause() {
		return whereClause;
	}
	public void setWhereClause(List<String> whereClause) {
		this.whereClause = whereClause;
	}
	public Map<String, Object> getParaValues() {
		return paraValues;
	}
	public void setParaValues(Map<String, Object> paraValues) {
		this.paraValues = paraValues;
	}
	
	public void addSelectClause(String clause) {	
		this.selectClause.add(clause);
	}
	
	public void addFromClause(String clause) {
		if (!this.fromClause.contains(clause)) {
			this.fromClause.add(clause);
		}
	}
	
	public void addWhereClause(String clause) {
		this.whereClause.add(clause);
	}
	
	public void addOrderbyClause(String clause) {
		this.orderByClause.add(clause);
		
	}
	
	public void addParaValue(String pName, Object value) {
		if (paraValues == null) {
			paraValues = new HashMap<String, Object>();
		}
		
		paraValues.put(pName, value);
	}
	
	public int getParaValuesStartIdx() {
		if (paraValues == null) {
			paraValues = new HashMap<String, Object>();
		}
		
		return paraValues.size() + 1;
	}
	
	public void clear(){
	    this.selectClause.clear();
	    this.fromClause.clear();
	    this.whereClause.clear();
	    this.orderByClause.clear();
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		result.append("select distinct ");
		// select clause
		for (int i = 0; i < this.selectClause.size(); i++) {
			if (i == 0) {
				result.append(selectClause.get(i));
			} else {
				result.append("," + selectClause.get(i));
			}
		}
		
		// from clause
		result.append(" from ");
		for (int i = 0; i < this.fromClause.size(); i++) {
			if (i == 0) {
				result.append(fromClause.get(i));
			} else {
				result.append("," + fromClause.get(i));
			}
		}
		
		// where clause
		if (!whereClause.isEmpty()) {
			result.append(" where ");
			for (int i = 0; i < this.whereClause.size(); i++) {
				if (i == 0) {
					result.append(whereClause.get(i));
				} else {
					// always with "and"
					result.append(" and " + whereClause.get(i));
				}
			}
		}
		
		return result.toString();
	}
}
