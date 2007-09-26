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

import junit.framework.TestCase;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Serializer;
import org.intalio.tempo.workflow.fds.tools.SoapTools;

public class SoapToolsTest extends TestCase {
    private Document createRequest()
            throws Exception {
        return new Builder().build(this.getClass().getResourceAsStream("/notifyRequest1.xml"));
    }

    private void printXML(Document document)
            throws Exception {
        Serializer sr = new Serializer(System.out);
        sr.setIndent(2);
        sr.write(document);
        sr.flush();
    }

    public void testSoapTools()
            throws Exception {
        Document request = createRequest();
        printXML(request);
        Document soap = SoapTools.wrapMessage(request);
        printXML(soap);
        Document request2 = SoapTools.unwrapMessage(soap);
        printXML(request2);
    }
}
