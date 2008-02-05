/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
 package org.intalio.tempo.workflow.tms.server.dao;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;

/**
 * Persistence for task using JPA.
 *
 */
public class JPATaskDaoConnection extends AbstractJPAConnection implements ITaskDAOConnection {

    private Query find_by_id;

    public JPATaskDaoConnection(EntityManager createEntityManager) {
    	super(createEntityManager);
    	find_by_id = entityManager.createNamedQuery("find_by_id");
    }

    public void createTask(Task task) throws TaskIDConflictException {
    	if(_logger.isDebugEnabled()) _logger.debug("create task of class:"+task.getClass().getName());
    	checkTransactionIsActive();
        entityManager.persist(task);
    }

    public boolean deleteTask(int internalTaskId, String taskID) {
    	if(_logger.isDebugEnabled()) _logger.debug("delete task with id:"+taskID);
    	checkTransactionIsActive();
        synchronized (find_by_id) {
            Query q = find_by_id.setParameter(1, taskID);
            Task t = (Task) (q.getResultList()).get(0);
            entityManager.remove(t);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public Task[] fetchAllAvailableTasks(UserRoles user) {
    	if(_logger.isDebugEnabled()) _logger.debug("fetch task");
        AuthIdentifierSet roles = user.getAssignedRoles();
        String userid = user.getUserID();
        String s = MessageFormat.format(Task.FIND_BY_USER_AND_ROLES, new Object[] { roles.toString(), "('"+userid+"')" });
        if(_logger.isDebugEnabled()) _logger.debug("fetchAllAvailableTasks query:"+s);
        Query q = entityManager.createNativeQuery(s, Task.class);
        List<Task> l = q.getResultList();
        return (Task[]) new ArrayList(l).toArray(new Task[l.size()]);
    }

    public Task fetchTaskIfExists(String taskID) {
        synchronized (find_by_id) {
            Query q = find_by_id.setParameter(1, taskID);
            return (Task) (q.getResultList()).get(0);
        }
    }

    public void updateTask(Task task) {
    	if(_logger.isDebugEnabled()) _logger.debug("update task:"+task.toString());
    	checkTransactionIsActive();
        entityManager.persist(task);
    }

}
