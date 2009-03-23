package org.intalio.tempo.workflow.tmsb4p.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaskJPAStatement {
	private static final String TASK_ALIAS = "t";
	private static final String SELECT_ALL_CLAUSE = "*";
	private static final String ORDER_BY_ASC = "asc";
	private static final String ORDER_BY_DESC = "desc";
	
	private String selectClause = null;
	private String whereClause = null;
	private String orderbyClause = null;
	
	public TaskJPAStatement(String sqlClause) throws SQLClauseException {
		parseSQLClause(sqlClause);
	}

	public TaskJPAStatement(String selectClause, String whereClause, String orderbyClause) {
		this.selectClause = selectClause;
		this.whereClause = whereClause;
		this.orderbyClause = orderbyClause;
	}

/*	public String getJPAClause() throws InvalidAttributeException  {
		StringBuffer result = new StringBuffer();

		// for the select clause
		result.append("select ");
		if (selectClauses == null) {
			// default to all the data
			result.append(this.alias);
		} else {
			for (int i = 0; i < this.selectClauses.size(); i++) {
				String clause = selectClauses.get(i);

				if (converter != null) {
					clause = converter.convertAttribute(clause);
				}

				if (i == 0) {
					result.append(clause);
				} else {
					result.append(", " + clause);
				}
			}
		}

		// for the from clause
		result.append(" from ").append(this.className).append(" " + this.alias);

		// for the where clause
		if (this.whereClause != null) {
			result.append(" where " + this.whereClause.toString(converter));
		}

		// for the order by clause
		if (this.orderByClause != null) {
			result.append(orderByClause.toString(converter));
		}

		return result.toString();
	}
	
	public static void main(String[] args) throws Exception {
		TaskClauseConverter taskConverter = new TaskClauseConverter("t");
		TaskJPAStatement statement = new TaskJPAStatement("Task", "t", taskConverter);
		
		List<String> selectClause = new ArrayList<String>();
		selectClause.add(TaskView.ID);
		selectClause.add(TaskView.STATUS);
		selectClause.add(TaskView.PRIORITY);
		selectClause.add(TaskView.SKIPABLE);
		selectClause.add("Task." + TaskView.PRIMARY_SEARCH_BY);
		
		statement.setSelectClauses(selectClause);

		SQLClause clause1 = new SQLClause(TaskView.ID, SQLOperators.EQUALS, "id");
		SQLClause clause2 = new SQLClause(TaskView.NAME, SQLOperators.GREATER_THAN, "name");
		SQLClause clause3 = new SQLClause(TaskView.STATUS, SQLOperators.LESS_THAN, "status");
		SQLClause clause4 = new SQLClause(TaskView.PRIORITY, SQLOperators.EQUALS, 1);
		SQLClause clause5 = new SQLClause(TaskView.SKIPABLE, SQLOperators.EQUALS, true);
		SQLClause clause6 = new SQLClause(TaskView.PRIMARY_SEARCH_BY, SQLOperators.EQUALS, "PRIMARY_SEARCH_BY");
		
		AndClause andClause1 = new AndClause();
		andClause1.addClause(clause1);
		andClause1.addClause(clause2);
		andClause1.addClause(clause3);
		
		OrClause orClause = new OrClause();
		orClause.addClause(clause4);
		orClause.addClause(clause5);
		
		AndClause andClause2 = new AndClause();
		andClause2.addClause(andClause1);
		andClause2.addClause(orClause);
		andClause2.addClause(clause6);
		
		statement.setWhereClauses(andClause2);
		
		OrderByClause orderByClause = new OrderByClause();
		orderByClause.addClause(new OrderByOption(TaskView.PRIORITY, "asc"));
		orderByClause.addClause(new OrderByOption("Task." + TaskView.PRIMARY_SEARCH_BY, "desc"));
	
		statement.setOrderByClause(orderByClause);
		
		System.out.println(statement.getJPAClause());
		
		Map<Integer, Object> paraValues = statement.getParaValues();
		Set keys = paraValues.keySet();
		
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			Object key = iterator.next();
			System.out.println(" key: " + key + "  value: " + paraValues.get(key));
		}
	}*/
	
	private String convertSelectClause() throws InvalidFieldException {
		String[] clauses = QueryUtil.parseSelectClause(this.selectClause);
		if ((clauses.length == 0) && (SELECT_ALL_CLAUSE.equals(clauses[0]))) {
			return TASK_ALIAS;
		}

		StringBuffer result = new StringBuffer();
		for (int i = 0; i < clauses.length; i++) {
			String field = TaskFieldConverter
					.getFieldForSelectClause(clauses[i]);
			if (field != null) {
				if (result.length() > 0) {
					result.append(",");
				}
				result.append(TASK_ALIAS + "." + field);
			}
		}

		return result.toString();
	}
	
	private String convertOrderbyClause() throws InvalidFieldException,
			SQLClauseException {
		// the orderby clause maybe as "order by Task.Priority asc, Task.Name desc"
		String[] clauses = QueryUtil.parseOrderbyClause(this.orderbyClause);
		if ((clauses.length == 0) && (SELECT_ALL_CLAUSE.equals(clauses[0]))) {
			return TASK_ALIAS;
		}

		String field = null;
		String orderbyOption = null;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < clauses.length; i++) {
			String[] temp = clauses[i].split(" ");
			if (temp.length > 1) {
				field = temp[0];
				orderbyOption = temp[1];
			}
			
			field = TaskFieldConverter
					.getFieldForOrderClause(clauses[i]);
			if (field != null) {
				if (result.length() > 0) {
					result.append(",");
				}
				result.append(TASK_ALIAS + "." + field);
				
				if (orderbyOption != null) {
					result.append(" ").append(orderbyOption);
				}
			}
			
			field = null;
			orderbyOption = null;
		}

		return result.toString();
	}
	
	/**
	 * It is to extract the select, from, where, and order by clause.
	 * @param sqlClause
	 */
	private void parseSQLClause(String sqlClause) throws SQLClauseException {
		if ((sqlClause == null) || (sqlClause.length() == 0)) {
			throw new SQLClauseException("SQL clause can't be empty!");
		}
		
		String newClause = sqlClause.toLowerCase();
		if (!newClause.startsWith("select")) {
			throw new SQLClauseException("No select clause found!");
		}
		
		if (newClause.indexOf(" from ") <= 0) {
			throw new SQLClauseException("No from clause found!");
		}
		
		// extract the select clause
		int fromIdx = newClause.indexOf(" from ");
		int whereIdx = newClause.indexOf(" where ");
		int orderbyIdx = newClause.indexOf(" order ");
	}
}
