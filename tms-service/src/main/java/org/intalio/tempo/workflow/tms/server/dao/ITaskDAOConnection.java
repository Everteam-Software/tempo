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
import org.intalio.tempo.workflow.task.CustomColumn;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.PIPATaskOutput;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskPrevOwners;
import org.intalio.tempo.workflow.task.audit.Audit;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
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
    public boolean deleteAttachment(String attachmentUrl) throws UnavailableAttachmentException;
    
    public void storePipaTask(PIPATask task);
    public void deletePipaTask(String formUrl);
    public void updatePipaTask(PIPATask pipaTask);
    public PIPATask fetchPipa(String formUrl) throws UnavailableTaskException ;
    public List<String> fetchCustomColumns() ;
    public List<CustomColumn> fetchCustomColumnfromProcessName(String processName) throws UnavailableTaskException ;
    void deleteCustomColumn(CustomColumn customColumn);
	void storeCustomColumn(CustomColumn customColumn);
	public PIPATaskOutput fetchPIPATaskOutput(String id, String userOwner);
	public void deletePIPATaskOutput(PIPATaskOutput output);
	public void updatePipaOutput(PIPATaskOutput pipaTaskOutput);
	public void insertPipaOutput(PIPATaskOutput pipaTaskOutput);
	
	/**
	 * Gives notification count which are in ready state available for logged in user and it's assigned roles.
	 * @param filter
	 * @param user
	 * @param userRolesList
	 * @return
	 */
	public long getPendingNotificationCount(Object filter, String user, List<String> userRolesList);
	
	/**
	 * Gives pa task count which are in ready state available for logged-in user and it's assigned roles.
	 * @param filter
	 * @param user
	 * @param userRolesList
	 * @return
	 */
	public long getPendingTaskCount(Object filter, String user, List<String> userRolesList);
	
	/**
	 * Gives pa task count which are in completed state available for logged-in user.
	 * @param filter
	 * @param user
	 * @return
	 */
	public long getCompletedTaskCountByUser(Object filter, String user);
	
	/**
	 * Gives pa task count which are in completed state available for logged-in user's assigned roles. 
	 * @param filter
	 * @param userRolesList
	 * @return
	 */
	public long getCompletedTaskCountByUserAssignedRoles(Object filter, List<String> userRolesList);
	
	/**
	 * Gives pa task count which are in claimed state available for logged-in user.
	 * @param filter
	 * @param user
	 * @return
	 */
	public long getClaimedTaskCount(Object filter, String user);

    /**
     * WF-1574: Audit's the task
     * @param audit
     */
    public void auditTask(Audit audit);
    /**
     * Gives pa task count which are in ready or claimed state available for all users.
     * @return
     */
    public List<Object> getPendingClaimedTaskCountForAllUsers();
    
    /**
     * save previous task owners
     * @return
     */
    public void storePreviousTaskOwners(TaskPrevOwners taskPrevOwners);
    
    /**
     * get previous task owners
     * @return
     */
	public TaskPrevOwners fetchTaskPreviousOwners(String taskID);
	
	/**
     * delete previous task owners
     * @return
     */
	public void deleteTaskPreviousOwners(String taskID);
}
