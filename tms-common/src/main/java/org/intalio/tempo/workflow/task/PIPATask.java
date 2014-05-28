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
import java.net.URISyntaxException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.traits.InitTask;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.w3c.dom.Document;

/**
 * Init task
 * 
 */
@Entity
@Table(name = "tempo_pipa")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@NamedQueries( { @NamedQuery(name = PIPATask.FIND_BY_URL, query = "select DISTINCT T FROM PIPATask T where T._processEndpoint=(?)") })
public class PIPATask extends Task implements InitTask, ITaskWithOutput {

    public static final String FIND_BY_URL = "find_by_url";

    @Column(name = "init_message")
    private String _initMessageNamespaceURI;

    @Column(name = "init_soap")
    private String _initOperationSOAPAction;

    @Column(name = "process_endpoint")
    private String _processEndpoint;
    
    @Column(name = "process_state")
    @Enumerated(EnumType.ORDINAL)
    private PIPATaskState _processState=PIPATaskState.READY;  

    @Transient
    private String output;
    
    public PIPATask() {
        super();
    }

    public PIPATask(String id, URI formURL) {
        super(id, formURL);
    }

    public PIPATask(String id, String formURL) {
        super(id, formURL);
    }

    public PIPATask(String id, URI formURL, URI processEndpoint, URI initMessageNamespaceURI, String initOperationSOAPAction) {
        super(id, formURL);
        setProcessEndpoint(processEndpoint);
        _initMessageNamespaceURI = initMessageNamespaceURI.toString();
        _initOperationSOAPAction = initOperationSOAPAction;
    }

    public URI getInitMessageNamespaceURI() {
        if (this._initMessageNamespaceURI == null) {
            throw new IllegalStateException("The required property initMessageNamespaceURI is not set.");
        }
        URI initMessageNamespaceURI = URI.create(_initMessageNamespaceURI);
        return initMessageNamespaceURI;
    }

    public void setInitMessageNamespaceURI(URI initMessageNamespaceURI) {
        if (initMessageNamespaceURI == null) {
            throw new RequiredArgumentException("initMessageNamespaceURI");
        }
        _initMessageNamespaceURI = initMessageNamespaceURI.toString();
    }

    public String getInitOperationSOAPAction() {
        if (_initOperationSOAPAction == null) {
            throw new IllegalStateException("The required property initOperationSOAPAction is not set.");
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
            throw new IllegalStateException("The required property processEndpoint is not set.");
        }
        return URI.create(_processEndpoint);
    }

    public void setProcessEndpointFromString(String processEndpoint) {
        _processEndpoint = convertToFieldURI(processEndpoint).toString();
    }

    public void setProcessEndpoint(URI processEndpoint) {
        if (processEndpoint != null)
            _processEndpoint = processEndpoint.toString();
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
        return (getID() != null) && (getFormURL() != null) && (_processEndpoint != null) && (getFormURL() != null) && (_initOperationSOAPAction != null)
                        && (getDescription() != null) && (getUserOwners() != null) && (getRoleOwners() != null);
    }

    /**
     * Normalizes an array of auth (user or role) identifiers, by replacing all
     * allowed delimeter characters (the forward slash, the backslash and the
     * period) with a single delimeter character (the backslash).
     * 
     * @param sourceIdentifiers
     *            An array of auth (user or role) identifiers, such as
     *            "group&#0092;user", "group/user"
     * @return The array of the same length which contains the same identifiers,
     *         normalized, such as "group&#0092;user".
     */
    private AuthIdentifierSet normalizeAuthIdentifiers(String[] sourceIdentifiers) {
        if (sourceIdentifiers == null)
            return null;
        AuthIdentifierSet set = new AuthIdentifierSet();
        for (int i = 0; i < sourceIdentifiers.length; ++i)
            // set.add(sourceIdentifiers[i].replace('/', '\\').replace('.',
            // '\\'));
            set.add(sourceIdentifiers[i].replace('/', '\\'));
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
        setRoleOwners(normalizeAuthIdentifiers(roleOwners));
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
        setUserOwners(normalizeAuthIdentifiers(userOwners));
    }

	public PIPATaskState getProcessState() {
		return _processState;
	}
	public void setProcessState(PIPATaskState processState) {
		this._processState = processState;
	}

	@Override
	public Document getOutput() {
		return XmlTooling.deserializeDocument(output);
	}
	
	@Override
	public void setOutput(Document outputDocument) {
	    if (outputDocument == null) {
	        throw new RequiredArgumentException("outputDocument");
	    }
	    output = XmlTooling.serializeDocument(outputDocument);
	}
	
	@Override
	public void setOutput(String output) {
		this.output = output;		
	}
}
