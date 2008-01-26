/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.tms.server.dao;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public class SimpleTaskDAOConnection implements ITaskDAOConnection {

    private static final Logger _logger = Logger.getLogger(SimpleTaskDAOConnection.class);
    
    private boolean _closed = false;
    
    private Map<String, Task> _tasks;
    
    SimpleTaskDAOConnection(Map<String, Task> tasks) {
        if (tasks == null) {
            throw new RequiredArgumentException("tasks");
        }
        _tasks = tasks;
        
        _logger.debug("Opened a simple DAO connection.");
    }

    public void commit() {
        
    }
    
    public void close() {
        _closed = true;
        _logger.debug("simple DAO closed.");
    }
    
    @Override
    public void finalize() {
        if (! _closed) {
            _logger.warn("simple DAO was not closed!");
        }
    }

    public synchronized Task[] fetchAllAvailableTasks(UserRoles user) {
        ArrayList<Task> availableTasks = new ArrayList<Task>();        
        for (Task task : _tasks.values()) {
            if (task.isAvailableTo(user)) {
                availableTasks.add(task);
            }
        }        
        return availableTasks.toArray(new Task[]{});
    }

    public synchronized Task fetchTaskIfExists(String taskID) {
    	
        return _tasks.get(taskID);
    }

    public synchronized void updateTask(Task task) {
        String id = task.getID();
        if (! _tasks.containsKey(id)) {
            throw new RuntimeException("Task with ID '" + id + "' does not exist");
        }
        _tasks.put(id, task);
    }

    public synchronized void createTask(Task task)
            throws TaskIDConflictException {
        String id = task.getID();
        if (_tasks.containsKey(id)) {
            throw new TaskIDConflictException("Task with ID '" + id + "' already exists");
        }
        _tasks.put(id, task);
    }

    public synchronized boolean deleteTask(int internalTaskId, String taskID) {
        Task removedTask = _tasks.remove(taskID);
        return removedTask != null;
    }

}
