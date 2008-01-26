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

package org.intalio.tempo.workflow.task.xml;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.w3c.dom.Document;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Task;

import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.traits.IChainableTask;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.traits.ICompleteReportingTask;
import org.intalio.tempo.workflow.task.traits.IProcessBoundTask;
import org.intalio.tempo.workflow.task.traits.ITaskWithAttachments;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.xml.attachments.AttachmentMarshaller;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.util.xml.OMDOMConvertor;
import org.intalio.tempo.workflow.util.xml.OMMarshaller;
import org.intalio.tempo.workflow.util.xml.XsdDateTime;

public class TaskMarshaller extends OMMarshaller {
    public TaskMarshaller(OMFactory omFactory) {
        super(omFactory, omFactory.createOMNamespace(TaskXMLConstants.TASK_NAMESPACE,
                TaskXMLConstants.TASK_NAMESPACE_PREFIX));
    }

    private void marshalAttachment(Attachment attachment, OMElement parentElement) {
        new AttachmentMarshaller(getOMFactory()).marshalAttachment(attachment, parentElement);
    }

    public OMElement marshalTaskMetadata(Task task, UserRoles roles) {
        OMElement taskMetadataElement = createElement("taskMetadata");
        createElement(taskMetadataElement, "taskId", task.getID());
        if (task instanceof ITaskWithState) {
            createElement(taskMetadataElement, "taskState", ((ITaskWithState) task).getState().toString());
        }
        createElement(taskMetadataElement, "taskType", TaskTypeMapper.getTypeClassName(task.getClass()));
        createElement(taskMetadataElement, "description", task.getDescription());
        if (task instanceof IProcessBoundTask) {
            createElement(taskMetadataElement, "processId", ((IProcessBoundTask) task).getProcessID());
        }
        String creationDate = new XsdDateTime(task.getCreationDate()).toString();
        createElement(taskMetadataElement, "creationDate", creationDate);
        for (String userOwner : task.getUserOwners()) {
            createElement(taskMetadataElement, "userOwner", userOwner);
        }
        for (String roleOwner : task.getRoleOwners()) {
            createElement(taskMetadataElement, "roleOwner", roleOwner);
        }

        createACL("claim", task, roles, taskMetadataElement);
        createACL("revoke", task, roles, taskMetadataElement);
        createACL("save", task, roles, taskMetadataElement);
        createACL("complete", task, roles, taskMetadataElement);
        
        createElement(taskMetadataElement, "formUrl", task.getFormURL().toString());
        
        if (task instanceof ITaskWithState) {
            ITaskWithState taskWithState = (ITaskWithState) task;
            if (taskWithState.getState().equals(TaskState.FAILED)) {
                createElement(taskMetadataElement, "failureCode", taskWithState.getFailureCode());
                createElement(taskMetadataElement, "failureReason", taskWithState.getFailureReason());
            }
        }
        
        if (task instanceof ICompleteReportingTask) {
            ICompleteReportingTask crTask = (ICompleteReportingTask) task;
            createElement(taskMetadataElement, "userProcessCompleteSOAPAction", crTask.getCompleteSOAPAction());
        }
        
        if (task instanceof ITaskWithAttachments) {
            ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
            OMElement attachmentsElement = createElement(taskMetadataElement, "attachments");
            for (Attachment attachment : taskWithAttachments.getAttachments()) {
                marshalAttachment(attachment, attachmentsElement);
            }
        }
        
        if (task instanceof IChainableTask) {
            IChainableTask chainableTask = (IChainableTask) task;
            String isChainedBeforeStr = chainableTask.isChainedBefore() ? "true" : "false";
            createElement(taskMetadataElement, "isChainedBefore", isChainedBeforeStr);
            if (chainableTask.isChainedBefore()) {
                createElement(taskMetadataElement, "previousTaskId", chainableTask.getPreviousTaskID());
            }
        }
        return taskMetadataElement;
    }

    private void createACL(String action, Task task, UserRoles roles, OMElement taskMetadata) {
        if (task.getAuthorizedActions().contains(action)) {
            OMElement acl = createElement(taskMetadata, action+"Action");
            for (String user: task.getAuthorizedUsers(action)) {
                createElement(acl, "user", user);
            }
            for (String role: task.getAuthorizedRoles(action)) { 
                createElement(acl, "role", role);
            }
            if (roles != null) {
                boolean authorized = task.isAuthorizedAction(roles, action); 
                createElement(acl, "authorized", authorized ? "true" : "false");
            }
        }
    }

    private void convertPayloadToOM(Document payload, OMElement parent) {
        OMElement convertedPayload = OMDOMConvertor.convertDOMToOM(payload, getOMFactory());
        parent.addChild(convertedPayload);
    }

    public void marshalTaskInput(ITaskWithInput task, OMElement parent) {
        convertPayloadToOM(task.getInput(), parent);
    }

    public void marshalTaskOutput(ITaskWithOutput task, OMElement parent) {
        Document output = task.getOutput();
        if (output == null) {
            throw new IllegalArgumentException("Task has no output");
        }
        convertPayloadToOM(output, parent);
    }

    public void marshalFullTask(Task task, OMElement parent, UserRoles user) {
        if (task == null) {
            throw new RequiredArgumentException("task");
        }
        if (parent == null) {
            throw new RequiredArgumentException("parent");
        }

        OMElement metadataElement = marshalTaskMetadata(task, user);
        metadataElement.setLocalName("metadata");
        parent.addChild(metadataElement);

        if (task instanceof ITaskWithInput) {
            ITaskWithInput taskWithInput = (ITaskWithInput) task;
            OMElement inputContainer = parent.getOMFactory().createOMElement("input", getOMNamespace());
            marshalTaskInput(taskWithInput, inputContainer);
            parent.addChild(inputContainer);
        }
        
        if (task instanceof ITaskWithOutput) {
            ITaskWithOutput taskWithOutput = (ITaskWithOutput) task;
            if (taskWithOutput.getOutput() != null) {
                OMElement outputContainer = parent.getOMFactory().createOMElement("output", getOMNamespace());
                marshalTaskOutput(taskWithOutput, outputContainer);
                parent.addChild(outputContainer);
            }
        }
    }
}
