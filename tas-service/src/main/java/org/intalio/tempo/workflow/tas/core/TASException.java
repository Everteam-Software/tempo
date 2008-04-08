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
package org.intalio.tempo.workflow.tas.core;

/**
 * The abstract base class for TAS exceptions.
 * 
 * @author Iwan Memruk
 * @version $Revision: 722 $
 */
public abstract class TASException extends Exception {

    /**
     * Serialization UID.
     */
    private static final long serialVersionUID = 3275036982478532351L;

    /**
     * Instance constructor.
     */
    protected TASException() {

    }

    /**
     * Instance constructor.
     * 
     * @param message
     *            A human-readable error description.
     */
    protected TASException(String message) {
        super(message);
    }

    /**
     * Instance constructor.
     * 
     * @param message
     *            A human-readable error description.
     * @param cause
     *            The <code>Throwable</code> instance that caused the error.
     */
    protected TASException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instance constructor.
     * 
     * @param cause
     *            The <code>Throwable</code> instance that caused the error.
     */
    protected TASException(Throwable cause) {
        super(cause);
    }
}
