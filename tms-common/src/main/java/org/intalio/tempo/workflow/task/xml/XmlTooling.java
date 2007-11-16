package org.intalio.tempo.workflow.task.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlTooling {
    static final Logger log = LoggerFactory.getLogger(XmlTooling.class);
    static final XmlTooling xml = new XmlTooling();
    static final ObjectPool builderPool = new StackObjectPool(new DocumentBuilderPool());
    static final ObjectPool transformerPool = new StackObjectPool(new TransformerPool());

    public Document parseXML(String xml) {
        if (xml == null || xml.equalsIgnoreCase("")) {
            return null;
        } else {
            try {
                return parseXml(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            } catch (Exception e) {
                return null;
            }
        }
    }

    public Document parseXml(InputStream is) {
        DocumentBuilder borrowObject = null;
        try {
            borrowObject = (DocumentBuilder) builderPool.borrowObject();
            return (borrowObject).parse(is);
        } catch (SAXException e) {
            throw new RuntimeException("Error while parsing XML", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (borrowObject != null)
                try {
                    builderPool.returnObject(borrowObject);
                } catch (Exception e) {
                }
        }
    }

    public String serializeXML(Document xml) {
        
        if (xml != null) {
            Source source = new DOMSource(xml);
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            Transformer transformer = null;
            try {
                transformer = (Transformer) transformerPool.borrowObject();
                transformer.transform(source, result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    transformerPool.returnObject(transformer);
                } catch (Exception e) {
                }
            }
            return writer.toString();
        } else
            return null;
    }

    public static String serializeDocument(Document doc) {
        synchronized (xml) {
            return xml.serializeXML(doc);
        }
    }
    
    public static Document deserializeDocument(String doc) {
        synchronized (xml) {
            return xml.parseXML(doc);
        }
    }
    
    public static boolean equals(Document doc1, Document doc2) {
        return serializeDocument(doc1).equals(serializeDocument(doc2));
    }

}
