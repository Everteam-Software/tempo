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
package org.intalio.tempo.workflow.fds.dispatches;


public class NoDispatcherException extends Exception {
    private static final long serialVersionUID = 8485151297510752908L;

    private String rootElementName;

    public NoDispatcherException(String rootElementName) {
        super("No dispatcher for message " + rootElementName);
        this.rootElementName = rootElementName;        
    }

    public String getRootElementName() {
        return rootElementName;       
    }
}
