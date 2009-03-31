package org.intalio.tempo.workflow.tmsb4p.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterValues {
	public static final String AND_FUNCTION = "and";
	public static final String OR_FUNCTION = "or";
	private List<SinglePara> paras = new ArrayList<SinglePara>();
	private Map<Integer, Object> values = new HashMap<Integer, Object>();
	private String m_funName = null;
	
	public ParameterValues() {
	}
	
	public ParameterValues(String function) {
		this.m_funName = function;
	}
	
	public void addPara(SinglePara sPara) {
		paras.add(sPara);
	}
	
	public String toJPAClause(String tableAlias, int startParaIdx) {
		StringBuffer result = new StringBuffer();
		
		int size = paras.size();
		if (size > 1) {
			result.append("(");
		}
		for (int i = 0; i < paras.size(); i++) {
			SinglePara para = paras.get(i);

			if (i > 0) {
				result.append(" ").append(m_funName).append(" ");
			}

			if (tableAlias != null) {
				result.append(tableAlias).append(".");
			}
			result.append(para.paraName);
			
			if (para.funName != null) {
				result.append(" " + para.funName + " ");
				
			}
			
			boolean isCollection = false;
			if (para.value != null) {
				if (QueryOperator.IN.equals(para.funName)
						|| (QueryOperator.NOT_IN.equals(para.funName))) {
					isCollection = true;
				}

				if (isCollection) 
					result.append("(");
				
				result.append("?" + startParaIdx);

				if (isCollection) 
					result.append(")");
				
				values.put(startParaIdx, para.value);
				startParaIdx++;
			}
		}
		
		if (size > 1) {
			result.append(")");
		}

		return result.toString();
	}
	
	public Map<Integer, Object> getJPAValues() {
		return this.values;
	}
	
	public static class SinglePara {
		public String paraName = null;
		public String funName = null;
		public Object value = null;
		
		public SinglePara(String paraName, String funName, Object value) {
			this.paraName = paraName;
			this.funName = funName;
			this.value = value;
		}
	}
}
