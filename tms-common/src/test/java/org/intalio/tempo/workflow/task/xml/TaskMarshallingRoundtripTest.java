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

import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.util.TaskEquality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class TaskMarshallingRoundtripTest extends TestCase {

    private static final Logger _logger = LoggerFactory.getLogger(TaskMarshallingRoundtripTest.class);

    /**
     * Test round trip marhsalling of PA task
     */
    public void testPAMarshallingRoundtrip() throws Exception {
        PATask task1 = new PATask("taskID", new URI("http://localhost/URL"), "processID", "urn:completeSOAPAction",
                TestUtils.createXMLDocument());
        testRoundTrip(task1);
    }

    // /**
    // * //TODO: this does nothing as we are not marshalling unmarshalling
    // PIPATask
    // * <ul>
    // * <li>Retrieve the taskId</li>
    // * <li>checking input and output</li>
    // * </ul>
    // * @see TMSRequestProcessor#
    // */
    // public void testPIPAMarshallingRoundtrip() throws Exception {
    // Document doc = new XmlTooling().getXmlDocument("/fullPIPATask1.xml");
    // PIPATask task1 = new PIPATask("taskID", new URI("http://localhost/URL"),
    // new URI("http://localhost/URL1"), new URI("urn:ns"), "urn:action");
    // }

    /**
     * Test round trip marshaling of notification task
     */
    public void testNotificationMarshallingRoundtrip() throws Exception {
        Notification task1 = new Notification("taskID", new URI("http://localhost/URL"), TestUtils.createXMLDocument());
        testRoundTrip(task1);
    }

    /**
     * Test PA round trip marshalling with input field set
     */
    public void testPAWithInput() throws Exception {
        Document doc = new XmlTooling().getXmlDocument("/inputWithNamespace.xml");
        PATask task = new PATask("taskID", new URI("http://localhost/URL"), "processID", "urn:completeSOAPAction", doc);
        task.setOutput(doc);
        PATask task2 = (PATask) testRoundTrip(task);
        TaskEquality.areDocumentsEqual(doc, task2.getInput());
        TaskEquality.areTasksEquals(task, task2);
    }

    public void testPAWithOutput() throws Exception {

    }

    private Task testRoundTrip(Task task1) throws Exception {
        TaskMarshaller marshaller = new TaskMarshaller();
        task1.getUserOwners().add("user1");
        task1.getUserOwners().add("user2");
        Task task2 = roundMe(task1, marshaller);
        TaskEquality.areTasksEquals(task1, task2);
        return task2;
    }

    private Task roundMe(Task task1, TaskMarshaller marshaller) {
        OMElement marshalledTask = marshaller.marshalFullTask(task1, null);
        Task task2 = new TaskUnmarshaller().unmarshalFullTask(marshalledTask);
        _logger.info(">>" + XmlTooling.taskToString(task2));
        return task2;
    }
}
