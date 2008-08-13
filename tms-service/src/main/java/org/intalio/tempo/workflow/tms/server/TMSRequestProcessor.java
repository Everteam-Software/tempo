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

package org.intalio.tempo.workflow.tms.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.intalio.tempo.deployment.utils.DeploymentServiceRegister;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.xml.TaskMarshaller;
import org.intalio.tempo.workflow.task.xml.TaskUnmarshaller;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.task.xml.attachments.AttachmentMarshaller;
import org.intalio.tempo.workflow.task.xml.attachments.AttachmentUnmarshaller;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.OMElementQueue;
import org.intalio.tempo.workflow.util.xml.OMMarshaller;
import org.intalio.tempo.workflow.util.xml.OMUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class TMSRequestProcessor extends OMUnmarshaller {
    final static Logger _logger = LoggerFactory.getLogger(TMSRequestProcessor.class);

    private ITMSServer _server;
    private PIPAComponentManager _pipa;
    private DeploymentServiceRegister _registerPipa;
    private static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();

    public TMSRequestProcessor() {
        super(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
        _logger.debug("Created TMSRequestProcessor");
    }

    public void setServer(ITMSServer server) {
        if (_registerPipa != null) {
            _registerPipa.destroy();
        }
        _logger.debug("TMSRequestProcessor.setServer:"+server.getClass().getSimpleName());
        _server = server;
        _pipa = new PIPAComponentManager(_server);
        _registerPipa = new DeploymentServiceRegister(_pipa);
        _registerPipa.init();
    }

    public OMElement getTaskList(final OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            final UserRoles user = _server.getUserRoles(participantToken);
            Task[] tasks = _server.getTaskList(participantToken);
            OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
                public OMElement marshalResponse(Task[] tasks) {
                    OMElement response = createElement("getTaskListResponse");
                    for (Task task : tasks)
                        response.addChild(new TaskMarshaller().marshalTaskMetadata(task, user));
                    return response;
                }
            }.marshalResponse(tasks);
            if (_logger.isDebugEnabled())
                _logger.debug(response.toString());
            return response;
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement getTask(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            final UserRoles user = _server.getUserRoles(participantToken);
            Task task = _server.getTask(taskID, participantToken);
            OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
                public OMElement marshalResponse(Task task) {
                    OMElement response = createElement("getTaskResponse");
                    response.addChild(new TaskMarshaller().marshalFullTask(task, user));
                    return response;
                }
            }.marshalResponse(task);
            return response;
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    private OMElement createOkResponse() {
        return new TMSResponseMarshaller(OM_FACTORY) {
            public OMElement createOkResponse() {
                OMElement okResponse = createElement("okResponse");
                return okResponse;
            }
        }.createOkResponse();
    }

    public OMElement create(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            OMElement taskElement = requireElement(rootQueue, "task");
            Task task = new TaskUnmarshaller().unmarshalFullTask(taskElement);
            if (task instanceof PIPATask) {
                throw new InvalidInputFormatException("Not allowed to create() PIPA tasks");
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.create(task, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement delete(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            List<String> taskIDs = new ArrayList<String>();
            while (true) {
                String taskID = expectElementValue(rootQueue, "taskId");
                if (taskID != null) taskIDs.add(taskID); else break;
            }
            if (taskIDs.isEmpty()) {
                throw new InvalidInputFormatException("At least one taskId element must be present");
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.delete(taskIDs.toArray(new String[] {}), participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement deleteAll(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            boolean fakeDelete = Boolean.valueOf(requireElementValue(rootQueue, "fakeDelete"));
            String subquery = expectElementValue(rootQueue, "subQuery");
            String taskType = expectElementValue(rootQueue, "taskType");
            _server.deleteAll(fakeDelete, subquery, taskType, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement setOutput(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            OMElement omOutputContainer = requireElement(rootQueue, "data");
            Document domOutput = null;
            if (omOutputContainer.getFirstElement() != null) {
                domOutput = new TaskUnmarshaller().unmarshalTaskOutput(omOutputContainer);
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.setOutput(taskID, domOutput, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement setOutputAndComplete(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            OMElement omOutputContainer = requireElement(rootQueue, "data");
            Document domOutput = null;
            if (omOutputContainer.getFirstElement() != null) {
                domOutput = new TaskUnmarshaller().unmarshalTaskOutput(omOutputContainer);
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.setOutputAndComplete(taskID, domOutput, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement complete(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.complete(taskID, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement fail(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String failureCode = requireElementValue(rootQueue, "code");
            String failureReason = requireElementValue(rootQueue, "message");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.fail(taskID, failureCode, failureReason, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement initProcess(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            OMElement omInputContainer = requireElement(rootQueue, "input");
            Document domInput = null;
            if (omInputContainer.getFirstElement() != null) {
                domInput = new TaskUnmarshaller().unmarshalTaskOutput(omInputContainer);
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            Document userProcessResponse = _server.initProcess(taskID, domInput, participantToken);
            OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
                public OMElement marshalResponse(Document userProcessResponse) {
                    OMElement response = createElement("initProcessResponse");
                    OMElement userProcessResponseWrapper = createElement(response, "userProcessResponse");
                    userProcessResponseWrapper.addChild(new XmlTooling().convertDOMToOM(userProcessResponse, this.getOMFactory()));
                    return response;
                }
            }.marshalResponse(userProcessResponse);
            return response;
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement getAttachments(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            Attachment[] attachments = _server.getAttachments(taskID, participantToken);
            OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
                public OMElement marshalResponse(Attachment[] attachments) {
                    OMElement response = createElement("getAttachmentsResponse");
                    for (Attachment attachment : attachments) {
                        new AttachmentMarshaller(getOMFactory()).marshalAttachment(attachment, response);
                    }
                    return response;
                }
            }.marshalResponse(attachments);
            return response;
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement addAttachment(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            OMElement attachmentElement = requireElement(rootQueue, "attachment");
            Attachment attachment = new AttachmentUnmarshaller().unmarshalAttachment(attachmentElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.addAttachment(taskID, attachment, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement removeAttachment(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String attachmentURL = requireElementValue(rootQueue, "attachmentUrl");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            try {
                _server.removeAttachment(taskID, new URL(attachmentURL), participantToken);
            } catch (MalformedURLException e) {
                throw new InvalidInputFormatException(e);
            }
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement reassign(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            AuthIdentifierSet users = expectAuthIdentifiers(rootQueue, "userOwner");
            AuthIdentifierSet roles = expectAuthIdentifiers(rootQueue, "roleOwner");
            TaskState taskState;
            String taskStateStr = requireElementValue(rootQueue, "taskState");
            try {
                taskState = TaskState.valueOf(taskStateStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidInputFormatException("Unknown task state: '" + taskStateStr + "'");
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.reassign(taskID, users, roles, taskState, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement getPipa(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "pipaurl");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            final UserRoles user = _server.getUserRoles(participantToken);
            Task task = _server.getPipa(taskID, participantToken);
            OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
                public OMElement marshalResponse(Task task) {
                    OMElement response = createElement("getPipaTaskResponse");
                    response.addChild(new TaskMarshaller().marshalFullTask(task, user));
                    return response;
                }
            }.marshalResponse(task);
            return response;
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement storePipa(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            OMElement taskElement = requireElement(rootQueue, "task");
            PIPATask task = (PIPATask) new TaskUnmarshaller().unmarshalFullTask(taskElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            // final UserRoles user = _server.getUserRoles(participantToken);
            _server.storePipa(task, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement deletePipa(OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "pipaurl");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            // final UserRoles user = _server.getUserRoles(participantToken);
            _server.deletePipa(taskID, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    public OMElement getAvailableTasks(final OMElement requestElement) throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            String taskType = requireElementValue(rootQueue, "taskType");
            String subQuery = requireElementValue(rootQueue, "subQuery");
            final UserRoles user = _server.getUserRoles(participantToken);
            Task[] tasks = _server.getAvailableTasks(participantToken, taskType, subQuery);
            OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
                public OMElement marshalResponse(Task[] tasks) {
                    OMElement response = createElement("getTaskListResponse");
                    for (Task task : tasks)
                        response.addChild(new TaskMarshaller().marshalTaskMetadata(task, user));
                    return response;
                }
            }.marshalResponse(tasks);
            return response;
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    private AxisFault makeFault(Exception e) {
        if (e instanceof TMSException) {
            if(_logger.isDebugEnabled()) _logger.debug(e.getMessage(), e);
            OMElement response = null;
            if (e instanceof InvalidInputFormatException) response = OM_FACTORY.createOMElement(TMSConstants.INVALID_INPUT_FORMAT);
            else if (e instanceof AccessDeniedException) response = OM_FACTORY.createOMElement(TMSConstants.ACCESS_DENIED);
            else if (e instanceof UnavailableTaskException) response = OM_FACTORY.createOMElement(TMSConstants.UNAVAILABLE_TASK);
            else if (e instanceof UnavailableAttachmentException) response = OM_FACTORY.createOMElement(TMSConstants.UNAVAILABLE_ATTACHMENT);
            else if (e instanceof AuthException) response = OM_FACTORY.createOMElement(TMSConstants.INVALID_TOKEN);

            else
                return AxisFault.makeFault(e);

            response.setText(e.getMessage());
            AxisFault axisFault = new AxisFault(e.getMessage(), e);
            axisFault.setDetail(response);
            return axisFault;
        } else {
            _logger.error(e.getMessage(), e);
            return AxisFault.makeFault(e);
        }
    }

    protected void finalize() throws Throwable {
        if (_registerPipa != null) {
            _registerPipa.destroy();
            _registerPipa = null;
        }
        super.finalize();
    }

    private abstract class TMSResponseMarshaller extends OMMarshaller {
        public TMSResponseMarshaller(OMFactory omFactory) {
            super(omFactory, omFactory.createOMNamespace(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX));
        }
    }
}
