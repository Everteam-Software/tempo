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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.util.xml.OMDOMConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskMarshallingRoundtripTest extends TestCase {

    private static final Logger _logger = LoggerFactory.getLogger(TaskMarshallingRoundtripTest.class);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TaskMarshallingRoundtripTest.class);
    }

    public void testPAMarshallingRoundtrip()
            throws Exception {
        PATask task1 = new PATask("taskID", new URI("http://localhost/URL"), "processID", "urn:completeSOAPAction",
                TestUtils.createXMLDocument());

        OMFactory factory = OMAbstractFactory.getOMFactory();
        TaskMarshaller marshaller = new TaskMarshaller();
        OMElement marshalledTask = factory.createOMElement("task", TaskXMLConstants.TASK_NAMESPACE,
                TaskXMLConstants.TASK_NAMESPACE_PREFIX);

        marshaller.marshalFullTask(task1, marshalledTask, null);
        _logger.info(TestUtils.toPrettyXML(marshalledTask));

        TaskUnmarshaller unmarshaller = new TaskUnmarshaller();

        Task task2 = unmarshaller.unmarshalFullTask(marshalledTask);

        Assert.assertTrue(task2.equalsTask(task1));
    }

    public void testPIPAMarshallingRoundtrip()
            throws Exception {
        PIPATask task1 = new PIPATask("taskID", new URI("http://localhost/URL"), new URI("http://localhost/URL1"),
                new URI("urn:ns"), "urn:action");

        OMFactory factory = OMAbstractFactory.getOMFactory();
        TaskMarshaller marshaller = new TaskMarshaller();
        OMElement marshalledTask = factory.createOMElement("task", TaskXMLConstants.TASK_NAMESPACE,
                TaskXMLConstants.TASK_NAMESPACE_PREFIX);

        marshaller.marshalFullTask(task1, marshalledTask, null);
        _logger.info(TestUtils.toPrettyXML(marshalledTask));

        TaskUnmarshaller unmarshaller = new TaskUnmarshaller();

        Task task2 = unmarshaller.unmarshalFullTask(marshalledTask);

        Assert.assertTrue(task2.equalsTask(task1));
    }

    public void testNotificationMarshallingRoundtrip() throws Exception {
        Notification task1 = new Notification("taskID", new URI("http://localhost/URL"), 
                TestUtils.createXMLDocument());
        
        OMFactory factory = OMAbstractFactory.getOMFactory();
        TaskMarshaller marshaller = new TaskMarshaller();
        OMElement marshalledTask = factory.createOMElement("task", TaskXMLConstants.TASK_NAMESPACE,
                TaskXMLConstants.TASK_NAMESPACE_PREFIX);

        marshaller.marshalFullTask(task1, marshalledTask, null);
        _logger.debug(TestUtils.toPrettyXML(marshalledTask));

        TaskUnmarshaller unmarshaller = new TaskUnmarshaller();
        
        Task task2 = unmarshaller.unmarshalFullTask(marshalledTask);
        
        Assert.assertTrue(task2.equalsTask(task1));
        _logger.debug(TestUtils.toPrettyXML(OMDOMConvertor.convertDOMToOM(((Notification) task2).getInput(), 
                OMAbstractFactory.getOMFactory())));
    }
}
