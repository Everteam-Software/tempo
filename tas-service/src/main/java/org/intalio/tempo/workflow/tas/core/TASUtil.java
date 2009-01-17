package org.intalio.tempo.workflow.tas.core;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TASUtil {

	/**
	 * Sanitize filename for inclusion into URL. e.g. C:\Foo -> Foo My
	 * Document.doc -> My+Document.doc
	 */
	public static String sanitize(String filename) {
		// find the local name: the last portion of the filename that does not
		// contain ":", "/" or "\"
		Pattern regex = Pattern.compile("(.*?)([^:/\\\\]+)$");
		Matcher match = regex.matcher(filename);
		if (match.find()) {
			try {
				String localname = match.group(2);
				return URLEncoder.encode(localname, "UTF-8");
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		} else
			throw new IllegalArgumentException("Invalid filename: " + filename);
	}

}