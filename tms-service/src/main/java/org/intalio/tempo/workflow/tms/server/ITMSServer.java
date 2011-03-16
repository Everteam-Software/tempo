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

package org.intalio.tempo.workflow.tms.server;

import java.net.URL;
import java.util.HashMap;

import org.apache.axis2.AxisFault;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.w3c.dom.Document;

import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;

//Added ITaskDAOConnection in every method signature for JIRA WF-1466
public interface ITMSServer {

    Task[] getTaskList(ITaskDAOConnection dao,String participantToken) throws TMSException;

    Task[] getAvailableTasks(ITaskDAOConnection dao,String participantToken, String taskType, String subQuery) throws TMSException;

    void skip(ITaskDAOConnection dao,String taskID, String participantToken) throws TMSException;
    
    Task getTask(ITaskDAOConnection dao,String taskID, String participantToken) throws TMSException;

    UserRoles getUserRoles(String participantToken) throws TMSException;

    PIPATask getPipa(ITaskDAOConnection dao,String formUrl, String participantToken) throws TMSException;

    Document initProcess(ITaskDAOConnection dao,String taskID, String user, String formUrl, Document input, String participantToken) throws TMSException, AxisFault;

    Attachment[] getAttachments(ITaskDAOConnection dao,String taskID, String participantToken) throws TMSException;

    void addAttachment(ITaskDAOConnection dao,String taskID, Attachment attachment, String participantToken) throws TMSException;

    void removeAttachment(ITaskDAOConnection dao,String taskID, URL attachmentURL, String participantToken) throws TMSException;

    void reassign(ITaskDAOConnection dao,String taskID, AuthIdentifierSet users, AuthIdentifierSet roles, TaskState state, String participantToken) throws TMSException;

    void storePipa(ITaskDAOConnection dao,PIPATask task, String participantToken) throws TMSException;

    void deletePipa(ITaskDAOConnection dao,String formUrl, String participantToken) throws TMSException;

    void setOutput(ITaskDAOConnection dao,String taskID, Document output, String participantToken) throws TMSException;

    void complete(ITaskDAOConnection dao,String taskID, String participantToken) throws TMSException;

    void setOutputAndComplete(ITaskDAOConnection dao,String taskID, Document output, String participantToken) throws TMSException;

    void fail(ITaskDAOConnection dao,String taskID, String failureCode, String failureReason, String participantToken) throws TMSException;

    void delete(ITaskDAOConnection dao,String[] taskIDs, String participantToken) throws TMSException;
    
    void deletefrominstance(ITaskDAOConnection dao,String instanceId, String participantToken) throws TMSException;

    void create(ITaskDAOConnection dao,Task task, String participantToken) throws TMSException;
    
    void update(ITaskDAOConnection dao,TaskMetadata task, String participantToken) throws TMSException,AxisFault;

    void deleteAll(ITaskDAOConnection dao,boolean fakeDelete, String subquery, String subqueryClass, String participantToken) throws TMSException;

    Task[] getAvailableTasks(ITaskDAOConnection dao,String participantToken, HashMap parameters) throws Exception;

    Long countAvailableTasks(ITaskDAOConnection dao,String participantToken, HashMap map) throws AuthException;
}