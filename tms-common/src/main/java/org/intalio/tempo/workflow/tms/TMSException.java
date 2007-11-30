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

package org.intalio.tempo.workflow.tms;

import org.intalio.tempo.workflow.WorkflowException;

public abstract class TMSException extends WorkflowException {

    public TMSException() {
        super();
    }

    public TMSException(String message) {
        super(message);
    }

    public TMSException(String message, Throwable cause) {
        super(message, cause);
    }

    public TMSException(Throwable cause) {
        super(cause);
    }

}
