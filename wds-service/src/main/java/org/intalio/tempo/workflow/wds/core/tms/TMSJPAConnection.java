package org.intalio.tempo.workflow.wds.core.tms;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.intalio.tempo.workflow.dao.AbstractJPAConnection;

public class TMSJPAConnection extends AbstractJPAConnection implements TMSConnectionInterface {

    public TMSJPAConnection(EntityManager em) {
        super(em);
    }
  
    public void deletePipaTask(String formUrl) {
    	_logger.info("delete task:"+formUrl);
        Query q = entityManager.createNamedQuery(PipaTask.FIND_BY_URL).setParameter(1,formUrl);
        try {
        	PipaTask toDelete = (PipaTask)q.getSingleResult();
            entityManager.remove(toDelete);	
        } catch (NoResultException nre) {
        	// this is okay, it means we did not find anything to delete
        }
        
    }

    public void storePipaTask(PipaTask task) {
        entityManager.persist(task);
    }

}
