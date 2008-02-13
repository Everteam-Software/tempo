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
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.task;

import java.net.URI;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;

import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

@Entity
@NamedQueries( { @NamedQuery(name = PIPATask.FIND_BY_URL, query = "select m from PIPATask m where m._formURL=?1", hints = { @QueryHint(name = "openjpa.hint.OptimizeResultCount", value = "1") }) })
public class PIPATask extends Task {

	public static final String FIND_BY_URL = "find_by_url";

	@Persistent
	@Factory("URI.create")
	@Externalizer("toString")
	@Column(name = "process_endpoint")
	private URI _processEndpoint;

	@Persistent
	@Factory("URI.create")
	@Externalizer("toString")
	@Column(name = "init_message")
	private URI _initMessageNamespaceURI;

	@Basic
	@Column(name = "init_soap")
	private String _initOperationSOAPAction;

	@Persistent
	@Column(name = "form_namespace")
	private String _formNamespace;

	public PIPATask() {
		super();
	}

	public PIPATask(String id, URI formURL) {
		super(id, formURL);
	}

	public PIPATask(String id, String formURL) {
		super(id, formURL);
	}

	public PIPATask(String id, URI formURL, URI processEndpoint,
			URI initMessageNamespaceURI, String initOperationSOAPAction) {
		super(id, formURL);
		_processEndpoint = processEndpoint;
		_initMessageNamespaceURI = initMessageNamespaceURI;
		_initOperationSOAPAction = initOperationSOAPAction;
	}

	public boolean equals(Object o) {
		if (!(o instanceof PIPATask))
			return false;
		PIPATask t = (PIPATask) o;
		boolean b = bothNullOrEqual(_initMessageNamespaceURI,_initMessageNamespaceURI);
		b &= bothNullOrEqual(_initOperationSOAPAction,t._initOperationSOAPAction);
		b &= bothNullOrEqual(_processEndpoint,t._processEndpoint);
		b &= super.equals(t);
		return b;
	}
	

	public URI getInitMessageNamespaceURI() {
		if (_initMessageNamespaceURI == null) {
			throw new IllegalStateException(
					"The required property initMessageNamespaceURI is not set.");
		}
		return _initMessageNamespaceURI;
	}

	public void setInitMessageNamespaceURI(URI initMessageNamespaceURI) {
		if (initMessageNamespaceURI == null) {
			throw new RequiredArgumentException("initMessageNamespaceURI");
		}
		_initMessageNamespaceURI = initMessageNamespaceURI;
	}

	public String getInitOperationSOAPAction() {
		if (_initOperationSOAPAction == null) {
			throw new IllegalStateException(
					"The required property initOperationSOAPAction is not set.");
		}
		return _initOperationSOAPAction;
	}

	public void setInitOperationSOAPAction(String initOperationSOAPAction) {
		if (initOperationSOAPAction == null) {
			throw new RequiredArgumentException("initOperationSOAPAction");
		}
		_initOperationSOAPAction = initOperationSOAPAction;
	}

	public URI getProcessEndpoint() {
		if (_processEndpoint == null) {
			throw new IllegalStateException(
					"The required property processEndpoint is not set.");
		}
		return _processEndpoint;
	}

//	public void setProcessEndpoint(URI processEndpoint) {
//		if (processEndpoint == null) {
//			throw new RequiredArgumentException("processEndpoint");
//		}
//		_processEndpoint = processEndpoint;
//	}

	public void setProcessEndpoint(String processEndpoint) {
		_processEndpoint = convertToFieldURI(processEndpoint);
	}

	private static void fieldToString(StringBuilder builder, String caption,
			String value) {
		builder.append(caption);
		builder.append(": '");
		builder.append(value);
		builder.append("'\n");
	}

	/**
	 * Returns <code>true</code> if this instance has all necessary properties
	 * specified.<br />
	 * Note: most instance will be invalid after initialization.
	 * 
	 * @return <code>true</code> if this instance has all necessary properties
	 *         specified.
	 */
	public boolean isValid() {
		return (getID() != null) && (getFormURL() != null)
				&& (_processEndpoint != null) && (getFormURL() != null)
				&& (_initOperationSOAPAction != null)
				&& (getDescription() != null) && (getUserOwners() != null)
				&& (getRoleOwners() != null);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		fieldToString(builder, "ID", getID());
		try {
			fieldToString(builder, "Description", getDescription());
			fieldToString(builder, "Valid?", String.valueOf(this.isValid()));
			fieldToString(builder, "Total user owners", String
					.valueOf(getUserOwners().size()));
			for (String userOwner : getUserOwners()) {
				fieldToString(builder, "User owner", userOwner);
			}
			fieldToString(builder, "Total role owners", String
					.valueOf(getRoleOwners().size()));
			for (String roleOwner : getRoleOwners()) {
				fieldToString(builder, "Role owner", roleOwner);
			}
			fieldToString(builder, "Form URL", this.getFormURL().toString());
			fieldToString(builder, "Process endpoint", getProcessEndpoint()
					.toString());
			fieldToString(builder, "Form namespace", _formNamespace);
			fieldToString(builder, "Init SOAPAction", _initOperationSOAPAction);
		} catch (Exception e) {
			// some fields are not valid.
		}
		return builder.toString();
	}

	/**
	 * Returns the task form namespace.
	 * 
	 * @return The task form namespace.
	 */
	public String getFormNamespace() {
		return _formNamespace;
	}

	/**
	 * Sets the task form namespace.
	 * 
	 * @param formNamespace
	 *            The task form namespace.
	 */
	public void setFormNamespace(String formNamespace) {
		_formNamespace = formNamespace;
	}

	/**
	 * Normalizes an array of auth (user or role) identifiers, by replacing all
	 * allowed delimeter characters (the forward slash, the backslash and the
	 * period) with a single delimeter character (the backslash).
	 * 
	 * @param sourceIdentifiers
	 *            An array of auth (user or role) identifiers, such as
	 *            "group&#0092;user", "group/user", "group.user".
	 * @return The array of the same length which contains the same identifiers,
	 *         normalized, such as "group&#0092;user".
	 */
	private AuthIdentifierSet normalizeAuthIdentifiers(
			String[] sourceIdentifiers) {
		if (sourceIdentifiers == null) return null;
		AuthIdentifierSet set = new AuthIdentifierSet();
		for (int i = 0; i < sourceIdentifiers.length; ++i)
			set.add(sourceIdentifiers[i].replace('/', '\\').replace('.', '\\'));
		return set;
	}

	/**
	 * Sets the role owners of the task. <br />
	 * Note: all allowed delimeter characters (the forward slash, the backslash
	 * and the period) are replaced with a unified delimeter -- the backslash.
	 * 
	 * @param roleOwners
	 *            An array of role identifiers, such as "group&#0092;role",
	 *            "group/role" or "group.role".
	 */
	public void setRoleOwners(String[] roleOwners) {
		_roleOwners = normalizeAuthIdentifiers(roleOwners);
	}

	/**
	 * Sets the ruser owners of the task. <br />
	 * Note: all allowed delimeter characters (the forward slash, the backslash
	 * and the period) are replaced with a unified delimeter -- the backslash.
	 * 
	 * @param userOwners
	 *            An array of user identifiers, such as "group&#0092;user",
	 *            "group/user" or "group.user".
	 */
	public void setUserOwners(String[] userOwners) {
		_userOwners = normalizeAuthIdentifiers(userOwners);
	}

}
