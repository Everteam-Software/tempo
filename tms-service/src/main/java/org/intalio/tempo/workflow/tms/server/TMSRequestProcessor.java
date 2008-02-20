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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
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
import org.intalio.tempo.workflow.tms.InvalidTaskStateException;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
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

    private abstract class TMSResponseMarshaller extends OMMarshaller {
        public TMSResponseMarshaller(OMFactory omFactory) {
            super(omFactory, omFactory.createOMNamespace(TaskXMLConstants.TASK_NAMESPACE,
                    TaskXMLConstants.TASK_NAMESPACE_PREFIX));
        }
    }

    private ITMSServer _server = null;

    public TMSRequestProcessor() throws Exception {
        super(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
    }

    public void setServer(ITMSServer server) {
        _server = server;
    }

    public OMElement getTaskList(final OMElement requestElement)
            throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");

            final UserRoles user = _server.getUserRoles(participantToken);
            Task[] tasks = _server.getTaskList(participantToken);
            for(Task t : tasks) {
              _logger.info(t.getClass().getName()+"\n"+t.toString());  
            }

            OMElement response = new TMSResponseMarshaller(requestElement.getOMFactory()) {
                public OMElement marshalResponse(Task[] tasks) {
                    OMElement response = createElement("getTaskListResponse");
                    for (Task task : tasks) 
                        response.addChild(new TaskMarshaller().marshalTaskMetadata(task, user));
                    return response;
                }
            }.marshalResponse(tasks);
            
            _logger.info(response.toString());

            return response;
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (Exception e) {
            _logger.error("Cannot retrieve task list",e);
            throw AxisFault.makeFault(e);
        }
    }

    public OMElement getTask(OMElement requestElement)
            throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");

            final UserRoles user = _server.getUserRoles(participantToken);
            Task task = _server.getTask(taskID, participantToken);
            
            OMElement response = new TMSResponseMarshaller(requestElement.getOMFactory()) {
                public OMElement marshalResponse(Task task) {
                    OMElement response = createElement("getTaskResponse");
                    response.addChild(new TaskMarshaller().marshalFullTask(task, user));
                    return response;
                }
            }.marshalResponse(task);
            
            if(_logger.isDebugEnabled()) _logger.debug(response.toString());

            return response;
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableTaskException e) {
            throw AxisFault.makeFault(e);
        }
    }

    private OMElement createOkResponse(OMFactory omFactory) {
        return new TMSResponseMarshaller(omFactory) {
            public OMElement createOkResponse() {
                OMElement okResponse = createElement("okResponse");
                return okResponse;
            }
        }.createOkResponse();
    }

    public OMElement create(OMElement requestElement)
            throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            OMElement taskElement = requireElement(rootQueue, "task");
            Task task = new TaskUnmarshaller().unmarshalFullTask(taskElement);
            if (task instanceof PIPATask) {
                throw new InvalidInputFormatException("Not allowed to create() PIPA tasks");
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");

            _server.create(task, participantToken);

            return createOkResponse(requestElement.getOMFactory());
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (TaskIDConflictException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public OMElement delete(OMElement requestElement)
            throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            List<String> taskIDs = new ArrayList<String>();
            boolean done = false;
            while (! done) {
                String taskID = expectElementValue(rootQueue, "taskId");
                if (taskID != null) {
                    taskIDs.add(taskID);
                } else {
                    done = true;
                }
            }
            if (taskIDs.isEmpty()) {
                throw new InvalidInputFormatException("At least one taskId element must be present");
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");

            _server.delete(taskIDs.toArray(new String[] {}), participantToken);

            return createOkResponse(requestElement.getOMFactory());
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableTaskException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public OMElement setOutput(OMElement requestElement)
            throws AxisFault {
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

            return createOkResponse(requestElement.getOMFactory());
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableTaskException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public OMElement setOutputAndComplete(OMElement requestElement)
            throws AxisFault {
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

            return createOkResponse(requestElement.getOMFactory());
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableTaskException e) {
            throw AxisFault.makeFault(e);
        } catch (InvalidTaskStateException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public OMElement complete(OMElement requestElement)
            throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");

            _server.complete(taskID, participantToken);

            return createOkResponse(requestElement.getOMFactory());
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableTaskException e) {
            throw AxisFault.makeFault(e);
        } catch (InvalidTaskStateException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public OMElement fail(OMElement requestElement)
            throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String failureCode = requireElementValue(rootQueue, "code");
            String failureReason = requireElementValue(rootQueue, "message");
            String participantToken = requireElementValue(rootQueue, "participantToken");

            _server.fail(taskID, failureCode, failureReason, participantToken);

            return createOkResponse(requestElement.getOMFactory());
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableTaskException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public OMElement initProcess(OMElement requestElement)
            throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            OMElement omInputContainer = requireElement(rootQueue, "input");

            Document domInput = null;
            if (omInputContainer.getFirstElement() != null) {
                TaskUnmarshaller taskUnmarshaller = new TaskUnmarshaller();
                domInput = taskUnmarshaller.unmarshalTaskOutput(omInputContainer);
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");

            Document userProcessResponse = _server.initProcess(taskID, domInput, participantToken);

            OMElement response = new TMSResponseMarshaller(requestElement.getOMFactory()) {
                public OMElement marshalResponse(Document userProcessResponse) {
                    OMElement response = createElement("initProcessResponse");
                    OMElement userProcessResponseWrapper = createElement(response, "userProcessResponse");
                    userProcessResponseWrapper.addChild(new XmlTooling().convertDOMToOM(userProcessResponse, this.getOMFactory()));
                    return response;
                }
            }.marshalResponse(userProcessResponse);

            return response;
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableTaskException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public OMElement getAttachments(OMElement requestElement)
            throws AxisFault {
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");

            Attachment[] attachments = _server.getAttachments(taskID, participantToken);

            OMElement response = new TMSResponseMarshaller(requestElement.getOMFactory()) {
                public OMElement marshalResponse(Attachment[] attachments) {
                    OMElement response = createElement("getAttachmentsResponse");
                    for (Attachment attachment : attachments) {
                        new AttachmentMarshaller(getOMFactory()).marshalAttachment(attachment, response);
                    }

                    return response;
                }
            }.marshalResponse(attachments);

            return response;
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableTaskException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public OMElement addAttachment(OMElement requestElement)
            throws AxisFault {
        try {
            if(_logger.isDebugEnabled())
                _logger.debug(requestElement.toString());
            
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            OMElement attachmentElement = requireElement(rootQueue, "attachment");
            Attachment attachment = new AttachmentUnmarshaller().unmarshalAttachment(attachmentElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");

            _server.addAttachment(taskID, attachment, participantToken);

            return createOkResponse(requestElement.getOMFactory());
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableTaskException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public OMElement removeAttachment(OMElement requestElement)
            throws AxisFault {
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

            return createOkResponse(requestElement.getOMFactory());
        } catch (InvalidInputFormatException e) {
            throw AxisFault.makeFault(e);
        } catch (AuthException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableTaskException e) {
            throw AxisFault.makeFault(e);
        } catch (UnavailableAttachmentException e) {
            throw AxisFault.makeFault(e);
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

            return createOkResponse(requestElement.getOMFactory());
    	} catch (InvalidInputFormatException e) {
    		throw AxisFault.makeFault(e);
    	} catch (UnavailableTaskException e) {
    		throw AxisFault.makeFault(e);
		} catch (AuthException e) {
    		throw AxisFault.makeFault(e);
		}
    }
}
