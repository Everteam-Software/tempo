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
package org.intalio.tempo.workflow.fds;

import java.io.InputStream;

import org.intalio.tempo.workflow.fds.core.UserProcessMessageConvertor;
import org.intalio.tempo.workflow.fds.core.WorkflowProcessesMessageConvertor;

import nu.xom.Builder;
import nu.xom.Document;

import junit.framework.TestCase;

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
        System.out.println(message.toXML());
    }

    public void testWorkflowProcessesMessageConversion() throws Exception {
        InputStream inputMessageStream
            = this.getClass().getClassLoader().getResourceAsStream(_WORKFLOW_PROCESSES_MESSAGE_XML);

        Document message = new Builder().build(inputMessageStream);
        WorkflowProcessesMessageConvertor convertor = new WorkflowProcessesMessageConvertor();
        convertor.convertMessage(message, null);
        System.out.println(message.toXML());
    }
}
