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
 */
package org.intalio.tempo.workflow.wds.client;

/**
 * Thrown to indicate an authentication/authorization WDS error.
 * 
 * @author Iwan Memruk
 * @version $Revision: 473 $
 */
public class AuthException extends WDSException {

    private static final long serialVersionUID = - 5710506403617082300L;

    /**
     * Instance constructor.
     */
    public AuthException() {

    }

    /**
     * Instance constructor.
     * 
     * @param message
     *            Human-readable problem description.
     */
    public AuthException(String message) {
        super(message);
    }

    /**
     * Instance constructor.
     * 
     * @param message
     *            Human-readable problem description.
     * @param cause
     *            The <code>Throwable</code> instance which caused this exception.
     */
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instance constructor.
     * 
     * @param cause
     *            The <code>Throwable</code> instance which caused this exception.
     */
    public AuthException(Throwable cause) {
        super(cause);
    }

}
