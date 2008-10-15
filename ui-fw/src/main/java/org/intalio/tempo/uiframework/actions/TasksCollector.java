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
import java.util.List;

import javax.servlet.http.HttpServletRequestWrapper;

import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.uiframework.model.TaskHolder;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TasksCollector {

    private static final Logger _log = LoggerFactory.getLogger(TasksCollector.class);
    private final Configuration conf = Configuration.getInstance();
    private final ArrayList<TaskHolder<Task>> _activityTasks = new ArrayList<TaskHolder<Task>>();
    private final ArrayList<TaskHolder<Task>> _notifications = new ArrayList<TaskHolder<Task>>();
    private final ArrayList<TaskHolder<Task>> _initTasks = new ArrayList<TaskHolder<Task>>();

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

        collectTasks(_token, _user, fmanager, taskManager, "Notification", "NOT T._state = TaskState.COMPLETED ORDER BY T._creationDate", _notifications);
        collectTasks(_token, _user, fmanager, taskManager, "PIPATask", "ORDER BY T._creationDate", _initTasks);
        collectTasks(_token, _user, fmanager, taskManager, "PATask", "NOT T._state = TaskState.COMPLETED ORDER BY T._creationDate", _activityTasks);

        if (_log.isDebugEnabled()) {
            _log.debug("(" + _notifications.size() + ") notifications, (" + _initTasks.size() + ") init tasks, (" + _activityTasks.size()
                            + ") activities were retrieved for participant token " + _token);
        }
    }

    private void collectTasks(final String token, final String user, FormManager fmanager, ITaskManagementService taskManager, String taskType, String query,
                    List<TaskHolder<Task>> tasksHolder) throws AuthException {
        Task[] tasks = taskManager.getAvailableTasks(taskType, query);
        for (Task task : tasks) {
            tasksHolder.add(new TaskHolder<Task>(task, URIUtils.getResolvedTaskURLAsString(_request, fmanager, task, token, user)));
        }
    }

    public ArrayList<TaskHolder<Task>> get_activityTasks() {
        return _activityTasks;
    }

    public ArrayList<TaskHolder<Task>> get_notifications() {
        return _notifications;
    }

    public ArrayList<TaskHolder<Task>> get_initTasks() {
        return _initTasks;
    }

}
