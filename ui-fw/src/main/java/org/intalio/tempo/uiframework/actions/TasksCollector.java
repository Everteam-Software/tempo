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
package org.intalio.tempo.uiframework.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequestWrapper;

import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.uiframework.model.TaskHolder;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.backport.java.util.Arrays;

public class TasksCollector {

    final String[] parameters = new String[] { "page", "rp", "sortname", "sortorder", "query", "qtype", "type" };

    final class ParameterMap extends HashMap<String, String> {
        public ParameterMap() {
            super(parameters.length);
            put("page", "1");
            put("page", "3");
            put("sortname", "_creationDate");
            put("sortorder", "DESC");
            put("query", null);
            put("qtype", null);
            put("type", "PATask");
            init();
        }

        public void init() {
            for (String param : parameters) {
                String value = _request.getParameter(param);
                if (value != null && !value.equalsIgnoreCase("undefined"))
                    put(param, value);
            }
        }

        public boolean isSet(String param) {
            return this.containsKey(param) && this.get(param) != null;
        }
    }

    private static final Logger _log = LoggerFactory.getLogger(TasksCollector.class);
    private final Configuration conf = Configuration.getInstance();
    private final ArrayList<TaskHolder<Task>> _tasks = new ArrayList<TaskHolder<Task>>();

    private HttpServletRequestWrapper _request;
    private String _user;
    private String _endpoint;
    private String _token;

    public TasksCollector(HttpServletRequestWrapper request, String user, String token) {
        this._request = request;
        this._user = user;
        this._token = token;
        this._endpoint = conf.getServiceEndpoint();
    }

    protected ITaskManagementService getTaskManager(String endpoint, String token){
    	return new RemoteTMSFactory(endpoint, token).getService();
    }

    public void retrieveTasks() throws Exception {
        final FormManager fmanager = FormManagerBroker.getInstance().getFormManager();
        final String endpoint = URIUtils.resolveURI(_request, _endpoint);
       // final ITaskManagementService taskManager = new RemoteTMSFactory(endpoint, _token).getService();
        final ITaskManagementService taskManager = getTaskManager(endpoint, _token);

        ParameterMap params = new ParameterMap();

        String type = params.get("type");
        if (type.equals(PATask.class.getSimpleName())) {
            StringBuffer query = new StringBuffer("NOT T._state = TaskState.COMPLETED ");
            if (params.isSet("qtype"))
                query.append(" AND T." + params.get("qtype") + " like '%" + params.get("query") + "%'");
            if (params.isSet("sortname"))
                query.append(" ORDER BY T." + params.get("sortname"));
            if (params.isSet("sortorder"))
                query.append(" " + params.get("sortorder"));
            collectTasks(_token, _user, fmanager, taskManager, "PATask", query.toString(), _tasks, params.get("rp"), params.get("page"));
        } else if (type.equals(Notification.class.getSimpleName())) {
            StringBuffer query = new StringBuffer("NOT T._state = TaskState.COMPLETED ");
            if (params.isSet("qtype"))
                query.append(" AND T." + params.get("qtype") + " like '%" + params.get("query") + "%'");
            if (params.isSet("sortname"))
                query.append(" ORDER BY T." + params.get("sortname"));
            if (params.isSet("sortorder"))
                query.append(" " + params.get("sortorder"));
            collectTasks(_token, _user, fmanager, taskManager, "Notification", query.toString(), _tasks, params.get("rp"), params.get("page"));
        } else if (type.equals(PIPATask.class.getSimpleName())) {
            StringBuffer query = new StringBuffer("");
            if (params.isSet("qtype"))
                query.append("T." + params.get("qtype") + " like '%" + params.get("query") + "%'");
            if (params.isSet("sortname"))
                query.append(" ORDER BY T." + params.get("sortname"));
            if (params.isSet("sortorder"))
                query.append(" " + params.get("sortorder"));
            collectTasks(_token, _user, fmanager, taskManager, "PIPATask", query.toString(), _tasks, params.get("rp"), params.get("page"));
        }
    }

    private void collectTasks(final String token, final String user, FormManager fmanager, ITaskManagementService taskManager, String taskType, String query,
                    List<TaskHolder<Task>> tasksHolder, final String taskPerPage, final String page) throws AuthException {
        Task[] tasks = taskManager.getAvailableTasks(taskType, query);
        ArrayList<Task> list = new ArrayList<Task>(Arrays.asList(tasks));

        int total = tasks.length;
        int itasksPerPage = 0;
        try {
            itasksPerPage = Integer.parseInt(taskPerPage);
        } catch (Exception e) {
        }
        int ipage = 1;
        try {
            ipage = Integer.parseInt(page);
        } catch (Exception e) {
        }
        int index = (ipage - 1) * itasksPerPage;
        int toIndex = index + itasksPerPage;
        if (toIndex > total)
            toIndex = total;
        List<Task> list2 = (itasksPerPage == 0 ? list : list.subList(index, toIndex));
        _request.setAttribute("totalPage", total);
        _request.setAttribute("currentPage", page);

        for (Task task : list2) {
            tasksHolder.add(new TaskHolder<Task>(task, URIUtils.getResolvedTaskURLAsString(_request, fmanager, task, token, user)));
        }

        if (_log.isDebugEnabled()) {
            _log.debug("DEBUG\n" + taskType + "\n" + query + "\n" + list.size());
            _log.debug("(" + _tasks.size() + ") tasks were retrieved for participant token " + _token);
        }
    }

    public ArrayList<TaskHolder<Task>> getTasks() {
        return _tasks;
    }

}
