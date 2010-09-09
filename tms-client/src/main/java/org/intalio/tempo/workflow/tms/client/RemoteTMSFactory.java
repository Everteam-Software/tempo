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

package org.intalio.tempo.workflow.tms.client;

import org.intalio.tempo.workflow.tms.ITaskManagementServiceFactory;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public class RemoteTMSFactory implements ITaskManagementServiceFactory {

    private String _endpoint;
    private String _participantToken;

    public RemoteTMSFactory(String endpoint, String participantToken) {
        if (endpoint == null) {
            throw new RequiredArgumentException("endpoint");
        }
        if (participantToken == null) {
            throw new RequiredArgumentException("participantToken");
        }
        _endpoint = endpoint;
        _participantToken = participantToken;
    }

    public ITaskManagementService getService() {
        return new RemoteTMSClient(_endpoint, _participantToken);
    }

}
