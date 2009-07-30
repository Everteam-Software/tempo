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

import org.dom4j.Document;

public interface IDispatcher {
    Document dispatchRequest(Document request) throws InvalidInputFormatException;
    Document dispatchResponse(Document response) throws InvalidInputFormatException;
    
    String getTargetEndpoint();
    String getTargetSoapAction();
}
