package org.intalio.tempo.workflow.task.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DocumentBuilderPool extends BasePoolableObjectFactory {

    static final Logger LOG = LoggerFactory.getLogger(DocumentBuilderPool.class);
    DocumentBuilderFactory factory;

    public DocumentBuilderPool() {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true); 
        factory.setValidating(false);
        factory.setExpandEntityReferences(false);
    }

    @Override
    public Object makeObject() throws Exception {
        DocumentBuilder newDocumentBuilder = factory.newDocumentBuilder();
        newDocumentBuilder.setErrorHandler(new ErrorHandler() {
            public void error(SAXParseException exception) throws SAXException {
                if(LOG.isDebugEnabled()) LOG.debug(exception.toString());
            }

            public void fatalError(SAXParseException exception) throws SAXException {
                if(LOG.isDebugEnabled()) LOG.debug(exception.toString());
            }

            public void warning(SAXParseException exception) throws SAXException {
                if(LOG.isDebugEnabled()) LOG.debug(exception.toString());
            }
            
        });
        return newDocumentBuilder;
    }

    @Override
    public void passivateObject(Object obj) throws Exception {
        ((DocumentBuilder) obj).reset();
    }

}
