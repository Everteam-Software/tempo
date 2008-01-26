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
package org.intalio.tempo.workflow.wds.core.xforms;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.intalio.tempo.workflow.wds.core.Item;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.commons.lang.CharEncoding;


/**
 * XFormsProcess handles incoming XForms items.
 */
public class XFormsProcessor {

    private static Logger LOG = Logger.getLogger(XFormsProcessor.class);

    private final static String XFORMS_CONTENT_TYPE = "application/xml";

    /**
     * Process an XForms document.
     * <ul>
     * <li>If the model schema is specified as a relative URI, it is changed to comply to the form URI, 
     *     e.g. if a form is stored at (oxf:/)my/uri/form1.xform and it specified "form1.xsd" 
     *     as its model schema, then the value of the "schema" attribute of the "xforms:model" 
     *     element will be rewritten as: "oxf:/my/uri/form1.xsd"</li>
     * <li>The XForms document is reformatted in a "pretty-print" way.</li>
     * </ul>
     */
    @SuppressWarnings("deprecation")
    public static Item processXForm(final String itemUri, InputStream inputStream) 
        throws IOException, ParserConfigurationException, SAXException
    {
        if (LOG.isDebugEnabled()) LOG.debug("Processing " + itemUri);
        StringWriter out = new StringWriter();
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        OutputFormat format = new OutputFormat();
        format.setEncoding(CharEncoding.UTF_8);
        format.setOmitXMLDeclaration(false);
        format.setOmitComments(false);
        format.setPreserveSpace(true);
        SchemaURLRewriter ser = new SchemaURLRewriter(out, format, itemUri);
        parser.getXMLReader().setContentHandler(ser);
        parser.getXMLReader().parse(new InputSource(inputStream));
        return new Item(itemUri, XFORMS_CONTENT_TYPE, out.toString().getBytes(CharEncoding.UTF_8));
    }

    static class SchemaURLRewriter extends XMLSerializer {
        String _itemUri;
        
        public SchemaURLRewriter(Writer writer, OutputFormat format, String itemUri) {
            super(writer, format);
            _itemUri = itemUri;
        }
        
        @SuppressWarnings("deprecation")
        @Override
        public void startElement(String uri, String localName, String qname, Attributes attrs) 
            throws SAXException
        {
            if ("xforms:model".equals(qname) 
                || ("model".equals(localName) && "http://www.w3.org/2002/xforms".equals(uri)))
            {
                if (LOG.isDebugEnabled()) LOG.debug("Found the model node");
                
                for (int i = 0; i < attrs.getLength(); i++) {
                    if ("schema".equals(attrs.getLocalName(i)) || "schema".equals(attrs.getQName(i))) {
                        if (LOG.isDebugEnabled()) LOG.debug("Found the schema attribute");
                        
                        String xsdPath = attrs.getValue(i);
                        if (!xsdPath.startsWith("http://")) {
                            String xformsFolder = null;
                            int indexOfSlash = _itemUri.lastIndexOf('/');
                            if (indexOfSlash == -1) {
                                xformsFolder = "";
                            } else {
                                xformsFolder = _itemUri.substring(0, indexOfSlash + 1);
                            }
                            xsdPath = "oxf:/" + xformsFolder + xsdPath;
                            AttributesImpl wattrs = new AttributesImpl(attrs);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Writing this value '" + xsdPath + "' for the schema path");
                            }
                            wattrs.setAttribute(i, attrs.getURI(i), attrs.getLocalName(i), 
                                                attrs.getQName(i), attrs.getType(i), xsdPath);
                            super.startElement(uri, localName, qname, wattrs);
                            return;
                        }
                        break;
                    }
                }
            }
            super.startElement(uri, localName, qname, attrs);
        }
    };

}
