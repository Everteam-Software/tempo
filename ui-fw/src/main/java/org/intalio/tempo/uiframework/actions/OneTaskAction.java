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

import org.intalio.tempo.web.controller.Action;
import org.intalio.tempo.web.controller.ActionError;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.uiframework.Constants;
import org.intalio.tempo.uiframework.UIFWApplicationState;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.model.TaskHolder;
import org.springframework.web.servlet.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;


public class OneTaskAction extends Action {
    private static final Logger _log = LoggerFactory.getLogger(OneTaskAction.class);

    @Override
    public ModelAndView execute() {
        return new ModelAndView(Constants.ONE_TASK_VIEW, createModel());
    }

    public ModelAndView getErrorView() {
        return new ModelAndView(Constants.ONE_TASK_VIEW, createModel());
    }

    protected void fillModel(Map model) {
        final UIFWApplicationState state = ApplicationState.getCurrentInstance(new HttpServletRequestWrapper(_request));
        final String token = state.getCurrentUser().getToken();
        final String user = state.getCurrentUser().getName();
        try {
            TasksCollector collector = new TasksCollector(new HttpServletRequestWrapper(_request), user, token);
            model.put("taskform", collector.retrieveOneTask(_request.getParameter("id")).getFormManagerURL());
        } catch (Exception ex) {
            _errors.add(new ActionError(-1, null, "com_intalio_bpms_workflow_tasks_retrieve_error", null, ActionError.getStackTrace(ex), null, null));
            _log.error("Error while retrieving task list", ex);
        }
        model.put("participantToken", token);
        model.put("currentUser", user);
        model.put("refreshTime", Configuration.getInstance().getRefreshTime());
        model.put("sessionTimeout", Configuration.getInstance().getSessionTimeout());
    }

}
