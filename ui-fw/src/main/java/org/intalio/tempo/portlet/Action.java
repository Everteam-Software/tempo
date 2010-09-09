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
package org.intalio.tempo.portlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.pluto.wrappers.PortletRequestWrapper;
import org.intalio.tempo.web.controller.ActionError;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.ModelAndView;

public abstract class Action<T> {

    protected HttpServletRequestWrapper _request;

    protected PortletResponse _response;

    protected BindException _bindErrors;

    protected final List<ActionError> _errors = new ArrayList<ActionError>();

    protected T _command;

    /** 
     * User must have one of the required roles in order to perform the action
     */
    protected String[] _requiredRoles = new String[0];
    
    public Action() {
    }

    /**
     * Returns the errors
     */
    public BindException getBindErrors() {
        return _bindErrors;
    }

    /**
     * Returns the request.
     */
    public ServletRequest getRequest() {
        return _request.getRequest();
    }

    /**
     * Returns the response.
     */
    public PortletResponse getResponse() {
        return _response;
    }

    public boolean validate() {
        return true;
    }

    public abstract ModelAndView execute();

    public abstract ModelAndView getErrorView();

    public void beforeValidation() {
    }

    public void afterValidation() {
    }

    public void beforeExecute() {
    }

    public void afterExecute() {
    }

    public T getCommand() {
        return _command;
    }

    public void setCommand(T command) {
        _command = command;
    }

    /**
     * Set the bind errors
     */
    public void setBindErrors(BindException errors) {
        _bindErrors = errors;
    }

    /**
     * Set the request
     */
    public void setRequest(PortletRequest request) {
        _request = new PortletRequestWrapper(request);
    }

    /**
     * Set the response
     */
    public void setResponse(PortletResponse response) {
        _response = response;
    }

    public final Map<String, Object> createModel() {
        Map<String, Object> model = _bindErrors.getModel();
        model.put("errors", _errors);
        fillModel(model);
        return model;
    }

    protected void fillModel(Map<String, Object> model) {
    }

    /**
     * Returns the errors.
     */
    public List<ActionError> getErrors() {
        return _errors;
    }

    /*
    public boolean isGetRequest() {
        return "GET".equals(_request.getMethod());
    }
	*/
    
    public final ModelAndView doExecution() {
        beforeValidation();
        boolean valid = validate();
        afterValidation();

        if (!valid) {
            return getErrorView();
        }
        
        beforeExecute();
        ModelAndView mnv = execute();
        afterExecute();
        return mnv;
    }
    
    
    public void setRequiredRoles(String[] roles) {
        if (roles == null) {
            roles = new String[0];
        }
        _requiredRoles = roles;
    }
    
    public String[] getRequiredRoles() {
        return _requiredRoles;
    }
}
