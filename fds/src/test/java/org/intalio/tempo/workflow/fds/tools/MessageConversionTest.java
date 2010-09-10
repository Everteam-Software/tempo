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
 */
package org.intalio.tempo.workflow.fds.tools;

import java.io.IOException;
import java.io.InputStream;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLTestCase;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.intalio.tempo.workflow.fds.core.UserProcessMessageConvertor;
import org.intalio.tempo.workflow.fds.core.WorkflowProcessesMessageConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class MessageConversionTest extends XMLTestCase {

    private static final String _USER_PROCESS_MESSAGE_XML = "userProcessMessage.xml";
    private static final String _WORKFLOW_PROCESSES_MESSAGE_XML = "workflowProcessesMessage.xml";
    private static final String _CREATE_MESSAGE_XML = "createTask.xml";
    static final Logger log = LoggerFactory.getLogger(MessageConversionTest.class);

    public void testUserProcessMessageConversion() throws Exception {
        InputStream inputMessageStream = this.getClass().getClassLoader()
                .getResourceAsStream(_USER_PROCESS_MESSAGE_XML);

        Document message = new SAXReader().read(inputMessageStream);
        UserProcessMessageConvertor convertor = new UserProcessMessageConvertor();
        convertor.convertMessage(message);
        compareDocument(_USER_PROCESS_MESSAGE_XML, message);
    }

    public void testWorkflowProcessesMessageConversion() throws Exception {
        Document message = getMessageDocument(_WORKFLOW_PROCESSES_MESSAGE_XML);
        WorkflowProcessesMessageConvertor convertor = new WorkflowProcessesMessageConvertor();
        convertor.convertMessage(message, null);
        compareDocument(_WORKFLOW_PROCESSES_MESSAGE_XML, message);
    }
    
    public void testCreateTaskMessageConversion() throws Exception {
        Document message = getMessageDocument(_CREATE_MESSAGE_XML);
        WorkflowProcessesMessageConvertor convertor = new WorkflowProcessesMessageConvertor();
        convertor.convertMessage(message, null);
    }

    private Document getMessageDocument(String messageFile) throws IOException, DocumentException {
        InputStream inputMessageStream = this.getClass().getClassLoader().getResourceAsStream(messageFile);
        return new SAXReader().read(inputMessageStream);
    }

    private void compareDocument(String original, Document converted) throws Exception {
        compareRootNodes(converted, getMessageDocument(original));
    }

    private void compareRootNodes(Document doc1, Document doc2) throws Exception {
        DifferenceListener myDifferenceListener = new DifferenceListener() {
            public int differenceFound(Difference difference) {
                if(difference.getDescription().contains("namespace")) {
                    // we are converting the name spaces, so those should be different.
                    return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                }
                String xpathLocation = difference.getControlNodeDetail().getXpathLocation();
                // this is the only location where the text should be different.
                if ("/Envelope[1]/Header[1]/To[1]/text()[1]".equalsIgnoreCase(xpathLocation))
                    return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                else {
                    return RETURN_ACCEPT_DIFFERENCE;
                }
            }

            public void skippedComparison(Node node, Node node1) {

            }

        };
        Diff myDiff = new Diff(doc1.getRootElement().asXML(), doc2.getRootElement().asXML());
        myDiff.overrideDifferenceListener(myDifferenceListener);
        assertTrue("test XML matches control skeleton XML " + myDiff, myDiff.similar());
    }
}
