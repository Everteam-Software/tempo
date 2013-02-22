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
import java.util.Map;

import javax.servlet.http.HttpServletRequestWrapper;

import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.Constants;
import org.intalio.tempo.uiframework.UIFWApplicationState;
import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.versions.BpmsDescriptorParser;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.controller.Action;
import org.intalio.tempo.web.controller.ActionError;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("unchecked")
public class TasksAction extends Action {
    private static final BpmsDescriptorParser BPMS_DESCRIPTOR_PARSER = new BpmsDescriptorParser();
    private static final Logger _log = LoggerFactory.getLogger(TasksAction.class);

    @Override
    public ModelAndView execute() {
        if (Boolean.valueOf(_request.getParameter("update")).booleanValue()) {
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
        final String userName = state.getCurrentUser().getDisplayName();
        try {
	        if (Boolean.valueOf(_request.getParameter("update")).booleanValue()) {	
		        	TasksCollector collector = getTaskCollector(user, token);
		            model.put("tasks", collector.retrieveTasks());		        
			}
            model.put("isWorkflowAdmin", state.getCurrentUser().isWorkFlowAdmin());
        } catch (Exception ex) {
            _errors.add(new ActionError(-1, null, "com_intalio_bpms_workflow_tasks_retrieve_error", null, ActionError.getStackTrace(ex), null, null));
            _log.error("Error while retrieving task list", ex);
        }
		
        model.put("participantToken", token);
        model.put("currentUser", user);
        model.put("currentUserName", userName);
        model.put("userRoles", state.getCurrentUser().getRoles());
        model.put("refreshTime", Configuration.getInstance().getRefreshTime());
        model.put("sessionTimeout", Configuration.getInstance().getSessionTimeout());   
        

        List<String> newColumnList=getCustomColumns(token); // Call the operation that returns the distinct list of custom column from DB.
//        newColumnList.add("id");
        
        model.put("newColumnList",newColumnList);
        BPMS_DESCRIPTOR_PARSER.addBpmsBuildVersionsPropertiesToMap(model);
    }
    
    private List<String> getCustomColumns(String token){
        List<String> customColumns = new ArrayList<String>();
        String _endpoint = Configuration.getInstance().getServiceEndpoint();
        final String endpoint = URIUtils.resolveURI(_request, _endpoint);
        ITaskManagementService taskManager = Configuration.getInstance().getTmsFactory().getService(endpoint, token);
        try {
            customColumns = taskManager.getCustomColumns();
        } catch (AuthException e) {
            _log.debug( "Not a valid token" + e);
        }
        List<String> customColumnsLowerCase = new ArrayList<String>();
        for(String customColumn : customColumns){
            customColumnsLowerCase.add(customColumn.toLowerCase());
        }
        
        return customColumnsLowerCase;
    }
}
