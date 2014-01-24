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

import java.util.Date;
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
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;

public interface ITaskDAOConnection {
    public void commit();
    public void close();

    public Task[] fetchAllAvailableTasks(UserRoles user);

    /**
     * get list of tasks available to users.
     * @param users List<String>
     * @return tasks List<String>
     */
    List<Task> fetchAllAvailableTasks(List<String> users);

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
	 * @param since
	 * @param user
	 * @param userRolesList
	 * @return
	 */
	public long getPendingNotificationCount(Object since, String user, List<String> userRolesList);

	/**
     * Gives notification count which are in ready state available for logged in user and it's assigned roles.
     * @param since
     * @param until
     * @param user
     * @param userRolesList
     * @return
     */
    public long getPendingNotificationCount(Object since, Object until, String user, List<String> userRolesList);

	/**
	 * Gives pa task count which are in ready state available for logged-in user and it's assigned roles.
	 * @param since
	 * @param user
	 * @param userRolesList
	 * @return
	 */
	public long getPendingTaskCount(Object since, String user, List<String> userRolesList);

	/**
     * Gives pa task count which are in ready state available for logged-in user and it's assigned roles.
     * @param since
     * @param until
     * @param user
     * @param userRolesList
     * @return
     */
    public long getPendingTaskCount(Object since, Object until, String user, List<String> userRolesList);

	/**
	 * Gives pa task count which are in completed state available for logged-in user.
	 * @param since
	 * @param user
	 * @return
	 */
	public long getCompletedTaskCountByUser(Object since, String user);

	/**
	 * Gives pa task count which are in completed state available for all user based on given limit.
	 * @param since
	 * @param until
	 * @return
	 */
	public Map<String, Long> getMaxTaskCompletionForUsers(Date since, Date until);

	/**
     * Gives pa task count which are in completed state available for logged-in user.
     * @param since
     * @param until
     * @param user
     * @return
     */
    public long getCompletedTaskCountByUser(Object since, Object until, String user);

	/**
	 * Gives pa task count which are in completed state available for logged-in user's assigned roles. 
	 * @param since
	 * @param userRolesList
	 * @return
	 */
	public long getCompletedTaskCountByUserAssignedRoles(Object since, List<String> userRolesList);

	/**
     * Gives pa task count which are in completed state available for logged-in user's assigned roles. 
     * @param since
     * @param until
     * @param userRolesList
     * @return
     */
    public long getCompletedTaskCountByUserAssignedRoles(Object since, Object until, List<String> userRolesList);

	/**
	 * Gives pa task count which are in claimed state available for logged-in user.
	 * @param since
	 * @param user
	 * @return
	 */
	public long getClaimedTaskCount(Object since, String user);

	/**
     * Gives pa task count which are in claimed state available for logged-in user.
     * @param since
     * @param until
     * @param user
     * @return
     */
    public long getClaimedTaskCount(Object since, Object until, String user);

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
     * Gives pa task distribution available for given users.
     * 
     * @return
     */
    public List<Object> getTaskDistributionByUsers(Date since, Date until,
            List<String> users, List<String> statusList);

    /**
     * Gives pa task distribution available for given users.
     * 
     * @return
     */
    public List<Object> getTaskDistributionByRoles(Date since, Date until,
            List<String> roles, List<String> statusList);

    /**
     * Gives average completion time summary for tasks assigned to given users.
     * @return
     */
    public List<Object> getAverageTaskCompletionSummary(Date since, Date until, List<String> users);

    /**
     * Gives pa task count based on Task Status.
     * @return
     */
    public List<Object> getTaskCountByStatus(Date since, Date until);

    /**
     * Gives pa task count based on Task Priority.
     * @return
     */
    public List<Object> getTaskCountByPriority(Date since, Date until);

    /**
     * Gives pa task count based on Task Creation Date.
     * @return
     */
    public Map<Integer, Integer> getTaskCountByCreationDate(Date since, Date until);

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
