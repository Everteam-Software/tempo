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
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */
package org.intalio.tempo.uiframework.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.intalio.tempo.workflow.task.TaskState;

import atg.taglib.json.util.JSONArray;
import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;

public class JsonUpdate extends HttpServlet {
    private static final long serialVersionUID = -8024081532754663413L;

    private final Configuration conf = Configuration.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/x-json");
        String token = request.getParameter("token");
        String userName = request.getParameter("user");
        String taskType = request.getParameter("taskType");
        String description = request.getParameter("description");
        if (token != null && userName != null && token.length() > 0 && userName.length() > 0) {
            JSONObject jroot = new JSONObject();
            JSONArray jtasks = new JSONArray();
            JSONArray jprocesses = new JSONArray();
            ITaskManagementService taskManager = getTMS(request, token);
            try {
                Task[] tasks;
                if (taskType == null || taskType.length() == 0)
                    taskType = "Task";
                String subQuery = "T._description like '%'";
                if (description != null && description.length() > 0)
                    subQuery = "T._description like '%" + description + "%'";
                tasks = taskManager.getAvailableTasks(taskType, subQuery);

                FormManager fmanager = FormManagerBroker.getInstance().getFormManager();

                for (Task task : tasks) {
					if (task instanceof Notification) {
						Notification notification = (Notification) task;
						if (!TaskState.COMPLETED.equals(notification.getState()))
						{
							JSONObject jtask = new JSONObject();
							jtask.put("taskUrl", URIUtils.getResolvedTaskURLAsString(new HttpServletRequestWrapper(request), fmanager, task, token, userName));
							jtask.put("description", notification.getDescription());
							jtask.put("creationDate", notification.getCreationDate());
							jtask.put("state", notification.getState());
							jtasks.add(jtask);
						}
					} else if (task instanceof PATask) {
						PATask paTask = (PATask) task;
						if (!TaskState.COMPLETED.equals(paTask.getState())) {
							JSONObject jtask = new JSONObject();
							jtask.put("taskUrl", URIUtils.getResolvedTaskURLAsString(new HttpServletRequestWrapper(request), fmanager, task, token, userName));
							jtask.put("description", paTask.getDescription());
							jtask.put("creationDate", paTask.getCreationDate());
							jtask.put("state", paTask.getState());
							jtasks.add(jtask);
						}
					} else if (task instanceof PIPATask) {
						PIPATask pipaTask = (PIPATask) task;
						JSONObject jprocess = new JSONObject();
						jprocess.put("taskUrl", URIUtils.getResolvedTaskURLAsString(new HttpServletRequestWrapper(request), fmanager, task, token, userName));
						jprocess.put("description", pipaTask.getDescription());
						jprocess.put("creationDate", pipaTask.getCreationDate());
						jprocesses.add(jprocess);
					}
                }
                jroot.put("tasks", jtasks);
                jroot.put("process", jprocesses);
            } catch (AuthException e1) {
                throw new ServletException(e1);
            } catch (JSONException e) {
                throw new ServletException(e);
            }
            PrintWriter out = response.getWriter();
            out.print(jroot);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }

    private ITaskManagementService getTMS(HttpServletRequest request, String participantToken) {
        String endpoint = URIUtils.resolveURI(request, conf.getServiceEndpoint());
        return new RemoteTMSFactory(endpoint, participantToken).getService();
    }
}
