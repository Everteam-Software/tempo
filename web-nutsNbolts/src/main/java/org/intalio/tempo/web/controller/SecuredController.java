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
package org.intalio.tempo.web.controller;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.sf.log.Log;

public class SecuredController extends AbstractController {

    private static final Logger LOG = LogManager.getLogger(SecuredController.class);

	protected ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {
		Log.trace("Entering ViewController.handleRenderRequestInternal()");
		ModelAndView modelAndView = new ModelAndView("tasks");
		String uname = (String)request.getAttribute("com.intalio.tempo.user");
		if (uname != null)
			Log.trace("GET THE USER IN SECURED CONTROLLER!:"+uname);
		Log.trace("Exiting ViewController.handleRenderRequestInternal() " + modelAndView);
		return modelAndView;
	}
	
	/*
    @Override
    protected final ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors)
            throws Exception {
        ModelAndView mav = null;//Constants.REDIRECTION_TO_LOGIN;
        //ApplicationState state = getApplicationState(request);
        //if (state != null) {
        //    User currentUser = state.getCurrentUser();
        //    if (currentUser != null) {
        String uname = (String)request.getSession().getAttribute("com.intalio.tempo.user");
        Log.trace("Secured Controller session attribue test user name:"+uname);
       
        
        Enumeration en = request.getSession().getAttributeNames();
		Log.trace("SecuredController Get all the session attribute");
		while(en.hasMoreElements()){
			Log.trace("Secured Controller:"+en.nextElement().toString());
		}
		
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
                
            //}
            //fillAuthorization(request, mav);
            //state.setPreviousAction(request.getRequestURL().toString());
        //}
        return mav;
    }
	*/
	
    /*
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
        ApplicationState state = ApplicationState.getCurrentInstance(request);
        if (state == null || state.getCurrentUser() == null) {
            return "UnknownUser";
        }
        return state.getCurrentUser().getName();
    }

    private ApplicationState getApplicationState(HttpServletRequest request) {
        ApplicationState state = ApplicationState.getCurrentInstance(request);
        if (state == null) {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            state = (ApplicationState) context.getBean("applicationState");
            try {
                state = state.getClass().newInstance();
            } catch (Exception e) {
                LOG.error("Unable to clone application state", e);
            }
            ApplicationState.setCurrentInstance(request, state);
        }
        return state;
    }
    */
}
