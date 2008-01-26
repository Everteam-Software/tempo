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


public class InvalidTaskStateException extends TMSException {

    /**
     * 
     */
    private static final long serialVersionUID = - 4058158782128861920L;

    public InvalidTaskStateException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public InvalidTaskStateException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public InvalidTaskStateException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    public InvalidTaskStateException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

}
