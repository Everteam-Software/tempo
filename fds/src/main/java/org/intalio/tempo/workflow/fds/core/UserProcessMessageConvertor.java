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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.XPath;
import org.intalio.tempo.workflow.fds.FormDispatcherConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts SOAP messages from a user process format to the Workflow Processes
 * format.
 * 
 * @author Iwan Memruk
 * @version $Revision: 1172 $
 * @see <a href="http://www.w3.org/TR/soap/">The SOAP specification.</a>
 */
public class UserProcessMessageConvertor {
    private static Logger _log = LoggerFactory.getLogger(UserProcessMessageConvertor.class);

    private static String REQUEST_PREFIX = "userProcess"; // xform

    /**
     * The XML namespace URI of the user process which has been the message
     * source. <br>
     * The value is fetched from the message during the conversion, stored in
     * this field, and may be used later by class users.
     */
    private String _userProcessNamespaceUri;

    /**
     * The endpoint URL of the user process which has been the message source. <br>
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
    @SuppressWarnings("unchecked")
    public void convertMessage(Document message) throws MessageFormatException, AxisFault {
        FormDispatcherConfiguration config = FormDispatcherConfiguration.getInstance();

        XPath xpath = null;
        xpath = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Body/soapenv:Fault");
        List<Node> fault = xpath.selectNodes(message);
        if (fault.size() != 0)
            throw new RuntimeException(fault.toString());

        // Check SOAP action
        xpath = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Body");
        xpath.setNamespaceURIs(MessageConstants.get_nsMap());
        List<Node> bodyQueryResult = xpath.selectNodes(message);
        if (bodyQueryResult.size() != 0) {
            Element root = (Element) bodyQueryResult.get(0);
            if (root.asXML().indexOf("createTaskRequest") != -1) {
                _soapAction = "createTask";
                xpath = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Header/addr:Action");
                xpath.setNamespaceURIs(MessageConstants.get_nsMap());
                List<Node> wsaActionQueryResult = xpath.selectNodes(message);
                if (wsaActionQueryResult.size() != 0) {
                    Element wsaToElement = (Element) wsaActionQueryResult.get(0);
                    wsaToElement.setText(_soapAction);
                } else
                    _log.warn("Did not find addr:Action in SOAP header");
            }
        }
        _log.debug("Converted SOAP Action: " + _soapAction);

        /*
         * Change the wsa:To endpoint to Workflow Processes, if a wsa:To header
         * is present.
         */
        xpath = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Header/addr:To");
        xpath.setNamespaceURIs(MessageConstants.get_nsMap());
        List<Node> wsaToQueryResult = xpath.selectNodes(message);
        if (wsaToQueryResult.size() != 0) {
            Element wsaToElement = (Element) wsaToQueryResult.get(0);
            String workflowProcessesUrl = config.getPxeBaseUrl() + config.getWorkflowProcessesRelativeUrl();
            wsaToElement.setText(workflowProcessesUrl);
        } else
            _log.debug("Did not find addr:To in SOAP header");

        /*
         * Change the session address to be FDS endpoint and retrieve sender
         * endpoint
         */
        xpath = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Header/intalio:callback/addr:Address");
        xpath.setNamespaceURIs(MessageConstants.get_nsMap());
        List<Node> callbackToQueryResult = xpath.selectNodes(message);
        if (callbackToQueryResult.size() != 0) {
            Element wsaToElement = (Element) callbackToQueryResult.get(0);
            _userProcessEndpoint = wsaToElement.getText();
            wsaToElement.setText(config.getFdsUrl());
        } else
            _log.debug("Did not find intalio:callback/addr:Address in SOAP header");

        /* Next, fetch the user process namespace URI from the task metadata */
        /*
         * 1. fetch the first element of SOAP envelope body.
         */
        List<Node> allSoapBodyElements = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Body//*").selectNodes(message);
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

        Map<String, String> namespaceURIs = new HashMap<String, String>(MessageConstants.get_nsMap());
        namespaceURIs.put(REQUEST_PREFIX, _userProcessNamespaceUri);

        /*
         * Add session in task meta data so that it can be retrieved when
         * workflow process needs to send a message to the user process
         */
        xpath = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Header/intalio:callback/intalio:session");
        xpath.setNamespaceURIs(namespaceURIs/* MessageConstants.get_nsMap() */);
        List<Node> sessionQueryResult = xpath.selectNodes(message);
        if (sessionQueryResult.size() != 0) {
            Element wsaToElement = (Element) sessionQueryResult.get(0);
            String session = wsaToElement.getText();
            xpath = DocumentHelper.createXPath("//" + REQUEST_PREFIX + ":taskMetaData");
            xpath.setNamespaceURIs(namespaceURIs/* MessageConstants.get_nsMap() */);
            List<Node> tmdQueryResult = xpath.selectNodes(message);
            Element tmdElement = (Element) tmdQueryResult.get(0);
            Element sessionElement = tmdElement.addElement("session", MessageConstants.IB4P_NS);
            sessionElement.setText(session);
        }

        // retrieve userProcessEndpoint from task meta data
        // or put sender endpoint in task meta data if not defined
        xpath = DocumentHelper.createXPath("//" + REQUEST_PREFIX + ":taskMetaData/" + REQUEST_PREFIX + ":userProcessEndpoint");
        xpath.setNamespaceURIs(namespaceURIs/* MessageConstants.get_nsMap() */);
        List<Node> endpointQueryResult = xpath.selectNodes(message);
        if (endpointQueryResult.size() != 0) {
            Element userProcessEndpointElement = (Element) endpointQueryResult.get(0);
            String value = userProcessEndpointElement.getText();
            if (value != null && value.length() > 0)
                _userProcessEndpoint = value;
            else if (_userProcessEndpoint != null) {
                _log.info("User process endpoint is empty in task metadata, adding " + _userProcessEndpoint);
                userProcessEndpointElement.setText(_userProcessEndpoint);
            }
        } else if (_userProcessEndpoint != null) {
            _log.info("User process endpoint is not defined in task metadata, adding " + _userProcessEndpoint);
            xpath = DocumentHelper.createXPath("//" + REQUEST_PREFIX + ":taskMetaData");
            xpath.setNamespaceURIs(namespaceURIs/* MessageConstants.get_nsMap() */);
            List<Node> tmdQueryResult = xpath.selectNodes(message);
            if (tmdQueryResult.size() > 0) {
                Element wsaToElement = (Element) tmdQueryResult.get(0);
                Element nsElement = wsaToElement.addElement("userProcessEndpoint", MessageConstants.IB4P_NS);
                nsElement.setText(_userProcessEndpoint);
            }
        }

        // Add user process namespace to taskmetadata if not already defined
        xpath = DocumentHelper.createXPath("//" + REQUEST_PREFIX + ":taskMetaData/" + REQUEST_PREFIX + ":userProcessNamespaceURI");
        xpath.setNamespaceURIs(namespaceURIs/* MessageConstants.get_nsMap() */);
        List<Node> nsQueryResult = xpath.selectNodes(message);
        if (nsQueryResult.size() == 0 && _userProcessNamespaceUri != null) {
            xpath = DocumentHelper.createXPath("//" + REQUEST_PREFIX + ":taskMetaData");
            xpath.setNamespaceURIs(namespaceURIs/* MessageConstants.get_nsMap() */);
            List<Node> tmdQueryResult = xpath.selectNodes(message);
            if (tmdQueryResult.size() > 0) {
                _log.info("User process namespace is not defined in task metadata, adding " + _userProcessNamespaceUri);
                Element wsaToElement = (Element) tmdQueryResult.get(0);
                Element nsElement = wsaToElement.addElement("userProcessNamespaceURI", MessageConstants.IB4P_NS);
                nsElement.setText(_userProcessNamespaceUri);
            }
        } else {
            Element wsaToElement = (Element) nsQueryResult.get(0);
            if (wsaToElement.getTextTrim().length() == 0) {
                _log.info("User process namespace is empty in task metadata, adding " + _userProcessNamespaceUri);
                wsaToElement.setText(_userProcessNamespaceUri);
            }
        }

        /*
         * Now, change the namespace of all soapenv:Body elements, except the
         * task input and the attachments, to ib4p.
         */
        xpath = DocumentHelper.createXPath("//" + REQUEST_PREFIX + ":taskInput//*");
        xpath.setNamespaceURIs(namespaceURIs/* MessageConstants.get_nsMap() */);
        List<Node> allTaskInputElements = xpath.selectNodes(message);
        xpath = DocumentHelper.createXPath("//" + REQUEST_PREFIX + ":attachments//*");
        xpath.setNamespaceURIs(namespaceURIs/* MessageConstants.get_nsMap() */);
        List<Node> allAttachmentsElements = xpath.selectNodes(message);
        for (int i = 0; i < allSoapBodyElements.size(); ++i) {
            Node node = (Node) allSoapBodyElements.get(i);
            if (!allTaskInputElements.contains(node) && !allAttachmentsElements.contains(node)) {

                Element element = (Element) node;
                element.remove(element.getNamespace());
                element.setQName(QName.get(element.getName(), "ib4p", MessageConstants.IB4P_NS));
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
     * latest message processed using the <code>convertMessage</code> method. <br>
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
