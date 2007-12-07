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
package org.intalio.tempo.workflow.fds.dispatches;

import java.util.List;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.XPath;
import org.intalio.tempo.workflow.fds.FormDispatcherConfiguration;
import org.intalio.tempo.workflow.fds.core.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NotifyDispatcher implements IDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(NotifyDispatcher.class);
    
    private static final String TMS_NS = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";

    private String userProcessNamespace;

    private static final String UID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static String generateUID() {
        StringBuilder builder = new StringBuilder();
        builder.append("notify");
        Random random = new Random();
        for (int i = 0; i < 4; ++i) {
            builder.append('-');
            for (int j = 0; j < 4; ++j) {
                builder.append(UID_CHARS.charAt(random.nextInt(UID_CHARS.length())));
            }
        }
        return builder.toString();
    }

    public NotifyDispatcher() {

    }

    public Document dispatchRequest(Document request) throws InvalidInputFormatException {
        Element rootElement = request.getRootElement();
        userProcessNamespace = rootElement.getNamespaceURI();

        rootElement.setName("createTaskRequest");
        Element taskElement = rootElement.addElement("task", userProcessNamespace);

        Element metadataElement = rootElement.element("metadata");
        metadataElement.detach();
        taskElement.add(metadataElement);
        if (metadataElement.selectSingleNode("taskId") == null) {
            Element taskIdElement = metadataElement.addElement("taskId", userProcessNamespace);
            taskIdElement.setText(generateUID());
        }
        if (metadataElement.selectSingleNode("taskType") == null) {
            Element taskTypeElement = metadataElement.addElement("taskType", userProcessNamespace);
            taskTypeElement.setText("NOTIFICATION");
        }

        Element inputElement = rootElement.element("input");
        inputElement.detach();
        taskElement.add(inputElement);

        // TODO: is this still necessary?
        rootElement.addElement("participantToken", userProcessNamespace);

        /*
         * Now, change the namespace the
         * input, to TMS_NS.
         */
        
        XPath xpath = DocumentHelper.createXPath("/createTaskRequest/task/input//*");
        List allTaskInputElements = xpath.selectNodes(request);
        
        xpath = DocumentHelper.createXPath("//*");
        List allBody = xpath.selectNodes(request);
        for (int i = 0; i < allBody.size(); ++i) {
            Node node = (Node)allBody.get(i);
            if (! allTaskInputElements.contains(node)) {
                Element element = (Element) node;
                element.remove(element.getNamespaceForURI(userProcessNamespace));
                element.setQName(QName.get(element.getName(),TMS_NS));
            }
        }

        return request;
    }

    public Document dispatchResponse(Document response) throws InvalidInputFormatException {
    	XPath xpath = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Body/soapenv:Fault");
    	xpath.setNamespaceURIs(MessageConstants.get_nsMap());
        List fault = xpath.selectNodes(response);
        if(fault.size() != 0) {
            // return fault as-is
            LOG.error("Fault response during notify:\n"+response.asXML());
            return response;
        }
        Document notifyResponse = DocumentHelper.createDocument();
        Element rootElement = notifyResponse.addElement("notifyResponse", userProcessNamespace);
        Element statusElement = rootElement.addElement("status", userProcessNamespace);
        statusElement.setText("OK");
        return notifyResponse;
    }

    public String getTargetEndpoint() {
        return FormDispatcherConfiguration.getInstance().getTmsUrl();
    }

    public String getTargetSoapAction() {
        return "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/create";
    }
}
