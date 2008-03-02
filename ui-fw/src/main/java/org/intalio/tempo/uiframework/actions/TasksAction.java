/**
 * Copyright (c) 2005-2006 Intalio inc.
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

import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.Constants;
import org.intalio.tempo.uiframework.UIFWApplicationState;
import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.uiframework.model.TaskHolder;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.controller.Action;
import org.intalio.tempo.web.controller.ActionError;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.portlet.ModelAndView;

@SuppressWarnings("unchecked")
public class TasksAction extends Action {
    private static final Logger _log = LoggerFactory.getLogger(TasksAction.class);

    private final Collection<Task> _tasks = new ArrayList<Task>();
    private final Collection<TaskHolder<? extends Task>> _activityTasks = new ArrayList<TaskHolder<? extends Task>>();
    private final Collection<TaskHolder<? extends Task>> _notifications = new ArrayList<TaskHolder<? extends Task>>();
    private final Collection<TaskHolder<PIPATask>> _initTasks = new ArrayList<TaskHolder<PIPATask>>();

    private void initLists() throws RemoteException, AuthException {
        _log.info("Parsing task list for UI-FW");
        
        FormManager fmanager = FormManagerBroker.getInstance().getFormManager();
        String peopleActivityUrl = resoleUrl(fmanager.getPeopleActivityURL());
        String notificationURL = resoleUrl(fmanager.getNotificationURL());
        String peopleInitiatedProcessURL = resoleUrl(fmanager.getPeopleInitiatedProcessURL());
        
        for (Object task : _tasks) {
            if (task instanceof Notification) {
                Notification notification = (Notification) task;
                if (!TaskState.COMPLETED.equals(notification.getState())) {
                    _notifications.add(new TaskHolder<Notification>(notification, notificationURL));
                }
            } else if (task instanceof PATask) {
                PATask paTask = (PATask) task;
                if (!TaskState.COMPLETED.equals(paTask.getState())) {
                    _activityTasks.add(new TaskHolder<PATask>(paTask, peopleActivityUrl));
                }
            } else if (task instanceof PIPATask) {
                PIPATask pipaTask = (PIPATask) task;
                _initTasks.add(new TaskHolder<PIPATask>(pipaTask, peopleInitiatedProcessURL));
            } else {
                _log.info("Ignoring task of class:" + task.getClass().getName());
            }

        }
    }


    private String resoleUrl(String url) {
        try {
            url = URIUtils.resolveURI(_request, url);
            if (_log.isDebugEnabled())
                _log.debug("Found URL:" + url);
        } catch (URISyntaxException ex) {
            _log.error("Invalid URL for peopleActivityUrl", ex);
        }
        return url;
    }

    protected ITaskManagementService getTMS(String participantToken) throws RemoteException {
        String endpoint = resoleUrl(Configuration.getInstance().getServiceEndpoint());
        return new RemoteTMSFactory(endpoint, participantToken).getService();
    }

    protected String getParticipantToken() {
        ApplicationState state = ApplicationState.getCurrentInstance(_request);
        return state.getCurrentUser().getToken();
    }

    public void retrieveTasks() {
        try {
            String pToken = getParticipantToken();
            ITaskManagementService taskManager = getTMS(pToken);
            if (_log.isDebugEnabled()) {
                _log.debug("Try to get Task List for participant token " + pToken);
            }
            Task[] tasks = taskManager.getTaskList();
            if (_log.isDebugEnabled()) {
                _log.debug("Task list of size " + tasks.length + " is retrieved for participant token " + pToken);
            }

            _tasks.addAll(Arrays.asList(tasks));
        } catch (Exception ex) {
            _errors.add(new ActionError(-1, null, "com_intalio_bpms_workflow_tasks_retrieve_error", null, ActionError
                    .getStackTrace(ex), null, null));
            _log.error("Error while retrieving task list", ex);
        }
    }

    @Override
    public ModelAndView execute() {
        retrieveTasks();

        try {
            initLists();
        } catch (Exception ex) {
            _log.error("Error during TasksAction execute()", ex);
        }
        //concat tasks data and notifications data.
        _activityTasks.addAll(_notifications);
        ModelAndView modelView = new ModelAndView("updates", createModel());
        return modelView;
    }

    public ModelAndView getErrorView() {
        return new ModelAndView(Constants.TASKS_VIEW, createModel());
    }

    protected void fillModel(Map model) {
        super.fillModel(model);
        UIFWApplicationState state = ApplicationState.getCurrentInstance(_request);
        model.put("activityTasks", _activityTasks);
        model.put("notifications", _notifications);
        model.put("initTasks", _initTasks);
        model.put("participantToken", state.getCurrentUser().getToken());
        model.put("currentUser", state.getCurrentUser().getName());
    }
}
