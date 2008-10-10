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

package org.intalio.tempo.workflow.task.xml;

import java.net.URI;
import java.net.URL;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskMarshallerTest extends TestCase {

    private static final Logger _logger = LoggerFactory.getLogger(TaskMarshallerTest.class);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TaskMarshallerTest.class);
    }

    private void testTaskMarshalling(Task task) throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        TaskMarshaller marshaller = new TaskMarshaller();
        OMElement parent = factory.createOMElement("task", TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
        marshaller.marshalFullTask(task, parent, null);
        _logger.debug(TestUtils.toPrettyXML(parent));
    }

    private void testTaskMetadataMarshalling(Task task, UserRoles roles) throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        TaskMarshaller marshaller = new TaskMarshaller();
        _logger.debug(TestUtils.toPrettyXML(marshaller.marshalTaskMetadata(task, roles)));
    }

    public void testPIPATaskMarshalling() throws Exception {
        PIPATask task = new PIPATask("id", new URI("http://localhost/form"), new URI("http://localhost/endpoint"), new URI("urn:initNS"), "urn:initSoapAction");
        task.getUserOwners().add("test/user1");
        task.getRoleOwners().add("test.role1");
        task.getRoleOwners().add("test\\role2");

        this.testTaskMarshalling(task);
    }

    public void testPATaskMarshalling() throws Exception {
        PATask task = new PATask("id", new URI("http://localhost/form"), "processID", "urn:completeSoapAction", TestUtils.createXMLDocument());
        task.getUserOwners().add("test/user1");
        task.getRoleOwners().add("test.role1");
        task.getRoleOwners().add("test\\role2");
        task.setOutput(TestUtils.createXMLDocument());
      
        task.addAttachment(new Attachment(new AttachmentMetadata(), new URL("http://localhost/url1")));
        AttachmentMetadata metadata = new AttachmentMetadata();
        metadata.setMimeType("image/jpeg");
        task.addAttachment(new Attachment(metadata, new URL("http://localhost/url2")));

        this.testTaskMarshalling(task);
    }

    public void testNotificationMarshalling() throws Exception {
        Notification task = new Notification("id", new URI("http://localhost/form"), TestUtils.createXMLDocument());

        this.testTaskMarshalling(task);
    }

    public void testPIPATaskMetaMarshalling() throws Exception {
        PIPATask task = new PIPATask("id", new URI("http://localhost/form"), new URI("http://localhost/endpoint"), new URI("urn:initNS"), "urn:initSoapAction");
        task.getUserOwners().add("test/user1");
        task.getRoleOwners().add("test.role1");
        task.getRoleOwners().add("test\\role2");
        this.testTaskMetadataMarshalling(task,null);
    }
    
    public void testPATaskMetaMarshalling() throws Exception {
        PATask task = new PATask("id", new URI("http://localhost/form"), "processID", "urn:completeSoapAction", TestUtils.createXMLDocument());
        task.getUserOwners().add("test/user1");
        task.getRoleOwners().add("test.role1");
        task.getRoleOwners().add("test\\role2");
        task.setOutput(TestUtils.createXMLDocument());

        task.addAttachment(new Attachment(new AttachmentMetadata(), new URL("http://localhost/url1")));
        AttachmentMetadata metadata = new AttachmentMetadata();
        metadata.setMimeType("image/jpeg");
        task.addAttachment(new Attachment(metadata, new URL("http://localhost/url2")));

        this.testTaskMetadataMarshalling(task, null);
    }
    
    public void testNotificationMetaMarshalling() throws Exception {
        Notification task = new Notification("id", new URI("http://localhost/form"));

        this.testTaskMetadataMarshalling(task, null);
    }
    
    public void testPATaskChainedBefore() throws Exception {
        PATask task = new PATask("id", new URI("http://localhost/form"), "processID", "urn:completeSoapAction", TestUtils.createXMLDocument());
        task.getUserOwners().add("test/user1");
        task.getRoleOwners().add("test.role1");
        
        task.setDescription("Testing task");
        task.setState(TaskState.READY);
        task.setPreviousTaskID("p1");
        task.setChainedBefore(true);
        
        this.testTaskMarshalling(task);
    }
    
    public void testPATaskMarshallingWithOtherProperty() throws Exception {
        PATask task = new PATask("id", new URI("http://localhost/form"), "processID", "urn:completeSoapAction", TestUtils.createXMLDocument());
        task.getUserOwners().add("test/user1");
        task.getRoleOwners().add("test.role1");
        task.getRoleOwners().add("test\\role2");
        task.setOutput(TestUtils.createXMLDocument());
      
        task.setState(TaskState.FAILED);
        task.setFailureCode("FATAL");
        task.setFailureReason("Don't really know");
        task.setDeadline(new Date());
        task.setPriority(1);
        task.setProcessID("processID1");
        task.authorizeActionForUser("claim", "test/user1");
        task.authorizeActionForRole("revoke", "test/role1");
        
        task.addAttachment(new Attachment(new AttachmentMetadata(), new URL("http://localhost/url1")));
        AttachmentMetadata metadata = new AttachmentMetadata();
        metadata.setMimeType("image/jpeg");
        task.addAttachment(new Attachment(metadata, new URL("http://localhost/url2")));

        this.testTaskMarshalling(task);
    }    
}
