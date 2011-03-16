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

import java.util.HashMap;
import java.util.List;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;

public interface ITaskDAOConnection {
    public void commit();
    public void close();

    public Task[] fetchAllAvailableTasks(UserRoles user);
    public Task[] fetchAvailableTasks(UserRoles user, Class className, String subQuery);
    public Task[] fetchAvailableTasks(HashMap parameters);
    public Long countAvailableTasks(HashMap parameters);
    public Task fetchTaskIfExists(String taskID) throws UnavailableTaskException ;
    public List<Task> fetchTaskfromInstanceID(String instanceid) throws UnavailableTaskException ;
    
    public void updateTask(Task task);
    public void createTask(Task task) throws TaskIDConflictException;
    public boolean deleteTask(int internalTaskId, String taskID) throws UnavailableTaskException ;
    
    public void storePipaTask(PIPATask task);
    public void deletePipaTask(String formUrl);
    public PIPATask fetchPipa(String formUrl) throws UnavailableTaskException ;
}
