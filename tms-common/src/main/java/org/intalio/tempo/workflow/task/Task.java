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
import java.util.Collection;
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
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.intalio.tempo.workflow.auth.ACL;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.BaseRestrictedEntity;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

@Entity
@Table(name = "tempo_task")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries( { @NamedQuery(name = Task.FIND_BY_ID, query = "select m from Task m where m._id=?1", hints = { @QueryHint(name = "openjpa.hint.OptimizeResultCount", value = "1") }),
                 @NamedQuery(name = Task.FIND_BY_ROLE_USER, query = "select DISTINCT m from Task m where m._userOwners in (?1) or m._roleOwners in (?2)"),
                 @NamedQuery(name = Task.FIND_BY_USER, query = "select DISTINCT m from Task m where m._userOwners in (?1)"),
                 @NamedQuery(name = Task.FIND_BY_ROLE, query = "select DISTINCT m from Task m where m._roleOwners in (?1)")
  })
public abstract class Task extends BaseRestrictedEntity {

    public static final String FIND_BY_ID = "find_by_id";
    public static final String FIND_BY_ROLE_USER = "find_by_role_user";
    public static final String FIND_BY_USER = "find_by_user";
    public static final String FIND_BY_ROLE = "find_by_role";
    
    @Column(name = "internal_id")
    @Basic
    private int _internalId;

    @Column(name = "taskid")
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
    @ContainerTable(name="tempo_acl_map")
    @MapKey(name = "action")
    private Map<String, ACL> _actionACLs = new HashMap<String, ACL>();

    public Task() {
        super();
        _actionACLs = new HashMap<String, ACL>();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Task) && ((Task) o).getID().equalsIgnoreCase(_id);
    }

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

    public void setID(String id) {
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
        acl.getUserOwners().add(user);
    }

    public void authorizeActionForRole(String action, String role) {
        ACL acl = getACLs(action);
        acl.getRoleOwners().add(role);
    }

    public void authorizeActionForUsers(String action, AuthIdentifierSet users) {
        ACL acl = getACLs(action);
        acl.getUserOwners().addAll(users);
    }

    public void authorizeActionForRoles(String action, AuthIdentifierSet roles) {
        ACL acl = getACLs(action);
        acl.getRoleOwners().addAll(roles);
    }

    private ACL getACLs(String action) {
        ACL acl = _actionACLs.get(action);
        if (acl == null) {
            acl = new ACL(action);
            _actionACLs.put(action, acl);
        }
        return acl;
    }

    public Collection<String> getAuthorizedUsers(String action) {
        ACL acl = _actionACLs.get(action);
        if (acl != null)
            return acl.getUserOwners();
        return new AuthIdentifierSet();
    }

    public Collection<String> getAuthorizedRoles(String action) {
        ACL acl = _actionACLs.get(action);
        if (acl != null)
            return acl.getRoleOwners();
        return new AuthIdentifierSet();
    }

    public boolean isAuthorizedAction(UserRoles user, String action) {
        // Note: Action is authorized if there's no ACL provided (default)
        ACL acl = _actionACLs.get(action);
        return acl==null ? true : acl.isAuthorizedAction(user, action);
    }

    public Set<String> getAuthorizedActions() {
        return _actionACLs.keySet();
    }

    @Override
	public String toString() {
		return "Task [_actionACLs=" + _actionACLs + ", _creationDate="
				+ _creationDate + ", _description=" + _description
				+ ", _formURL=" + _formURL + ", _id=" + _id + ", _internalId="
				+ _internalId + "]";
	}

}
