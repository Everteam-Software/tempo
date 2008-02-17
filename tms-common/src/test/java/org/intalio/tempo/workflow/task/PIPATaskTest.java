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
import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.util.TaskEquality;

public class PIPATaskTest extends TestCase {

    private PIPATask createPIPATask() throws Exception {
        return new PIPATask("taskID", new URI("http://localhost/form"), new URI("http://localhost/process"),
                new URI("urn:initNS"), "urn:initSOAPAction");
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PIPATaskTest.class);
    }

    public void testPIPATask() throws Exception {
        String id = "taskID";
        URI formURL = new URI("http://localhost/form");
        URI processEndpoint = new URI("http://localhost/process-endpoint");
        URI namespace = new URI("urn:initNS");
        String soapAction = "urn:initSOAPAction";

        PIPATask task = new PIPATask(id, formURL, processEndpoint, namespace, soapAction);
        Assert.assertEquals(processEndpoint, task.getProcessEndpoint());
        Assert.assertEquals(namespace, task.getInitMessageNamespaceURI());
        Assert.assertEquals(soapAction, task.getInitOperationSOAPAction());

        try {
            PIPATask task1 = new PIPATask(id, formURL, null, namespace, soapAction);
            task1.getProcessEndpoint();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {

        }
        try {
            PIPATask task1 = new PIPATask(id, formURL, processEndpoint, null, soapAction);
            task1.getInitMessageNamespaceURI();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {

        }
        try {
            PIPATask task1 = new PIPATask(id, formURL, processEndpoint, namespace, null);
            task1.getInitOperationSOAPAction();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {

        }
    }

    public void testGetAndSetInitMessageNamespaceURI() throws Exception {
        PIPATask task = this.createPIPATask();
        URI nsURI = new URI("urn:test");
        task.setInitMessageNamespaceURI(nsURI);
        Assert.assertEquals(nsURI, task.getInitMessageNamespaceURI());
        try {
            task.setInitMessageNamespaceURI(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetAndSetInitOperationSOAPAction() throws Exception {
        PIPATask task = this.createPIPATask();
        String soapAction = "urn:test";
        task.setInitOperationSOAPAction(soapAction);
        Assert.assertEquals(soapAction, task.getInitOperationSOAPAction());
        try {
            task.setInitOperationSOAPAction(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetAndSetProcessEndpoint() throws Exception {
        PIPATask task = this.createPIPATask();
        URI processEndpoint = new URI("http://localhost/processEndpoint");
        task.setProcessEndpointFromString(processEndpoint.toString());
        Assert.assertEquals(processEndpoint, task.getProcessEndpoint());
        try {
            task.setProcessEndpointFromString(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }
    
    public void testPIPAEquality() throws Exception {
            PIPATask task1 = createPIPATask();
            TaskEquality.isEqual(task1, task1);
            
            PIPATask task2 = createPIPATask();
            task2.setCreationDate(new Date());
            try {
                TaskEquality.isEqual(task1, task2);
            } catch (Exception NotEqualException) {
                
            }
                
    }
}
