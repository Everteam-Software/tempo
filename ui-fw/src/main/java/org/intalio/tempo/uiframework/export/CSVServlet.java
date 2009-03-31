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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import com.Ostermiller.util.CSVPrinter;

public class CSVServlet extends ExternalTasksServlet {

    private static final long serialVersionUID = 4204605680520386297L;

    @Override
    public void generateFile(HttpServletRequest request, String token, String user, ServletOutputStream outputStream)
                    throws Exception {
        
        // open csv stream
        CSVPrinter csvp = new CSVPrinter(outputStream);
        
        // sort tasks
        ArrayList<Map<ExportKey, String>> tasks = sortTasks();
        
        // write headers
        if(tasks.size()>1) {
            Set<ExportKey> keySet = tasks.get(0).keySet();
            for(ExportKey key : keySet) csvp.write(key.name());
            csvp.println();
        }

        // write values
        for(Map<ExportKey, String> entry : tasks) {
            Collection<String> en = entry.values();
            csvp.writeln(en.toArray(new String[en.size()]));
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
