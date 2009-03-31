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

package org.intalio.tempo.uiframework;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.intalio.tempo.uiframework.model.TaskHolder;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.traits.ICompleteReportingTask;
import org.intalio.tempo.workflow.task.traits.IProcessBoundTask;
import org.intalio.tempo.workflow.task.traits.ITaskWithDeadline;
import org.intalio.tempo.workflow.task.traits.ITaskWithPriority;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.traits.InitTask;

import com.Ostermiller.util.CSVPrinter;

public class CSVServlet extends ExternalTasksServlet {

    private static final long serialVersionUID = 4204605680520386297L;

    @Override
    public void generateFile(HttpServletRequest request, String token, String user, ArrayList<TaskHolder<Task>> tasks, ServletOutputStream outputStream)
                    throws Exception {
        CSVPrinter csvp = new CSVPrinter(outputStream);

        for (TaskHolder<Task> t : tasks) {
            Task task = t.getTask();

            ArrayList<String> csvStrings = new ArrayList<String>();

            csvStrings.add(simpleDateFormat.format(task.getCreationDate()));
            csvStrings.add(task.getDescription());
            csvStrings.add(task.getID());
            csvStrings.add(task.getFormURLAsString());
            if (task instanceof ITaskWithPriority)
                csvStrings.add(String.valueOf(((ITaskWithPriority) task).getPriority()));
            if (task instanceof ITaskWithState)
                csvStrings.add(String.valueOf(((ITaskWithState) task).getState().getName()));
            if (task instanceof IProcessBoundTask)
                csvStrings.add(String.valueOf(((IProcessBoundTask) task).getProcessID()));
            if (task instanceof InitTask) {
                csvStrings.add(String.valueOf(((InitTask) task).getProcessEndpoint()));
                csvStrings.add(String.valueOf(((InitTask) task).getInitMessageNamespaceURI()));
                csvStrings.add(String.valueOf(((InitTask) task).getInitOperationSOAPAction()));
            }
            if (task instanceof ICompleteReportingTask)
                csvStrings.add(String.valueOf(((ICompleteReportingTask) task).getCompleteSOAPAction()));
            if (task instanceof ITaskWithDeadline) {
                ITaskWithDeadline deadlinedTask = (ITaskWithDeadline) task;
                Date deadline = deadlinedTask.getDeadline();
                if(deadline!=null) csvStrings.add(simpleDateFormat.format(deadline));
            }
            csvp.writeln(csvStrings.toArray(new String[csvStrings.size()]));
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
