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

package org.intalio.tempo.workflow.tms.client.dependent_tests;

import java.net.URI;
import java.net.URL;
import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.w3c.dom.Document;

import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.attachments.Attachment;

public class RemoteTMSClientTest extends TestCase {

    private static final String TOKEN = "VE9LRU4mJnVzZXI9PXRlc3Rcc3lzdGVtLXRlc3QmJmlzc3VlZD09MTEzNzQxOTg"
            + "xNTAwMyYmcm9sZXM9PXN5c3RlbVxzeXN0ZW0mJmZ1bGxOYW1lPT1Qcm9kdWN0IE1hbmFnZXIgIzEmJmVtYWlsPT1wcm9kL"
            + "W1hbmFnZXIxQGludGFsaW8uY29tJiZub25jZT09LTI4OTY1NDQxODc3OTI0MjY0MDUmJnRpbWVzdGFtcD09MTEzNzQxOTg"
            + "xNTAwMyYmZGlnZXN0PT1wVVc0aXFiMWd1ZnV5TEwxYXNZcit4MS8rRW89JiYmJlRPS0VO";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(RemoteTMSClientTest.class);
    }

    public void testBasicPATaskLifecycle()
            throws Exception {
        ITaskManagementService tms = new RemoteTMSFactory(
                "http://localhost:8080/axis2/services/TaskManagementServices", TOKEN).getService();
        Task[] tasks = tms.getTaskList();
        Assert.assertNotNull(tasks);

        String task1ID = ((Integer) new Random().nextInt(10000)).toString();
        PATask task1 = new PATask(task1ID, new URI("http://localhost/1"), "processID", "urn:completeSOAPAction",
                TestUtils.createXMLDocument());
        task1.getUserOwners().add("test.system-test");
        tms.create(task1);

        PATask task2 = (PATask) tms.getTask(task1ID);
        Assert.assertTrue(task1.equalsTask(task2));

        String task3ID = ((Integer) new Random().nextInt(10000)).toString();
        PATask task3 = new PATask(task3ID, new URI("http://localhost/3"), "processID", "urn:completeSOAPAction",
                TestUtils.createXMLDocument());
        task3.getUserOwners().add("test.system-test");
        tms.create(task3);

        Document output1 = TestUtils.createXMLDocument();
        output1.getDocumentElement().appendChild(output1.createTextNode("Hi world"));
        tms.setOutput(task3ID, output1);

        PATask task4 = (PATask) tms.getTask(task3ID);
        Assert.assertEquals("Hi world", task4.getOutput().getDocumentElement().getTextContent().trim());
        Assert.assertEquals(TaskState.READY, task4.getState());

        Document output2 = TestUtils.createXMLDocument();
        output2.getDocumentElement().appendChild(output2.createTextNode("Hi world #2"));
        tms.setOutputAndComplete(task4.getID(), output2);

        PATask task5 = (PATask) tms.getTask(task4.getID());
        Assert.assertEquals("Hi world #2", task5.getOutput().getDocumentElement().getTextContent().trim());
        Assert.assertEquals(TaskState.COMPLETED, task5.getState());

        String task6ID = ((Integer) new Random().nextInt(10000)).toString();
        PATask task6 = new PATask(task6ID, new URI("http://localhost/6"), "processID", "urn:completeSOAPAction",
                TestUtils.createXMLDocument());
        task6.getUserOwners().add("test.system-test");
        tms.create(task6);
        tms.complete(task6ID);

        PATask task7 = (PATask) tms.getTask(task6ID);
        Assert.assertEquals(TaskState.COMPLETED, task7.getState());

        tms.fail(task7.getID(), "code", "reason");

        PATask task8 = (PATask) tms.getTask(task7.getID());
        Assert.assertEquals(TaskState.FAILED, task8.getState());
        Assert.assertEquals("code", task8.getFailureCode());
        Assert.assertEquals("reason", task8.getFailureReason());

        String task8ID = task8.getID();

        tms.delete(new String[] { task3ID, task8ID });
        Task[] tasks2 = tms.getTaskList();
        for (Task task : tasks2) {
            if (task.getID().equals(task3ID) || task.getID().equals(task8ID)) {
                Assert.fail();
            }
        }
    }

    public void testAttachments()
            throws Exception {
        ITaskManagementService tms = new RemoteTMSFactory(
                "http://localhost:8080/axis2/services/TaskManagementServices", TOKEN).getService();

        String task1ID = ((Integer) new Random().nextInt(10000)).toString();
        PATask task1 = new PATask(task1ID, new URI("http://localhost/1"), "processID", "urn:completeSOAPAction",
                TestUtils.createXMLDocument());
        task1.getUserOwners().add("test.system-test");
        Attachment attachment1 = new Attachment(new AttachmentMetadata(), new URL("http://localhost/a1"));
        task1.addAttachment(attachment1);
        Attachment attachment2 = new Attachment(new AttachmentMetadata(), new URL("http://localhost/a2"));
        task1.addAttachment(attachment2);
        tms.create(task1);

        PATask task2 = (PATask) tms.getTask(task1.getID());
        Assert.assertEquals(2, task2.getAttachments().size());

        tms.removeAttachment(task1.getID(), attachment1.getPayloadURL());

        PATask task3 = (PATask) tms.getTask(task1.getID());
        Assert.assertEquals(1, task3.getAttachments().size());
        Assert.assertEquals(attachment2.getPayloadURL(), task3.getAttachments().iterator().next().getPayloadURL());

        Attachment[] attachments = tms.getAttachments(task1.getID());
        Assert.assertEquals(1, attachments.length);
        Assert.assertEquals(attachment2.getPayloadURL(), attachments[0].getPayloadURL());

        tms.removeAttachment(task1.getID(), attachment2.getPayloadURL());
        tms.addAttachment(task1.getID(), attachment1);

        PATask task4 = (PATask) tms.getTask(task1.getID());
        Assert.assertEquals(1, task4.getAttachments().size());
        Assert.assertEquals(attachment1.getPayloadURL(), task4.getAttachments().iterator().next().getPayloadURL());
    }
}
