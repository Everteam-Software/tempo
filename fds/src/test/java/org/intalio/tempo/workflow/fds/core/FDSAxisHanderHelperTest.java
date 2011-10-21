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
package org.intalio.tempo.workflow.fds.core;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;


import junit.framework.TestCase;

public class FDSAxisHanderHelperTest extends TestCase{   
    //needs to be static as this object needs to statefull
    private static FDSAxisHandlerHelper helper = new FDSAxisHandlerHelper(false);
    
    private static String ubpSoapAction  = "http://www.example.com/AbsenceRequest/AbsenceRequest_To_Manager/AbsenceRequest_To_Manager_PortType/Receive_Absence_Request--006";
    private static String userProcessNamespaceURI = "http://www.intalio.com/workflow/forms/AbsenceRequest/AbsenceRequest";
    private static String tmpProcessNamespaceURI = "http://www.intalio.com/bpms/workflow/ib4p_20051115";

    
    private Document createUBPRequest() throws Exception {
        return new SAXReader().read(this.getClass().getResourceAsStream("/UBPRequest.xml"));
    }

    private Document createWFResponse() throws Exception {
        return new SAXReader().read(this.getClass().getResourceAsStream("/TMPResponse.xml"));
    }    
    
    public void testProcessOutMessage() throws Exception {
        Document transformedRequest = helper.processOutMessage(createUBPRequest(), null,"");
        assertEquals("createTask", helper.getSoapAction());
        assertEquals("http://localhost:8080/ode/workflow/ib4p", helper.getTargetEPR());
        
        XPath xpathSelector = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Body/*[1]");
        xpathSelector.setNamespaceURIs(MessageConstants.get_nsMap());
        List<Node> bodyNodes = xpathSelector.selectNodes(transformedRequest);
        
        assertEquals(true, !bodyNodes.isEmpty());
        
        if(!bodyNodes.isEmpty()){
            Node payload = bodyNodes.get(0);            
            assertEquals(tmpProcessNamespaceURI, ((Element)payload).getNamespaceURI());            
        }

        xpathSelector = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Body//ib4p:session");
        xpathSelector.setNamespaceURIs(MessageConstants.get_nsMap());
        bodyNodes = xpathSelector.selectNodes(transformedRequest);
        
        assertEquals(true, !bodyNodes.isEmpty());
        
        if(!bodyNodes.isEmpty()){
            Node payload = bodyNodes.get(0);            
            assertEquals(tmpProcessNamespaceURI, ((Element)payload).getNamespaceURI());            
        }        
        
    }
    
    public void testProcessInMessage() throws Exception {
        Document transformedRequest = helper.processInMessage(createWFResponse(), "createTaskResponse","");
        assertEquals("createTaskResponse", helper.getSoapAction());

        XPath xpathSelector = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Body/*[1]");
        xpathSelector.setNamespaceURIs(MessageConstants.get_nsMap());
        List<Node> bodyNodes = xpathSelector.selectNodes(transformedRequest);
        
        assertEquals(true, !bodyNodes.isEmpty());
        
        if(!bodyNodes.isEmpty()){
            Node payload = bodyNodes.get(0);            
            assertEquals(userProcessNamespaceURI, ((Element)payload).getNamespaceURI());            
        }        
        
    }   
}
