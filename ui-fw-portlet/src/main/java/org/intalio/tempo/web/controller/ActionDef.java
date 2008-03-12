/**
 * Copyright (C) 2006, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id$
 * $Log$
 */
package org.intalio.tempo.web.controller;

import java.util.Collection;
import java.util.LinkedList;

import org.intalio.tempo.web.User;

public class ActionDef {
    private String _actionName;
    private String _actionClass;
    private Collection<String> _grantedRoles = new LinkedList<String>();
    
    /**
     * @return Returns the actionClass.
     */
    public String getActionClass() {
        return _actionClass;
    }

    /**
     * @param actionClass The actionClass to set.
     */
    public void setActionClass(String actionClass) {
        _actionClass = actionClass;
    }

    /**
     * @return Returns the actionName.
     */
    public String getActionName() {
        return _actionName;
    }

    /**
     * @param actionName The actionName to set.
     */
    public void setActionName(String actionName) {
        _actionName = actionName;
    }

    /**
     * @return Returns the grantedRoles.
     */
    public Collection<String> getGrantedRoles() {
        return _grantedRoles;
    }
    
    public void setGrantedRoles(Collection<String> grantedRoles) {
        _grantedRoles = grantedRoles;
    }

    public boolean isAuthorized(User user ) {
        for (String role : _grantedRoles) {
            if (user.hasRole(role)) {
                return true;
            }
        }
        return false;
    }
}
