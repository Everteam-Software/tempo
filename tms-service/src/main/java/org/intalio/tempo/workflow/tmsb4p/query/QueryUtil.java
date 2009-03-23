package org.intalio.tempo.workflow.tmsb4p.query;

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
}
