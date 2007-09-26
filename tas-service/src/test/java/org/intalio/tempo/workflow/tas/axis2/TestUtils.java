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
package org.intalio.tempo.workflow.tas.axis2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.w3c.dom.Document;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class TestUtils {

    public static OMElement loadElementFromResource(String resource) throws Exception {
        InputStream requestInputStream = TestUtils.class.getResourceAsStream(resource);

        XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(requestInputStream);
        StAXOMBuilder builder = new StAXOMBuilder(parser);

        return builder.getDocumentElement();
    }
    
    public static String toPrettyXML(OMElement element)
            throws Exception {
        String uglyString = element.toString();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(uglyString.getBytes()));

        OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        XMLSerializer serializer = new XMLSerializer(outputStream, format);
        serializer.serialize(doc);
        
        return new String(outputStream.toByteArray(), "UTF-8");
    }

}
