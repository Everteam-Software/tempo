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

import org.intalio.tempo.workflow.util.RequiredArgumentException;

public class PIPATask extends Task {
    private URI _processEndpoint;

    private URI _initMessageNamespaceURI;

    private String _initOperationSOAPAction;

    public PIPATask(String id,
                    URI formURL,
                    URI processEndpoint,
                    URI initMessageNamespaceURI,
                    String initOperationSOAPAction) {
        super(id, formURL);
        _processEndpoint = processEndpoint;
        _initMessageNamespaceURI = initMessageNamespaceURI;
        _initOperationSOAPAction = initOperationSOAPAction;
    }

    public URI getInitMessageNamespaceURI() {
        if (_initMessageNamespaceURI == null) {
            throw new IllegalStateException("The required property initMessageNamespaceURI is not set.");
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
        return _processEndpoint;
    }

    public void setProcessEndpoint(URI processEndpoint) {
        if (processEndpoint == null) {
            throw new RequiredArgumentException("processEndpoint");
        }
        _processEndpoint = processEndpoint;
    }

}
