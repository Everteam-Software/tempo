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

import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class NotificationTest extends TestCase {
    Logger log = LoggerFactory.getLogger(Notification.class);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PATaskTest.class);
    }

    private Document createXMLDocument() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.newDocument();
        doc.appendChild(doc.createElement("testDocument"));
        return doc;
    }

    public void testNotification() throws Exception {
        Document input = createXMLDocument();
        Notification n = new Notification("id", new URI("http://localhost/"), input);
        Assert.assertTrue(XmlTooling.equals(input, n.getInput()));
    }

    public void testNotificationStateNull() throws Exception {
        Notification n = new Notification("1", new URI("http://localhost"));

        try {
            n.setState(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testNotificationFailureCode() throws Exception {
        Notification n = new Notification("1", new URI("http://localhost"));

        try {
            n.setFailureCode("failure");
            String code = n.getFailureCode();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {

        }
        
        n.setState(TaskState.FAILED);
        n.setFailureCode("failure");
        String code = n.getFailureCode();
        assertEquals("failure", code);
    }
    
    public void testNotificationFailureReason() throws Exception {
        Notification n = new Notification("1", new URI("http://localhost"));
        
        try {
            n.setFailureReason("failureReason");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e){
            
        }
        
        n.setState(TaskState.FAILED);
        n.setFailureReason("failureReason");
        String failureReason = n.getFailureReason();
        assertEquals("failureReason", failureReason);
    }
    
    public void testTaskStateName() throws Exception {
        Notification n = new Notification("1", new URI("http://localhost"));
        
        n.setState(TaskState.COMPLETED);
        assertEquals("COMPLETED", n.getState().getName());
    }

}
