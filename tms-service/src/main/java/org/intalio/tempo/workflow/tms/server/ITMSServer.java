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

import org.apache.axis2.AxisFault;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.tms.TMSException;
import org.w3c.dom.Document;

public interface ITMSServer {

    Task[] getTaskList(String participantToken) throws TMSException;

    Task[] getAvailableTasks(String participantToken, String taskType, String subQuery) throws TMSException;

    void skip(String taskID, String participantToken) throws TMSException;
    
    Task getTask(String taskID, String participantToken) throws TMSException;

    UserRoles getUserRoles(String participantToken) throws TMSException;

    PIPATask getPipa(String formUrl, String participantToken) throws TMSException;

    Document initProcess(String taskID, String user, String formUrl, Document input, String participantToken) throws TMSException, AxisFault;

    Attachment[] getAttachments(String taskID, String participantToken) throws TMSException;

    void addAttachment(String taskID, Attachment attachment, String participantToken) throws TMSException;

    void removeAttachment(String taskID, URL attachmentURL, String participantToken) throws TMSException;

    void reassign(String taskID, AuthIdentifierSet users, AuthIdentifierSet roles, TaskState state, String participantToken) throws TMSException;

    void storePipa(PIPATask task, String participantToken) throws TMSException;

    void deletePipa(String formUrl, String participantToken) throws TMSException;

    void setOutput(String taskID, Document output, String participantToken) throws TMSException;

    void complete(String taskID, String participantToken) throws TMSException;

    void setOutputAndComplete(String taskID, Document output, String participantToken) throws TMSException;

    void fail(String taskID, String failureCode, String failureReason, String participantToken) throws TMSException;

    void delete(String[] taskIDs, String participantToken) throws TMSException;

    void create(Task task, String participantToken) throws TMSException;

    void deleteAll(boolean fakeDelete, String subquery, String subqueryClass, String participantToken) throws TMSException;
}