package org.intalio.tempo.workflow.task.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.pool.BasePoolableObjectFactory;

public class DocumentBuilderPool extends BasePoolableObjectFactory {

    DocumentBuilderFactory factory;

    public DocumentBuilderPool() {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setExpandEntityReferences(false);
    }

    @Override
    public Object makeObject() throws Exception {
        DocumentBuilder newDocumentBuilder = factory.newDocumentBuilder();
        return newDocumentBuilder;
    }

    @Override
    public void passivateObject(Object obj) throws Exception {
        ((DocumentBuilder) obj).reset();
    }

}
