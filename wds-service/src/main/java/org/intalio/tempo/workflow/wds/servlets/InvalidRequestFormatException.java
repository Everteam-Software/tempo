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
package org.intalio.tempo.workflow.wds.servlets;

/**
 * Indicates an incoming request has invalid/incorrect format.
 */
public class InvalidRequestFormatException extends Exception {
    private static final long serialVersionUID = 1138428958040239L;

    public InvalidRequestFormatException(String message) {
        super(message);
    }

    public InvalidRequestFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRequestFormatException(Throwable cause) {
        super(cause);
    }
}
