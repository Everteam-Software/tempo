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
import java.util.Collection;
import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public class TaskTest extends TestCase {

    private class TestTask extends Task {

        public TestTask(String id, URI formURL) {
            super(id, formURL);
        }
        
        public TestTask(String id, String formURL){
            super(id, formURL);
        }

    }

    private Task createTestTask()
            throws Exception {
        Task testTask = new TestTask("taskID", new URI("http://localhost/"));
        return testTask;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TaskTest.class);
    }

    public void testTask()
            throws Exception {
        String id = "taskID";
        URI url = new URI("http://localhost/");
        Task task = new TestTask(id, url);
        Assert.assertEquals(id, task.getID());
        Assert.assertEquals(url, task.getFormURL());
        Assert.assertEquals("", task.getDescription());

        try {
            new TestTask(null, url);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }

        try {
            new TestTask(id, (URI)null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetID()
            throws Exception {
        String id = "myTask";
        Task task = new TestTask(id, new URI("http://localhost/"));
        Assert.assertEquals(id, task.getID());
    }

    public void testGetAndSetDescription()
            throws Exception {
        Task task = this.createTestTask();
        task.setDescription("abc");
        Assert.assertEquals("abc", task.getDescription());
        try {
            task.setDescription(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetAndSetCreationDate()
            throws Exception {
        Task task = this.createTestTask();
        Assert.assertNotNull(task.getCreationDate());
        Date date = new Date();
        task.setCreationDate(date);
        Assert.assertEquals(date, task.getCreationDate());
        try {
            task.setCreationDate(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }

    public void testGetAndSetFormURL()
            throws Exception {
        URI url1 = new URI("http://localhost/1");
        Task task = new TestTask("taskID", url1);
        Assert.assertEquals(url1, task.getFormURL());
        URI url2 = new URI("http://localhost/2");
        task.setFormURL(url2);
        Assert.assertEquals(url2, task.getFormURL());
        String stringURL = "http://localhost/3";
        URI url3 = new URI(stringURL);
        task.setFormURLFromString(stringURL);
        Assert.assertEquals(url3, task.getFormURL());

        try {
            task.setFormURL((URI) null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }

        try {
            task.setFormURLFromString(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }

        try {
            task.setFormURLFromString("ada:dada:12345#1#2");
            Assert.fail("URISyntaxException expected");
        } catch (InvalidTaskException e) {

        }
    }
    
    public void testSetAndGetInternalID() throws Exception {
        Task task1 = new TestTask("id1", "http://localhost");
        
        task1.setInternalID(13);
        assertEquals(13, task1.getInternalId());
    }
    
    public void testGetFormURLAsString() throws Exception {
        Task task = new TestTask("id1", new URI("http://localhost"));
        assertEquals("http://localhost", task.getFormURLAsString());
    }
    
    public void testAuthorizeActionForUsers() throws Exception {
        Task task = new TestTask("id1", new URI("http://localhost"));
        AuthIdentifierSet ais = new AuthIdentifierSet();
        ais.add("test/user1");
        
        task.authorizeActionForUsers("retrieve", ais);
        
        assertEquals((Collection)ais, task.getAuthorizedUsers("retrieve"));
    }
    
    public void testAuthorizeActionForRoles() throws Exception {
        Task task = new TestTask("id1", new URI("http://localhost"));
        AuthIdentifierSet ais = new AuthIdentifierSet();
        ais.add("test/role1");
        
        task.authorizeActionForRoles("retrieve", ais);
        
        assertEquals((Collection)ais, task.getAuthorizedRoles("retrieve"));
    }

    public void testEquals() throws Exception {
        Task task1 = new TestTask("id1", new URI("http://localhost"));
        Task task2 = new TestTask("id1", new URI("http://localhost"));
        
        assertTrue(task1.equals(task2));
    }
    
    public void testToString() throws Exception {
        Task task1 = new TestTask("id1", new URI("http://localhost"));
        assertEquals("Workflow Task id1", task1.toString());
    }
}
