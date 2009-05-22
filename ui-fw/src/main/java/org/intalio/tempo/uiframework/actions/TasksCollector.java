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

/**
 * This class is responsible for collecting task and sending them back to the
 * user interface
 * 
 * @author niko
 * 
 */
public class TasksCollector {

    final String[] parameters = new String[] { "page", "rp", "sortname", "sortorder", "query", "qtype", "type", "full" };

    /**
     * Parameters that can come from the user interface
     */
    final class ParameterMap extends HashMap<String, String> {
        public ParameterMap() {
            super(parameters.length);
            put("full", "false");
            put("page", "1");
            put("sortname", "_creationDate");
            put("sortorder", "DESC");
            put("query", null);
            put("qtype", null);
            put("type", "PATask");
            init();
        }

        /**
         * Do not copy over null values and empty values for parameters
         */
        public void init() {
            for (String param : parameters) {
                String value = _request.getParameter(param);
                if (value != null && !value.trim().equalsIgnoreCase("") && !value.equalsIgnoreCase("undefined"))
                	put(param, value);
            }
        }

        /**
         * Tells whether a parameter is defined and has a non-null value
         */
        public boolean isSet(String param) {
            return this.containsKey(param) && this.get(param) != null;
        }
    }

    private static final Logger _log = LoggerFactory.getLogger(TasksCollector.class);
    private final Configuration conf = Configuration.getInstance();

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

    protected ITaskManagementService getTaskManager(String endpoint, String token) {
        return new RemoteTMSFactory(endpoint, token).getService();
    }

    public ArrayList<TaskHolder<Task>> retrieveTasks() throws Exception {
        final FormManager fmanager = FormManagerBroker.getInstance().getFormManager();
        final ArrayList<TaskHolder<Task>> _tasks = new ArrayList<TaskHolder<Task>>();
        final String endpoint = URIUtils.resolveURI(_request, _endpoint);
        final ITaskManagementService taskManager = getTaskManager(endpoint, _token);

        ParameterMap params = new ParameterMap();
        String type = params.get("type");

        if (type.equals(Notification.class.getSimpleName()) || type.equals(PATask.class.getSimpleName())) {
            StringBuffer baseQuery = new StringBuffer("(T._state = TaskState.READY OR T._state = TaskState.CLAIMED) ");
            collect(fmanager, _tasks, taskManager, params, type, baseQuery);
        } else if (type.equals(PIPATask.class.getSimpleName())) {
            StringBuffer baseQuery = new StringBuffer("");
            collect(fmanager, _tasks, taskManager, params, type, baseQuery);
        } else {
            _log.error("Cannot collect task of type:" + type);
        }
        return _tasks;
    }

    private void collect(final FormManager fmanager, final ArrayList<TaskHolder<Task>> _tasks, final ITaskManagementService taskManager, ParameterMap params,
                    String type, StringBuffer query) throws AuthException { 
        // do we have a valid query
        boolean validQuery = params.isSet("qtype") && params.isSet("query");
        // if yes, append it
        if (validQuery) {
            // PIPA have no base query, since they have no state.
            // so we don't need the keyword AND here.
            if (query.length()!=0) {query.append(" AND ");}
            query.append(" T." + params.get("qtype") + " like '%" + params.get("query") + "%'");
        }
        // keep this for counting total tasks
        String countQuery = query.toString();
        // set the order column
        if (params.isSet("sortname"))
            query.append(" ORDER BY T." + params.get("sortname"));
        // set the sort order
        if (params.isSet("sortorder"))
            query.append(" " + params.get("sortorder"));
        // count total and get those tasks
        collectTasks(_token, _user, fmanager, taskManager, type, countQuery, query, _tasks, params.get("rp"), params.get("page"), Boolean.valueOf(params.get("full")));
    }

    private void collectTasks(final String token, final String user, FormManager fmanager, ITaskManagementService taskManager, String taskType,
                    String countQuery, StringBuffer query, List<TaskHolder<Task>> tasksHolder, final String taskPerPage, final String page, final boolean collectFull)
                    throws AuthException {
        // get total number of tasks
        long total = taskManager.countAvailableTasks(taskType, countQuery);

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
        long toIndex = index + itasksPerPage;
        if (toIndex > total)
            toIndex = total;
        _request.setAttribute("totalPage", total);
        _request.setAttribute("currentPage", page);

        Task[] tasks = taskManager.getAvailableTasks(taskType, query.toString(), String.valueOf(index), String.valueOf(itasksPerPage), collectFull);
        for (Task task : tasks) {
            tasksHolder.add(new TaskHolder<Task>(task, URIUtils.getResolvedTaskURLAsString(_request, fmanager, task, token, user)));
        }

        if (_log.isDebugEnabled()) {
            _log.debug("DEBUG\n" + taskType + "\n" + query + "\n" + tasks.length);
            _log.debug("(" + tasks.length + ") tasks were retrieved for participant token " + _token);
        }
    }

}
