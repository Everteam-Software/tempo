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
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.Constants;
import org.intalio.tempo.uiframework.UIFWApplicationState;
import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.uiframework.model.TaskHolder;
import org.intalio.tempo.uiframework.versions.BpmsVersionsServlet;
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
import org.springframework.web.servlet.ModelAndView;

public class TasksAction extends Action {
    private static final Logger _log = LoggerFactory.getLogger(TasksAction.class);

    protected final Collection<Task> _tasks = new ArrayList<Task>();

    protected final Collection<TaskHolder<PATask>> _activityTasks = new ArrayList<TaskHolder<PATask>>();

    protected final Collection<TaskHolder<Notification>> _notifications = new ArrayList<TaskHolder<Notification>>();

    protected final Collection<TaskHolder<PIPATask>> _initTasks = new ArrayList<TaskHolder<PIPATask>>();

    /**
     * Applies directly to TMS web-service for task list retrieval.
     */
    protected void initActivityTaskList() throws RemoteException, AuthException {
        ArrayList<TaskHolder<PATask>> activityTasks = new ArrayList<TaskHolder<PATask>>(_tasks.size());
        FormManager fmanager = FormManagerBroker.getInstance().getFormManager();
        String peopleActivityUrl = fmanager.getPeopleActivityURL();
        try {
            peopleActivityUrl = URIUtils.resolveURI(_request, peopleActivityUrl);
        } catch (URISyntaxException ex) {
            _log.error("Invalid URL for peopleActivityUrl", ex);
        }

        for (Task task : _tasks) {
            if (task instanceof PATask) {
                PATask paTask = (PATask) task;
                if (!TaskState.COMPLETED.equals(paTask.getState())) {
                    activityTasks.add(new TaskHolder<PATask>(paTask, peopleActivityUrl));
                }
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("activityTasks size " + activityTasks.size());
        }

        Collections.sort(activityTasks, new java.util.Comparator<TaskHolder<PATask>>() {
            public int compare(TaskHolder<PATask> o1, TaskHolder<PATask> o2) {
                Date o1Date = o1.getTask().getCreationDate();
                Date o2Date = o2.getTask().getCreationDate();
                return o1Date == null ? (o2Date == null ? 0 : -1) : o1Date.compareTo(o2Date);
            }
        });

        _activityTasks.addAll(activityTasks);
    }

    // FIXME: copy-pasted from the above
    protected void initNotificationList() throws RemoteException, AuthException {
        ArrayList<TaskHolder<Notification>> notifications = new ArrayList<TaskHolder<Notification>>(_tasks.size());
        FormManager fmanager = FormManagerBroker.getInstance().getFormManager();
        String notificationURL = fmanager.getNotificationURL();
        try {
            notificationURL = URIUtils.resolveURI(_request, notificationURL);
        } catch (URISyntaxException ex) {
            _log.error("Invalid URL for notificationURL", ex);
        }

        for (Task task : _tasks) {
            if (task instanceof Notification) {
                Notification notification = (Notification) task;
                if (!TaskState.COMPLETED.equals(notification.getState())) {
                    notifications.add(new TaskHolder<Notification>(notification, notificationURL));
                }
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("notifications size " + notifications.size());
        }

        Collections.sort(notifications, new java.util.Comparator<TaskHolder<Notification>>() {
            public int compare(TaskHolder<Notification> o1, TaskHolder<Notification> o2) {
                Date o1Date = o1.getTask().getCreationDate();
                Date o2Date = o2.getTask().getCreationDate();
                return o1Date == null ? (o2Date == null ? 0 : -1) : o1Date.compareTo(o2Date);
            }
        });

        _notifications.addAll(notifications);
    }

    /**
     * Applies directly to TMS web-service for task list retrieval.
     */
    protected void initInitTaskList() throws RemoteException, AuthException {
        FormManager fmanager = FormManagerBroker.getInstance().getFormManager();
        ArrayList<TaskHolder<PIPATask>> initTasks = new ArrayList<TaskHolder<PIPATask>>(_tasks.size());

        String peopleInitiatedProcessURL = fmanager.getPeopleInitiatedProcessURL();
        try {
            peopleInitiatedProcessURL = URIUtils.resolveURI(_request, peopleInitiatedProcessURL);
        } catch (URISyntaxException ex) {
            _log.error("Invalid URL for peopleInitiatedProcesURL", ex);
        }

        for (Task task : _tasks) {
            if (task instanceof PIPATask) {
                PIPATask pipaTask = (PIPATask) task;
                initTasks.add(new TaskHolder<PIPATask>(pipaTask, peopleInitiatedProcessURL));
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("Process Initiating Task list of size " + initTasks.size() + " obtained from request map");
        }

        Collections.sort(initTasks, new java.util.Comparator<TaskHolder<PIPATask>>() {
            public int compare(TaskHolder<PIPATask> o1, TaskHolder<PIPATask> o2) {
                Date o1Date = o1.getTask().getCreationDate();
                Date o2Date = o2.getTask().getCreationDate();
                return o1Date == null ? (o2Date == null ? 0 : -1) : o1Date.compareTo(o2Date);
            }
        });

        _initTasks.addAll(initTasks);
    }

    protected ITaskManagementService getTMS(String participantToken) throws RemoteException {
        String endpoint = Configuration.getInstance().getServiceEndpoint();
        try {
            endpoint = URIUtils.resolveURI(_request, endpoint);
        } catch (URISyntaxException ex) {
            _log.error("Invalid URL for TMS endpoint", ex);
        }

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
            initActivityTaskList();
            initNotificationList();
            initInitTaskList();
        } catch (Exception ex) {
            _log.error("Error during TasksAction execute()", ex);
        }
        
        
        String updateFlag = _request.getParameter("update");
        
        ModelAndView modelView = null;
        
        //udateFlag==true -> auto update
        if(updateFlag != null && updateFlag.equals("true")) {
        	modelView = new ModelAndView("updates", createModel());
        } else {
        	modelView = new ModelAndView(Constants.TASKS_VIEW, createModel());
        }
        
        return modelView;
    }

    public ModelAndView getErrorView() {
        return new ModelAndView(Constants.TASKS_VIEW, createModel());
    }

    @Override
    protected void fillModel(Map model) {
        super.fillModel(model);
        //fillBuildProperties(model);
        UIFWApplicationState state = ApplicationState.getCurrentInstance(_request);
        model.put("activityTasks", _activityTasks);
        model.put("notifications", _notifications);
        model.put("initTasks", _initTasks);
        model.put("participantToken", state.getCurrentUser().getToken());
        model.put("currentUser", state.getCurrentUser().getName());
    }

    private void fillBuildProperties(Map<String, Object> model) {
        Properties buildProperties = BpmsVersionsServlet.getBPMSVersionsProperties();
        if (buildProperties != null) {
            model.put("version", buildProperties.getProperty("bpms-version"));
            model.put("build", buildProperties.getProperty("bpms-build-number"));
        }
    }
}
