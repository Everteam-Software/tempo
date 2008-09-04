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
 *
 * $Id: XFormsManager.java 2764 2006-03-16 18:34:41Z ozenzin $
 * $Log:$
 */
package org.intalio.tempo.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.Constants;
import org.intalio.tempo.web.User;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class SecuredController extends UIController {
    private static final Logger LOG = LogManager.getLogger(SecuredController.class);

    @Override
    protected final ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors)
            throws Exception {
        ModelAndView mav = Constants.REDIRECTION_TO_LOGIN;
        ApplicationState state = getApplicationState(request);
        User currentUser = state.getCurrentUser();
        if (currentUser != null) {
            if (_defaultAction == null) {
                mav = securedShowForm(request, response, errors);
            } else {
                // Do default action
                Action<Object> action = instantiateDefaultAction();
                action.setRequest(request);
                action.setResponse(response);
                action.setCommand(getCommand(request));
                action.setBindErrors(errors);
                mav = action.doExecution();
            }
            
        }
        fillAuthorization(request, mav);
        state.setPreviousAction(request.getRequestURL().toString());
        return mav;
    }

    @Override
    protected final ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) throws Exception {
        ApplicationState state = getApplicationState(request);
        User currentUser = state.getCurrentUser();
        if (currentUser != null) {
            return super.processFormSubmission(request, response, command, errors);
        }
        // save request position 
        state.setPreviousAction(request.getRequestURL().toString());
        // redirect to login page
        return Constants.REDIRECTION_TO_LOGIN;
    }

    protected ModelAndView securedShowForm(HttpServletRequest request, HttpServletResponse response,
            BindException errors) throws Exception {
        return null;
    }

    public static String getCurrentUserName(HttpServletRequest request) {
        ApplicationState state = ApplicationState.getCurrentInstance(new HttpServletRequestWrapper(request));
        if (state == null || state.getCurrentUser() == null) {
            return "UnknownUser";
        }
        return state.getCurrentUser().getName();
    }

}
