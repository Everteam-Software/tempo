/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.intalio.tempo.workflow.tms.server.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.jpa.TaskFetcher;

/**
 * Persistence for task using JPA.
 */
public class JPATaskDaoConnection extends AbstractJPAConnection implements ITaskDAOConnection {

    private TaskFetcher _fetcher;

    public JPATaskDaoConnection(EntityManager createEntityManager) {
        super(createEntityManager);
        _fetcher = new TaskFetcher(createEntityManager);
    }

    public void createTask(Task task) throws TaskIDConflictException {
        checkTransactionIsActive();
        entityManager.persist(task);
    }

    public boolean deleteTask(int internalTaskId, String taskID) throws UnavailableTaskException {
        checkTransactionIsActive();
        entityManager.remove(_fetcher.fetchTaskIfExists(taskID));
        return true;
    }

    public Task[] fetchAllAvailableTasks(UserRoles user) {
        return _fetcher.fetchAllAvailableTasks(user);
    }

    public Task fetchTaskIfExists(String taskID) throws UnavailableTaskException {
        return _fetcher.fetchTaskIfExists(taskID);
    }

    public void updateTask(Task task) {
        checkTransactionIsActive();
        entityManager.persist(task);
    }

    public void deletePipaTask(String formUrl) {
        try {
            PIPATask toDelete = _fetcher.fetchPipaFromUrl(formUrl);
            checkTransactionIsActive();
            entityManager.remove(toDelete);
        } catch (Exception nre) {
            throw new NoResultException(nre.getMessage());
        }
    }

    public void storePipaTask(PIPATask task) {
        _logger.info("store pipa task:" + task.getFormURL());
        checkTransactionIsActive();
        entityManager.persist(task);
    }

    public PIPATask fetchPipa(String formUrl) {
        PIPATask pipa = _fetcher.fetchPipaFromUrl(formUrl);
        return pipa;
    }

    public Task[] fetchAvailableTasks(UserRoles user, Class className, String subQuery) {
        return _fetcher.fetchAvailableTasks_With_Filter(user, className, subQuery);
    }

    public Task[] fetchAvailableTasks(HashMap parameters) {
        return _fetcher.fetchAvailableTasks(parameters);
    }

    public Long countAvailableTasks(HashMap parameters) {
        return _fetcher.countTasks(parameters);
    }


	public Collection<String> updateLateTasks() {
		return _fetcher.updateLateTasks();
	}

}
