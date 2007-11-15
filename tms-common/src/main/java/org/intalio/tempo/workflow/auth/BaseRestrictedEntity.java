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

public abstract class BaseRestrictedEntity implements IRestrictedEntity {

    private AuthIdentifierSet _userOwners = new AuthIdentifierSet();

    private AuthIdentifierSet _roleOwners = new AuthIdentifierSet();

    protected BaseRestrictedEntity() {
    }

    public AuthIdentifierSet getUserOwners() {
        return _userOwners;
    }

    public AuthIdentifierSet getRoleOwners() {
        return _roleOwners;
    }

    public boolean isAvailableTo(UserRoles credentials) {
        for (String userOwner : _userOwners) {
            if (userOwner.equals(credentials.getUserID()))
                return true;
        }
        return _roleOwners.intersects(credentials.getAssignedRoles());
    }
}
