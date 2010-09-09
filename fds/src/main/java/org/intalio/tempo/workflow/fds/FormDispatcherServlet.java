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

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.intalio.tempo.workflow.fds.core.MessageFormatException;
import org.intalio.tempo.workflow.fds.core.MessageSender;
import org.intalio.tempo.workflow.fds.core.UserProcessMessageConvertor;
import org.intalio.tempo.workflow.fds.core.WorkflowProcessesMessageConvertor;
import org.intalio.tempo.workflow.fds.dispatches.Dispatchers;
import org.intalio.tempo.workflow.fds.dispatches.IDispatcher;
import org.intalio.tempo.workflow.fds.dispatches.InvalidInputFormatException;
import org.intalio.tempo.workflow.fds.dispatches.NoDispatcherException;
import org.intalio.tempo.workflow.fds.tools.SoapTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet perfoms message conversion between the Workflow Processes and
 * user processes.
 * <p>
 * The work-cycle of this servlet is as follows:
 * <ul>
 * <li>receive a request from a source component</li>
 * <li>transform the received request to the target component format</li>
 * <li>send the transformed request to the target component</li>
 * <li>get the reply from the target component</li>
 * <li>transform the received reply into the source component format</li>
 * <li>return the transformed reply to the source component</li>
 * </ul>
 * <p>
 * The source component may be the Workflow Processes or a user process.
 * <p>
 * If the source component is a user process, then the target component is
 * always the Workflow Processes. If the source component is the Workflow
 * Processes, then the target is a specific user process, specified inside the
 * incoming request from the Workflow Processes.
 * <p>
 * The servlet decides whether the received request comes from the Workflow
 * Processes or from a user process based on the request URI. If the URI is
 * <code>/workflow/ib4p</code>, then the request is perceived as a Workflow
 * Processes request. Otherwise, if the URI starts with <code>/workflow/</code>
 * but is not <code>/workflow/ib4p</code>, it is understood to be a request from
 * a user process.
 * <p>
 * Other request URI's (not starting with <code>/workflow/</code>) are not
 * handled by this servlet.
 * <p>
 * The method of incoming requests is <code>HTTP POST</code>, the format is
 * SOAP-adherent.
 * 
 * @author Iwan Memruk
 * @version $Revision: 1172 $
 * @see <a href="http://www.w3.org/TR/soap/">The SOAP specification.</a>
 */
public class FormDispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 4254576521832014537L;

    private static Logger _log = LoggerFactory.getLogger(FormDispatcherServlet.class);

    /**
     * The common prefix for all handled request URI's. <br>
     * This must comply to the servlet mapping specified in <code>web.xml</code>
     * .
     */
    private static final String _URI_PREFIX = "/fds/workflow";

    /**
     * The fixed URI for Workflow Processes request.
     */
    private static final String _IB4P_URI = "/ib4p";

    /**
     * The human-readable description of the servlet.
     */
    private static final String _SERVLET_DESCRIPTION = "Intalio|BPMS Form Dispatcher Services";

    /**
     * Instance constructor.
     */
    public FormDispatcherServlet() {

    }

    @Override
    public String getServletInfo() {
        return _SERVLET_DESCRIPTION;
    }

    /**
     * Processes an incoming request.
     * 
     * @param request
     *            The HTTP request incoming from the client.
     * @param response
     *            The HTTP response to return to the client.
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        // fetch the part of the URI after the fixed prefix
        String fdsUri = request.getRequestURI().substring(_URI_PREFIX.length());
        _log.debug("Request URI: " + fdsUri);

        // fetch the SOAP header (we need to propagate it when sending the
        // transforming request to the target component).
        String soapAction = request.getHeader("SOAPAction");
        _log.debug("SOAPAction: " + soapAction);

        // We need both convertors in each case (since we need to convert
        // replies as well
        WorkflowProcessesMessageConvertor wf2up = new WorkflowProcessesMessageConvertor();
        UserProcessMessageConvertor up2wf = new UserProcessMessageConvertor();

        SAXReader reader = new SAXReader();
        MessageSender messageSender = getMessageSender();
        FormDispatcherConfiguration config = FormDispatcherConfiguration.getInstance();

        // the XML document to return as the HTTP response payload
        Document responseDocument = null;
        try {
            if (fdsUri.equals(_IB4P_URI)) {
                // it is a request from the Workflow Processes
                _log.info("Workflow Processes -> User Process");

                _log.debug("Parsing the request from the Workflow Processes.");
                Document workflowProcessesRequest = reader.read(request.getInputStream());
                if (_log.isDebugEnabled()) {
                    _log.debug("Workflow process request:\n" + workflowProcessesRequest.asXML() + "\n");
                    _log.debug("Converting the request to the user process format.");
                }

                // null means that the convertor will figure out the user
                // process namespace itself
                wf2up.convertMessage(workflowProcessesRequest, null);
                if (_log.isDebugEnabled()) {
                    _log.debug("Workflow process request (after conversion):\n" + workflowProcessesRequest.asXML() + "\n");
                }

                if (wf2up.getSoapAction() != null) {
                    soapAction = wf2up.getSoapAction();
                    _log.debug("Completion SOAP Action: '" + soapAction + "'");
                }

                // SOAP Action should always be quoted (WS-Interop)
                if (soapAction == null || soapAction.length() == 0) {
                    soapAction = "\"\"";
                } else if (soapAction.charAt(0) != '\"') {
                    soapAction = "\"" + soapAction + "\"";
                }

                String userProcessEndpoint = wf2up.getUserProcessEndpoint();
                _log.debug("Sending the request to the user process and getting the response");
                Document userProcessResponse = messageSender.requestAndGetReply(workflowProcessesRequest, userProcessEndpoint, soapAction);
                if (_log.isDebugEnabled()) {
                    _log.debug("User process response:\n" + userProcessResponse.asXML() + "\n");
                    _log.debug("Converting the response to the Workflow Processes format.");
                }
                up2wf.convertMessage(userProcessResponse);
                if (_log.isDebugEnabled()) {
                    _log.debug("Sending the converted response back to the Workflow Processes.");
                    _log.debug("User process response (after conversion)\n" + userProcessResponse.asXML() + "\n");
                }
                responseDocument = userProcessResponse;
            } else {
                // it is a request from a user process
                _log.debug("User Process -> Workflow Processes");

                // get the full URL of the workflow process endpoint
                String workflowProcessesEndpoint = config.getPxeBaseUrl() + config.getWorkflowProcessesRelativeUrl();

                _log.debug("Parsing the request from the user process.");
                Document userProcessRequest = reader.read(request.getInputStream());
                if (_log.isDebugEnabled()) {
                    _log.debug("User process request:\n" + userProcessRequest.asXML() + "\n");
                }

                Document pureRequest = SoapTools.unwrapMessage(userProcessRequest);
                Element rootElement = pureRequest.getRootElement();
                String rootElementName = rootElement.getName();

                IDispatcher dispatcher = null;
                try {
                    dispatcher = Dispatchers.createDispatcher(rootElementName);
                } catch (NoDispatcherException e) {
                    _log.debug("No custom dispatcher, using the default processing");
                }

                if (dispatcher != null) {
                    // TODO: convert the default processing to an IDispatcher
                    try {
                        Document processedRequest = dispatcher.dispatchRequest(pureRequest);
                        Document wrappedRequest = SoapTools.wrapMessage(processedRequest);
                        String endpoint = dispatcher.getTargetEndpoint();
                        String dispatcherSoapAction = dispatcher.getTargetSoapAction();
                        Document rawResponse = messageSender.requestAndGetReply(wrappedRequest, endpoint, dispatcherSoapAction);

                        Document unwrappedResponse = SoapTools.unwrapMessage(rawResponse);
                        Document processedResponse = dispatcher.dispatchResponse(unwrappedResponse);
                        responseDocument = SoapTools.wrapMessage(processedResponse);
                    } catch (InvalidInputFormatException e) {
                        _log.error("Error converting user process request", e);
                        // TODO: return a SOAP fault
                        throw new RuntimeException(e);
                    }
                } else {
                    _log.debug("Converting the request to the Workflow Processes format.");
                    up2wf.convertMessage(userProcessRequest);
                    if (_log.isDebugEnabled()) {
                        _log.debug("\n" + userProcessRequest.asXML() + "\n");
                    }
                    String userProcessNamespaceUri = up2wf.getUserProcessNamespaceUri();
                    _log.debug("Sending the converted request to the Workflow Processes and getting the response.");

                    if (up2wf.getSoapAction() != null){
                        soapAction = up2wf.getSoapAction();
                    }
                    _log.debug("SOAP Action:" + soapAction);

                    Document workflowProcessesResponse = messageSender.requestAndGetReply(userProcessRequest, workflowProcessesEndpoint, soapAction);
                    if (_log.isDebugEnabled()) {
                        _log.debug("\n" + workflowProcessesResponse.asXML() + "\n");
                    }
                    _log.debug("Converting the response to the user process format.");
                    wf2up.convertMessage(workflowProcessesResponse, userProcessNamespaceUri);
                    if (_log.isDebugEnabled()) {
                        _log.debug("Sending the converted response back to the user process.");
                        _log.debug("Converted response:\n" + workflowProcessesResponse.asXML() + "\n");
                    }
                    responseDocument = workflowProcessesResponse;
                }
            }

            // for interoperability, try to reuse character-set from request if
            // our platform supports it;
            // otherwise we use UTF-8 as the W3C recommendation.
            // http://www.w3.org/International/O-HTTP-charset
            String charset = request.getCharacterEncoding();
            if (charset == null || !Charset.isSupported(charset))
                charset = "UTF-8";
            response.setCharacterEncoding(charset);
            response.setContentType("text/xml; charset=" + charset);
            response.getWriter().write(responseDocument.asXML());

            _log.info("Request processed OK.");
        } catch (IOException e) {
            _log.warn("Input/output error: " + e.getMessage(), e);
        } catch (DocumentException e) {
            _log.warn("Invalid XML in message: " + e.getMessage(), e);
            // } catch (ParsingException e) {
            // _log.warn("Malformed XML in message: " + e.getMessage(), e);
        } catch (MessageFormatException e) {
            _log.warn("Invalid message format: " + e.getMessage(), e);
        } catch (InvalidInputFormatException e) {
            _log.warn("Invalid message format: " + e.getMessage(), e);
        }
    }

    protected MessageSender getMessageSender() {
        MessageSender messageSender = new MessageSender();
        return messageSender;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }
}
