/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.uiframework;

import java.util.Calendar;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;

import com.Ostermiller.util.CSVPrinter;

public class CSVServlet extends ExternalTasksServlet {

    private static final long serialVersionUID = 4204605680520386297L;

    @Override
    public void generateFile(HttpServletRequest request, String token, String user, Task[] tasks, FormManager fmanager, ServletOutputStream outputStream)
                    throws Exception {
        CSVPrinter csvp = new CSVPrinter(outputStream);
        
        for (Task t : tasks) {
            if(!(t instanceof PIPATask)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(t.getCreationDate());
                String date = cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.YEAR);
                csvp.writeln(new String[]{date, t.getDescription(), "All day", "All day"});    
            }
        }
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
