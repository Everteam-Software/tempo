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

public class RemoteTMSException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public RemoteTMSException() {
        super();
    }

    public RemoteTMSException(String arg0) {
        super(arg0);
    }

    public RemoteTMSException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public RemoteTMSException(Throwable arg0) {
        super(arg0);
    }

}
