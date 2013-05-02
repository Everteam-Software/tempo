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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.CustomColumn;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.PIPATaskOutput;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskPrevOwners;
import org.intalio.tempo.workflow.task.audit.Audit;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTaskDAOConnection implements ITaskDAOConnection {

    private static final Logger _logger = LoggerFactory.getLogger(SimpleTaskDAOConnection.class);

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
        if (!_closed) {
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
        return availableTasks.toArray(new Task[] {});
    }

    public synchronized Task fetchTaskIfExists(String taskID) {
        return _tasks.get(taskID);
    }

    public synchronized void updateTask(Task task) {
        String id = task.getID();
        if (!_tasks.containsKey(id)) {
            throw new RuntimeException("Task with ID '" + id + "' does not exist");
        }
        _tasks.put(id, task);
    }

    public synchronized void createTask(Task task) throws TaskIDConflictException {
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

    public void deletePipaTask(String formUrl) {
        throw new RuntimeException("Not Implemented");
    }

    public void storePipaTask(PIPATask task) {
        throw new RuntimeException("Not Implemented");
    }

    public PIPATask fetchPipa(String formUrl) {
        throw new RuntimeException("Not Implemented");
    }

    public Task[] fetchAvailableTasks(UserRoles user, Class className, String subQuery) {
        throw new RuntimeException("Not Implemented");
    }

    public Task[] fetchAvailableTasks(HashMap parameters) {
        throw new RuntimeException("Not Implemented");
    }

    public Long countAvailableTasks(HashMap parameters) {
        throw new RuntimeException("Not Implemented");
    }

    public List<Task> fetchTaskfromInstanceID(String instanceid) {
        throw new RuntimeException("Not Implemented");        
    }
    
    public boolean deleteAttachment(String attachmentUrl) {
        throw new RuntimeException("Not Implemented");
    }

	@Override
	public void deleteCustomColumn(CustomColumn customColumn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<CustomColumn> fetchCustomColumnfromProcessName(
			String processName) throws UnavailableTaskException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeCustomColumn(CustomColumn customColumn) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public List<String> fetchCustomColumns() {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public void updatePipaTask(PIPATask pipaTask) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PIPATaskOutput fetchPIPATaskOutput(String id, String userOwner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePIPATaskOutput(PIPATaskOutput output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updatePipaOutput(PIPATaskOutput pipaTaskOutput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertPipaOutput(PIPATaskOutput pipaTaskOutput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getPendingNotificationCount(Object filter, String user, List<String> userRolesList) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public long getPendingTaskCount(Object filter, String user, List<String> userRolesList) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getCompletedTaskCountByUser(Object filter, String user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getClaimedTaskCount(Object filter, String user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getCompletedTaskCountByUserAssignedRoles(Object filter,
			List<String> userRolesList) {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public void auditTask(Audit audit) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public List<Object> getPendingClaimedTaskCountForAllUsers() {
        // TODO Auto-generated method stub
        return new ArrayList<Object>();
        
       
    }

	@Override
	public void storePreviousTaskOwners(TaskPrevOwners taskPrevOwners) {
		 throw new RuntimeException("Not Implemented");
	}

	@Override
	public TaskPrevOwners fetchTaskPreviousOwners(String taskID) {
		 throw new RuntimeException("Not Implemented");
	}

	@Override
	public void deleteTaskPreviousOwners(String taskID) {
		 throw new RuntimeException("Not Implemented");
	}
}
