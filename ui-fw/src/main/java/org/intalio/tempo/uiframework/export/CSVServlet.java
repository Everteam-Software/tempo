/**
 * Copyright (c) 2005-20089 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.uiframework.export;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.intalio.tempo.uiframework.export.ExportKey.ExportSITAKey;

import com.Ostermiller.util.CSVPrinter;

public class CSVServlet extends ExternalTasksServlet {

	private static final long serialVersionUID = 4204605680520386297L;

	public void generateFile(HttpServletRequest request, String token,
			String user, ServletOutputStream outputStream) throws Exception {

		// open csv stream
		CSVPrinter csvp = new CSVPrinter(outputStream);

		// sort tasks
		ArrayList<LinkedHashMap<String, String>> tasks = sortSITAIntalioTasks();
		// write headers
		if (tasks.size() > 0) {
			Set<String> keySet = tasks.get(0).keySet();
			for (String key : keySet)
				csvp.write(key);
			csvp.println();

			// write values
			for (LinkedHashMap<String, String> entry : tasks) {
				Set<String> keySet1 = tasks.get(0).keySet();
				for (String key : keySet1) {
					csvp.write(entry.get(key));
				}

				csvp.println();
			}
		} else {
			csvp.writeln("No items are available");
		}

		// close csv file
		csvp.close();
	}

	@Override
	public String getFileExt() {
		return ".csv";
	}

	@Override
	public String getFileMimeType() {
		return "application/csv";
	}

}
