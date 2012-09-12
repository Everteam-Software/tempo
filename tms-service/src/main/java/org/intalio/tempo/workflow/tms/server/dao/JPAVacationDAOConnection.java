/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.workflow.tms.server.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.task.Vacation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPAVacationDAOConnection extends AbstractJPAConnection implements VacationDAOConnection {
	private static final Logger LOG = LoggerFactory.getLogger(JPAVacationDAOConnection.class);
	private Vacation _vacation;

	public JPAVacationDAOConnection(EntityManager createEntityManager) {
		super(createEntityManager);
		_vacation = new Vacation(createEntityManager);
	}

	public void insertVacationDetails(Vacation vacation) {
		checkTransactionIsActive();
		entityManager.persist(vacation);
	}

	public List<Vacation> getVacationDetails(String user) {
		Query query = entityManager.createNamedQuery(Vacation.GET_VACATION_DETAILS, Vacation.class).setParameter(
				"user", user);
		List<Vacation> result = query.getResultList();
		return result;
	}

	public List<Vacation> getVacationDetails() {
		Query query = entityManager.createNamedQuery(Vacation.FETCH_VACATION_SUMMARY, Vacation.class);
		List<Vacation> resultList = query.getResultList();
		return resultList;
	}

	public Boolean deleteVacationDetails(int id) {
		checkTransactionIsActive();
		LOG.debug("vacation details=" + _vacation.fetchVacationByID(id));
		entityManager.remove(_vacation.fetchVacationByID(id));
		return true;
	}
}
