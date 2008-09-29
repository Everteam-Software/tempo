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

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public class OMElementQueueTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(OMElementQueueTest.class);
    }

    public void testConstructor() throws Exception {
        Exception rae = null;
        try {
            new OMElementQueue(null);
        } catch (Exception e) {
            rae = e;
        }
        assertEquals(rae.getClass(), RequiredArgumentException.class);
    }

    public void testGetNext() throws Exception {
        OMFactory factory = org.apache.axiom.om.OMAbstractFactory.getOMFactory();
        QName name = new QName("localPart");
        OMElement element = factory.createOMElement(name);
        OMElement element2 = factory.createOMElement(name);
        OMElementQueue queue = new OMElementQueue(element);
        queue.pushElementBack(element2);
        OMElement next = queue.getNextElement();
        assertEquals(next, element2);
    }
}
