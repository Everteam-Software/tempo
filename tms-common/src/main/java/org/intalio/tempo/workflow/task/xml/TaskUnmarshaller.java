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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.xml.TaskTypeMapper;
import org.intalio.tempo.workflow.task.traits.IChainableTask;
import org.intalio.tempo.workflow.task.Task.ACL;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.traits.ICompleteReportingTask;
import org.intalio.tempo.workflow.task.traits.IProcessBoundTask;
import org.intalio.tempo.workflow.task.traits.ITaskWithAttachments;
import org.intalio.tempo.workflow.task.traits.ITaskWithDeadline;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.traits.ITaskWithPriority;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.xml.attachments.AttachmentUnmarshaller;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.OMDOMConvertor;
import org.intalio.tempo.workflow.util.xml.OMElementQueue;
import org.intalio.tempo.workflow.util.xml.OMUnmarshaller;
import org.intalio.tempo.workflow.util.xml.XsdDateTime;

public class TaskUnmarshaller extends OMUnmarshaller {

	@SuppressWarnings("unused")
	private static final Logger _logger = Logger
			.getLogger(TaskUnmarshaller.class);

	public TaskUnmarshaller() {
		super(TaskXMLConstants.TASK_NAMESPACE,
				TaskXMLConstants.TASK_NAMESPACE_PREFIX);
	}

	private Attachment unmarshalAttachment(OMElement rootElement)
			throws InvalidInputFormatException {
		return new AttachmentUnmarshaller().unmarshalAttachment(rootElement);
	}

	public Task unmarshalTaskFromMetadata(OMElement rootElement)
			throws InvalidInputFormatException {
		if (rootElement == null) {
			throw new RequiredArgumentException("rootElement");
		}

		OMElementQueue rootQueue = new OMElementQueue(rootElement);

		String taskID = requireElementValue(rootQueue, "taskId");
		String taskStateStr = expectElementValue(rootQueue, "taskState");
		String taskTypeStr = requireElementValue(rootQueue, "taskType");
		String description = expectElementValue(rootQueue, "description");
		String processID = expectElementValue(rootQueue, "processId");
		String creationDateStr = expectElementValue(rootQueue, "creationDate");
	
		AuthIdentifierSet userOwners = expectAuthIdentifiers(rootQueue,
				"userOwner");
		AuthIdentifierSet roleOwners = expectAuthIdentifiers(rootQueue,
				"roleOwner");
		String deadline = expectElementValue(rootQueue, "deadline");
		String priority = expectElementValue(rootQueue, "priority");
		
		Task.ACL claim = readACL(rootQueue, "claim");
		Task.ACL revoke = readACL(rootQueue, "revoke");
		Task.ACL save = readACL(rootQueue, "save");
		Task.ACL complete = readACL(rootQueue, "complete");

		String formURLStr = requireElementValue(rootQueue, "formUrl");
		URI formURL = null;
		try {
			formURL = new URI(formURLStr);
		} catch (URISyntaxException e) {
			throw new InvalidInputFormatException(e);
		}

		String failureCode = expectElementValue(rootQueue, "failureCode");
		String failureReason = expectElementValue(rootQueue, "failureReason");
		expectElementValue(rootQueue, "userProcessEndpoint"); // TODO: these
		// violate the
		// WSDL! do
		// something
		expectElementValue(rootQueue, "userProcessNamespaceURI");
		
		
		String completeSOAPAction = expectElementValue(rootQueue,
				"userProcessCompleteSOAPAction");
		OMElement attachmentsElement = expectElement(rootQueue, "attachments");
		String isChainedBeforeStr = expectElementValue(rootQueue,
				"isChainedBefore");
		String previousTaskID = expectElementValue(rootQueue, "previousTaskId");
	
		Class<? extends Task> taskClass = TaskTypeMapper
				.getTypeClassByName(taskTypeStr);
		Task resultTask = null;
		TaskState taskState = null;

		if (!ITaskWithState.class.isAssignableFrom(taskClass)) {
			forbidParameter(taskStateStr, "task state");
			forbidParameter(failureCode, "failure code");
			forbidParameter(failureReason, "failure reason");
		} else {
			try {
				taskState = (taskStateStr == null) ? TaskState.READY
						: TaskState.valueOf(taskStateStr.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new InvalidInputFormatException("Unknown task state: '"
						+ taskStateStr + "'");
			}
		}
		
		if (IProcessBoundTask.class.isAssignableFrom(taskClass)) {
			requireParameter(processID, "processID");
		} else {
			forbidParameter(processID, "processID");
		}
		if (ICompleteReportingTask.class.isAssignableFrom(taskClass)) {
			requireParameter(completeSOAPAction, "completion SOAPAction");
		} else {
			forbidParameter(completeSOAPAction, "completion SOAPAction");
		}
		if (!ITaskWithAttachments.class.isAssignableFrom(taskClass)) {
			forbidParameter(attachmentsElement, "task attachment(s)");
		}
		if (!IChainableTask.class.isAssignableFrom(taskClass)) {
			forbidParameter(isChainedBeforeStr, "is-chained-before flag");
			forbidParameter(previousTaskID, "previous chained task ID");
		}
		
		// TODO: the following is a loathsome if-cascade:
		if (taskClass.equals(PIPATask.class)) {
			resultTask = new PIPATask(taskID, formURL, null, null, null);
		} else if (taskClass.equals(PATask.class)) {
			resultTask = new PATask(taskID, formURL, processID,
					completeSOAPAction, null);
		} else if (taskClass.equals(Notification.class)) {
			resultTask = new Notification(taskID, formURL, null);
		} else {
			throw new RuntimeException("Unknown task class: " + taskClass);
		}

		resultTask.getUserOwners().addAll(userOwners);
		resultTask.getRoleOwners().addAll(roleOwners);
	
		resultTask.setDescription(description == null ? "" : description);
		_logger.debug("Setting date from " + creationDateStr);

		if ((creationDateStr != null) && (creationDateStr.trim().length() > 0)) {
			resultTask.setCreationDate(new XsdDateTime(creationDateStr)
					.getTime());
		} else {
			resultTask.setCreationDate(new Date());
		}
		_logger.debug("Date set to " + resultTask.getCreationDate());

		authorize(resultTask, "claim", claim);
		authorize(resultTask, "revoke", revoke);
		authorize(resultTask, "save", save);
		authorize(resultTask, "complete", complete);
		
		if (ITaskWithState.class.isAssignableFrom(taskClass)) {
			ITaskWithState taskWithState = (ITaskWithState) resultTask;
			taskWithState.setState(taskState);
			if (taskWithState.getState().equals(TaskState.FAILED)) {
				requireParameter(failureCode, "failure code");

				taskWithState.setFailureCode(failureCode);
				taskWithState.setFailureReason(failureReason == null ? ""
						: failureReason);
			} else {
				forbidParameter(failureCode, "failure code");
				forbidParameter(failureReason, "failure reason");
			}
		}
		if (IProcessBoundTask.class.isAssignableFrom(taskClass)) {
			((IProcessBoundTask) resultTask).setProcessID(processID);
		}
		if (ICompleteReportingTask.class.isAssignableFrom(taskClass)) {
		
			((ICompleteReportingTask) resultTask)
					.setCompleteSOAPAction(completeSOAPAction);
			
		}
		try{
		
		}catch(Exception e){e.printStackTrace();}
		if (ITaskWithAttachments.class.isAssignableFrom(taskClass)) {
			ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) resultTask;
			if (attachmentsElement != null) {
				OMElementQueue attachmentQueue = new OMElementQueue(
						attachmentsElement);
				boolean done = false;
				while (!done) {
					OMElement attachmentElement = expectElement(
							attachmentQueue, "attachment");
					if (attachmentElement != null) {
						Attachment attachment = unmarshalAttachment(attachmentElement);
						taskWithAttachments.addAttachment(attachment);
					} else {
						done = true;
					}
				}
			}
		}
		
		if (IChainableTask.class.isAssignableFrom(taskClass)) {
			IChainableTask chainableTask = (IChainableTask) resultTask;
			if (isChainedBeforeStr != null) {
				if ("1".equals(isChainedBeforeStr)
						|| "true".equals(isChainedBeforeStr)) {
					if (previousTaskID == null) {
						throw new InvalidInputFormatException(
								"tms:previousTaskId is required "
										+ "if tms:isChainedBefore is true");
					}
					chainableTask.setPreviousTaskID(previousTaskID);
					chainableTask.setChainedBefore(true);
				} else {
					if ((previousTaskID != null)
							&& (!"".equals(previousTaskID))) {
						throw new InvalidInputFormatException(
								"tms:previousTaskId must be empty or not present "
										+ "if tms:isChainedBefore is false");
					}
				}
			} else {
				if (previousTaskID != null) {
					throw new InvalidInputFormatException(
							"tms:isChainedBefore is required "
									+ "if tms:previousTaskId is present");
				}
			}
		}
		
		/// the following is added to support task deadlines
		if (ITaskWithDeadline.class.isAssignableFrom(taskClass)) {
			ITaskWithDeadline taskWithDeadline = (ITaskWithDeadline) resultTask;
			if ((deadline != null) && (deadline.trim().length() > 0)) {
				taskWithDeadline.setDeadline(new XsdDateTime(deadline)
						.getTime());
			} else {
				taskWithDeadline.setDeadline(null);
			}

		}
		
		// the following is added to support task priorities
		if (ITaskWithPriority.class.isAssignableFrom(taskClass)) {
			
			ITaskWithPriority taskWithDeadline = (ITaskWithPriority) resultTask;
			if ((priority != null) && (priority.trim().length() > 0)) {
				
				taskWithDeadline.setPriority(Integer.valueOf(priority));
			} else {
				
				try{
				taskWithDeadline.setPriority(null);}
				catch(Exception e){
					e.printStackTrace();
				}
				
			}

		}
		
		return resultTask;
	}

	/*
	 * private Date calculateDeadline(OMElementQueue rootQueue) { OMElement
	 * scheduledActions=expectElement(rootQueue, "scheduledActions"); OMElement
	 * expiration=expectElement(rootQueue, "expiration"); OMElementQueue
	 * expirationQueue=new OMElementQueue(expiration); String
	 * _until=expectElementValue(expirationQueue, "until"); String
	 * _for=expectElementValue(expirationQueue, "for"); DateFormat
	 * dateFormatter=new DateFormat();
	 * 
	 * if(dateFormatter.parse(_until, new ParsePosition(0))!=null)return
	 * dateFormatter.parse(_until, new ParsePosition(0)); else if(Time()) return
	 * null; }
	 */

	private void authorize(Task resultTask, String action, ACL acl) {
		for (String user : acl._users) {
			resultTask.authorizeActionForUser(action, user);
		}
		for (String role : acl._roles) {
			resultTask.authorizeActionForRole(action, role);
		}
	}

	private Task.ACL readACL(OMElementQueue rootQueue, String action) {
		Task.ACL acl = new Task.ACL();
		OMElement el = expectElement(rootQueue, action + "Action");
		if (el != null) {
			OMElementQueue queue = new OMElementQueue(el);
			acl._users = expectAuthIdentifiers(queue, "user");
			acl._roles = expectAuthIdentifiers(queue, "role");
		}
		return acl;
	}

	private Document unmarshalTaskPayload(OMElement containerElement)
			throws InvalidInputFormatException {
		if (containerElement == null) {
			throw new RequiredArgumentException("containerElement");
		}
		Iterator<OMElement> it = containerElement.getChildElements();
		if (!it.hasNext()) {
			throw new InvalidInputFormatException(
					"Payload container element must contain exactly one child element");
		}
		Document result = null;
		OMElement firstPayloadElement = it.next();
		if (it.hasNext()) {
			throw new InvalidInputFormatException(
					"Task payload must consist of exactly one element.");
		} else {
			result = OMDOMConvertor.convertOMToDOM(firstPayloadElement);
		}
		return result;
	}

	public Document unmarshalTaskInput(OMElement inputContainerElement)
			throws InvalidInputFormatException {
		return unmarshalTaskPayload(inputContainerElement);
	}

	public Document unmarshalTaskOutput(OMElement outputContainerElement)
			throws InvalidInputFormatException {
		return unmarshalTaskPayload(outputContainerElement);
	}

	public Task unmarshalFullTask(OMElement rootElement)
			throws InvalidInputFormatException {
		if (rootElement == null) {
			throw new RequiredArgumentException("rootElement");
		}
		Task resultTask = null;

		OMElementQueue queue = new OMElementQueue(rootElement);
		OMElement metadataElement = requireElement(queue, "metadata");
		OMElement inputElement = expectElement(queue, "input");
		OMElement outputElement = expectElement(queue, "output");

		resultTask = unmarshalTaskFromMetadata(metadataElement);
		if (resultTask instanceof ITaskWithInput) {
			requireParameter(inputElement, "task input");
			Document input = unmarshalTaskInput(inputElement);
			((ITaskWithInput) resultTask).setInput(input);
		} else {
			forbidParameter(inputElement, "task input");
		}
		if ((resultTask instanceof ITaskWithOutput) && outputElement != null) {
			requireParameter(outputElement, "task output");
			Document output = unmarshalTaskOutput(outputElement);
			((ITaskWithOutput) resultTask).setOutput(output);
		} else {
			forbidParameter(outputElement, "task output");
		}

		return resultTask;
	}
}
