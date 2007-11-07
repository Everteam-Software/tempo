package org.intalio.tempo.workflow.tms.server.dao;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.pool.BasePoolableObjectFactory;

public class TransformerPool extends BasePoolableObjectFactory {
	final TransformerFactory factory = TransformerFactory.newInstance();
	
	@Override
	public Object makeObject() throws Exception {
		return factory.newTransformer();
	}

	@Override
	public void passivateObject(Object obj) throws Exception {
		((Transformer)obj).reset();
	}

}
