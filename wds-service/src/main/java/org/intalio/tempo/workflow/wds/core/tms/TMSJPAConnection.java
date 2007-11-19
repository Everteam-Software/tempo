package org.intalio.tempo.workflow.wds.core.tms;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class TMSJPAConnection implements TMSConnectionInterface {

    private EntityManager entityManager;

    public TMSJPAConnection(EntityManager em) {
        this.entityManager = em;
    }
    
    public void close() {
        entityManager.close();
    }

    public void commit() {
        entityManager.getTransaction().commit();
    }

    public void deletePipaTask(String formUrl) {
        Query q = entityManager.createNamedQuery(PipaTask.FIND_BY_URL).setParameter(1,formUrl);
        PipaTask toDelete = (PipaTask)q.getSingleResult();
        entityManager.remove(toDelete);
    }

    public void storePipaTask(PipaTask task) {
        entityManager.persist(task);
    }

}
