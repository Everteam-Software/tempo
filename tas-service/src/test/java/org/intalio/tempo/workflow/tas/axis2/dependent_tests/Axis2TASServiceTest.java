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
package org.intalio.tempo.workflow.tas.axis2.dependent_tests;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.intalio.tempo.workflow.tas.axis2.TASAxis2Bridge;
import org.intalio.tempo.workflow.tas.axis2.TASAxis2BridgeTest;
import org.intalio.tempo.workflow.tas.axis2.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Axis2TASServiceTest extends TestCase {

    private static final Logger _logger = LoggerFactory.getLogger(TASAxis2BridgeTest.class);

    private static final String TAS_ENDPOINT = "http://localhost:8080/axis2/services/tas";
    private static final String ADD_SOAP_ACTION = "http://www.intalio.com/BPMS/Workflow/TaskAttachmentService/add";
    private static final String DELETE_SOAP_ACTION
        = "http://www.intalio.com/BPMS/Workflow/TaskAttachmentService/delete";

    private static OMElement sendRequest(OMElement request, String soapAction)
            throws Throwable {
        Options options = new Options();
        options.setAction(soapAction);
        options.setTo(new EndpointReference(TAS_ENDPOINT));

        ServiceClient serviceClient = new ServiceClient();
        serviceClient.setOptions(options);

        OMElement result = null;
        try {
            result = serviceClient.sendReceive(request);
        } catch (AxisFault f) {
            String message = (f.getMessage() == null ? "(no message)" : f.getMessage());
            String detail = (f.getDetail() == null ? "(no detailed description)" : f.getDetail().getText());
            _logger.error("Remote exception:\n" + message + '\n' + detail, f);
            throw f.getCause();
        }
        return result;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Axis2TASServiceTest.class);
    }

    public void testAddDelete() throws Throwable {
        try {
            final QName URL = new QName(TASAxis2Bridge.TAS_XMLNS, "url"); 
            String url;
            {
                OMElement addRequest = TestUtils.loadElementFromResource("/remoteAddRequest.xml");
                _logger.debug(TestUtils.toPrettyXML(addRequest));
                OMElement response = Axis2TASServiceTest.sendRequest(addRequest, ADD_SOAP_ACTION);
                _logger.debug(TestUtils.toPrettyXML(response));
                OMElement urlElement = response.getFirstChildWithName(URL);
                assertNotNull(urlElement);
                url = urlElement.getText();
                assertNotNull(url);
            }
            
            final QName ATTACHMENT_URL = new QName(TASAxis2Bridge.TAS_XMLNS, "attachmentURL"); 
            {
                OMElement deleteRequest = TestUtils.loadElementFromResource("/remoteDeleteRequest.xml");
                OMElement urlElement = deleteRequest.getFirstChildWithName(ATTACHMENT_URL);
                assertNotNull(urlElement);
                urlElement.setText(url);
                _logger.debug(TestUtils.toPrettyXML(deleteRequest));
                OMElement response = Axis2TASServiceTest.sendRequest(deleteRequest, DELETE_SOAP_ACTION);
                _logger.debug(TestUtils.toPrettyXML(response));
            }

        } catch (Exception e) {
            _logger.error("error", e);
            throw e;
        }
        
    }

}
