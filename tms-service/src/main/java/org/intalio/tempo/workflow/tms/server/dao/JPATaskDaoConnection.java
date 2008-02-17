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

import javax.persistence.EntityManager;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
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
        if (_logger.isDebugEnabled())
            _logger.debug("create task of class:" + task.getClass().getName());
        checkTransactionIsActive();
        entityManager.persist(task);
    }

    public boolean deleteTask(int internalTaskId, String taskID) {
        if (_logger.isDebugEnabled())
            _logger.debug("delete task with id:" + taskID);
        checkTransactionIsActive();
        entityManager.remove(_fetcher.fetchTaskIfExists(taskID));
        return true;
    }

    public Task[] fetchAllAvailableTasks(UserRoles user) {
        return _fetcher.fetchAllAvailableTasks(user);
    }

    public Task fetchTaskIfExists(String taskID) {
        return _fetcher.fetchTaskIfExists(taskID);
    }

    public void updateTask(Task task) {
        if (_logger.isDebugEnabled()) _logger.debug("update task:" + task.toString());
        checkTransactionIsActive();
        entityManager.persist(task);
    }

}
