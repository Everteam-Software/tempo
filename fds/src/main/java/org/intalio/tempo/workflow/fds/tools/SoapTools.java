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
package org.intalio.tempo.workflow.fds.tools;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.intalio.tempo.workflow.fds.core.MessageConstants;
import org.intalio.tempo.workflow.fds.dispatches.InvalidInputFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoapTools {
    private static final Logger _log = LoggerFactory.getLogger(SoapTools.class);
    private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final XPath path = DocumentHelper.createXPath("/soapenv:Envelope/soapenv:Body/*[1]");
    static {
        path.setNamespaceURIs(MessageConstants._nsMap);
    }

    public static Document unwrapMessage(Document soapEnvelope) throws InvalidInputFormatException {
        if(_log.isDebugEnabled()) _log.debug(soapEnvelope.asXML());
    	Document result = DocumentHelper.createDocument();
    	Node node = null;
    	synchronized (path) { node = path.selectSingleNode(soapEnvelope); }
    	result.add((Node)node.clone());
        return result;
    }

    public static Document wrapMessage(Document message) {
    	Document document = DocumentHelper.createDocument();
    	Element envelopeElement = document.addElement("Envelope", SOAP_NS);
        Element bodyElement = envelopeElement.addElement("Body", SOAP_NS);
        Element rootElementCopy = (Element) message.getRootElement().clone();

        bodyElement.add(rootElementCopy);

        return document;
    }
}
