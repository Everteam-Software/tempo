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
package org.intalio.tempo.workflow.wds.core.tms;

import java.util.Map;

import org.intalio.tempo.workflow.dao.AbstractJPAConnectionFactory;

/**
 * This class is a factory for end-users to get 
 * {@link org.intalio.tempo.workflow.wds.core.tms.TMSConnection} instances.
 */
public class TMSJPAConnectionFactory extends AbstractJPAConnectionFactory implements TMSConnectionFactoryInterface {

    /**
     * Instance constructor
     */
	public TMSJPAConnectionFactory(Map<String, Object> properties) {
		super("org.intalio.tempo.tms", properties);
	}
    
    @Override
	public synchronized TMSJPAConnection openConnection() {
    	return new TMSJPAConnection(factory.createEntityManager());
	}

	/**
     * Returns a new TMS connection instance. <br />
     * It is required that you use the <code>close()</code> method after using the connection instance.
     */
    public synchronized TMSConnectionInterface getTMSConnection() {
        return openConnection();
    }
}
