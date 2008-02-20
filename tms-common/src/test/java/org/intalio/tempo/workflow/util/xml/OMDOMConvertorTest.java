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
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.util.xml;

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.w3c.dom.Document;

public class OMDOMConvertorTest extends TestCase {

    static final XmlTooling xmlTooling = new XmlTooling();
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(OMDOMConvertorTest.class);
    }

    public void testConversion() throws Exception {
        
        Document doc = xmlTooling.getXmlDocument("/createTaskRequest2.xml");
        OMFactory factory = OMAbstractFactory.getOMFactory();
        Document doc2 = rountTripTooling(doc, factory);
        OMElement el = xmlTooling.convertDOMToOM(doc, factory);
        OMElement el2 = xmlTooling.convertDOMToOM(doc2, factory);
        assertEquals(el.toString(), el2.toString());
    }

    private Document rountTripTooling(Document doc, OMFactory factory) {
        OMElement el = xmlTooling.convertDOMToOM(doc, factory);
        return xmlTooling.convertOMToDOM(el);
    }
}
