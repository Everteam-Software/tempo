/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.workflow.wds.core;

import java.util.Map;

import org.intalio.tempo.workflow.dao.AbstractJPAConnectionFactory;

public class JPAItemDaoConnectionFactory extends AbstractJPAConnectionFactory implements ItemDaoConnectionFactory {

    public JPAItemDaoConnectionFactory(Map<String,Object> properties) {
        super("org.intalio.tempo.wds", properties);
    }

    public JPAItemDaoConnectionFactory() {
        super("org.intalio.tempo.wds");
    }

    @Override
	public JPAItemDaoConnection openConnection() {
    	return new JPAItemDaoConnection(factory.createEntityManager());
	}

	public synchronized ItemDaoConnection getItemDaoConnection() {
    	return openConnection();
    }

}
