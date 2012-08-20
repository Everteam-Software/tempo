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
		commit();
		close();
	}

	public List<Vacation> getVacationDetails(String user) {
		LOG.debug("Current User:" + user);
		Query query = entityManager.createNamedQuery(Vacation.GET_VACATION_DETAILS).setParameter("user", user);
		List<Vacation> resultList = query.getResultList();
		LOG.debug("resultList :" + resultList);
		return resultList;
	}
	
	public List<Vacation> getVacationDetails() {
        LOG.debug("Fetching details for all the users");
        Query query = entityManager.createNamedQuery(Vacation.FETCH_VACATION_SUMMARY);
        List<Vacation> resultList = query.getResultList();
        LOG.debug("resultList :" + resultList);
        return resultList;
    }
	
	public Boolean deleteVacationDetails(int id) {
		checkTransactionIsActive();
		LOG.debug("vacation details=" + _vacation.fetchVacationByID(id));
		entityManager.remove(_vacation.fetchVacationByID(id));
		commit();
		close();
		return true;
	}
}
