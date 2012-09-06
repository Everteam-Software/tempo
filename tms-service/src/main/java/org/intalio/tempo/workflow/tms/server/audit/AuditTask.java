/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.workflow.tms.server.audit;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.audit.Audit;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;

/**
 * WF-1574: Audit's the task.
 */
public class AuditTask {

    private static final Logger _logger = LoggerFactory.getLogger(AuditTask.class);
    private IAuthProvider authProvider;

    public IAuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(IAuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    /**
     * Audit's creation of PATask and Notification.
     * 
     * @param joinPoint
     */
    public void auditCreate(JoinPoint joinPoint) {
        Object[] createArgs = joinPoint.getArgs();
        ITaskDAOConnection dao = (ITaskDAOConnection) createArgs[0];
        Task task = (Task) createArgs[1];
        String participantToken = (String) createArgs[2];
        String actionPerformed = joinPoint.getSignature().getName();
        _logger.debug("intercepting action : " + actionPerformed);
        _logger.debug("task id: " + task.getID());
        // TODO:Get the userId (participantToken is empty, how to check which
        // user is creating task?)
        // So, for now persisting without any user.
        persistAuditing(dao, actionPerformed, task.getID(), null);
    }

    /**
     * Audit's completion of notification.
     * 
     * @param joinPoint
     * @throws AuthException
     */
    public void auditComplete(JoinPoint joinPoint) throws AuthException {
        Object[] createArgs = joinPoint.getArgs();
        ITaskDAOConnection dao = (ITaskDAOConnection) createArgs[0];
        String taskId = (String) createArgs[1];
        String participantToken = (String) createArgs[2];
        //As the operation name "complete" is not user friendly. So, giving custom actionPerformed name.
        String actionPerformed = "completeNotification"; //joinPoint.getSignature().getName();
        UserRoles userRoles = authProvider.authenticate(participantToken);
        String user = userRoles.getUserID();
        _logger.debug("intercepting action : " + actionPerformed);
        _logger.debug("intercepting arguments task id: " + taskId);
        _logger.debug("user : " + user);
        persistAuditing(dao, actionPerformed, taskId, user);
    }

    /**
     * Audit's reassign, claim, revoke action.
     * 
     * @param joinPoint
     * @throws AuthException
     */
    public void auditReassign(JoinPoint joinPoint) throws AuthException {
        Object[] createArgs = joinPoint.getArgs();
        ITaskDAOConnection dao = (ITaskDAOConnection) createArgs[0];
        String taskId = (String) createArgs[1];
        AuthIdentifierSet users = (AuthIdentifierSet) createArgs[2];
        AuthIdentifierSet roles = (AuthIdentifierSet) createArgs[3];
        String state = ((TaskState) createArgs[4]).getName();
        String participantToken = (String) createArgs[5];
        UserRoles userRoles = authProvider.authenticate(participantToken);
        String user = userRoles.getUserID();
        String actionPerformed = joinPoint.getSignature().getName();
        _logger.debug("intercepting action : " + actionPerformed);
        _logger.debug("intercepting arguments task id: " + taskId);
        _logger.debug("intercepting arguments users: " + users);
        _logger.debug("intercepting arguments roles: " + roles);
        _logger.debug("intercepting arguments state: " + state);
        _logger.debug("user : " + user);
        persistAuditing(dao, actionPerformed, taskId, user, arrayToString(users.toArray()), arrayToString(roles.toArray()), state);
    }

    /**
     * Audit's skip action.
     * 
     * @param joinPoint
     * @throws AuthException
     */
    public void auditSkip(JoinPoint joinPoint) throws AuthException {
        Object[] createArgs = joinPoint.getArgs();
        ITaskDAOConnection dao = (ITaskDAOConnection) createArgs[0];
        String taskId = (String) createArgs[1];
        String participantToken = (String) createArgs[2];
        String actionPerformed = joinPoint.getSignature().getName();
        UserRoles userRoles = authProvider.authenticate(participantToken);
        String user = userRoles.getUserID();
        _logger.debug("intercepting action : " + actionPerformed);
        _logger.debug("intercepting arguments task id: " + taskId);
        _logger.debug("user : " + user);
        persistAuditing(dao, actionPerformed, taskId, user);
    }

    /**
     * Audit's update action.
     * 
     * @param joinPoint
     * @throws AuthException
     */
    public void auditUpdate(JoinPoint joinPoint) throws AuthException {
        Object[] createArgs = joinPoint.getArgs();
        ITaskDAOConnection dao = (ITaskDAOConnection) createArgs[0];
        TaskMetadata taskMetadata = (TaskMetadata) createArgs[1];
        String participantToken = (String) createArgs[2];
        UserRoles userRoles = authProvider.authenticate(participantToken);
        String user = userRoles.getUserID();
        String actionPerformed = joinPoint.getSignature().getName();
        _logger.debug("intercepting action : " + actionPerformed);
        _logger.debug("intercepting arguments dao: " + dao);
        _logger.debug("intercepting arguments taskMetadata: " + taskMetadata);
        _logger.debug("user : " + user);
        String taskId = null;
        String description = null;
        String priority = null;
        if (taskMetadata != null) {
            try {
                taskId = taskMetadata.getTaskId();
                description = taskMetadata.getDescription();
                priority = "" + taskMetadata.getPriority();
            } catch (Exception e) {
                // ignore
            }
        }
        persistAuditing(dao, actionPerformed, taskId, user, description, priority);
    }

    /**
     * Audit's deletion of PATask and Notification.
     * 
     * @param joinPoint
     * @throws AuthException
     */
    public void auditDelete(JoinPoint joinPoint) throws AuthException {
        Object[] createArgs = joinPoint.getArgs();
        ITaskDAOConnection dao = (ITaskDAOConnection) createArgs[0];
        String[] taskIds = (String[]) createArgs[1];
        String participantToken = (String) createArgs[2];
        String actionPerformed = joinPoint.getSignature().getName();
        UserRoles userRoles = authProvider.authenticate(participantToken);
        String user = userRoles.getUserID();
        _logger.debug("intercepting action : " + actionPerformed);
        _logger.debug("intercepting arguments task id: " + taskIds);
        _logger.debug("user: " + user);
        for (int index = 0; index < taskIds.length; index++) {
            persistAuditing(dao, actionPerformed, taskIds[index], user);
        }
    }

    /**
     * Audit's the completion of PATask.
     * @param joinPoint
     * @throws AuthException
     */
    public void auditSetOutputAndComplete(JoinPoint joinPoint) throws AuthException {
        Object[] createArgs = joinPoint.getArgs();
        ITaskDAOConnection dao = (ITaskDAOConnection) createArgs[0];
        String taskId = (String) createArgs[1];
        String participantToken = (String) createArgs[3];
        //As the operation name "setOutputAndComplete" is not user friendly. So, giving custom actionPerformed name.
        String actionPerformed = "completePATask"; //joinPoint.getSignature().getName();
        UserRoles userRoles = authProvider.authenticate(participantToken);
        String user = userRoles.getUserID();
        _logger.debug("intercepting action : " + actionPerformed);
        _logger.debug("intercepting arguments task id: " + taskId);
        _logger.debug("user : " + user);
        persistAuditing(dao, actionPerformed, taskId, user);
    }

    /**
     * Audit's when pipa is stored.
     * @param joinPoint
     */
    public void auditStorePipa(JoinPoint joinPoint) {
        Object[] createArgs = joinPoint.getArgs();
        ITaskDAOConnection dao = (ITaskDAOConnection) createArgs[0];
        Task task = (Task) createArgs[1];
        String participantToken = (String) createArgs[2];
        String actionPerformed = joinPoint.getSignature().getName();
        _logger.debug("intercepting action : " + actionPerformed);
        _logger.debug("task id: " + task.getID());
        // TODO:Get the userId (participantToken is empty, how to check which
        // user is storing pipa?)
        // So, for now persisting without any user.
        persistAuditing(dao, actionPerformed, task.getID(), null);
    }

    public Audit initAudit(String actionPerformed, String taskId, String user) {
        Audit audit = new Audit();
        audit.setActionPerformed(actionPerformed);
        audit.setAuditDate(new Date());
        audit.setTaskId(taskId);
        audit.setUser(user);
        return audit;
    }

    public void persistAuditing(ITaskDAOConnection dao, String actionPerformed, String taskId, String user) {
        Audit audit = initAudit(actionPerformed, taskId, user);
        persistAuditing(dao, audit);
    }

    public void persistAuditing(ITaskDAOConnection dao, String actionPerformed, String taskId, String user, String description, String priority) {
        Audit audit = initAudit(actionPerformed, taskId, user);
        audit.setUpdatedDescription(description);
        audit.setUpdatedPriority(priority);
        persistAuditing(dao, audit);
    }

    public void persistAuditing(ITaskDAOConnection dao, String actionPerformed, String taskId, String user, String users, String roles, String state) {
        Audit audit = initAudit(actionPerformed, taskId, user);
        audit.setAssignedUsers(users);
        audit.setAssignedRoles(roles);
        audit.setUpdatedState(state);
        persistAuditing(dao, audit);
    }

    public void persistAuditing(ITaskDAOConnection dao, Audit audit) {
        dao.auditTask(audit);
        dao.commit();
    }

    public String arrayToString(Object[] identifier) {
        StringBuilder builder = new StringBuilder();
        int identifierLength = identifier.length;
        for (int index = 0; index < identifierLength; index++) {
            builder.append(identifier[index]);
            if (index < (identifierLength - 1)) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

}
