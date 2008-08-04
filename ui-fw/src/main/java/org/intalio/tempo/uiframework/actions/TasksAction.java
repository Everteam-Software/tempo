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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequestWrapper;

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

@SuppressWarnings("unchecked")
public class TasksAction extends Action {
    private static final ArrayList<Task> EMPTY_TASK_LIST = new ArrayList<Task>(0);

    private static final Logger _log = LoggerFactory.getLogger(TasksAction.class);

    private final Configuration conf = Configuration.getInstance();
    private final ArrayList<TaskHolder<PATask>> _activityTasks = new ArrayList<TaskHolder<PATask>>();
    private final ArrayList<TaskHolder<Notification>> _notifications = new ArrayList<TaskHolder<Notification>>();
    private final ArrayList<TaskHolder<PIPATask>> _initTasks = new ArrayList<TaskHolder<PIPATask>>();

    private void initLists(Collection<Task> tasks) throws RemoteException, AuthException {
        if (_log.isDebugEnabled())
            _log.debug("Parsing task list for UI-FW");

        FormManager fmanager = FormManagerBroker.getInstance().getFormManager();
        String token = getParticipantToken();
        String user = getUser();

        for (Object task : tasks) {
            _log.info(((Task) task).getID() + ":" + task.getClass().getName());
            if (task instanceof Notification) {
                Notification notification = (Notification) task;
                if (!TaskState.COMPLETED.equals(notification.getState())) {
                    _notifications.add(new TaskHolder<Notification>(notification, URIUtils.getResolvedTaskURLAsString(new HttpServletRequestWrapper(_request),
                                    fmanager, notification, token, user)));
                }
            } else if (task instanceof PATask) {
                PATask paTask = (PATask) task;
                if (!TaskState.COMPLETED.equals(paTask.getState()) && !TaskState.FAILED.equals(paTask.getState())) {
                    _activityTasks.add(new TaskHolder<PATask>(paTask, URIUtils.getResolvedTaskURLAsString(new HttpServletRequestWrapper(_request), fmanager,
                                    paTask, token, user)));
                }
            } else if (task instanceof PIPATask) {
                PIPATask pipaTask = (PIPATask) task;
                _initTasks.add(new TaskHolder<PIPATask>(pipaTask, URIUtils.getResolvedTaskURLAsString(new HttpServletRequestWrapper(_request), fmanager,
                                pipaTask, token, user)));
            } else {
                if (_log.isDebugEnabled())
                    _log.debug("Ignoring task of class:" + task.getClass().getName());
            }
        }
    }

    protected ITaskManagementService getTMS(String participantToken) throws RemoteException {
        String endpoint = URIUtils.resolveURI(_request, conf.getServiceEndpoint());
        return new RemoteTMSFactory(endpoint, participantToken).getService();
    }

    protected String getParticipantToken() {
        ApplicationState state = ApplicationState.getCurrentInstance(_request);
        return state.getCurrentUser().getToken();
    }

    protected String getUser() {
        ApplicationState state = ApplicationState.getCurrentInstance(_request);
        return state.getCurrentUser().getName();
    }

    private Collection<Task> retrieveTasks() {
        try {
            String pToken = getParticipantToken();
            ITaskManagementService taskManager = getTMS(pToken);
            if (_log.isDebugEnabled()) {
                _log.debug("Try to get Task List for participant token " + pToken);
            }
            Task[] tasks = taskManager.getAvailableTasks("Task", "ORDER BY T._creationDate");
            if (_log.isDebugEnabled()) {
                _log.debug("Task list of size " + tasks.length + " is retrieved for participant token " + pToken);
            }

            return new ArrayList<Task>(Arrays.asList(tasks));
        } catch (Exception ex) {
            _errors.add(new ActionError(-1, null, "com_intalio_bpms_workflow_tasks_retrieve_error", null, ActionError.getStackTrace(ex), null, null));
            _log.error("Error while retrieving task list", ex);
            return EMPTY_TASK_LIST;
        }
    }

    @Override
    public ModelAndView execute() {
        try {
            initLists(retrieveTasks());
        } catch (Exception ex) {
            _log.error("Error during TasksAction execute()", ex);
        }

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
        UIFWApplicationState state = ApplicationState.getCurrentInstance(_request);
        model.put("activityTasks", _activityTasks);
        model.put("notifications", _notifications);
        model.put("initTasks", _initTasks);
        model.put("participantToken", state.getCurrentUser().getToken());
        model.put("currentUser", state.getCurrentUser().getName());
        model.put("refreshTime", conf.getRefreshTime());

        Properties buildProperties = BpmsVersionsServlet.getBPMSVersionsProperties();
        if (_log.isDebugEnabled())
            _log.debug(buildProperties.toString());
        if (buildProperties != null) {
            model.put("version", buildProperties.getProperty(BpmsVersionsServlet.BPMS_VERSION_PROP));
            model.put("build", buildProperties.getProperty(BpmsVersionsServlet.BPMS_BUILD_NUMBER_PROP));
        }
    }
}
