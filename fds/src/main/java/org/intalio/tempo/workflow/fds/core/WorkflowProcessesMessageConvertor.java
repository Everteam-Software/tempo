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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts SOAP messages from the Workflow Processes format to user process
 * formats.
 * 
 * @author Iwan Memruk
 * @version $Revision: 1172 $
 * @see <a href="http://www.w3.org/TR/soap/">The SOAP specification.</a>
 */
public class WorkflowProcessesMessageConvertor {
    private static final Logger LOG = LoggerFactory.getLogger(UserProcessMessageConvertor.class);

    /**
     * The XML namespace URI of the user process which has been the message
     * source. <br>
     * The value is fetched from the message during the conversion <i>if no user
     * process namespace has been specified to the <code>converMessage</code>
     * method</i>, stored in this field, and may be used later by class users.
     * 
     * @see #convertMessage(Document, String)
     */
    private String _userProcessNamespaceUri;

    /**
     * The endpoint URL of the user process which has been the message source.
     * <br>
     * The value is fetched from the message during the conversion, stored in
     * this field, and may be used later by class users. <br>
     * Contains <code>null<code> if the processes message was a reply.
     */
    private String _userProcessEndpoint;

    private String _soapAction;
    
    /**
     * Converts a SOAP message from the Workflow Processes format to the format
     * of the user process the message is targetted for. <br>
     * The target user process is figured out from the message payload. <br>
     * The conversion is done in-place. The passed <code>Document</code>
     * instance gets converted to the user process format and its previous
     * format is lost.
     * 
     * @param message
     *            The SOAP message coming from the Workflow Processes to convert
     *            to the user process format.
     * @param userProcessNamespaceUri
     *            The user process namespace URI. Should be <code>null</code>
     *            when converting the <i>requests</i>. Must be specified when
     *            converting the <i>replies</i>, since in this case no
     *            information about the target user process is specified inside
     *            the message.
     * @throws MessageFormatException
     *             If the specified message has an invalid format. Note that if
     *             this exception is thrown, <code>message</code> may have
     *             already been partly processed and therefore should be assumed
     *             to be corrupted.
     */
    public void convertMessage(Document message, String userProcessNamespaceUri)
            throws MessageFormatException {
        XPathContext xPathContext = MessageConstants.getXPathContext();
        Nodes fault = message.query("/soapenv:Envelope/soapenv:Body/soapenv:Fault/node()", xPathContext);
        if(fault.size() != 0) {
            // return fault as-is
            LOG.error("Fault in response:\n"+message.toXML());
            return;
        }
                
        //retrieve session
        Nodes sessionNodes = message.query("//ib4p:taskMetaData/ib4p:session[1]", xPathContext);
        if (sessionNodes.size() > 0) {
            Element sessionElement = (Element) sessionNodes.get(0);
            String session = sessionElement.getValue();
            
            //remove callback
            Nodes callbackNodes = message.query("/soapenv:Envelope/soapenv:Header/intalio:callback", xPathContext);
            if (callbackNodes.size() != 0) {
                Element wsaTo = (Element) callbackNodes.get(0);
                Element header = (Element)wsaTo.getParent();
                header.removeChild(wsaTo);
                sessionElement = new Element("session", MessageConstants.INTALIO_NS);
                sessionElement.appendChild(new Text(session));
                header.appendChild(sessionElement);
            }
        }

        /* fetch the user process endpoint element from the task metadata */
        Nodes userProcessEndpointNodes = message.query("//ib4p:taskMetaData/ib4p:userProcessEndpoint[1]", xPathContext);
        if (userProcessEndpointNodes.size() > 0) {
            /* found the user process endpoint element */
            Element userProcessEndpointElement = (Element) userProcessEndpointNodes.get(0);
            /* save it for later use */
            _userProcessEndpoint = userProcessEndpointElement.getValue();

            /* do we have a wsa:To element? */
            Nodes wsaToNodes = message.query("//wsa:To", xPathContext);
            if (wsaToNodes.size() != 0) {
                /* We have the wsa:To element. Set the correct target endpoint */
                Element wsaTo = (Element) wsaToNodes.get(0);
                wsaTo.removeChildren();
                wsaTo.appendChild(new Text(_userProcessEndpoint));
            }

            /* do we have a addr:To element? */
            Nodes addrToNodes = message.query("//addr:To", xPathContext);
            if (addrToNodes.size() != 0) {
                /* We have the wsa:To element. Set the correct target endpoint */
                Element wsaTo = (Element) addrToNodes.get(0);
                wsaTo.removeChildren();
                wsaTo.appendChild(new Text(_userProcessEndpoint));
            }
        }
        

        /*
         * If the user process namespace URI is not specified explicitly, the
         * userProcessNamespaceURI element must be present in the metadata
         * section.
         */
        if (userProcessNamespaceUri == null) {
            Nodes namespaceElementQueryResult = message.query("//ib4p:taskMetaData/ib4p:userProcessNamespaceURI[1]",
                    xPathContext);
            if (namespaceElementQueryResult.size() == 0) {
                throw new MessageFormatException("No user process namespace specified "
                        + "and no ib4p:userProcessNamespaceURI element present to determine those.");
            }
            Element userProcessNamespaceUriElement = (Element) namespaceElementQueryResult.get(0);
            userProcessNamespaceUri = userProcessNamespaceUriElement.getValue();
            _userProcessNamespaceUri = userProcessNamespaceUri;
        }

        Nodes soapActionQueryResult = message.query("//ib4p:taskMetaData/ib4p:userProcessCompleteSOAPAction[1]", 
                xPathContext);
        if (soapActionQueryResult.size() > 0) {
            Element soapActionElement = (Element) soapActionQueryResult.get(0);
            _soapAction = soapActionElement.getValue();
            
            Nodes actionNodes = message.query("//addr:Action[1]", xPathContext);
            if (actionNodes.size() > 0) {
                Element wsaTo = (Element) actionNodes.get(0);
                wsaTo.removeChildren();
                wsaTo.appendChild(new Text(_soapAction));
            }
        }
        
        // TODO: generate a unique namespace prefix?
        String userProcessNamespace = "userProcess";

        /* Select all elements inside the soap envelope body. */
        Nodes bodyNodes = message.query("//soapenv:Body//*", xPathContext);
        /* Select all elements inside the task output payload. */
        Nodes taskOutputNodes = message.query("//ib4p:taskOutput//*", xPathContext);
        /*
         * Change namespace for all the elements which are inside the soap
         * envelope body but not inside the task output payload.
         */
        for (int i = 0; i < bodyNodes.size(); ++i) {
            Node node = bodyNodes.get(i);

            if (! taskOutputNodes.contains(node)) {
                Element element = (Element) node;

                element.setNamespaceURI(userProcessNamespaceUri);
                element.setNamespacePrefix(userProcessNamespace);
            }
        }
    }

    /**
     * Returns the XML namespace URI of the user process which has been the
     * target of the latest message processed using the
     * <code>convertMessage</code> method. <br>
     * <br>
     * Returns <code>null</code> if an explicit namespace URI has been
     * specified to the <code>convertMessage</code> method.
     * 
     * @return The XML namespace URI of the user process which has been the
     *         target of the latest message processed using the
     *         <code>convertMessage</code> method. <code>null</code>, if
     *         the namespace URI has been explicitly specified to the
     *         <code>convertMessage</code> method.
     * @see #convertMessage(Document, String)
     */
    public String getUserProcessNamespaceUri() {
        return _userProcessNamespaceUri;
    }

    /**
     * Returns the endpoint URL of the user process which has been the target of
     * the latest message processed using the <code>convertMessage</code>
     * method. <br>
     * Returns <code>null<code> if the processes message was a reply.
     * 
     * @return The endpoint URL of the user process which has been the source of
     *         the latest message processed using the
     *         <code>convertMessage</code> method. <code>null<code> if the processed message was a reply.
     * @see #convertMessage(Document, String)
     */
    public String getUserProcessEndpoint() {
        return _userProcessEndpoint;
    }

    public String getSoapAction() {
        return _soapAction;
    }
}
