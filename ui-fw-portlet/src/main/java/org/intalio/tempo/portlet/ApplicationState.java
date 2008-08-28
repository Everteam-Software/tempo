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

import java.io.Serializable;

import javax.portlet.PortletRequest;

import org.intalio.tempo.web.User;

public abstract class ApplicationState implements Serializable {
    private static final long serialVersionUID = -7511327773807678214L;

    public static final String PARAMETER_NAME = "APPLICATION_STATE";

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
     * A secure random number issued after login to avoid re-authentication for every request
     */
    private String _secureRandom;

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

    
    public String getSecureRandom() {
        return _secureRandom;
    }
    
    
    public void setSecureRandom(String random) {
        _secureRandom = random;
    }
    
    
    public static <T extends ApplicationState> T getCurrentInstance(PortletRequest request) {
        T state = (T) request.getPortletSession().getAttribute(PARAMETER_NAME);
        return state;
    }

    public static void setCurrentInstance(PortletRequest request, ApplicationState state) {
        request.getPortletSession().setAttribute(PARAMETER_NAME, state);
    }
    
}
