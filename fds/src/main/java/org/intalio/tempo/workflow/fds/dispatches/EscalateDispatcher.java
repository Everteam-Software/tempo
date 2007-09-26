package org.intalio.tempo.workflow.fds.dispatches;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Text;

import org.intalio.tempo.workflow.fds.FormDispatcherConfiguration;

class EscalateDispatcher implements IDispatcher {
	private static final String NS_URI = "http://www.intalio.com/bpms/workflow/ib4p_20051115";
	private static final String NS_PREFIX = "b4p";	
	
	private String userProcessNamespace;

	public Document dispatchRequest(Document request)
			throws InvalidInputFormatException {
		Element rootElement = request.getRootElement();
        userProcessNamespace = rootElement.getNamespaceURI();		
		Nodes nodes = request.query("//*");
		for (int i = 0; i < nodes.size(); ++i) {
			Element element = (Element) nodes.get(i);
			element.setNamespaceURI(NS_URI);
			element.setNamespacePrefix(NS_PREFIX);
		}
		rootElement.setLocalName("escalateTaskRequest"); // TODO: fix this in VC!
		return request;
	}

	public Document dispatchResponse(Document response)
			throws InvalidInputFormatException {
		// TODO: process the TMP response
		
        Element rootElement = new Element("escalateResponse", userProcessNamespace);
        Document escalateResponse = new Document(rootElement);
        Element statusElement = new Element("status", userProcessNamespace);
        statusElement.appendChild(new Text("OK"));
        rootElement.appendChild(statusElement);

        return escalateResponse;
	}

	public String getTargetEndpoint() {
		return FormDispatcherConfiguration.getInstance().getPxeBaseUrl() + "/workflow/ib4p";
	}

	public String getTargetSoapAction() {
		return "escalateTask";
	}
}
