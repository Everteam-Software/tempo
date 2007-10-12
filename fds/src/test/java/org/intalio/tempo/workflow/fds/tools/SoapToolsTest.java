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
import java.io.ByteArrayOutputStream;
import org.dom4j.util.NodeComparator;

public class SoapToolsTest extends TestCase {
    private Document createRequest()
            throws Exception {
        return new Builder().build(this.getClass().getResourceAsStream("/notifyRequest1.xml"));
    }

    private void printXML(Document document)
            throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer sr = new Serializer(out);
        sr.setIndent(2);
        sr.write(document);
        sr.flush();
    }

    public void testSoapTools()
            throws Exception {
        Document request = createRequest();
        Document soap = SoapTools.wrapMessage(request);
        Document request2 = SoapTools.unwrapMessage(soap);
		assertTrue(new NodeComparator().compare( request, request2 ) == 0);
    }
}
