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

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;
import nu.xom.XPathContext;

import org.apache.log4j.Logger;
import org.intalio.tempo.workflow.fds.FormDispatcherConfiguration;

/**
 * Converts SOAP messages from a user process format to the Workflow Processes
 * format.
 * 
 * @author Iwan Memruk
 * @version $Revision: 1172 $
 * @see <a href="http://www.w3.org/TR/soap/">The SOAP specification.</a>
 */
public class UserProcessMessageConvertor {
    private static Logger _log = Logger.getLogger(UserProcessMessageConvertor.class);

    /**
     * The XML namespace URI of the user process which has been the message
     * source. <br>
     * The value is fetched from the message during the conversion, stored in
     * this field, and may be used later by class users.
     */
    private String _userProcessNamespaceUri;

    /**
     * The endpoint URL of the user process which has been the message source.
     * <br>
     * The value is fetched from the message during the conversion, stored in
     * this field, and may be used later by class users.
     */
    private String _userProcessEndpoint = null;
    
    /**
     * Converted soap action, if applicable
     */
    private String _soapAction = null;

    /**
     * Converts a SOAP message from a user process to the WorkflowProcesses
     * format. <br>
     * The conversion is done in-place. The passed <code>Document</code>
     * instance gets converted to the Workflow Processes format and its previous
     * format is lost.
     * 
     * @param message
     *            The SOAP message from a user process to convert to the
     *            Workflow Processes format.
     * @throws MessageFormatException
     *             If the specified message has an invalid format. Note that if
     *             this exception is thrown, <code>message</code> may have
     *             already been partly processed and therefore should be assumed
     *             to be corrupted.
     */
    public void convertMessage(Document message)
            throws MessageFormatException {
        XPathContext globalXPathContext = MessageConstants.getXPathContext();
        FormDispatcherConfiguration config = FormDispatcherConfiguration.getInstance();

        Nodes fault = message.query("/soapenv:Envelope/soapenv:Body/soapenv:Fault/node()", globalXPathContext);
        if(fault.size() != 0) {
            // return fault as-is
            _log.error("Fault in response:\n"+message.toXML());
            return;
        }
        
        //Check SOAP action
        Nodes bodyQueryResult = message.query("/soapenv:Envelope/soapenv:Body/node()", globalXPathContext);
        if(bodyQueryResult.size() != 0) {
        	Node root = bodyQueryResult.get(0);
        	if(root.toString().indexOf("createTaskRequest") != -1) {
        		_soapAction = "createTask";
        		Nodes wsaActionQueryResult = message.query("/soapenv:Envelope/soapenv:Header/addr:Action[1]", globalXPathContext);
                if (wsaActionQueryResult.size() != 0) {
                    Element wsaToElement = (Element) wsaActionQueryResult.get(0);
                    wsaToElement.removeChildren();
                    wsaToElement.appendChild(new Text(_soapAction));
                } else _log.warn("Did not find addr:Action in SOAP header");
        	}
        }
        _log.debug("Converted SOAP Action: " + _soapAction);

        /*
         * Change the wsa:To endpoint to Workflow Processes, if a wsa:To
         * header is present.
         */
        Nodes wsaToQueryResult = message.query("/soapenv:Envelope/soapenv:Header/addr:To[1]", globalXPathContext);
        if (wsaToQueryResult.size() != 0) {
            Element wsaToElement = (Element) wsaToQueryResult.get(0);
            String workflowProcessesUrl = config.getPxeBaseUrl() + config.getWorkflowProcessesRelativeUrl();
            wsaToElement.removeChildren();
            wsaToElement.appendChild(new Text(workflowProcessesUrl));
        } else _log.debug("Did not find addr:To in SOAP header");
        
        /*
         * Change the session address to be FDS endpoint
         * and retrieve sender endpoint
         */
        Nodes callbackToQueryResult = message.query("/soapenv:Envelope/soapenv:Header/intalio:callback/addr:Address", globalXPathContext);
        if (callbackToQueryResult.size() != 0) {
            Element wsaToElement = (Element) callbackToQueryResult.get(0);
            _userProcessEndpoint = wsaToElement.getValue();
            wsaToElement.removeChildren();
            wsaToElement.appendChild(new Text(config.getFdsUrl()));
        } else _log.debug("Did not find intalio:callback/addr:Address in SOAP header");

        /* Next, fetch the user process namespace URI from the task metadata */
        /*
         * 1. fetch the first element of SOAP envelope body.
         */
        Nodes allSoapBodyElements = message.query("/soapenv:Envelope/soapenv:Body//*", globalXPathContext);
        if (allSoapBodyElements.size() == 0) {
            throw new MessageFormatException("No elements found inside soapenv:Body.");
        }
        Element firstPayloadElement = (Element) allSoapBodyElements.get(0);

        /*
         * 2. fetch its namespace and use it to fetch the userProcessEndpoint
         * and userProcessNamespaceURI element (which should be in the same
         * namespace). If those elements are not found, nothing is reported.
         * This is necessary for converting responses, where this information is
         * not present.
         */
        String messageNamespace = firstPayloadElement.getNamespaceURI();
        _userProcessNamespaceUri = messageNamespace;

        XPathContext messageXPathContext = new XPathContext();
        messageXPathContext.addNamespace("userProcess", messageNamespace);
        
        /*
         * Add session in task meta data
         * so that it can be retrieved when workflow process needs to send a message to the user process
         */
        Nodes sessionQueryResult = message.query("/soapenv:Envelope/soapenv:Header/intalio:callback/intalio:session", globalXPathContext);
        if (sessionQueryResult.size() != 0) {
            Element wsaToElement = (Element) sessionQueryResult.get(0);
            String session = wsaToElement.getValue();
            Nodes tmdQueryResult = message.query("//userProcess:taskMetaData", messageXPathContext);
            Element tmdElement = (Element) tmdQueryResult.get(0);
            Element sessionElement = new Element("session", MessageConstants.IB4P_NS);
            sessionElement.setNamespacePrefix("ib4p");
            sessionElement.appendChild(new Text(session));
            tmdElement.appendChild(sessionElement);
        }

        //retrieve userProcessEndpoint from task meta data 
        //or put sender endpoint in task meta data if not defined
        Nodes endpointQueryResult = message.query("//userProcess:taskMetaData/userProcess:userProcessEndpoint[1]",
                messageXPathContext);
        if (endpointQueryResult.size() != 0) {
            Element userProcessEndpointElement = (Element) endpointQueryResult.get(0);
            String value = userProcessEndpointElement.getValue();
            if(value != null && value.length()>0) _userProcessEndpoint = value;
            else if(_userProcessEndpoint != null){
            	_log.info("User process endpoint is empty in task metadata, adding " + _userProcessEndpoint);
            	userProcessEndpointElement.removeChildren();
            	userProcessEndpointElement.appendChild(_userProcessEndpoint);
            }
        } else if(_userProcessEndpoint != null) {
        	_log.info("User process endpoint is not defined in task metadata, adding " + _userProcessEndpoint);
        	Nodes tmdQueryResult = message.query("//userProcess:taskMetaData[1]",
                    messageXPathContext);
        	if(tmdQueryResult.size()>0) {
	            Element wsaToElement = (Element) tmdQueryResult.get(0);
	            Element nsElement = new Element("userProcessEndpoint", MessageConstants.IB4P_NS);
	            nsElement.appendChild(_userProcessEndpoint);
	            wsaToElement.appendChild(nsElement);
        	}
        }
        
        //Add user process namespace to taskmetadata if not already defined
        
        Nodes nsQueryResult = message.query("//userProcess:taskMetaData/userProcess:userProcessNamespaceURI[1]",
                messageXPathContext);
        if (nsQueryResult.size() == 0 && _userProcessNamespaceUri != null) {
        	Nodes tmdQueryResult = message.query("//userProcess:taskMetaData[1]",
                    messageXPathContext);
        	if(tmdQueryResult.size()>0) {
            	_log.info("User process namespace is not defined in task metadata, adding " + _userProcessNamespaceUri);
	            Element wsaToElement = (Element) tmdQueryResult.get(0);
	            Element nsElement = new Element("userProcessNamespaceURI", MessageConstants.IB4P_NS);
	            nsElement.appendChild(_userProcessNamespaceUri);
	            wsaToElement.appendChild(nsElement);
        	}
        } else {
        	Element wsaToElement = (Element) nsQueryResult.get(0);
        	if(wsaToElement.getValue().length() == 0) {
            	_log.info("User process namespace is empty in task metadata, adding " + _userProcessNamespaceUri);
        		wsaToElement.removeChildren();
                wsaToElement.appendChild(_userProcessNamespaceUri);
        	}
        }
        
        /*
         * Now, change the namespace of all soapenv:Body elements, except the
         * task input, to ib4p.
         */
        Nodes allTaskInputElements = message.query("//userProcess:taskInput//*", messageXPathContext);

        for (int i = 0; i < allSoapBodyElements.size(); ++i) {
            Node node = allSoapBodyElements.get(i);
            if (! allTaskInputElements.contains(node)) {
                Element element = (Element) node;
                element.setNamespaceURI(MessageConstants.IB4P_NS);
                element.setNamespacePrefix("ib4p");
            }
        }
    }

    /**
     * The XML namespace URI of the user process which has been the source of
     * the latest message processed using the <code>convertMessage</code>
     * method. <br>
     * 
     * @return The XML namespace URI of the user process which has been the
     *         source of the latest message processed using the
     *         <code>convertMessage</code> method.
     * @see #convertMessage(Document)
     */
    public String getUserProcessNamespaceUri() {
        return _userProcessNamespaceUri;
    }

    /**
     * The endpoint URL of the user process which has been the source of the
     * latest message processed using the <code>convertMessage</code> method.
     * <br>
     * 
     * @return The endpoint URL of the user process which has been the source of
     *         the latest message processed using the
     *         <code>convertMessage</code> method.
     * @see #convertMessage(Document)
     */
    public String getUserProcessEndpoint() {
        return _userProcessEndpoint;
    }
    
    public String getSoapAction() {
    	return _soapAction;
    }
}
