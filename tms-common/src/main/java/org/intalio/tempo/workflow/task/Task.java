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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentMap;
import org.intalio.tempo.workflow.auth.ACL;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.BaseRestrictedEntity;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

@Entity
@Table(name="tasks")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(
            name=Task.FIND_BY_ID, 
            query="select m from Task m where m._id=?1", 
            hints={ @QueryHint  (name="openjpa.hint.OptimizeResultCount", value="1")})
    })
public abstract class Task extends BaseRestrictedEntity {
    
    public static final String FIND_BY_ID ="find_by_id";
    public static final String FIND_BY_USER ="find_by_user";
    
    @Column(name="internal_id")
    @Basic
    private int _internalId;

    @Column(name="tid")
    @Basic
    private String _id;

    @Column(name="description")
    @Basic
    private String _description = "";

    @Column(name="creation_date")
    @Basic
    private Date _creationDate = new Date();

    @Persistent
    @Factory("URI.create")
    @Externalizer("toString")
    @Column(name="form_url")
    private URI _formURL;

    @PersistentMap(keyCascade=CascadeType.ALL, elementCascade=CascadeType.ALL)
    @MapKey(name="action")
    private Map<String,ACL> _actionACLs = new HashMap<String,ACL>();
    
    public Task() {
        
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Task)) return false;
        Task t = (Task)o;
        boolean b = _id.equalsIgnoreCase(t.getID());
        b = b && (_formURL.equals(t.getFormURL()));
        b = b && (_description.equals(t.getDescription()));
        b = b && (_creationDate.equals(t.getCreationDate()));
        b = b && (_actionACLs.keySet().equals(t.getAuthorizedActions()));
        return b;
    }



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
            acl = new ACL(action);
            _actionACLs.put(action, acl);
        }
        acl.users.add(user);
    }
    
    public void authorizeActionForRole(String action, String role) {
        ACL acl = _actionACLs.get(action);
        if (acl == null) {
            acl = new ACL(action);
            _actionACLs.put(action, acl);
        }
        acl.roles.add(role);
    }
    
    public AuthIdentifierSet getAuthorizedUsers(String action) {
        ACL acl = _actionACLs.get(action);
        if (acl != null) return acl.users;
        return new AuthIdentifierSet();
    }

    public AuthIdentifierSet getAuthorizedRoles(String action) {
        ACL acl = _actionACLs.get(action);
        if (acl != null) return acl.roles;
        return new AuthIdentifierSet();
    }

    public boolean isAuthorizedAction(UserRoles user, String action) {
        // Note: Action is authorized if there's no ACL provided (default)
        ACL acl = _actionACLs.get(action);
        if (acl == null) return true;
        if (acl.users.isEmpty() && acl.roles.isEmpty()) return true;
        if (acl.users.contains(user.getUserID())) return true;
        if (acl.roles.intersects(user.getAssignedRoles())) return true;
        return false;
    }

    public Set<String> getAuthorizedActions() {
        return _actionACLs.keySet();
    }
    
    @Override
    public String toString() {
        return "Workflow Task " + _id;
    }
    
}
