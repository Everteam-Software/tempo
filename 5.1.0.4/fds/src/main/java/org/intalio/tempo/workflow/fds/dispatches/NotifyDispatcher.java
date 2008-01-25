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

import java.util.Random;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Text;
import nu.xom.XPathContext;

import org.apache.log4j.Logger;
import org.intalio.tempo.workflow.fds.FormDispatcherConfiguration;
import org.intalio.tempo.workflow.fds.core.MessageConstants;
import org.intalio.tempo.workflow.fds.tools.NamespaceConvertor;
import org.intalio.tempo.workflow.fds.tools.XPath;

class NotifyDispatcher implements IDispatcher {
    private static final Logger LOG = Logger.getLogger(NotifyDispatcher.class);
    
    private static final String TMS_NS = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";

    private String userProcessNamespace;
    private XPathContext xpathContext;

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
        xpathContext = new XPathContext("up", userProcessNamespace);

        XPath xpath = new XPath(xpathContext);
        rootElement.setLocalName("createTaskRequest");
        Element taskElement = new Element("task", userProcessNamespace);
        rootElement.appendChild(taskElement);
        Element metadataElement = xpath.requireElement(rootElement, "up:metadata");
        if (metadataElement.getFirstChildElement("taskId", userProcessNamespace) == null) {
            Element taskIdElement = new Element("taskId", userProcessNamespace);
            taskIdElement.appendChild(new Text(generateUID()));
            metadataElement.insertChild(taskIdElement, 0);
        }
        if (metadataElement.getFirstChildElement("taskType", userProcessNamespace) == null) {
            Element taskTypeElement = new Element("taskType", userProcessNamespace);
            taskTypeElement.appendChild(new Text("NOTIFICATION"));
            metadataElement.insertChild(taskTypeElement, 1);
        }
        metadataElement.detach();
        Element inputElement = xpath.requireElement(rootElement, "up:input");
        inputElement.detach();
        taskElement.appendChild(metadataElement);
        taskElement.appendChild(inputElement);

        // TODO: is this still necessary?
        rootElement.appendChild(new Element("participantToken", userProcessNamespace));

        NamespaceConvertor nsConvertor = new NamespaceConvertor(TMS_NS, xpathContext);
        nsConvertor.addExcludeQuery("/*[1]/up:task/up:input/*");
        nsConvertor.apply(request);

        return request;
    }

    public Document dispatchResponse(Document response) throws InvalidInputFormatException {
        XPathContext globalXPathContext = MessageConstants.getXPathContext();
        Nodes fault = response.query("/soapenv:Envelope/soapenv:Body/soapenv:Fault/node()", globalXPathContext);
        if(fault.size() != 0) {
            // return fault as-is
            LOG.error("Fault response during notify:\n"+response.toXML());
            return response;
        }
        Element rootElement = new Element("notifyResponse", userProcessNamespace);
        Document notifyResponse = new Document(rootElement);
        Element statusElement = new Element("status", userProcessNamespace);
        statusElement.appendChild(new Text("OK"));
        rootElement.appendChild(statusElement);
        return notifyResponse;
    }

    public String getTargetEndpoint() {
        return FormDispatcherConfiguration.getInstance().getTmsUrl();
    }

    public String getTargetSoapAction() {
        return "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/create";
    }
}
