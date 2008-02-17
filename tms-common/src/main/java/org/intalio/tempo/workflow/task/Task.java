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

package org.intalio.tempo.workflow.task;

import java.net.URI;
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

import org.apache.openjpa.persistence.PersistentMap;
import org.intalio.tempo.workflow.auth.ACL;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.BaseRestrictedEntity;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

@Entity
@Table(name = "TASKS")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries( { @NamedQuery(name = Task.FIND_BY_ID, query = "select m from Task m where m._id=?1", hints = { @QueryHint(name = "openjpa.hint.OptimizeResultCount", value = "1") }) })
public abstract class Task extends BaseRestrictedEntity {

    public static final String FIND_BY_ID = "find_by_id";

    @Column(name = "internal_id")
    @Basic
    private int _internalId;

    @Column(name = "tid")
    @Basic
    private String _id;

    @Column(name = "description")
    @Basic
    private String _description = "";

    @Column(name = "creation_date")
    @Basic
    private Date _creationDate = new Date();

    @Basic
    @Column(name = "form_url")
    private String _formURL;

    @PersistentMap(keyCascade = CascadeType.ALL, elementCascade = CascadeType.ALL)
    @MapKey(name = "action")
    private Map<String, ACL> _actionACLs = new HashMap<String, ACL>();

    public Task() {
        _actionACLs = new HashMap<String, ACL>();
    }

    @Override
    public boolean equals(Object o) {
        throw new RuntimeException("Do not use me for testing");
        // if (!(o instanceof Task))
        // return false;
        // Task t = (Task) o;
        // boolean b = _id.equalsIgnoreCase(t.getID());
        // b &= _formURL.equals(t._formURL);
        // b &= _description.equals(t._description);
        // b &= _creationDate.equals(t._creationDate);
        // b &= bothNullOrEqual(_actionACLs, t._actionACLs);
        // b &= _userOwners.equals(t._userOwners);
        // b &= _roleOwners.equals(_roleOwners);
        // return b;
    }

    //
    // boolean bothNullOrEqual(Object o1, Object o2) {
    // return o1 == null && o2 == null || o1.equals(o2);
    // }

    public Task(String id, URI formURL) {
        this();
        this.setID(id);
        this.setFormURL(formURL);
    }

    public Task(String id, String formURL) {
        this();
        this.setID(id);
        this.setFormURLFromString(formURL);
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
        return URI.create(_formURL);
    }

    public String getFormURLAsString() {
        return _formURL.toString();
    }

    public void setFormURL(URI formURL) {
        if (formURL == null) {
            throw new RequiredArgumentException("formURL");
        }
        _formURL = formURL.toString();
    }

    public void setFormURLFromString(String formURL) {
        _formURL = convertToFieldURI(formURL).toString();
    }

    protected URI convertToFieldURI(String fieldURL) {
        if (fieldURL == null) {
            throw new RequiredArgumentException("formURL");
        }
        try {
            return new URI(fieldURL);
        } catch (Exception e) {
            // we can use URI.create here, but we want to check for the validity
            // of the uri whenever we set it
            throw new InvalidTaskException("Form URL is not valid", e);
        }
    }

    public void authorizeActionForUser(String action, String user) {
        ACL acl = getACLs(action);
        acl.getUsers().add(user);
    }

    public void authorizeActionForRole(String action, String role) {
        ACL acl = getACLs(action);
        acl.getRoles().add(role);
    }

    public void authorizeActionForUsers(String action, AuthIdentifierSet users) {
        ACL acl = getACLs(action);
        acl.getUsers().addAll(users);
    }

    public void authorizeActionForRoles(String action, AuthIdentifierSet roles) {
        ACL acl = getACLs(action);
        acl.getRoles().addAll(roles);
    }

    private ACL getACLs(String action) {
        ACL acl = _actionACLs.get(action);
        if (acl == null) {
            acl = new ACL(action);
            _actionACLs.put(action, acl);
        }
        return acl;
    }

    public AuthIdentifierSet getAuthorizedUsers(String action) {
        ACL acl = _actionACLs.get(action);
        if (acl != null)
            return acl.getUsers();
        return new AuthIdentifierSet();
    }

    public AuthIdentifierSet getAuthorizedRoles(String action) {
        ACL acl = _actionACLs.get(action);
        if (acl != null)
            return acl.getRoles();
        return new AuthIdentifierSet();
    }

    public boolean isAuthorizedAction(UserRoles user, String action) {
        // Note: Action is authorized if there's no ACL provided (default)
        ACL acl = _actionACLs.get(action);
        if (acl == null)
            return true;
        if (acl.getUsers().isEmpty() && acl.getRoles().isEmpty())
            return true;
        if (acl.getUsers().contains(user.getUserID()))
            return true;
        if (acl.getRoles().intersects(user.getAssignedRoles()))
            return true;
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
