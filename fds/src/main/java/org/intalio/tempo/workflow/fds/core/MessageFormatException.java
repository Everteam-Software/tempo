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
package org.intalio.tempo.workflow.fds.core;

/**
 * Thrown to indicate that the message being processed has an invalid format.
 * 
 * @author Iwan Memruk
 * @version $Revision: 36 $
 */
public class MessageFormatException extends Exception {

    /**
     * The serial version UID, required by Java 5, since the class implements
     * <code>Serializable</code>
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 2312315321L;

    /**
     * Instance constructor.
     * 
     * @param message The human-readable message that describes the error specifics.
     */
    public MessageFormatException(String message) {
        super(message);
    }

    /**
     * Instance constructor.
     * 
     * @param message The human-readable message that describes the error specifics.
     * @param cause The <code>Throwable</code> instance which is the cause of this exception.
     */
    public MessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
