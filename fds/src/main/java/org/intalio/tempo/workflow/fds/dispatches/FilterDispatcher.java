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

abstract class FilterDispatcher implements IDispatcher {
    protected IDispatcher targetDispatcher;

    public FilterDispatcher(IDispatcher targetDispatcher) {
        this.targetDispatcher = targetDispatcher;
    }
    
    protected abstract void beforeDispatchRequest(Document sourceRequest);
    
    protected abstract void afterDispatchRequest(Document dispatchedRequest);

    public final Document dispatchRequest(Document request)
            throws InvalidInputFormatException {
        beforeDispatchRequest(request);
        Document dispatchedRequest = targetDispatcher.dispatchRequest(request);
        afterDispatchRequest(dispatchedRequest);
        
        return dispatchedRequest;
    }
    
    protected abstract void beforeDispatchResponse(Document sourceResponse);
    
    protected abstract void afterDispatchResponse(Document dispatchedResponse);

    public final Document dispatchResponse(Document response)
            throws InvalidInputFormatException {
        beforeDispatchResponse(response);
        Document dispatchedResponse = targetDispatcher.dispatchResponse(response);
        afterDispatchResponse(dispatchedResponse);
        
        return dispatchedResponse;
    }

    public final String getTargetEndpoint() {
        return targetDispatcher.getTargetEndpoint();
    }

    public final String getTargetSoapAction() {
        return targetDispatcher.getTargetSoapAction();
    }

}
