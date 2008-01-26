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

package org.intalio.tempo.workflow.task;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.BaseRestrictedEntity;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public abstract class Task extends BaseRestrictedEntity {

    private int _internalId;

    private String _id;

    private String _description = "";

    private Date _creationDate = new Date();
    
    private URI _formURL;
    
    private Map<String,ACL> _actionACLs = new HashMap<String,ACL>();
    
    public Task(String id, URI formURL) {
        this.setID(id);
        this.setFormURL(formURL);
    }

    public boolean equalsTask(Task rhs) {
        if (rhs == null) {
            throw new RequiredArgumentException("rhs");
        }
        return _id.equals(rhs._id) && _description.equals(rhs._description);
    }

    public String getID() {
        return _id;
    }

    private void setID(String id) {
        if (id == null) {
            throw new RequiredArgumentException("id");
        }
        _id = id;
    }

    public int getInternalId() {
        return _internalId;
    }

    public void setInternalID(int internalId) {
        _internalId = internalId;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new RequiredArgumentException("description");
        }
        _description = description;
    }

    public Date getCreationDate() {
        return _creationDate;
    }

    public void setCreationDate(Date date) {
        if (date == null) {
            throw new RequiredArgumentException("date");
        }
        _creationDate = date;
    }

    public URI getFormURL() {
        return _formURL;
    }

    public void setFormURL(URI formURL) {
        if (formURL == null) {
            throw new RequiredArgumentException("formURL");
        }
        _formURL = formURL;
    }

    public void setFormURL(String formURL) throws URISyntaxException {
        if (formURL == null) {
            throw new RequiredArgumentException("formURL");
        }
        _formURL = new URI(formURL);
    }

    public void authorizeActionForUser(String action, String user) {
        ACL acl = _actionACLs.get(action);
        if (acl == null) {
            acl = new ACL();
            _actionACLs.put(action, acl);
        }
        acl._users.add(user);
    }
    
    public void authorizeActionForRole(String action, String role) {
        ACL acl = _actionACLs.get(action);
        if (acl == null) {
            acl = new ACL();
            _actionACLs.put(action, acl);
        }
        acl._roles.add(role);
    }
    
    public AuthIdentifierSet getAuthorizedUsers(String action) {
        ACL acl = _actionACLs.get(action);
        if (acl != null) return acl._users;
        return new AuthIdentifierSet();
    }

    public AuthIdentifierSet getAuthorizedRoles(String action) {
        ACL acl = _actionACLs.get(action);
        if (acl != null) return acl._roles;
        return new AuthIdentifierSet();
    }

    public boolean isAuthorizedAction(UserRoles user, String action) {
        // Note: Action is authorized if there's no ACL provided (default)
        ACL acl = _actionACLs.get(action);
        if (acl == null) return true;
        if (acl._users.isEmpty() && acl._roles.isEmpty()) return true;
        if (acl._users.contains(user.getUserID())) return true;
        if (acl._roles.intersects(user.getAssignedRoles())) return true;
        return false;
    }

    public Set<String> getAuthorizedActions() {
        return _actionACLs.keySet();
    }
    
    @Override
    public String toString() {
        return "Workflow Task " + _id;
    }
    
    public static class ACL {
        public AuthIdentifierSet _users = new AuthIdentifierSet();
        public AuthIdentifierSet _roles = new AuthIdentifierSet();
    }


}
