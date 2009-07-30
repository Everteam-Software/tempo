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
package org.intalio.tempo.workflow.fds.tools;

import org.custommonkey.xmlunit.XMLTestCase;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class SoapToolsTest extends XMLTestCase {
    private Document createRequest()
            throws Exception {
        return new SAXReader().read(this.getClass().getResourceAsStream("/notifyRequest1.xml"));
    }
    
    public void testSoapMessage() throws Exception {
        Document request = new SAXReader().read(this.getClass().getResourceAsStream("/soapMessage.xml"));
        Document soap = SoapTools.wrapMessage(request);
        Document request2 = SoapTools.unwrapMessage(soap);
        assertXMLEqual(request.asXML(), request2.asXML());
    }

    public void testSoapTools()
            throws Exception {
        Document request = createRequest();
        Document soap = SoapTools.wrapMessage(request);
        Document request2 = SoapTools.unwrapMessage(soap);
        assertXMLEqual(request.asXML(), request2.asXML());
    }
}
