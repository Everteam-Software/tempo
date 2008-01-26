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

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.XPathContext;

import org.intalio.tempo.workflow.fds.dispatches.InvalidInputFormatException;

public class SoapTools {
    private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final XPathContext xpathContext = new XPathContext();
    static {
        xpathContext.addNamespace("S", SOAP_NS);
    }
    private static final XPath xpath = new XPath(xpathContext);

    public static Document unwrapMessage(Document soapEnvelope) throws InvalidInputFormatException {
        Element rootElement = xpath.requireElement(soapEnvelope, "//S:Body/*[1]");
        Element rootElementCopy = (Element) rootElement.copy();
        return new Document(rootElementCopy);
    }

    public static Document wrapMessage(Document message) {
        Element envelopeElement = new Element("Envelope", SOAP_NS);
        Element bodyElement = new Element("Body", SOAP_NS);
        envelopeElement.appendChild(bodyElement);
        Element rootElementCopy = (Element) message.getRootElement().copy();
        bodyElement.appendChild(rootElementCopy);

        return new Document(envelopeElement);
    }

    private SoapTools() {

    }
}
