/**
 * Copyright (c) 2005-2007 Intalio inc.
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

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.intalio.tempo.workflow.fds.FormDispatcherConfiguration;

class EscalateDispatcher implements IDispatcher {
	private static final String NS_URI = "http://www.intalio.com/bpms/workflow/ib4p_20051115";
	private static final String NS_PREFIX = "b4p";	
	
	private String userProcessNamespace;

	public Document dispatchRequest(Document request)
			throws InvalidInputFormatException {
		Element rootElement = request.getRootElement();
        userProcessNamespace = rootElement.getNamespaceURI();		
		List nodes = DocumentHelper.createXPath("//*").selectNodes(request);
		for (int i = 0; i < nodes.size(); ++i) {
			Element element = (Element) nodes.get(i);
			element.addNamespace(NS_PREFIX, NS_URI);
		}
		rootElement.setName("escalateTaskRequest"); // TODO: fix this in VC!
		return request;
	}

	public Document dispatchResponse(Document response)
			throws InvalidInputFormatException {
		// TODO: process the TMP response
		Document document = DocumentHelper.createDocument();
		
        Element rootElement = document.addElement("escalateResponse");
        rootElement.addNamespace(null, userProcessNamespace);

        Element statusElement = rootElement.addElement("status");
        statusElement.addNamespace(null, userProcessNamespace);
        statusElement.setText("OK");

        return document;
	}

	public String getTargetEndpoint() {
		return FormDispatcherConfiguration.getInstance().getPxeBaseUrl() + "/workflow/ib4p";
	}

	public String getTargetSoapAction() {
		return "escalateTask";
	}
}
