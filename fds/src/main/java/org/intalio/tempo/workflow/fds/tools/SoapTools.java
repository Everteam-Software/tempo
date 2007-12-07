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
import org.intalio.tempo.workflow.fds.dispatches.InvalidInputFormatException;

public class SoapTools {
    private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";

    public static Document unwrapMessage(Document soapEnvelope) throws InvalidInputFormatException {
    	
    	Document result = DocumentHelper.createDocument();
    	
        Element rootElement = (Element)soapEnvelope.getRootElement();
        Element notifyRequest = ((Element)rootElement.elements("Body").get(0)).element("notifyRequest");
        Element rootElementCopy = (Element) notifyRequest.clone();
        
        result.add(rootElementCopy);
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

    private SoapTools() {

    }
}
