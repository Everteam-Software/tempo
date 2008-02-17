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
import java.security.SecureRandom;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.xml.TaskMarshaller;
import org.intalio.tempo.workflow.task.xml.TaskUnmarshaller;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.intalio.tempo.workflow.util.TaskEquality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class RemoteTMSClientTest extends TestCase {

    static final Logger _logger = LoggerFactory.getLogger(RemoteTMSClientTest.class);
    
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String TOKEN = "VE9LRU4mJnVzZXI9PWludGFsaW9cYWRtaW4mJmlzc3VlZD09MTE5NzQ0ODYzNjIzNSYmcm9sZXM9PWludGFsaW9ccHJvY2Vzc2FkbWluaXN0cmF0b3IsZXhhbXBsZXNcZW1wbG95ZWUsaW50YWxpb1xwcm9jZXNzbWFuYWdlcixleGFtcGxlc1xtYW5hZ2VyJiZmdWxsTmFtZT09QWRtaW5pbmlzdHJhdG9yJiZlbWFpbD09YWRtaW5AZXhhbXBsZS5jb20mJm5vbmNlPT0tMjM1OTg5Mzc4MzQ0MDg3MTE5MCYmdGltZXN0YW1wPT0xMTk3NDQ4NjM2MjM2JiZkaWdlc3Q9PXdLNzJnTXhDWDhzS2ZoM29oOFJwcGVjYlV1ND0mJiYmVE9LRU4=";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(RemoteTMSClientTest.class);
    }

    public void testInput() throws Exception {
        ITaskManagementService tms = new RemoteTMSFactory(
                "http://localhost:8080/axis2/services/TaskManagementServices", TOKEN).getService();
        Document input1 = Utils.createXMLDocument("/absr.xml");
        String task1ID = nextRandom();
        PATask task1 = new PATask(task1ID, new URI("http://localhost/1"), "processID", "urn:completeSOAPAction", input1);
        task1.getUserOwners().add("intalio.admin");
        tms.create(task1);
        testRoundTrip(task1, input1);

        PATask task2 = (PATask) tms.getTask(task1ID);
        TaskEquality.areTasksEquals(task1, task2);
    }
    
    private void testRoundTrip(PATask task1, Document input) throws Exception {
        TaskMarshaller marshaller = new TaskMarshaller();
        OMElement marshalledTask = marshaller.marshalFullTask(task1, null);
        TaskUnmarshaller unmarshaller = new TaskUnmarshaller();
        PATask task2 = (PATask)unmarshaller.unmarshalFullTask(marshalledTask);
        Assert.assertEquals(task1.getInput().toString(),task2.getInput().toString());
    }
   

    public void testBasicPATaskLifecycle() throws Exception {
        ITaskManagementService tms = new RemoteTMSFactory(
                "http://localhost:8080/axis2/services/TaskManagementServices", TOKEN).getService();
        Task[] tasks = tms.getTaskList();
        Assert.assertNotNull(tasks);

        String task1ID = nextRandom();
        PATask task1 = new PATask(task1ID, new URI("http://localhost/1"), "processID", "urn:completeSOAPAction",
                Utils.createXMLDocument());
        task1.getUserOwners().add("intalio.admin");
        tms.create(task1);

        PATask task2 = (PATask) tms.getTask(task1ID);
        TaskEquality.areTasksEquals(task1, task2);

        String task3ID = nextRandom();
        PATask task3 = new PATask(task3ID, new URI("http://localhost/3"), "processID", "urn:completeSOAPAction",
                Utils.createXMLDocument());
        task3.getUserOwners().add("intalio.admin");
        task3.getUserOwners().add("intalio\\admin");
        tms.create(task3);

        Document output1 = Utils.createXMLDocument();
        output1.getDocumentElement().appendChild(output1.createTextNode("Hi world"));
        tms.setOutput(task3ID, output1);

        PATask task4 = (PATask) tms.getTask(task3ID);
        Assert.assertEquals("Hi world", task4.getOutput().getDocumentElement().getTextContent().trim());
        Assert.assertEquals(TaskState.READY, task4.getState());

        Document output2 = Utils.createXMLDocument();
        output2.getDocumentElement().appendChild(output2.createTextNode("Hi world #2"));
        tms.setOutputAndComplete(task3ID, output2);

        PATask task5 = (PATask) tms.getTask(task4.getID());
        Assert.assertEquals("Hi world #2", task5.getOutput().getDocumentElement().getTextContent().trim());
        Assert.assertEquals(TaskState.COMPLETED, task5.getState());

        String task6ID = nextRandom();
        PATask task6 = new PATask(task6ID, new URI("http://localhost/6"), "processID", "urn:completeSOAPAction",
                Utils.createXMLDocument());
        task6.getUserOwners().add("intalio.admin");
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

        // TODO: add system user for testing that can delete tasks
//        tms.delete(new String[] { task3ID, task8ID });
//        Task[] tasks2 = tms.getTaskList();
//        for (Task task : tasks2) {
//            if (task.getID().equals(task3ID) || task.getID().equals(task8ID)) {
//                Assert.fail();
//            }
//        }
    }

    private String nextRandom() {
        return String.valueOf(((Integer) SECURE_RANDOM.nextInt(100000000)));
    }

    public void testAttachments() throws Exception {
        ITaskManagementService tms = new RemoteTMSFactory(
                "http://localhost:8080/axis2/services/TaskManagementServices", TOKEN).getService();

        String task1ID = nextRandom();
        PATask task1 = new PATask(task1ID, new URI("http://localhost/1"), "processID", "urn:completeSOAPAction",
                Utils.createXMLDocument());
        task1.getUserOwners().add("test.system-test");
        task1.getUserOwners().add("intalio.admin");
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
