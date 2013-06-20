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

package org.intalio.tempo.workflow.tms;

import java.net.URL;
import java.util.List;

import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.InvalidTaskException;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.w3c.dom.Document;

public interface ITaskManagementService {
    void close();

    Task[] getTaskList() throws AuthException;

    Task[] getAvailableTasks(final String taskType, final String subQuery) throws AuthException;
    
    Long countAvailableTasks(final String taskType, final String subQuery) throws AuthException;
    
    Task[] getAvailableTasks(final String taskType, final String subQuery, final String first, final String max) throws AuthException;

    Task[] getAvailableTasks(final String taskType, final String subQuery, final String first, final String max, final String fetchMetaData) throws AuthException;

    Task getTask(String taskID) throws AuthException, UnavailableTaskException;

    void setOutput(String taskID, Document output) throws AuthException, UnavailableTaskException, InvalidTaskStateException;

    void complete(String taskID) throws AuthException, UnavailableTaskException, InvalidTaskStateException;

    void setOutputAndComplete(String taskID, Document output) throws AuthException, UnavailableTaskException, InvalidTaskStateException;

    void fail(String taskID, String failureCode, String failureReason) throws AuthException, UnavailableTaskException, InvalidTaskStateException;

    void delete(String[] taskIDs) throws AuthException, UnavailableTaskException;

    void deleteAll(String fakeDelete, String subQuery, String taskType) throws AuthException, UnavailableTaskException;

    void create(Task task) throws AuthException, TaskIDConflictException;
    
    void update(Task task) throws AuthException, UnavailableTaskException;

    Document init(String taskID, Document output) throws AuthException, UnavailableTaskException;

    Attachment[] getAttachments(String taskID) throws AuthException, UnavailableTaskException;

    void addAttachment(String taskID, Attachment attachment) throws AuthException, UnavailableTaskException;

    void removeAttachment(String taskID, URL attachmentURL) throws AuthException, UnavailableTaskException, UnavailableAttachmentException;

    void reassign(String taskID, AuthIdentifierSet users, AuthIdentifierSet roles, TaskState state) throws AuthException, UnavailableTaskException;

    void storePipa(PIPATask task) throws AuthException, InvalidTaskException;

    void deletePipa(String formUrl) throws AuthException, UnavailableTaskException;

    PIPATask getPipa(String formUrl) throws AuthException, UnavailableTaskException;
    
    List<String> getCustomColumns() throws AuthException;
}
