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

package org.intalio.tempo.workflow.tms.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.task.xml.TaskTypeMapper.TaskType;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.TaskEquality;
import org.w3c.dom.Document;

import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnectionFactory;
import org.intalio.tempo.workflow.tms.server.dao.SimpleTaskDAOConnectionFactory;


public class TMSServerTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TMSServerTest.class);
    }

    public void setUp() throws Exception {
        System.setProperty("org.intalio.tempo.configDirectory",
                "src/test/resources/");
    }

    public void testPATaskLifecycle() throws Exception {
        ITMSServer server = Utils.createTMSServer();

        PATask paTask = new PATask("taskID", new URI("http://localhost/1"), "processID", "urn:completeSOAPAction", Utils.createXMLDocument(), new HashMap<String, String>());
        paTask.getUserOwners().add("test/user1");
        paTask.getRoleOwners().add("test/role3");
        paTask.getCustomMetadata().put("test", "intalio");
        paTask.getUserOwners().add("test/user1");
        paTask.getRoleOwners().add("test/role3");
        ITaskDAOConnectionFactory daoFactory=new SimpleTaskDAOConnectionFactory();
        ITaskDAOConnection dao=daoFactory.openConnection();

        server.create(dao,paTask, "token1");

        TaskEquality.areTasksEquals(paTask, server.getTask(dao,"taskID", "token1"));
        TaskEquality.areTasksEquals(paTask, server.getTask(dao,"taskID", "token2"));
        TaskEquality.areTasksEquals(paTask, server.getTaskList(dao,"token1")[0]);
        TaskEquality.areTasksEquals(paTask, server.getTaskList(dao,"token2")[0]);
        try {
            server.getTask(dao,"taskID", "token3");
            Assert.fail("AccessDeniedException expected");
        } catch (AccessDeniedException e) {

        }
        Assert.assertEquals(0, server.getTaskList(dao,"token3").length);
        
        Document newOutput1 = Utils.createXMLDocument();
        server.setOutput(dao,"taskID", newOutput1, "token1");
        PATask taskWithSetOutput = (PATask) server.getTask(dao,"taskID", "token2");
        Assert.assertTrue(XmlTooling.equals(newOutput1, taskWithSetOutput.getOutput()));
        Assert.assertEquals(TaskState.READY, taskWithSetOutput.getState());
        
        // test reassign
        org.intalio.tempo.workflow.auth.AuthIdentifierSet newUsers = new org.intalio.tempo.workflow.auth.AuthIdentifierSet(new String[]{"test/user1", "test/user2"});
        org.intalio.tempo.workflow.auth.AuthIdentifierSet newRoles = new org.intalio.tempo.workflow.auth.AuthIdentifierSet(new String[]{"test/role2", "test/role3"});
        server.reassign(dao,"taskID", newUsers, newRoles, TaskState.READY, "", "ESCALATE");

        Document newOutput2 = Utils.createXMLDocument();
        server.setOutputAndComplete(dao,"taskID", newOutput2, "token2");
        PATask completedTask = (PATask) server.getTask(dao,"taskID", "token2");
        Assert.assertTrue(XmlTooling.equals(newOutput2, completedTask.getOutput()));
        Assert.assertEquals(TaskState.COMPLETED, completedTask.getState());

        String failureCode = "failure-code";
        String failureReason = "failure reason";
        server.fail(dao,"taskID", failureCode, failureReason, "token1");
        PATask failedTask = (PATask) server.getTask(dao,"taskID", "token2");
        Assert.assertEquals(TaskState.FAILED, failedTask.getState());
        Assert.assertEquals(failureCode, failedTask.getFailureCode());
        Assert.assertEquals(failureReason, failedTask.getFailureReason());

        try {
            server.delete(dao,new String[] { "taskID" }, "token1");
            Assert.fail("AuthException expected");
        } catch (UnavailableTaskException e) {

        }

        server.delete(dao,new String[] { "taskID" }, "system-user-token");
        Assert.assertEquals(0, server.getTaskList(dao,"token1").length);

        try {
            server.delete(dao,new String[] { "taskID2" }, "system-user-token");
            Assert.fail("Unavailable task expected");
        } catch (UnavailableTaskException e) {

        }

    }

    private PATask getPATask(String id) throws URISyntaxException, Exception {
        PATask pa = new PATask(id, new URI("http://localhost/1"), "processID", "urn:completeSOAPAction",
                    Utils.createXMLDocument(), new HashMap<String, String>());
        pa.getUserOwners().add("test/user1");
        pa.getRoleOwners().add("test/role3");
        pa.getCustomMetadata().put("test", "intalio");
        return pa;
    }
    
    public void testCannotUpdateNotification() throws Exception {
        ITMSServer server = Utils.createTMSServerJPA();
        Notification notification = new Notification("taskID", new URI("http://localhost/1"), Utils.createXMLDocument());
        notification.getUserOwners().add("test/user1");
        notification.getRoleOwners().add("test/role3");
        ITaskDAOConnectionFactory daoFactory=new SimpleTaskDAOConnectionFactory();
        ITaskDAOConnection dao=daoFactory.openConnection();

        server.create(dao,notification, "token1");
        try {
            TaskMetadata metadata = TaskMetadata.Factory.newInstance();  
            metadata.setTaskId("taskID");
            metadata.setTaskType(TaskType.NOTIFICATION.name());
            server.update(dao,metadata, "token");
            fail("should not be able to update a notification");
        } catch (Exception e) {
            // expected
        }    
    }
    
    public void testCannotUpdatePAIfNoPrevious() throws Exception {
        ITMSServer server = Utils.createTMSServerJPA();
        TaskMetadata metadata = TaskMetadata.Factory.newInstance();  
        metadata.setTaskId("taskID");
        metadata.setTaskType(TaskType.ACTIVITY.name());
        try {
        	ITaskDAOConnectionFactory daoFactory=new SimpleTaskDAOConnectionFactory();
        	 ITaskDAOConnection dao=daoFactory.openConnection();
        	 server.update(dao,metadata, "token");
            fail("should not be able to update a notification");
        } catch (Exception e) {
            // expected
        }    
    }
    
    public void testCanUpdatePriority() throws Exception {
        ITMSServer server = Utils.createTMSServerJPA();
        String id = ""+System.currentTimeMillis();
        PATask pa = getPATask(id);
        ITaskDAOConnectionFactory daoFactory=new SimpleTaskDAOConnectionFactory();
        ITaskDAOConnection dao=daoFactory.openConnection();
        server.create(dao,pa, "token1");
        TaskMetadata metadata = TaskMetadata.Factory.newInstance();  
        metadata.setTaskId(id);
        
        metadata.setPriority(5);
        metadata.setDescription("desc2");
        //Date date = new Date();
        //tu.setDeadline(date);
        server.update(dao,metadata, "token1");
        PATask pa2 = (PATask) server.getTask(dao,pa.getID(), "token1");
        Assert.assertNotNull(pa2);
        
        Assert.assertEquals(5, (int)pa2.getPriority());
        Assert.assertEquals("desc2", pa2.getDescription());
        //Assert.assertEquals(date, pa2.getDeadline());
    }

    public void testNotificationLifecycle() throws Exception {
        ITMSServer server = Utils.createTMSServer();
        Notification notification = new Notification("taskID", new URI("http://localhost/1"), Utils.createXMLDocument());
        notification.getUserOwners().add("test/user1");
        notification.getRoleOwners().add("test/role3");
        ITaskDAOConnectionFactory daoFactory=new SimpleTaskDAOConnectionFactory();
        ITaskDAOConnection dao=daoFactory.openConnection();
        server.create(dao,notification, "token1");

        TaskEquality.areTasksEquals(notification, server.getTask(dao,"taskID", "token1"));
        TaskEquality.areTasksEquals(notification, server.getTask(dao,"taskID", "token2"));
        TaskEquality.areTasksEquals(notification, server.getTaskList(dao,"token1")[0]);
        TaskEquality.areTasksEquals(notification, server.getTaskList(dao,"token2")[0]);
        try {
            server.getTask(dao,"taskID", "token3");
            Assert.fail("AccessDeniedException expected");
        } catch (AccessDeniedException e) {

        }
        Assert.assertEquals(0, server.getTaskList(dao,"token3").length);

        server.complete(dao,"taskID", "token2");
        Notification completedTask = (Notification) server.getTask(dao,"taskID", "token2");
        Assert.assertEquals(TaskState.COMPLETED, completedTask.getState());

        String failureCode = "failure-code";
        String failureReason = "failure reason";
        server.fail(dao,"taskID", failureCode, failureReason, "token1");
        Notification failedTask = (Notification) server.getTask(dao,"taskID", "token2");
        Assert.assertEquals(TaskState.FAILED, failedTask.getState());
        Assert.assertEquals(failureCode, failedTask.getFailureCode());
        Assert.assertEquals(failureReason, failedTask.getFailureReason());

        try {
            server.delete(dao,new String[] { "taskID" }, "token1");
            Assert.fail("AuthException expected");
        } catch (UnavailableTaskException e) {

        }

        server.delete(dao,new String[] { "taskID" }, "system-user-token");
        Assert.assertEquals(0, server.getTaskList(dao,"token1").length);
    }
    
//    public void testXX() throws Exception{
//    	 ITMSServer server = Utils.createTMSServer();
//    	 
//        try {
//        	OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
//            OMElementQueue rootQueue = new OMElementQueue(createTaskRequest);
//            String taskID = requireElementValue(rootQueue, "taskId");
//            OMElement omInputContainer = requireElement(rootQueue, "input");
//            Document domInput = null;
//            if (omInputContainer.getFirstElement() != null) {
//                domInput = new TaskUnmarshaller().unmarshalTaskOutput(omInputContainer);
//            }
//            String participantToken = requireElementValue(rootQueue, "participantToken");
//            Document userProcessResponse = _server.initProcess(taskID, domInput, participantToken);
//            if (userProcessResponse == null)
//                throw new RuntimeException("TMP did not return a correct message while calling init");
//            OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
//                public OMElement marshalResponse(Document userProcessResponse) {
//                    OMElement response = createElement("initProcessResponse");
//                    OMElement userProcessResponseWrapper = createElement(response, "userProcessResponse");
//                    userProcessResponseWrapper.addChild(new XmlTooling().convertDOMToOM(userProcessResponse, this.getOMFactory()));
//                    return response;
//                }
//            }.marshalResponse(userProcessResponse);
//            return response;
//        } catch (Exception e) {
//            throw makeFault(e);
//        }
//    }
}
