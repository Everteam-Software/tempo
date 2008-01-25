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
package org.intalio.tempo.workflow.tas.axis2;

/**
 * Thrown to indicate that an XML message has an unexpected/invalid format.
 * 
 * @author Iwan Memruk
 * @version $Revision: 722 $
 * @see org.intalio.tempo.workflow.tas.axis2.TASAxis2Bridge
 */
class InvalidMessageFormatException extends Exception {

    /**
     * Serialization UID.
     */
    private static final long serialVersionUID = - 196400859881656013L;

    /**
     * Instance constructor.
     */
    public InvalidMessageFormatException() {
    
    }

    /**
     * Instance constructor.
     * 
     * @param message
     *            A human-readable error description.
     */    
    public InvalidMessageFormatException(String message) {
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
    public InvalidMessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instance constructor.
     * 
     * @param cause
     *            The <code>Throwable</code> instance that caused the error.
     */        
    public InvalidMessageFormatException(Throwable cause) {
        super(cause);
    }

}
