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

import org.custommonkey.xmlunit.XMLTestCase;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.intalio.tempo.workflow.fds.core.UserProcessMessageConvertor;
import org.intalio.tempo.workflow.fds.core.WorkflowProcessesMessageConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageConversionTest extends XMLTestCase {

    private static final String _USER_PROCESS_MESSAGE_XML = "userProcessMessage.xml";
    private static final String _WORKFLOW_PROCESSES_MESSAGE_XML = "workflowProcessesMessage.xml";
    static final Logger log = LoggerFactory.getLogger(MessageConversionTest.class);

    public void testUserProcessMessageConversion() throws Exception {
        InputStream inputMessageStream
            = this.getClass().getClassLoader().getResourceAsStream(_USER_PROCESS_MESSAGE_XML);

        Document message = new SAXReader().read(inputMessageStream);
        UserProcessMessageConvertor convertor = new UserProcessMessageConvertor();
        convertor.convertMessage(message);
        compareDocument(_USER_PROCESS_MESSAGE_XML,message);
    }

    public void testWorkflowProcessesMessageConversion() throws Exception {
        Document message = getMessageDocument(_WORKFLOW_PROCESSES_MESSAGE_XML);
        WorkflowProcessesMessageConvertor convertor = new WorkflowProcessesMessageConvertor();
        convertor.convertMessage(message, null);
        compareDocument(_WORKFLOW_PROCESSES_MESSAGE_XML,message);
    }

	private Document getMessageDocument(String messageFile) throws IOException, DocumentException {
		InputStream inputMessageStream
            = this.getClass().getClassLoader().getResourceAsStream(messageFile);
        return new SAXReader().read(inputMessageStream);
	}
	

	private void compareDocument(String original, Document converted) throws Exception {
	    compareRootNodes(converted, getMessageDocument(original));
	}
	
	private void compareRootNodes(Document doc1, Document doc2) throws Exception {
	    assertXMLEqual(doc1.getRootElement().asXML(), doc2.getRootElement().asXML());
	}
}
