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

import java.util.Calendar;

import org.apache.axiom.om.OMElement;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.traits.IChainableTask;
import org.intalio.tempo.workflow.task.traits.ICompleteReportingTask;
import org.intalio.tempo.workflow.task.traits.IProcessBoundTask;
import org.intalio.tempo.workflow.task.traits.ITaskWithAttachments;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.util.xml.XmlBeanMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;

public class TaskMarshaller extends XmlBeanMarshaller {
    final static Logger _log = LoggerFactory.getLogger(TaskMarshaller.class);

    public TaskMarshaller() {
        super(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
    }

    public OMElement marshalTaskMetadata(Task task, UserRoles roles) {
        OMElement om = XmlTooling.convertDocument(marshalXMLTaskMetadata(task, roles));
        om.setLocalName(TaskXMLConstants.TASK_LOCAL_NAME);
        om.setNamespace(TaskXMLConstants.TASK_OM_NAMESPACE);
        return om;
    }

    private XmlObject marshalXMLTaskMetadata(Task task, UserRoles roles) {
        TaskMetadata taskMetadataElement = TaskMetadata.Factory.newInstance();
        taskMetadataElement.setTaskId(task.getID());
        if (task instanceof ITaskWithState) {
            taskMetadataElement.setTaskState(((ITaskWithState) task).getState().toString());
        }
        taskMetadataElement.setTaskType(TaskTypeMapper.getTypeClassName(task.getClass()));
        taskMetadataElement.setDescription(task.getDescription());

        if (task instanceof IProcessBoundTask) {
            taskMetadataElement.setProcessId(((IProcessBoundTask) task).getProcessID());

        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(task.getCreationDate());
        taskMetadataElement.setCreationDate(cal);

        for (String userOwner : task.getUserOwners()) {
            XmlString XmlStrUserOwner = taskMetadataElement.addNewUserOwner();
            XmlStrUserOwner.setStringValue(userOwner);
        }
        for (String roleOwner : task.getRoleOwners()) {
            XmlString XmlStrRoleOwner = taskMetadataElement.addNewRoleOwner();
            XmlStrRoleOwner.setStringValue(roleOwner);
        }

        createACL("claim", task, roles, taskMetadataElement);
        createACL("revoke", task, roles, taskMetadataElement);
        createACL("save", task, roles, taskMetadataElement);
        createACL("complete", task, roles, taskMetadataElement);

        taskMetadataElement.setFormUrl(task.getFormURL().toString());

        if (task instanceof ITaskWithState) {
            ITaskWithState taskWithState = (ITaskWithState) task;
            if (taskWithState.getState().equals(TaskState.FAILED)) {
                taskMetadataElement.setFailureCode(taskWithState.getFailureCode());
                taskMetadataElement.setFailureReason(taskWithState.getFailureReason());
            }
        }

        if (task instanceof ICompleteReportingTask) {
            ICompleteReportingTask crTask = (ICompleteReportingTask) task;
            taskMetadataElement.setUserProcessCompleteSOAPAction(crTask.getCompleteSOAPAction());
        }

        if (task instanceof ITaskWithAttachments) {
            ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
            for (Attachment attachment : taskWithAttachments.getAttachments()) {
                TaskMetadata.Attachments xmlAttachments = taskMetadataElement.addNewAttachments();
                com.intalio.bpms.workflow.taskManagementServices20051109.Attachment xmlAttachment = xmlAttachments
                        .addNewAttachment();
                com.intalio.bpms.workflow.taskManagementServices20051109.AttachmentMetadata xmlAttachmentMetadata = xmlAttachment
                        .addNewAttachmentMetadata();

                AttachmentMetadata metadata = attachment.getMetadata();
                xmlAttachmentMetadata.setMimeType(metadata.getMimeType());
                xmlAttachmentMetadata.setFileName(metadata.getFileName());
                xmlAttachmentMetadata.setTitle(metadata.getTitle());
                xmlAttachmentMetadata.setDescription(metadata.getDescription());
                Calendar attachmentCreateDate = Calendar.getInstance();
                attachmentCreateDate.setTime(metadata.getCreationDate());
                xmlAttachmentMetadata.setCreationDate(attachmentCreateDate);

                xmlAttachment.setPayloadUrl(attachment.getPayloadURL().toString());
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

    private void createACL(String action, Task task, UserRoles roles, XmlObject taskMetadata) {
        if (task.getAuthorizedActions().contains(action)) {
            XmlObject acl = createElement(taskMetadata, action + "Action");
            for (String user : task.getAuthorizedUsers(action)) {
                createElement(acl, "user", user);
            }
            for (String role : task.getAuthorizedRoles(action)) {
                createElement(acl, "role", role);
            }
            if (roles != null) {
                boolean authorized = task.isAuthorizedAction(roles, action);
                createElement(acl, "authorized", authorized ? "true" : "false");
            }
        }
    }

    public void marshalTaskInput(ITaskWithInput task,
            com.intalio.bpms.workflow.taskManagementServices20051109.Task.Input parent) {
        try {
            XmlObject xmlTaskInput = XmlObject.Factory.parse(task.getInput());
            parent.set(xmlTaskInput);
        } catch (XmlException e) {
            _log.error("Error while marshalling input", e);
        }
    }

    public void marshalTaskOutput(ITaskWithOutput task,
            com.intalio.bpms.workflow.taskManagementServices20051109.Task.Output parent) {
        Document output = task.getOutput();
        if (output == null) {
            throw new IllegalArgumentException("Task has no output");
        }
        try {
            XmlObject xmlTaskOutput = XmlObject.Factory.parse(output);
            parent.set(xmlTaskOutput);
        } catch (XmlException e) {
            _log.error("Error while marshalling output", e);
        }
    }

    public OMElement marshalFullTask(Task task, UserRoles user) {
        com.intalio.bpms.workflow.taskManagementServices20051109.Task  taskElement = com.intalio.bpms.workflow.taskManagementServices20051109.Task.Factory.newInstance();
        marshalFullTask(task, taskElement, user);
        OMElement om = XmlTooling.convertDocument(taskElement);
        om.setLocalName(TaskXMLConstants.TASK_LOCAL_NAME);
        om.setNamespace(TaskXMLConstants.TASK_OM_NAMESPACE);
        return om;
    }
    
    // for compatibility usage
    public void marshalFullTask(Task task, OMElement parent, UserRoles user) {
        try {
            parent.addChild(marshalFullTask(task, user));
        } catch (Exception e) {
            _log.error("Error while marshalling fulltask", e);
        }
    }
    
    private void marshalFullTask(Task task, com.intalio.bpms.workflow.taskManagementServices20051109.Task parent,
            UserRoles user) {

        XmlObject metadataElement = marshalXMLTaskMetadata(task, user);
        if(_log.isDebugEnabled()) _log.debug(metadataElement.xmlText());
        parent.setMetadata((TaskMetadata) metadataElement);

        if (task instanceof ITaskWithInput) {
            ITaskWithInput taskWithInput = (ITaskWithInput) task;
            com.intalio.bpms.workflow.taskManagementServices20051109.Task.Input taskInput = parent.addNewInput();
            marshalTaskInput(taskWithInput, taskInput);
        }

        if (task instanceof ITaskWithOutput) {
            ITaskWithOutput taskWithOutput = (ITaskWithOutput) task;
            if (taskWithOutput.getOutput() != null) {
                com.intalio.bpms.workflow.taskManagementServices20051109.Task.Output taskOutput = parent.addNewOutput();
                marshalTaskOutput(taskWithOutput, taskOutput);
            }
        }
    }

}