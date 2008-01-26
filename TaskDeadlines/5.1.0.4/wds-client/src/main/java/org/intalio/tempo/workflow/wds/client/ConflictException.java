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

import org.intalio.tempo.workflow.wds.client.WDSException;

/**
 * Thrown to indicate a URI conflict during an attempt to store a WDS item. 
 * 
 * @author Iwan Memruk
 * @version $Revision: 473 $
 */
public class ConflictException extends WDSException {

    private static final long serialVersionUID = 9178019879122761354L;

    public ConflictException() { }

    /**
     * Instance constructor.
     * 
     * @param message
     *            Human-readable problem description.
     */
    public ConflictException(String message) {
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
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instance constructor.
     * 
     * @param cause
     *            The <code>Throwable</code> instance which caused this exception.
     */
    public ConflictException(Throwable cause) {
        super(cause);
    }
}
