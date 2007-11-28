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

package org.intalio.tempo.workflow.tms.server;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TMSRequestProcessorTest extends TestCase {

    private static final Logger _logger = LoggerFactory.getLogger(TMSRequestProcessorTest.class);

    private TMSRequestProcessor createRequestProcessor() throws Exception {
        ITMSServer server = TestUtils.createTMSServer();
        TMSRequestProcessor proc = new TMSRequestProcessor();
        proc.setServer(server);
        return proc;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TMSRequestProcessorTest.class);
    }

    public void testCreateAndGetTaskList() throws Exception {
        TMSRequestProcessor requestProcessor = this.createRequestProcessor();

        OMElement createTaskRequest = TestUtils.loadElementFromResource("/createTaskRequest1.xml");
        OMElement createTaskResponse = requestProcessor.create(createTaskRequest);
        _logger.debug(TestUtils.toPrettyXML(createTaskResponse));

        OMElement getTaskListRequest = TestUtils.loadElementFromResource("/getTaskListRequest1.xml");
        OMElement getTaskListResponse = requestProcessor.getTaskList(getTaskListRequest);
        _logger.debug(TestUtils.toPrettyXML(getTaskListResponse));
    }

    public void testGetTask() throws Exception {
        TMSRequestProcessor requestProcessor = this.createRequestProcessor();

        OMElement createTaskRequest = TestUtils.loadElementFromResource("/createTaskRequest1.xml");
        requestProcessor.create(createTaskRequest);

        OMElement getTaskRequest1 = TestUtils.loadElementFromResource("/getTaskRequest1.xml");
        OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
        _logger.debug(TestUtils.toPrettyXML(getTaskResponse1));
    }

    public void testDelete() throws Exception {
        TMSRequestProcessor requestProcessor = this.createRequestProcessor();

        OMElement createTaskRequest1 = TestUtils.loadElementFromResource("/createTaskRequest1.xml");
        requestProcessor.create(createTaskRequest1);
        OMElement createTaskRequest2 = TestUtils.loadElementFromResource("/createTaskRequest2.xml");
        requestProcessor.create(createTaskRequest2);

        OMElement deleteRequest = TestUtils.loadElementFromResource("/deleteRequest1.xml");
        OMElement deleteResponse = requestProcessor.delete(deleteRequest);
        _logger.debug(TestUtils.toPrettyXML(deleteResponse));
    }

    public void testSetOutput() throws Exception {
        TMSRequestProcessor requestProcessor = this.createRequestProcessor();

        OMElement createTaskRequest = TestUtils.loadElementFromResource("/createTaskRequest1.xml");
        requestProcessor.create(createTaskRequest);

        OMElement setOutputRequest = TestUtils.loadElementFromResource("/setOutputRequest1.xml");
        requestProcessor.setOutput(setOutputRequest);

        OMElement getTaskRequest1 = TestUtils.loadElementFromResource("/getTaskRequest1.xml");
        OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
        _logger.debug(TestUtils.toPrettyXML(getTaskResponse1));
    }

    public void testComplete() throws Exception {
        TMSRequestProcessor requestProcessor = this.createRequestProcessor();

        OMElement createTaskRequest = TestUtils.loadElementFromResource("/createTaskRequest1.xml");
        requestProcessor.create(createTaskRequest);

        OMElement completeRequest = TestUtils.loadElementFromResource("/completeRequest1.xml");
        requestProcessor.complete(completeRequest);

        OMElement getTaskRequest1 = TestUtils.loadElementFromResource("/getTaskRequest1.xml");
        OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
        _logger.debug(TestUtils.toPrettyXML(getTaskResponse1));
    }

    public void testSetOutputAndComplete() throws Exception {
        TMSRequestProcessor requestProcessor = this.createRequestProcessor();

        OMElement createTaskRequest = TestUtils.loadElementFromResource("/createTaskRequest1.xml");
        requestProcessor.create(createTaskRequest);

        OMElement setOutputAndCompleteRequest = TestUtils.loadElementFromResource("/setOutputAndCompleteRequest1.xml");
        requestProcessor.setOutputAndComplete(setOutputAndCompleteRequest);

        OMElement getTaskRequest1 = TestUtils.loadElementFromResource("/getTaskRequest1.xml");
        OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
        _logger.debug(TestUtils.toPrettyXML(getTaskResponse1));
    }

    public void testFail() throws Exception {
        TMSRequestProcessor requestProcessor = this.createRequestProcessor();

        OMElement createTaskRequest = TestUtils.loadElementFromResource("/createTaskRequest1.xml");
        requestProcessor.create(createTaskRequest);

        OMElement failRequest = TestUtils.loadElementFromResource("/failRequest1.xml");
        requestProcessor.fail(failRequest);

        OMElement getTaskRequest1 = TestUtils.loadElementFromResource("/getTaskRequest1.xml");
        OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
        _logger.debug(TestUtils.toPrettyXML(getTaskResponse1));
    }

    public void testAddGetAndRemoveAttachment() throws Exception {
        TMSRequestProcessor requestProcessor = this.createRequestProcessor();

        OMElement createTaskRequest = TestUtils.loadElementFromResource("/createTaskRequest1.xml");
        requestProcessor.create(createTaskRequest);

        OMElement addAttachmentRequest = TestUtils.loadElementFromResource("/addAttachmentRequest1.xml");
        requestProcessor.addAttachment(addAttachmentRequest);

        OMElement getTaskRequest1 = TestUtils.loadElementFromResource("/getTaskRequest1.xml");
        OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
        _logger.debug(TestUtils.toPrettyXML(getTaskResponse1));

        OMElement getAttachmentsRequest1 = TestUtils.loadElementFromResource("/getAttachmentsRequest1.xml");
        OMElement getAttachmentsResponse1 = requestProcessor.getAttachments(getAttachmentsRequest1);
        _logger.debug(TestUtils.toPrettyXML(getAttachmentsResponse1));

        OMElement removeAttachmentRequest = TestUtils.loadElementFromResource("/removeAttachmentRequest1.xml");
        requestProcessor.removeAttachment(removeAttachmentRequest);

        OMElement getTaskResponse2 = requestProcessor.getTask(getTaskRequest1);
        _logger.debug(TestUtils.toPrettyXML(getTaskResponse2));
    }

    public void testMessedUpAttachmentMetadata() throws Exception {
        TMSRequestProcessor requestProcessor = this.createRequestProcessor();

        OMElement createTaskRequest = TestUtils.loadElementFromResource("/createTaskRequest1.xml");
        requestProcessor.create(createTaskRequest);

        OMElement addAttachmentRequest = TestUtils.loadElementFromResource("/addAttachmentRequest2.xml");
        requestProcessor.addAttachment(addAttachmentRequest);

        OMElement getTaskRequest1 = TestUtils.loadElementFromResource("/getTaskRequest1.xml");
        OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
        _logger.debug(TestUtils.toPrettyXML(getTaskResponse1));

        OMElement getAttachmentsRequest1 = TestUtils.loadElementFromResource("/getAttachmentsRequest1.xml");
        OMElement getAttachmentsResponse1 = requestProcessor.getAttachments(getAttachmentsRequest1);
        _logger.debug(TestUtils.toPrettyXML(getAttachmentsResponse1));
    }
}
