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
package org.intalio.tempo.workflow.fds.dispatchers;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.intalio.tempo.workflow.fds.dispatches.Dispatchers;
import org.intalio.tempo.workflow.fds.dispatches.IDispatcher;

public class NotifyDispatcherTest extends TestCase {
    private Document createRequest() throws Exception {
        return new SAXReader().read(this.getClass().getResourceAsStream("/notifyRequest1.xml"));
    }

    private Document createResponse() throws Exception {
        return new SAXReader().read(this.getClass().getResourceAsStream("/notifyResponse1.xml"));
    }

//    private void printXML(Document document) throws Exception {
//        Serializer sr = new Serializer(System.out);
//        sr.setIndent(2);
//        sr.write(document);
//        sr.flush();
//    }

    public void testNotifyDispatcher() throws Exception {
        IDispatcher dispatcher = Dispatchers.createDispatcher("notifyRequest");

        Document request = createRequest();
        dispatcher.dispatchRequest(request);
        Document response = createResponse();
        dispatcher.dispatchResponse(response);
    }
}
