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
@NamedQueries( { 
    @NamedQuery(name = Task.FIND_BY_ID, query = "select m from Task m where m._id=?1", hints = { @QueryHint(name = "openjpa.hint.OptimizeResultCount", value = "1") })
}
)
//@NamedNativeQueries({
//    @NamedNativeQuery(name = Task._FIND_BY_USERS, query = Task.FIND_BY_USERS),
//    @NamedNativeQuery(name = Task._FIND_BY_ROLES, query = Task.FIND_BY_ROLES),
//    @NamedNativeQuery(name = Task._FIND_BY_USER_AND_ROLES, query = Task.FIND_BY_USER_AND_ROLES)
//})
    
public abstract class Task extends BaseRestrictedEntity {

	public static final String FIND_BY_ID = "find_by_id";
	public static final String FIND_BY_USER = "find_by_user";
	public static final String _FIND_BY_USERS = "find_by_user";
	public static final String _FIND_BY_ROLES = "find_by_roles";
	public static final String _FIND_BY_USER_AND_ROLES = "find_by_user_and_roles";

	public static final String FIND_BY_USERS = "SELECT * FROM TASKS m WHERE m.USERS IN (SELECT SET_ID FROM BACKING_SET WHERE AUTH_ID IN  {0})";
	public static final String FIND_BY_ROLES = "SELECT * FROM TASKS m WHERE m.ROLES IN (SELECT SET_ID FROM BACKING_SET WHERE AUTH_ID IN  {0})";
	
	public static final String FIND_BY_USER_AND_ROLES = "SELECT * from TASKS m WHERE (m.ROLES IN (SELECT SET_ID FROM BACKING_SET WHERE AUTH_ID IN  {0})) OR (m.USERS IN (SELECT SET_ID FROM BACKING_SET WHERE AUTH_ID IN  {1}))";
	
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

	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Task))
			return false;
		Task t = (Task) o;
		boolean b = _id.equalsIgnoreCase(t.getID());
		b &= _formURL.equals(t._formURL);
		b &= _description.equals(t._description);
		b &= _creationDate.equals(t._creationDate);
		b &= bothNullOrEqual(_actionACLs,t._actionACLs);
		b &= _userOwners.equals(t._userOwners);
		b &= _roleOwners.equals(_roleOwners);
		return b;
	}

	boolean bothNullOrEqual(Object o1, Object o2) {
		return o1 == null && o2 == null || o1.equals(o2);
	}

	public Task(String id, URI formURL) {
		this.setID(id);
		this.setFormURL(formURL);
	}

	public Task(String id, String formURL) {
		this.setID(id);
		this.setFormURLFromString(formURL);
	}

	public boolean equalsTask(Task rhs) {
		if (rhs == null) {
			return false;
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
			throw new InvalidTaskException("Form URL is not valid", e);
		}
	}

	public void authorizeActionForUser(String action, String user) {
		ACL acl = getACLs(action);
		acl.users.add(user);
	}

	public void authorizeActionForRole(String action, String role) {
		ACL acl = getACLs(action);
		acl.roles.add(role);
	}

	public void authorizeActionForUsers(String action, AuthIdentifierSet users) {
		ACL acl = getACLs(action);
		acl.users.addAll(users);
	}

	public void authorizeActionForRoles(String action, AuthIdentifierSet roles) {
		ACL acl = getACLs(action);
		acl.roles.addAll(roles);
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
			return acl.users;
		return new AuthIdentifierSet();
	}

	public AuthIdentifierSet getAuthorizedRoles(String action) {
		ACL acl = _actionACLs.get(action);
		if (acl != null)
			return acl.roles;
		return new AuthIdentifierSet();
	}

	public boolean isAuthorizedAction(UserRoles user, String action) {
		// Note: Action is authorized if there's no ACL provided (default)
		ACL acl = _actionACLs.get(action);
		if (acl == null)
			return true;
		if (acl.users.isEmpty() && acl.roles.isEmpty())
			return true;
		if (acl.users.contains(user.getUserID()))
			return true;
		if (acl.roles.intersects(user.getAssignedRoles()))
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
