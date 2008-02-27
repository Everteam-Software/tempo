/**
 * Copyright (c) 2005-2008 Intalio inc.
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

import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.axiom.om.OMElement;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
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
import org.intalio.tempo.workflow.task.traits.ITaskWithDeadline;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.traits.ITaskWithPriority;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.traits.InitTask;
import org.intalio.tempo.workflow.util.xml.XmlBeanMarshaller;
import org.intalio.tempo.workflow.util.xml.XsdDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;

public class TaskMarshaller extends XmlBeanMarshaller {
    final static Logger _log = LoggerFactory.getLogger(TaskMarshaller.class);
    final static String[] ACTIONS = new String[] { "claim", "revoke", "save", "complete" };

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

        if (task instanceof InitTask) {
            InitTask itask = (InitTask) task;
            taskMetadataElement.setInitMessageNamespaceURI(itask.getInitMessageNamespaceURI().toString());
            taskMetadataElement.setInitOperationSOAPAction(((InitTask) task).getInitOperationSOAPAction());
            taskMetadataElement.setProcessEndpoint(((InitTask) task).getProcessEndpoint().toString());
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

        for (String action : ACTIONS)
            createACL(action, task, roles, taskMetadataElement);

        taskMetadataElement.setFormUrl(task.getFormURL().toString());

        if (task instanceof ITaskWithState) {
            ITaskWithState taskWithState = (ITaskWithState) task;
            if (taskWithState.getState().equals(TaskState.FAILED)) {
                taskMetadataElement.setFailureCode(taskWithState.getFailureCode());
                taskMetadataElement.setFailureReason(taskWithState.getFailureReason());
            }
        }

        if (task instanceof ITaskWithDeadline) {
            ITaskWithDeadline crTask = (ITaskWithDeadline) task;

            if (crTask.getDeadline() == null)
                createElement(taskMetadataElement, "deadline", null);
            else {
                createElement(taskMetadataElement, "deadline", (new XsdDateTime(crTask.getDeadline())).toString());
            }
        }
        if (task instanceof ITaskWithPriority) {
            ITaskWithPriority crTask = (ITaskWithPriority) task;

            if (crTask.getPriority() == null)
                createElement(taskMetadataElement, "priority", null);
            else {
                createElement(taskMetadataElement, "priority", crTask.getPriority().toString());
            }
        }

        if (task instanceof ICompleteReportingTask) {
            ICompleteReportingTask crTask = (ICompleteReportingTask) task;
            taskMetadataElement.setUserProcessCompleteSOAPAction(crTask.getCompleteSOAPAction());
        }

        if (task instanceof ITaskWithAttachments) {
            ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
            TaskMetadata.Attachments xmlAttachments = taskMetadataElement.addNewAttachments();
            for (Attachment attachment : taskWithAttachments.getAttachments()) {
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

                final URL payloadURL = attachment.getPayloadURL();
                xmlAttachment.setPayloadUrl(payloadURL.toString());
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
            parent.set(extractXmlObject(task.getInput()));
        } catch (Exception e) {
            // if we can't marshal the input, the task is loosing some data.
            throw new RuntimeException(e);
        }
    }

    /**
     * The default namespace is lost when using xmlbeans. This methods sets some
     * xmlbeans options so we can find/restore the namespaces properly from the
     * original document to the XmlObject
     * 
     * @param doc
     *            the original xml document
     * @return an <code>XmlObject</code> instance with the default namespace
     *         restored properly
     * @throws XmlException
     *             only if parsing fails. Other exceptions that can occured when
     *             looking for NS are hidden
     */
    static XmlObject extractXmlObject(final Document doc) throws XmlException {
        XmlOptions opts = new XmlOptions();
        try {
            Node firstChild = doc.getFirstChild();
            String nodeName = firstChild.getNodeName();
            int dot = nodeName.lastIndexOf(":");
            String xmlns = (dot > 0) ? "xmlns:" + nodeName.substring(0, dot) : "xmlns";
            NamedNodeMap atts = firstChild.getAttributes();
            int len = atts.getLength();
            String uri = null;
            for (int i = 0; i < len && uri == null; i++) {
                String nn = atts.item(i).getNodeName();
                if (nn.equalsIgnoreCase(xmlns)) {
                    uri = atts.item(i).getNodeValue();
                    if (_log.isDebugEnabled())
                        _log.debug("Restoring NS:" + uri);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("", uri);
                    opts.setLoadSubstituteNamespaces(map);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while settings xml opts", e);
        }
        XmlObject xmlTaskOutput = XmlObject.Factory.parse(doc, opts);
        return xmlTaskOutput;
    }

    public void marshalTaskOutput(ITaskWithOutput task,
            com.intalio.bpms.workflow.taskManagementServices20051109.Task.Output parent) {
        try {
            parent.set(extractXmlObject(task.getOutput()));
        } catch (XmlException e) {
            // if we can't marshal the output, the task is loosing some data.
            throw new RuntimeException(e);
        }
    }

    public OMElement marshalFullTask(Task task, UserRoles user) {
        com.intalio.bpms.workflow.taskManagementServices20051109.Task taskElement = com.intalio.bpms.workflow.taskManagementServices20051109.Task.Factory
                .newInstance();
        marshalFullTask(task, taskElement, user);
        OMElement om = XmlTooling.convertDocument(taskElement);
        if (om.getLocalName().equalsIgnoreCase("xml-fragment")) {
            om.setLocalName(TaskXMLConstants.TASK_LOCAL_NAME);
            om.setNamespace(TaskXMLConstants.TASK_OM_NAMESPACE);
        }
        return om;
    }

    // for compatibility usage
    public void marshalFullTask(Task task, OMElement parent, UserRoles user) {
        try {
            parent.addChild(marshalFullTask(task, user));
        } catch (Exception e) {
            // if we can't marshal the task, better fail fast.
            throw new RuntimeException(e);
        }
    }

    private void marshalFullTask(Task task, com.intalio.bpms.workflow.taskManagementServices20051109.Task parent,
            UserRoles user) {

        XmlObject metadataElement = marshalXMLTaskMetadata(task, user);
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