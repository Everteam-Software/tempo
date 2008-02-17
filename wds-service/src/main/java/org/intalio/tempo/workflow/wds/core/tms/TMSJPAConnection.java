package org.intalio.tempo.workflow.wds.core.tms;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.util.jpa.TaskFetcher;

public class TMSJPAConnection extends AbstractJPAConnection implements TMSConnectionInterface {

    private TaskFetcher _fetcher;

    public TMSJPAConnection(EntityManager em) {
        super(em);
        this._fetcher = new TaskFetcher(em);
    }

    public void deletePipaTask(String formUrl) {
        _logger.info("delete task:" + formUrl);
        try {
            PIPATask toDelete = _fetcher.fetchPipaFromUrl(formUrl);
            checkTransactionIsActive();
            entityManager.remove(toDelete);
        } catch (NoResultException nre) {
            // this is okay, it means we did not find anything to delete, and
            // its already deleted
        }
    }

    public void storePipaTask(PIPATask task) {
        _logger.info("store pipa task:" + task.getFormURL());
        checkTransactionIsActive();
        entityManager.persist(task);
    }

    public Task[] fetchAllAvailableTasks(UserRoles user) {
        return _fetcher.fetchAllAvailableTasks(user);
    }

}
