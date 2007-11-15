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
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.util.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public final class OMDOMConvertor {

    private static final Logger _logger = LoggerFactory.getLogger(OMDOMConvertor.class);

    public static OMElement convertDOMToOM(Document document, OMFactory omFactory) { 
        // TODO: this is extremely slow.
        // Rewrite this!
        try {
            Source source = new DOMSource(document);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Result result = new StreamResult(outStream);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);

            ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
            XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(inStream);
            StAXOMBuilder builder = new StAXOMBuilder(omFactory, parser);

            OMElement omElement = builder.getDocumentElement();
            _logger.debug(document + " is parsed to " + omElement.getQName());
            return omElement;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Document convertOMToDOM(OMElement omElement) { 
        // TODO: rewrite this extremely slow method.
        try {
            String content = "<?xml version='1.0' encoding='UTF-8'?>";
            content += omElement.toStringWithConsume();
            ByteArrayInputStream inStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(inStream);
            return document;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
