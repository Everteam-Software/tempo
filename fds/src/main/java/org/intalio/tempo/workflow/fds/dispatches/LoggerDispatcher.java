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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoggerDispatcher extends FilterDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(LoggerDispatcher.class);
    private Class<? extends IDispatcher> targetDispatcherClass;

    public LoggerDispatcher(IDispatcher targetDispatcher) {
        super(targetDispatcher);
        targetDispatcherClass = targetDispatcher.getClass();
    }

    private void logMessage(String message, org.dom4j.Document doc) {
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Dispatcher " + targetDispatcherClass.getCanonicalName() + ": " + message);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                OutputFormat of = OutputFormat.createPrettyPrint();
                of.setEncoding("UTF-8");

                XMLWriter writer = new XMLWriter(bos, of);
                writer.write(doc);
                
                String serializedDoc = bos.toString();

                logger.debug(serializedDoc);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void beforeDispatchRequest(Document sourceRequest) {
        logMessage("source request", sourceRequest);
    }

    @Override
    protected void afterDispatchRequest(Document dispatchedRequest) {
        logMessage("dispatched request", dispatchedRequest);
    }

    @Override
    protected void beforeDispatchResponse(Document sourceResponse) {
        logMessage("source response", sourceResponse);
    }

    @Override
    protected void afterDispatchResponse(Document dispatchedResponse) {
        logMessage("dispatched response", dispatchedResponse);
    }
}
