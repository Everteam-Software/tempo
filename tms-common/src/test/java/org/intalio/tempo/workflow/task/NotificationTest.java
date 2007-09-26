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
package org.intalio.tempo.workflow.task;

import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.intalio.tempo.workflow.task.Notification;

public class NotificationTest extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PATaskTest.class);
    }

    private Document createXMLDocument()
            throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.newDocument();
        doc.appendChild(doc.createElement("testDocument"));

        return doc;
    }

    public void testNotification() throws Exception {
        Document input = createXMLDocument();
        Notification n = new Notification("id", new URI("http://localhost/"), input);

        Assert.assertEquals(input, n.getInput());
    }
}
