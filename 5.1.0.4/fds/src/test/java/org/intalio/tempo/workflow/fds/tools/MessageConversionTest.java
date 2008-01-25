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

import junit.framework.TestCase;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.dom4j.util.NodeComparator;
import org.intalio.tempo.workflow.fds.core.UserProcessMessageConvertor;
import org.intalio.tempo.workflow.fds.core.WorkflowProcessesMessageConvertor;

public class MessageConversionTest extends TestCase {

    private static final String _USER_PROCESS_MESSAGE_XML = "userProcessMessage.xml";
    private static final String _WORKFLOW_PROCESSES_MESSAGE_XML = "workflowProcessesMessage.xml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MessageConversionTest.class);
    }

    public MessageConversionTest(String name) {
        super(name);
    }

    @Override
    protected void setUp()
            throws Exception {
        super.setUp();

        System.out.println();
    }

    @Override
    protected void tearDown()
            throws Exception {
        super.tearDown();

        System.out.println();
    }

    public void testUserProcessMessageConversion() throws Exception {
        InputStream inputMessageStream
            = this.getClass().getClassLoader().getResourceAsStream(_USER_PROCESS_MESSAGE_XML);

        Document message = new Builder().build(inputMessageStream);
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

	private Document getMessageDocument(String messageFile) throws ParsingException,
			ValidityException, IOException {
		InputStream inputMessageStream
            = this.getClass().getClassLoader().getResourceAsStream(messageFile);
        return new Builder().build(inputMessageStream);
	}
	

	private void compareDocument(String original, Document converted) throws Exception {
		assertTrue(new NodeComparator().compare( getMessageDocument(original), converted) == 0);
	}
}
