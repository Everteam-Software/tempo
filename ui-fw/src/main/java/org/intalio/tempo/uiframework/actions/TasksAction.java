/**
 * Copyright (c) 2005-2009 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: XFormsManager.java 2764 2006-03-16 18:34:41Z ozenzin $
 * $Log:$
 */

package org.intalio.tempo.uiframework.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequestWrapper;

import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.Constants;
import org.intalio.tempo.uiframework.UIFWApplicationState;
import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.uiframework.model.TaskHolder;
import org.intalio.tempo.versions.BpmsDescriptorParser;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.controller.Action;
import org.intalio.tempo.web.controller.ActionError;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("unchecked")
public class TasksAction extends Action {
    private static final BpmsDescriptorParser BPMS_DESCRIPTOR_PARSER = new BpmsDescriptorParser();

    private static final Logger _log = LoggerFactory.getLogger(TasksAction.class);

    private final Configuration conf = Configuration.getInstance();
    private final ArrayList<TaskHolder<Task>> _activityTasks = new ArrayList<TaskHolder<Task>>();
    private final ArrayList<TaskHolder<Task>> _notifications = new ArrayList<TaskHolder<Task>>();
    private final ArrayList<TaskHolder<Task>> _initTasks = new ArrayList<TaskHolder<Task>>();

    private void retrieveTasks() {
        try {
            final UIFWApplicationState state = ApplicationState.getCurrentInstance(_request);
            final String token = state.getCurrentUser().getToken();
            final String user = state.getCurrentUser().getName();
            final FormManager fmanager = FormManagerBroker.getInstance().getFormManager();
            final String endpoint = URIUtils.resolveURI(_request, conf.getServiceEndpoint());
            final ITaskManagementService taskManager = new RemoteTMSFactory(endpoint, token).getService();

            collectTasks(token, user, fmanager, taskManager, "Notification", "NOT T._state = TaskState.COMPLETED ORDER BY T._creationDate", _notifications);
            collectTasks(token, user, fmanager, taskManager, "PIPATask", "ORDER BY T._creationDate", _initTasks);
            collectTasks(token, user, fmanager, taskManager, "PATask", "NOT T._state = TaskState.COMPLETED ORDER BY T._creationDate", _activityTasks); 

            if (_log.isDebugEnabled()) {
                _log.debug("(" + _notifications.size() + ") notifications, (" + _initTasks.size() + ") init tasks, (" + _activityTasks.size()
                                + ") activities were retrieved for participant token " + token);
            }
        } catch (Exception ex) {
            _errors.add(new ActionError(-1, null, "com_intalio_bpms_workflow_tasks_retrieve_error", null, ActionError.getStackTrace(ex), null, null));
            _log.error("Error while retrieving task list", ex);
        }
    }

    private void collectTasks(final String token, final String user, FormManager fmanager, ITaskManagementService taskManager, String taskType, String query, List<TaskHolder<Task>> tasksHolder)
                    throws AuthException {
        Task[] tasks = taskManager.getAvailableTasks(taskType,query);
        for (Task task : tasks) {
            tasksHolder.add(new TaskHolder<Task>(task, URIUtils.getResolvedTaskURLAsString(new HttpServletRequestWrapper(_request),
                            fmanager, task, token, user)));
        }
    }

    @Override
    public ModelAndView execute() {
        retrieveTasks();
        if (Boolean.valueOf(_request.getParameter("update"))) {
            return new ModelAndView(Constants.TASKS_UPDATE_VIEW, createModel());
        } else {
            return new ModelAndView(Constants.TASKS_VIEW, createModel());
        }
    }

    public ModelAndView getErrorView() {
        return new ModelAndView(Constants.TASKS_VIEW, createModel());
    }

    protected void fillModel(Map model) {
        final UIFWApplicationState state = ApplicationState.getCurrentInstance(_request);
        final String token = state.getCurrentUser().getToken();
        final String user = state.getCurrentUser().getName();
        
        model.put("activityTasks", _activityTasks);
        model.put("notifications", _notifications);
        model.put("initTasks", _initTasks);
        model.put("participantToken", token);
        model.put("currentUser", user);
        model.put("refreshTime", conf.getRefreshTime());
        BPMS_DESCRIPTOR_PARSER.addBpmsBuildVersionsPropertiesToMap(model);
    }
}
