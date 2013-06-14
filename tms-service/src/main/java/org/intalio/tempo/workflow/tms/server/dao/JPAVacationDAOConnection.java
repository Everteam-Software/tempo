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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang.time.DateUtils;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.task.audit.VacationAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPAVacationDAOConnection extends AbstractJPAConnection implements VacationDAOConnection {
	private static final Logger LOG = LoggerFactory.getLogger(JPAVacationDAOConnection.class);
	private Vacation _vacation;

	public JPAVacationDAOConnection(EntityManager createEntityManager) {
		super(createEntityManager);
		_vacation = new Vacation(createEntityManager);
	}

	@Override
	public final void insertVacationDetails(final Vacation vacation) {
		vacation.setIs_active(1);
		checkTransactionIsActive();
		entityManager.persist(vacation);
	}

	@Override
	public final void updateVacationDetails(final Vacation vacation) {
		checkTransactionIsActive();
		Vacation prevVacation = _vacation.fetchVacationByID(vacation.getId());
		
		//null check required
		prevVacation.setFromDate(vacation.getFromDate());
		prevVacation.setToDate(vacation.getToDate());
		prevVacation.setDescription(vacation.getDescription());
		prevVacation.setUser(vacation.getUser());
		prevVacation.setSubstitute(vacation.getSubstitute());
		vacation.setIs_active(1);
		entityManager.persist(prevVacation);
	}

    /**
     * get vacation details for selected user.
     * @param user String
     * @return vacation list List<Vacation>
     */
    public final List<Vacation> getVacationDetails(final String user) {
        Calendar cal = Calendar.getInstance();
        Query query = entityManager
                .createNamedQuery(Vacation.GET_VACATION_DETAILS,
                        Vacation.class)
                .setParameter("user", user)
                .setParameter("toDate", this.trimDate(cal.getTime()),
                        TemporalType.DATE);
        List<Vacation> result = query.getResultList();
        return result;
    }

    @Override
    public final List<Vacation> getMatchedVacations(final Date fromDate,
            final Date toDate) {
        Query query = entityManager
                .createNamedQuery(Vacation.FETCH_MATCHED_VACATION,
                        Vacation.class)
                .setParameter("fromDate", this.trimDate(fromDate),
                        TemporalType.DATE)
                .setParameter("toDate", this.trimDate(toDate),
                        TemporalType.DATE);
		List<Vacation> result = query.getResultList();
		return result;
	}

	/**
	 * get vacation details for all users.
	 * @return vacation list List<Vacation>
	 */
	public final List<Vacation> getVacationDetails() {
	    Calendar cal = Calendar.getInstance();
	    Query query = entityManager.createNamedQuery(
                Vacation.FETCH_VACATION_SUMMARY, Vacation.class).setParameter(
                "toDate", this.trimDate(cal.getTime()), TemporalType.DATE);
		List<Vacation> resultList = query.getResultList();
		return resultList;
	}

	@Override
	public final Boolean deleteVacationDetails(final int id) {
		checkTransactionIsActive();
		LOG.debug("vacation details=" + _vacation.fetchVacationByID(id));
		Vacation prevVacation = _vacation.fetchVacationByID(id);
		prevVacation.setIs_active(0);
		entityManager.persist(prevVacation);
		return true;
	}

    /**
     * Gets vacation details of given start date.
     * @param fromDate Date
     * @return vacations list List<Vacation>
     */
    @Override
    public final List<Vacation> getVacationsByStartDate(final Date fromDate) {
        Query query = entityManager.createNamedQuery(
                Vacation.FETCH_START_VACATION, Vacation.class).setParameter(
                "fromDate", this.trimDate(fromDate), TemporalType.DATE);
        List<Vacation> result = query.getResultList();
        return result;
    }

    /**
     * Gets vacation details of given end date.
     * @param toDate Date
     * @return vacations list List<Vacation>
     */
    @Override
    public final List<Vacation> getVacationsByEndDate(final Date toDate) {
        Query query = entityManager.createNamedQuery(
                Vacation.FETCH_END_VACATION, Vacation.class).setParameter(
                "toDate", this.trimDate(toDate), TemporalType.DATE);
        List<Vacation> result = query.getResultList();
        return result;
    }

    /**
     * get Matched or intersected vacations list for substitute.
     *
     * @param fromDate
     *            Date
     * @param toDate
     *            Date
     * @param substitute
     *            String
     *
     * @return vacations List<Vacation>
     */
    @Override
    public final List<Vacation> getSubstituteMatchedVacations(
            final String substitute, final Date fromDate, final Date toDate) {
        Query query = entityManager
                .createNamedQuery(Vacation.FETCH_SUBSTITUTE_MATCHED_VACATION,
                        Vacation.class)
                .setParameter("fromDate", this.trimDate(fromDate),
                        TemporalType.DATE)
                .setParameter("toDate", this.trimDate(toDate),
                        TemporalType.DATE)
                .setParameter("user", substitute);
        List<Vacation> result = query.getResultList();
        return result;
    }

    @Override
    public final void auditVacation(final VacationAudit vacationAudit) {
      checkTransactionIsActive();
      entityManager.persist(vacationAudit);
      }

    @Override
    public final Vacation getVacationsByID(final int id) {
        checkTransactionIsActive();
        return _vacation.fetchVacationByID(id);
    }

    /**
     * trim time from date object.
     * @param date Date
     * @return trimmedDate Date
     */
    private Date trimDate(final Date date) {
        return DateUtils.truncate(date, Calendar.DATE);
    }
}
