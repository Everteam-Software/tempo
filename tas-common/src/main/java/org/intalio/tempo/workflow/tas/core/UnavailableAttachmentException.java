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
 * Thrown if TAS is requested to process an unknown, nonexistent or inaccessible attachment.
 * 
 * @author Iwan Memruk
 * @version $Revision: 722 $
 */
public class UnavailableAttachmentException extends TASException {

    /**
     * Serialization UID.
     */
    private static final long serialVersionUID = - 3391475884919752277L;

    /**
     * Instance constructor.
     */
    public UnavailableAttachmentException() {
        super();
    }

    /**
     * Instance constructor.
     * 
     * @param message
     *            A human-readable error description.
     */
    public UnavailableAttachmentException(String message) {
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
    public UnavailableAttachmentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instance constructor.
     * 
     * @param cause
     *            The <code>Throwable</code> instance that caused the error.
     */
    public UnavailableAttachmentException(Throwable cause) {
        super(cause);
    }

}
