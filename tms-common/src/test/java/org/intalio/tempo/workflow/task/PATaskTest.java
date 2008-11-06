/**
 * Copyright (c) 2005-2008 Intalio inc.
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
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.w3c.dom.Document;

public class PATaskTest extends TestCase {

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

    private PATask createPATask()
            throws Exception {
        String taskID = "taskID";
        URI formURL = new URI("http://localhost/");
        String processID = "processID";
        String completeSOAPAction = "urn:complete";
        Document input = this.createXMLDocument();

        return new PATask(taskID, formURL, processID, completeSOAPAction, input);
    }

    public void testPATask()
            throws Exception {
        String taskID = "taskID";
        URI formURL = new URI("http://localhost/");
        String processID = "processID";
        String completeSOAPAction = "urn:complete";
        Document input = this.createXMLDocument();

        PATask task = new PATask(taskID, formURL, processID, completeSOAPAction, input);
        Assert.assertEquals(processID, task.getProcessID());
        Assert.assertEquals(completeSOAPAction, task.getCompleteSOAPAction());
        Assert.assertTrue(XmlTooling.equals(input, task.getInput()));
        Assert.assertEquals(TaskState.READY, task.getState());
        Assert.assertNull(task.getOutput());
        Assert.assertTrue(task.getAttachments().isEmpty());

        try {
            new PATask(taskID, formURL, null, completeSOAPAction, input);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
        try {
            new PATask(taskID, formURL, processID, null, input);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
        try {
            PATask inputLessTask = new PATask(taskID, formURL, processID, completeSOAPAction, null);
            inputLessTask.getInput();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {

        }
    }

    public void testGetAndSetProcessID()
            throws Exception {
        PATask task = this.createPATask();
        String processID = "myProcessID";
        task.setProcessID(processID);
        Assert.assertEquals(processID, task.getProcessID());
        try {
            task.setProcessID(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetAndSetState()
            throws Exception {
        PATask task = this.createPATask();
        task.setState(TaskState.COMPLETED);
        Assert.assertEquals(TaskState.COMPLETED, task.getState());
        task.setState(TaskState.FAILED);
        Assert.assertEquals(TaskState.FAILED, task.getState());
        try {
            task.setState(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetAndSetFailureCode()
            throws Exception {
        PATask task = this.createPATask();
        try {
            task.getFailureCode();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {

        }
        try {
            task.setFailureCode("abc");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {

        }
        task.setState(TaskState.FAILED);
        Assert.assertEquals("", task.getFailureCode());
        String failureCode = "failure-code";
        task.setFailureCode(failureCode);
        Assert.assertEquals(failureCode, task.getFailureCode());
        try {
            task.setFailureCode(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetAndSetFailureReason()
            throws Exception {
        PATask task = this.createPATask();
        try {
            task.getFailureReason();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {

        }
        try {
            task.setFailureReason("abc");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {

        }
        task.setState(TaskState.FAILED);
        Assert.assertEquals("", task.getFailureReason());
        String failureReason = "failure reason";
        task.setFailureReason(failureReason);
        Assert.assertEquals(failureReason, task.getFailureReason());
        try {
            task.setFailureReason(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetAndSetCompleteSOAPAction()
            throws Exception {
        PATask task = this.createPATask();
        String soapAction = "urn:mySoapAction";
        task.setCompleteSOAPAction(soapAction);
        Assert.assertEquals(soapAction, task.getCompleteSOAPAction());
        try {
            task.setCompleteSOAPAction(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetInput()
            throws Exception {
        PATask task = this.createPATask();
        Document input = this.createXMLDocument();
        task.setInput(input);
        Assert.assertTrue(XmlTooling.equals(input, task.getInput()));
        try {
        	input = null;
            task.setInput(input);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetOutput() throws Exception {
        PATask task = this.createPATask();
        Document output = this.createXMLDocument();
        task.setOutput(output);
        Assert.assertTrue(XmlTooling.equals(output, task.getOutput()));
        try {
        	output = null;
            task.setOutput(output);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }
    
    public void testAssignClaimAndAvailable() throws Exception {
    	PATask task = this.createPATask();
    	task.setState(TaskState.READY);
    	task.getRoleOwners().add("group");
    	task.getUserOwners().add("gregor");
    	UserRoles gregor = new UserRoles("gregor", new String[]{"group"});
    	UserRoles niko = new UserRoles("niko", new String[]{"group"});
    	Assert.assertTrue(task.isAvailableTo(gregor));
    	Assert.assertTrue(task.isAvailableTo(niko));
    	task.setState(TaskState.CLAIMED);
    	Assert.assertTrue(task.isAvailableTo(gregor));
    	Assert.assertFalse(task.isAvailableTo(niko));
    }

    public void testAttachments() throws Exception {
        PATask task = this.createPATask();
        Attachment attachment = new Attachment(new AttachmentMetadata(), new URL("http://localhost/a1"));
        Assert.assertNull(task.addAttachment(attachment));
        Assert.assertTrue(task.getAttachments().size() == 1);
        Assert.assertEquals(attachment, task.getAttachments().iterator().next());
        Assert.assertEquals(attachment, task.removeAttachment(attachment.getPayloadURL()));
        Assert.assertTrue(task.getAttachments().isEmpty());
    }
}
