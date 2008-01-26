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

import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.tms.InvalidTaskStateException;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.w3c.dom.Document;

public interface ITMSServer {

    Task[] getTaskList(String participantToken)
            throws AuthException;

    Task getTask(String taskID, String participantToken)
            throws AuthException,
                UnavailableTaskException;

    public UserRoles getUserRoles(String participantToken) throws AuthException;

    void setOutput(String taskID, Document output, String participantToken)
            throws AuthException, UnavailableTaskException;

    void complete(String taskID, String participantToken)
            throws AuthException,
                UnavailableTaskException,
                InvalidTaskStateException;
    void exit(String taskID, String participantToken)
    throws AuthException,
        UnavailableTaskException,
        InvalidTaskStateException;
    void setOutputAndComplete(String taskID, Document output, String participantToken)
            throws AuthException,
                UnavailableTaskException,
                InvalidTaskStateException;

    void fail(String taskID, String failureCode, String failureReason, String participantToken)
            throws AuthException,
                UnavailableTaskException;

    void delete(String[] taskIDs, String participantToken)
            throws AuthException,
                UnavailableTaskException;

    void create(Task task, String participantToken)
            throws AuthException,
                TaskIDConflictException;

    Document initProcess(String taskID, Document input, String participantToken)
            throws AuthException,
                UnavailableTaskException;

    Attachment[] getAttachments(String taskID, String participantToken)
            throws AuthException,
                UnavailableTaskException;

    void addAttachment(String taskID, Attachment attachment, String participantToken)
            throws AuthException,
                UnavailableTaskException;

    void removeAttachment(String taskID, URL attachmentURL, String participantToken)
            throws AuthException,
                UnavailableAttachmentException,
                UnavailableTaskException;
    
    void reassign(String taskID, AuthIdentifierSet users, AuthIdentifierSet roles, TaskState state,
    		String participantToken) throws AuthException, UnavailableTaskException;
}