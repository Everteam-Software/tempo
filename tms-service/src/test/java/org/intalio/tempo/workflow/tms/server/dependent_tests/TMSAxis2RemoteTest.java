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

package org.intalio.tempo.workflow.tms.server.dependent_tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.intalio.tempo.workflow.tms.server.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TMSAxis2RemoteTest extends TestCase {

    private static final Logger _logger = LoggerFactory.getLogger(TMSAxis2RemoteTest.class);

    private static final String TMS_ENDPOINT = "http://localhost:8080/axis2/services/TaskManagementServices";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TMSAxis2RemoteTest.class);
    }

    private OMElement sendRequest(OMElement request, String soapAction) throws Exception {
        Options options = new Options();
        options.setAction(soapAction);
        options.setTo(new EndpointReference(TMS_ENDPOINT));

        ServiceClient serviceClient = new ServiceClient();
        serviceClient.setOptions(options);

        try {
            return serviceClient.sendReceive(request);
        } catch (AxisFault f) {
            String message = (f.getMessage() == null ? "(no message)" : f.getMessage());
            String detail = (f.getDetail() == null ? "(no detailed description)" : f.getDetail().getText());
            _logger.error("Remote exception:\n" + message + '\n' + detail);
            if (f.getCause() != null)
                throw (Exception) f.getCause();
            else
                throw f;
        }
    }

    public void requestToServer(String remoteRequestFilename, String expectedAxisFault) throws Exception {
        try {
            OMElement request = TestUtils.loadElementFromResource("/remote/" + remoteRequestFilename);
            OMElement listResponse = sendRequest(request,
                    "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109");
            _logger.info(TestUtils.toPrettyXML(listResponse));
        } catch (AxisFault f) {
            if (expectedAxisFault != null)
                Assert.assertTrue(f.getMessage().contains(expectedAxisFault));
            else
                throw f;
        }
    }

    public void testGetTaskList() throws Exception {
        requestToServer("getTaskListRequest1.xml", null);
        requestToServer("createTaskRequest1.xml", null);
        requestToServer("deleteRequest1.xml", "Only User with System Role");
        requestToServer("getTaskRequest1.xml", null);
    }
}
