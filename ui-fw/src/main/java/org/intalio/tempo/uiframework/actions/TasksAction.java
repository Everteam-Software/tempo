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

import java.util.Map;

import javax.servlet.http.HttpServletRequestWrapper;

import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.Constants;
import org.intalio.tempo.uiframework.UIFWApplicationState;
import org.intalio.tempo.versions.BpmsDescriptorParser;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.controller.Action;
import org.intalio.tempo.web.controller.ActionError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("unchecked")
public class TasksAction extends Action {
    private static final BpmsDescriptorParser BPMS_DESCRIPTOR_PARSER = new BpmsDescriptorParser();
    private static final Logger _log = LoggerFactory.getLogger(TasksAction.class);

    @Override
    public ModelAndView execute() {
        if (Boolean.valueOf(_request.getParameter("update"))) {
            return new ModelAndView(Constants.TASKS_UPDATE_VIEW, createModel());
        } else {
            return new ModelAndView(Constants.TASKS_VIEW, createModel());
        }
    }

    public ModelAndView getErrorView() {
        return new ModelAndView(Constants.TASKS_VIEW, createModel());
    }

    protected TasksCollector getTaskCollector(String user, String token){
    	return new TasksCollector(new HttpServletRequestWrapper(_request), user, token);
    }
    protected void fillModel(Map model) {
        final UIFWApplicationState state = ApplicationState.getCurrentInstance(new HttpServletRequestWrapper(_request));
        final String token = state.getCurrentUser().getToken();
        final String user = state.getCurrentUser().getName();
        try {
//            TasksCollector collector = new TasksCollector(new HttpServletRequestWrapper(_request), user, token);
        	 TasksCollector collector = getTaskCollector(user, token);
            collector.retrieveTasks();
            model.put("activityTasks", collector.get_activityTasks());
            model.put("notifications", collector.get_notifications());
            model.put("initTasks", collector.get_initTasks());
        } catch (Exception ex) {
            _errors.add(new ActionError(-1, null, "com_intalio_bpms_workflow_tasks_retrieve_error", null, ActionError.getStackTrace(ex), null, null));
            _log.error("Error while retrieving task list", ex);
        }

        model.put("participantToken", token);
        model.put("currentUser", user);
        model.put("refreshTime", Configuration.getInstance().getRefreshTime());
        BPMS_DESCRIPTOR_PARSER.addBpmsBuildVersionsPropertiesToMap(model);
    }
}
