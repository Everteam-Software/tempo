/**
 * Copyright (C) 2005, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id$
 * $Log$
 */
package org.intalio.tempo.web;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class ApplicationState implements Serializable {
    private static final long serialVersionUID = -7511327773807678214L;

    private static final String PARAMETER_NAME = "APPLICATION_STATE";

    /**
     * User currently logged in.
     */
    private User _currentUser;

    /**
     * The action before login user entered. If user is not logged in, but requests to access secured page he will be
     * redirected to login page and when user logs succesfully, redirect to page.
     */
    private String _previousAction;

    /**
     * Returns user which is currently logged in.
     * 
     * @return User currently logged in.
     */
    public User getCurrentUser() {
        return _currentUser;
    }

    /**
     * Sets current user.
     */
    public void setCurrentUser(User currentUser) {
        _currentUser = currentUser;
    }

    /**
     * Set previous action
     */
    public void setPreviousAction(String actionURL) {
        _previousAction = actionURL;
    }

    /**
     * Get previous action
     */
    public String getPreviousAction() {
        return _previousAction;
    }

    public static <T extends ApplicationState> T getCurrentInstance(HttpServletRequest request) {
        HttpSession session = request.getSession();
        T state = (T) session.getAttribute(PARAMETER_NAME);
        return state;
    }

    public static void setCurrentInstance(HttpServletRequest request, ApplicationState state) {
        HttpSession session = request.getSession();
        session.setAttribute(PARAMETER_NAME, state);
    }
    
}
