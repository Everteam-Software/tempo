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


public class TaskIDConflictException extends TMSException {

    private static final long serialVersionUID = - 7147054177991439296L;

    public TaskIDConflictException() {
        super();
    }

    public TaskIDConflictException(String message) {
        super(message);
    }

    public TaskIDConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskIDConflictException(Throwable cause) {
        super(cause);
    }

}
