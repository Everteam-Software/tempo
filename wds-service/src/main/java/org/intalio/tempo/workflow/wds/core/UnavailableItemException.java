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
package org.intalio.tempo.workflow.wds.core;

/**
 * Thrown to indicate a WDS item unavailability. 
 * 
 * @author Iwan Memruk
 * @version $Revision: 40 $
 */
public class UnavailableItemException extends Exception {

    /**
     * Serialization UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instance constructor.
     */    
    public UnavailableItemException() {
        super();
    }

    /**
     * Instance constructor
     * 
     * @param message Human-readable error description.
     */    
    public UnavailableItemException(String message) {
        super(message);
    }

    /**
     * Instance constructor
     * 
     * @param message Human-readable error description.
     * @param cause The <code>Throwable</code> that caused this exception.
     */    
    public UnavailableItemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instance constructor
     * 
     * @param cause The <code>Throwable</code> that caused this exception.
     */    
    public UnavailableItemException(Throwable cause) {
        super(cause);
    }

}
