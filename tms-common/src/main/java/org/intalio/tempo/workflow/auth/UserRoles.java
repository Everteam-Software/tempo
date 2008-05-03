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
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.auth;

import org.intalio.tempo.workflow.util.RequiredArgumentException;

public class UserRoles {
    private String _userID;
    private AuthIdentifierSet _assignedRoles;

    public UserRoles(String userID, String[] assignedRoles) {
        this(userID, new AuthIdentifierSet(assignedRoles));
    }

    public UserRoles(String userID, AuthIdentifierSet assignedRoles) {
        if (userID == null) throw new RequiredArgumentException("userID");
        if (assignedRoles == null) throw new RequiredArgumentException("assignedRoles");
        
        _userID = AuthIdentifierNormalizer.normalizeAuthIdentifier(userID);
        _assignedRoles = assignedRoles;
    }

    public String getUserID() {
        return _userID;
    }

    public AuthIdentifierSet getAssignedRoles() {
        return _assignedRoles;
    }
    
    @Override
    public String toString() {
        return "UserRoles{user="+_userID+",roles="+_assignedRoles+"}";
    }
}
