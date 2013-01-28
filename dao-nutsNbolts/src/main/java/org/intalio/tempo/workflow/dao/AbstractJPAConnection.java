/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */

package org.intalio.tempo.workflow.dao;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum ACTION {
	BEGIN,
	CLOSE,
	LOAD,
	COMMIT,
	ROLLBACK,
	CLEAR
}

/**
 * Common class for JPA-based connection
 */
public class AbstractJPAConnection {

	protected Logger _logger;
	protected EntityManager entityManager;

	public AbstractJPAConnection(EntityManager createEntityManager) {
		_logger = LoggerFactory.getLogger(this.getClass());
		_logger.debug(ACTION.LOAD.toString());
		entityManager = createEntityManager;
	}

	public void close() {
	    // commit();
	    if (!entityManager.getTransaction().isActive()) {
			entityManager.close();
		_logger.debug(ACTION.CLOSE.toString());
	    }
	}
	
	public void checkTransactionIsActive() {
		if (!entityManager.getTransaction().isActive()) {
			_logger.debug(ACTION.BEGIN.toString());
			entityManager.getTransaction().begin();	
		}
	}

	public void commit() {
		if (entityManager.getTransaction().isActive()) {
			_logger.debug(ACTION.COMMIT.toString());
			entityManager.getTransaction().commit();
		}
	}

}
