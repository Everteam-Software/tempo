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
 * Thrown to indicate an attempt to retrieve or delete a nonexistent item.
 * 
 * @author Iwan Memruk
 * @version $Revision: 131 $
 */
public class UnavailableItemException extends WDSException {

    /**
     * Serialization UID.
     */
    private static final long serialVersionUID = - 2325642959840075741L;

    /**
     * Instance constructor.
     */    
    public UnavailableItemException() {

    }

    /**
     * Instance constructor.
     * 
     * @param message
     *            Human-readable problem description.
     */
    public UnavailableItemException(String message) {
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
    public UnavailableItemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instance constructor.
     * 
     * @param cause
     *            The <code>Throwable</code> instance which caused this exception.
     */    
    public UnavailableItemException(Throwable cause) {
        super(cause);
    }

}
