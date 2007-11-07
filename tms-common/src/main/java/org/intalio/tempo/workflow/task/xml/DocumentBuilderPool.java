package org.intalio.tempo.workflow.task.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.pool.BasePoolableObjectFactory;

public class DocumentBuilderPool extends BasePoolableObjectFactory {

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
	@Override
	public Object makeObject() throws Exception {
		return factory.newDocumentBuilder();
	}

	@Override
	public void passivateObject(Object obj) throws Exception {
		((DocumentBuilder)obj).reset();
	}

}
