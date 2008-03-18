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
 * Base class for logical WDS exceptions, such as item nonexistance or URI conflict.
 * 
 * @author Iwan Memruk
 * @version $Revision: 473 $
 */
public class WDSException extends Exception {
    private static final long serialVersionUID = 335028427052560736L;

    public WDSException() { }

    /**
     * Instance constructor.
     * 
     * @param message human-readable problem description.
     */
    public WDSException(String message) {
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
    public WDSException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instance constructor.
     * 
     * @param cause
     *            The <code>Throwable</code> instance which caused this exception.
     */
    public WDSException(Throwable cause) {
        super(cause);
    }

}
