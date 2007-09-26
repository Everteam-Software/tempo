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
import org.w3c.dom.Document;

public class OMDOMConvertorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(OMDOMConvertorTest.class);
    }

    public void testConversion() throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement el = factory.createOMElement("Foo", "http://example.com", "tns");
        el.setText("Sample Text/ééà");

        System.out.println("Element: " + el.toStringWithConsume());

        // will throw an exception if character set doesn't match
        Document document = OMDOMConvertor.convertOMToDOM(el);

        System.out.println("Document: " + OMDOMConvertor.convertDOMToOM(document, factory));
    }
}
