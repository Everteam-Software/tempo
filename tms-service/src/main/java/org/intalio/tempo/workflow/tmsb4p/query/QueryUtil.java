package org.intalio.tempo.workflow.tmsb4p.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

public class QueryUtil {
	public static String[] parseSelectClause(String clause) {
		if ((clause == null) || (clause.length() == 0)) {
			return null;
		}

		String newClause = clause.toLowerCase();
		if (newClause.startsWith("select")) {
			newClause = newClause.substring("select".length());
		}

		String[] result = newClause.split(",");
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim();
		}
		return result;
	}

	public static String[] parseOrderbyClause(String clause)
			throws SQLClauseException {
		if ((clause == null) || (clause.length() == 0)) {
			return null;
		}

		String newClause = clause.toLowerCase();
		if (newClause.startsWith("order")) {
			newClause = newClause.substring("order".length());
			newClause = newClause.trim();
			if (!newClause.startsWith("by ")) {
				throw new SQLClauseException("Invalid order by clause: "
						+ clause);
			}

			newClause = newClause.substring("by ".length()).trim();
		}

		String[] result = newClause.split(",");
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim();
		}
		return result;
	}
	
	public static Date formatDate(String dateValue) throws ParseException {
		// full date format pattern maybe as: yyyy.MM.dd HH:mm:ss z 
		if (dateValue == null) {
			return null;
		}

		// check the url about the datetime spec: 
		// http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#dateTime
		String fullPattern = "yyyy-MM-dd HH:mm:ss z";
		int length = dateValue.length();

		String actualPattern = fullPattern;
		if (fullPattern.length() > dateValue.length()) {
			actualPattern = fullPattern.substring(0, dateValue.length());
		}

		SimpleDateFormat format = new SimpleDateFormat(actualPattern);

		return format.parse(dateValue);
	}
	
	public static List<String> parseString(String data, String seperator) {
		if (data == null) {
			return null;
		}
		String[] arrs = data.split(seperator);

		List<String> result = new ArrayList<String>();
		for (int i = 0; i < arrs.length; i++) {
			result.add(arrs[i]);
		}

		return result;
	}
		
	public static void main(String[] args) throws Exception{
		System.out.println(formatDate("2009-11-27 16"));
		System.out.println(formatDate("2009"));
		System.out.println(formatDate("2009-1"));
		System.out.println(formatDate("2009-11-2"));
		System.out.println(formatDate("2009-11-27"));
		System.out.println(formatDate("2009-11-27 16:34"));
		System.out.println(formatDate("2009-11-27 16:"));
		System.out.println(formatDate("2009-11-27 16:34:3"));
		System.out.println(formatDate("2009-11-27 16:34:30 CST"));
		
	}
}
