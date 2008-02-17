package org.intalio.tempo.workflow.task.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.intalio.tempo.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The usual tooling needed around xml Using pools to transform
 * <code>Document</code> back and forth from <code>String</code>
 * 
 */
public class XmlTooling {
    private static final XMLInputFactory XmlInputFactory = XMLInputFactory.newInstance();
    static final Logger log = LoggerFactory.getLogger(XmlTooling.class);
    static final XmlTooling xml = new XmlTooling();
    static final ObjectPool builderPool = new StackObjectPool(new DocumentBuilderPool());
    static final ObjectPool transformerPool = new StackObjectPool(new TransformerPool());

    /**
     * Convenience method to turn an xml string into an xml
     * <code>Document</code>
     */
    public Document parseXML(String xml) {
        if (xml == null || xml.equalsIgnoreCase("")) {
            return null;
        } else {
            try {
                return parseXml(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Parse an input stream and turn it into a <code>Document</code>
     */
    public Document parseXml(InputSource is) {
        DocumentBuilder borrowObject = null;
        try {
            borrowObject = (DocumentBuilder) builderPool.borrowObject();
            return borrowObject.parse(is);
        } catch (SAXException e) {
            throw new RuntimeException("Error while parsing XML", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (borrowObject != null)
                try {
                    builderPool.returnObject(borrowObject);
                } catch (Exception e) {
                    // if the object cannot be returned to the pool,
                    // then we have no more references on it and it should be
                    // garbage collected.
                }
        }
    }

    /**
     * Load an xml file from the classpath and turns it into a document
     */
    public Document getXmlDocument(String filename) throws Exception {
        return xml.parseXml(new InputSource(this.getClass().getResourceAsStream(filename)));
    }

    /**
     * Convert a <code>Document</code> into a string.
     * 
     * @see #serializeXMLToWriter(Document)
     */
    public String serializeXML(Document xml) {
        if (xml != null) {
            StringWriter writer = (StringWriter) serializeXMLToWriter(xml, new StringWriter());
            return writer.toString();
        } else
            return null;
    }

    /**
     * Convert document to a writer that can then be further manipulated
     */
    public Writer serializeXMLToWriter(Document xml, Writer writer) {
        Source source = new DOMSource(xml);
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
                // no more references on the used transformer.
            }
        }
        return writer;
    }

    /**
     * Static and Synchronized call to convert a <code>Document</code> to a
     * <code>String</code>
     */
    public static String serializeDocument(Document doc) {
        synchronized (xml) {
            return xml.serializeXML(doc);
        }
    }

    /**
     * Reverse of the above
     * 
     * @see #serializeXML(Document)
     */
    public static Document deserializeDocument(String doc) {
        synchronized (xml) {
            return xml.parseXML(doc);
        }
    }

    /**
     * Convenience method to compare two documents
     */
    public static boolean equals(Document doc1, Document doc2) {
        return serializeDocument(doc1).equals(serializeDocument(doc2));
    }

    /**
     * Convert an <code>XmlObject</code> object from xmlbeans library to an
     * axis <code>OMElement</code> synchronized call This calls is using the
     * xmlbeans options necessary to generate a proper xml before being parsed
     * again
     */
    public static OMElement convertDocument(XmlObject doc) {
        synchronized (xml) {
            return xml.convertXML(doc);
        }
    }

    /**
     * Same as the above but non static
     * 
     * @see XmlTooling#convertDocument(XmlObject)
     */
    private OMElement convertXML(XmlObject xmlObject) {
        HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
        suggestedPrefixes.put(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
        XmlOptions opts = new XmlOptions();
        opts.setSaveSuggestedPrefixes(suggestedPrefixes);
        OMElement dm = null;
        InputStream is = xmlObject.newInputStream(opts);
        StAXOMBuilder builder;
        try {
            builder = new StAXOMBuilder(is);
            dm = builder.getDocumentElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dm;
    }

    /**
     * Convert a task to the axis <code>OMElement</code> object
     */
    public static OMElement marshallTask(Task task) {
        TaskMarshaller marshaller = new TaskMarshaller();
        return marshaller.marshalFullTask(task, null);
    }

    /**
     * Using marshalling to convert a task to string when needed. This is mostly
     * to remove the code converting the task to a string out of the task class
     * itself
     * 
     * @see XmlTooling#marshallTask(Task)
     */
    public static String taskToString(Task task) {
        return marshallTask(task).toString();
    }

    /**
     * Convert DOM to OM element
     * 
     * @see #convertOMToDOM(OMElement)
     * @see #serializeXMLToWriter(Document, Writer)
     */
    public static OMElement convertDOMToOM(final Document document, OMFactory omFactory) {
        try {
            final PipedReader pr = new PipedReader();
            final PipedWriter pw = new PipedWriter(pr);
            new Thread(new Runnable() {
                public void run() {
                    new XmlTooling().serializeXMLToWriter(document, pw);
                }
            }).start();
            XMLStreamReader parser = XmlInputFactory.createXMLStreamReader(pr);
            StAXOMBuilder builder = new StAXOMBuilder(omFactory, parser);
            return builder.getDocumentElement();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Convert an OMElement to a <code>org.w3dc.Document</code>
     */
    public static Document convertOMToDOM(final OMElement omElement) {
        try {
            // final PipedReader pr = new PipedReader();
            // final PipedWriter pw = new PipedWriter(pr);
            //
            // new Thread(new Runnable() {
            // public void run() {
            // try {
            // OMOutputFormat f = new OMOutputFormat();
            // f.setAutoCloseWriter(false);
            // omElement.serialize(pw, f);
            // } catch (Exception e) {
            // throw new RuntimeException(e.getMessage(), e);
            // }
            // }
            // }).start();
            return new XmlTooling().parseXml(new InputSource(new StringReader(omElement.toStringWithConsume())));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
