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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.task.CustomColumn;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.PIPATaskOutput;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskPrevOwners;
import org.intalio.tempo.workflow.task.audit.Audit;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.jpa.AttachmentFetcher;
import org.intalio.tempo.workflow.util.jpa.TaskFetcher;

/**
 * Persistence for task using JPA.
 */
public class JPATaskDaoConnection extends AbstractJPAConnection implements ITaskDAOConnection {

    private TaskFetcher _fetcher;
    private AttachmentFetcher _attachmentFetcher;

    public JPATaskDaoConnection(EntityManager createEntityManager) {
        super(createEntityManager);
        _fetcher = new TaskFetcher(createEntityManager);
        _attachmentFetcher = new AttachmentFetcher( createEntityManager );
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

    /**
     * get list of tasks available to users.
     * @param users List<String>
     * @return tasks List<String>
     */
    public final List<Task> fetchAllAvailableTasks(final List<String> users) {
        return _fetcher.fetchAllAvailableTasks(users);
    }

    public Task fetchTaskIfExists(String taskID) throws UnavailableTaskException {
        return _fetcher.fetchTaskIfExists(taskID);
    }

    public void updateTask(Task task) {
        checkTransactionIsActive();
        entityManager.persist(task);
    }
    public void updatePipaTask(PIPATask pipaTask){
    	checkTransactionIsActive();
        entityManager.persist(pipaTask);    	
    }
    
    
    public List<Task> fetchTaskfromInstanceID(String instanceid) throws UnavailableTaskException {
        List<Task> pat=_fetcher.fetchTaskIfExistsfrominstanceID(instanceid);
        return pat;
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
    
    //Fix for WF-1479
    public boolean deleteAttachment(String attachmentUrl) throws UnavailableAttachmentException{
        checkTransactionIsActive();
        entityManager.remove(_attachmentFetcher.fetchAttachmentIfExists(attachmentUrl));
        return true;
    }

    public void storePipaTask(PIPATask task) {
        _logger.info("store pipa task:" + task.getFormURL());
        checkTransactionIsActive();
        entityManager.persist(task);
    }

    public PIPATask fetchPipa(String formUrl) throws UnavailableTaskException {
        PIPATask pipa = _fetcher.fetchPipaFromUrl(formUrl);
        return pipa;
    }

    public Task[] fetchAvailableTasks(UserRoles user, Class className, String subQuery) {
        return _fetcher.fetchAvailableTasks(user, className, subQuery);
    }

    public Task[] fetchAvailableTasks(HashMap parameters) {
        return _fetcher.fetchAvailableTasks(parameters);
    }

    public Long countAvailableTasks(HashMap parameters) {
        return _fetcher.countTasks(parameters);
    }
    
    public List<String> fetchCustomColumns(){
        List<String> customColumns = _fetcher.fetchCustomColumns();
        return customColumns;
    }
    
    public List<CustomColumn> fetchCustomColumnfromProcessName(String processName) throws UnavailableTaskException {
        List<CustomColumn> customMetadata=_fetcher.fetchCustomColumnIfExistsfromprocessname(processName);
        return customMetadata;
    }
    
    @Override
	public void deleteCustomColumn(CustomColumn toDeleteCustomColumn) {
	   try {
            checkTransactionIsActive();
           	entityManager.remove(toDeleteCustomColumn);
            commit();
        } catch (Exception nre) {
            throw new NoResultException(nre.getMessage());
        }
	
	}

	@Override
	public void storeCustomColumn(CustomColumn customColumn) {
        checkTransactionIsActive();
        entityManager.persist(customColumn);
	}

	@Override
	public void insertPipaOutput(PIPATaskOutput pipaTaskOutput) {
	    checkTransactionIsActive();
	    entityManager.persist(pipaTaskOutput);	
	}

	@Override
	public PIPATaskOutput fetchPIPATaskOutput(String taskId, String userOwner) {
		return _fetcher.fetchPIPATaskOutput(taskId, userOwner);
	}

	@Override
	public void updatePipaOutput(PIPATaskOutput pipaTaskOutput) {
		checkTransactionIsActive();
	    entityManager.persist(pipaTaskOutput);	
	}

	@Override
	public void deletePIPATaskOutput(PIPATaskOutput output) {
		checkTransactionIsActive();
		entityManager.remove(output);

	}

	@Override
    public long getPendingNotificationCount(Object since, String user, List<String> userRolesList) {
        Query query = entityManager.createNamedQuery(Notification.GET_PENDING_NOTIFICATION_COUNT).setParameter("since", since).setParameter("userOwner", user).setParameter("roleOwners", userRolesList);
        return (Long) query.getSingleResult();
    }

	@Override
	public long getPendingNotificationCount(Object since, Object until, String user, List<String> userRolesList) {
		Query query = entityManager.createNamedQuery(Notification.GET_PENDING_NOTIFICATION_COUNT).setParameter("since", since).setParameter("until", until).setParameter("userOwner", user).setParameter("roleOwners", userRolesList);
		return (Long) query.getSingleResult();
	}

	@Override
    public long getPendingTaskCount(Object since, String user, List<String> userRolesList) {
        Query query = entityManager.createNamedQuery(PATask.GET_PENDING_TASK_COUNT).setParameter("since", since).setParameter("userOwner", user).setParameter("roleOwners", userRolesList);
        return (Long) query.getSingleResult();
    }

	@Override
	public long getPendingTaskCount(Object since, Object until, String user, List<String> userRolesList) {
		Query query = entityManager.createNamedQuery(PATask.GET_PENDING_TASK_COUNT).setParameter("since", since).setParameter("until", until).setParameter("userOwner", user).setParameter("roleOwners", userRolesList);
		return (Long) query.getSingleResult();
	}

	@Override
    public long getCompletedTaskCountByUser(Object since, String user) {
        Query query = entityManager.createNamedQuery(PATask.GET_COMPLETED_TASK_COUNT_BY_USER).setParameter("since", since).setParameter("userOwner", user);
        return (Long) query.getSingleResult();
    }

	@Override
	public long getCompletedTaskCountByUser(Object since, Object until, String user) {
		Query query = entityManager.createNamedQuery(PATask.GET_COMPLETED_TASK_COUNT_BY_USER).setParameter("since", since).setParameter("until", until).setParameter("userOwner", user);
		return (Long) query.getSingleResult();
	}

	@Override
    public long getCompletedTaskCountByUserAssignedRoles(Object since,
            List<String> userRolesList) {
        Query query = entityManager.createNamedQuery(PATask.GET_COMPLETED_TASK_COUNT_BY_USER_ASSIGNED_ROLES).setParameter("since", since).setParameter("roleOwners", userRolesList);
        return (Long) query.getSingleResult();
    }

	@Override
	public long getCompletedTaskCountByUserAssignedRoles(Object since, Object until,
			List<String> userRolesList) {
		Query query = entityManager.createNamedQuery(PATask.GET_COMPLETED_TASK_COUNT_BY_USER_ASSIGNED_ROLES).setParameter("since", since).setParameter("until", until).setParameter("roleOwners", userRolesList);
		return (Long) query.getSingleResult();
	}

	@Override
    public long getClaimedTaskCount(Object since, String user) {
        Query query = entityManager.createNamedQuery(PATask.GET_CLAIMED_TASK_COUNT).setParameter("since", since).setParameter("userOwner", user);
        return (Long) query.getSingleResult();
    }

	@Override
	public long getClaimedTaskCount(Object since, Object until, String user) {
		Query query = entityManager.createNamedQuery(PATask.GET_CLAIMED_TASK_COUNT).setParameter("since", since).setParameter("until", until).setParameter("userOwner", user);
		return (Long) query.getSingleResult();
	}

    @Override
    public void auditTask(Audit audit) {
        checkTransactionIsActive();
        entityManager.persist(audit);
    }

    public List<Object> getPendingClaimedTaskCountForAllUsers() {
        List<Object> taskCntForAllUsers = _fetcher.fetchTaskCountForUsers();
        return taskCntForAllUsers;           
    }

    public List<Object> getTaskDistributionByUsers(Date since, Date until,
            List<String> users, List<String> statusList) {
        List<Object> taskCnt = _fetcher
                .fetchTaskDistributionByUserStatusAndTime(since, until, users,
                        statusList);
        return taskCnt;
    }

    public List<Object> getTaskDistributionByRoles(Date since, Date until,
            List<String> roles, List<String> statusList) {
        List<Object> taskCnt = _fetcher
                .fetchTaskDistributionByRoleStatusAndTime(since, until, roles,
                        statusList);
        return taskCnt;
    }

    public List<Object> getAverageTaskCompletionSummary(Date since, Date until, List<String> users) {
        List<Object> taskCnt = _fetcher.fetchAverageTaskCompletionTime(since, until, users);
        return taskCnt;
    }

    public List<Object> getTaskCountByStatus(Date since, Date until) {
        List<Object> taskCntByStatus = _fetcher.fetchTaskCountByStatus(since, until);
        return taskCntByStatus;
    }

    public List<Object> getTaskCountByPriority(Date since, Date until) {
        List<Object> taskCntByPriority = _fetcher.fetchTaskCountByPriority(since, until);
        return taskCntByPriority;
    }

    public Map<String, Date> getTaskCountByCreationDate(Date since, Date until) {
        Map<String, Date> taskCntByCreationDate = _fetcher.fetchTaskCountByCreationDate(since, until);
        return taskCntByCreationDate;
    }

    @Override
    public void storePreviousTaskOwners(TaskPrevOwners taskPrevOwners) {
        entityManager.persist(taskPrevOwners);
	}

	@Override
	public TaskPrevOwners fetchTaskPreviousOwners(String taskID) {
		TaskPrevOwners taskPrevOwners = new TaskPrevOwners(entityManager);
		return taskPrevOwners.fetchPrevOwnersByID(taskID);
	}

	@Override
    public void deleteTaskPreviousOwners(String taskID) {
        TaskPrevOwners prevOwners = fetchTaskPreviousOwners(taskID);
        if (prevOwners != null)
            entityManager.remove(prevOwners);
    }

    @Override
    public Map<String, Long> getMaxTaskCompletionForUsers(Date since,
            Date until) {
        Map<String, Long> taskSummary = _fetcher.getMaxTaskCompletionForUsers(since, until);
        return taskSummary;
    }

}
