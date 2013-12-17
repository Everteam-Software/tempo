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

package org.intalio.tempo.workflow.tms.client;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.databinding.utils.ConverterUtil;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.InvalidTaskException;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.xml.TaskMarshaller;
import org.intalio.tempo.workflow.task.xml.TaskUnmarshaller;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.task.xml.attachments.AttachmentMarshaller;
import org.intalio.tempo.workflow.task.xml.attachments.AttachmentUnmarshaller;
import org.intalio.tempo.workflow.task.xml.vacation.VacationUnmarshaller;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.InvalidTaskStateException;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.OMElementQueue;
import org.intalio.tempo.workflow.util.xml.OMMarshaller;
import org.intalio.tempo.workflow.util.xml.OMUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.intalio.bpms.common.AxisUtil;


public class RemoteTMSClient implements ITaskManagementService {

    private static final Logger _log = LoggerFactory.getLogger(OMUnmarshaller.class);

    private EndpointReference _endpoint;
    private String _participantToken;
    private OMFactory _omFactory; 
    private long _httpTimeOut = 30000;
    
    private String httpChunking = "true";

    public RemoteTMSClient () {
    }

    public boolean isChunking() {
    	return Boolean.parseBoolean(httpChunking);
    }
    
	public String getHttpChunking() {
		return httpChunking;
	}

	public void setHttpChunking(String httpChunking) {
		this.httpChunking = httpChunking;
	}

	private class TMSMarshaller extends OMMarshaller {
        protected TMSMarshaller() {
            super(_omFactory, TaskXMLConstants.TASK_OM_NAMESPACE);
        }
    }

    public RemoteTMSClient(String endpoint, String participantToken) {
        _endpoint = new EndpointReference(endpoint);
        _participantToken = participantToken;
        _omFactory = OMAbstractFactory.getOMFactory();
    }
    public RemoteTMSClient(String endpoint, String participantToken, long httpTimeOut) {
        _endpoint = new EndpointReference(endpoint);
        _participantToken = participantToken;
        _omFactory = OMAbstractFactory.getOMFactory();
        setHttpTimeOut(httpTimeOut);
    }

    public void close() {

    }

    private OMElement sendRequest(OMElement request, String soapAction) {
        if (_log.isDebugEnabled())
            _log.debug(request.toString());

        Thread currentThread = Thread.currentThread();
        ClassLoader cl = currentThread.getContextClassLoader();
        AxisUtil util = null;
        try {
        	util = new AxisUtil();
            ServiceClient serviceClient = util.getServiceClient();
            try {
            	serviceClient.getOptions().setTo(_endpoint);
            	serviceClient.getOptions().setAction(soapAction);
	
	            _log.debug("serviceClient.getOptions().getTimeOutInMilliSeconds() = "+serviceClient.getOptions().getTimeOutInMilliSeconds() + " ms");
	        	// Disabling chunking as lighthttpd doesnt support it
				if (this.isChunking()) {
					serviceClient
							.getOptions()
							.setProperty(
									org.apache.axis2.transport.http.HTTPConstants.CHUNKED,
									Boolean.TRUE);
				} else {

					serviceClient
							.getOptions()
							.setProperty(
									org.apache.axis2.transport.http.HTTPConstants.CHUNKED,
									Boolean.FALSE);
				}
	            OMElement response = serviceClient.sendReceive(request);
	            response.build();
	            return response;
            } finally {
            	util.closeClient(serviceClient);
            }
        } catch (AxisFault f) {
            throw new RemoteTMSException(f);
        } finally {
            currentThread.setContextClassLoader(cl);
        }
    }

    public Task[] getTaskList() throws AuthException {
        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("getTaskListRequest");
                createElement(request, "participantToken", _participantToken);
                return request;
            }
        }.marshalRequest();
        OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "getTaskList");

        List<Task> tasks = new ArrayList<Task>();
        OMElementQueue rootQueue = new OMElementQueue(response);
        while (true) {
            OMElement taskElement = expectElement(rootQueue, "task");
            if (taskElement == null)
                break;

            try {
                Task task = new TaskUnmarshaller().unmarshalTaskFromMetadata(taskElement);
                tasks.add(task);
            } catch (Exception e) {
                _log.error("Error reading task: " + taskElement, e);
            }
        }
        return tasks.toArray(new Task[tasks.size()]);
    }

    private OMElement expectElement(OMElementQueue queue, String name) {
        OMElement element = queue.getNextElement();
        if (element != null) {
            if (element.getQName().equals(TaskXMLConstants.TASK_QNAME)) {
                return element;
            } else {
                queue.pushElementBack(element);
            }
        }
        return null;
    }

    public Task getTask(final String taskID) throws AuthException, UnavailableTaskException {
        if (taskID == null) {
            throw new RequiredArgumentException("taskID");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("getTaskRequest");
                createElement(request, "taskId", taskID);
                createElement(request, "participantToken", _participantToken);

                return request;
            }
        }.marshalRequest();

        try {
            OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "getTask");
            OMElement taskElement = response.getFirstElement();
            Task task = new TaskUnmarshaller().unmarshalFullTask(taskElement);

            return task;
        } catch (InvalidInputFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private void setOutputOperation(final String taskID, final Document output, final boolean complete) {
        if (taskID == null) {
            throw new RequiredArgumentException("taskID");
        }
        if (output == null) {
            throw new RequiredArgumentException("output");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement(complete ? "setOutputAndCompleteRequest" : "setOutputRequest");
                createElement(request, "taskId", taskID);
                OMElement data = createElement(request, "data");
                OMElement omOutput = new XmlTooling().convertDOMToOM(output, getOMFactory());
                data.addChild(omOutput);
                createElement(request, "participantToken", _participantToken);

                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + (complete ? "setOutputAndComplete" : "setOutput"));
    }

    public void setOutput(final String taskID, final Document output) throws AuthException, UnavailableTaskException, InvalidTaskStateException {
        setOutputOperation(taskID, output, false);
    }

    public void complete(final String taskID) throws AuthException, UnavailableTaskException, InvalidTaskStateException {
        if (taskID == null) {
            throw new RequiredArgumentException("taskID");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("completeRequest");
                createElement(request, "taskId", taskID);
                createElement(request, "participantToken", _participantToken);

                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "complete");
    }

    public void setOutputAndComplete(String taskID, Document output) throws AuthException, UnavailableTaskException, InvalidTaskStateException {
        setOutputOperation(taskID, output, true);
    }

    public void fail(final String taskID, final String failureCode, final String failureReason) throws AuthException, UnavailableTaskException,
                    InvalidTaskStateException {
        if (taskID == null) {
            throw new RequiredArgumentException("taskID");
        }
        if (failureCode == null) {
            throw new RequiredArgumentException("failureCode");
        }
        if (failureReason == null) {
            throw new RequiredArgumentException("failureReason");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("failRequest");
                createElement(request, "taskId", taskID);
                createElement(request, "code", failureCode);
                createElement(request, "message", failureReason);
                createElement(request, "participantToken", _participantToken);

                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "fail");
    }

    public void delete(final String[] taskIDs) throws AuthException, UnavailableTaskException {
        if (taskIDs == null) {
            throw new RequiredArgumentException("taskIDs");
        }
        if (taskIDs.length == 0) {
            throw new IllegalArgumentException("Task ID array is empty");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("deleteRequest");
                for (String taskID : taskIDs) {
                    if (taskID == null) {
                        throw new RequiredArgumentException("One of the given taskID's is null");
                    }
                    createElement(request, "taskId", taskID);
                }
                createElement(request, "participantToken", _participantToken);

                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "delete");
    }

    public void deleteAll(final String fakeDelete, final String subQuery, final String taskType) throws AuthException, UnavailableTaskException {
        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("deleteAllRequest");
                createElement(request, "taskType", taskType);
                createElement(request, "subQuery", subQuery);
                createElement(request, "fakeDelete", fakeDelete);
                createElement(request, "participantToken", _participantToken);
                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "deleteAll");
    }

    public void create(final Task task) throws AuthException, TaskIDConflictException {
        if (task == null) {
            throw new RequiredArgumentException("task");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("createTaskRequest");
                new TaskMarshaller().marshalFullTask(task, request, null);
                createElement(request, "participantToken", _participantToken);
                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "create");
    }
    
    public void update(final Task task) throws AuthException, UnavailableTaskException {
        if (task == null) {
            throw new RequiredArgumentException("task");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("updateTaskRequest");
                OMElement meta = new TaskMarshaller().marshalFullTask(task, null);
                request.addChild(meta);
                createElement(request, "participantToken", _participantToken);
                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "update");
    }

    public Document init(final String taskID, final Document input) throws AuthException, UnavailableTaskException {
        if (taskID == null) {
            throw new RequiredArgumentException("taskID");
        }
        if (input == null) {
            throw new RequiredArgumentException("input");
        }

        final XmlTooling xmlTooling = new XmlTooling();
        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("initRequest");
                createElement(request, "taskId", taskID);
                OMElement data = createElement(request, "input");
                OMElement omOutput = xmlTooling.convertDOMToOM(input, getOMFactory());
                data.addChild(omOutput);
                createElement(request, "participantToken", _participantToken);

                return request;
            }
        }.marshalRequest();

        OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "initProcess");
        OMElement userProcessResponseWrapper = response.getFirstElement();
        OMElement userProcessResponse = userProcessResponseWrapper.getFirstElement();

        return xmlTooling.convertOMToDOM(userProcessResponse);
    }

    public Attachment[] getAttachments(final String taskID) throws AuthException, UnavailableTaskException {
        if (taskID == null) {
            throw new RequiredArgumentException("taskID");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("getAttachmentsRequest");
                createElement(request, "taskId", taskID);
                createElement(request, "participantToken", _participantToken);

                return request;
            }
        }.marshalRequest();

        try {
            OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "getAttachments");
            Iterator<?> i = response.getChildElements();
            ArrayList<Attachment> attachments = new ArrayList<Attachment>();
            while (i.hasNext()) {
                OMElement attachmentElement = (OMElement) i.next();
                Attachment attachment = new AttachmentUnmarshaller().unmarshalAttachment(attachmentElement);
                attachments.add(attachment);
            }

            return attachments.toArray(new Attachment[] {});
        } catch (InvalidInputFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAttachment(final String taskID, final Attachment attachment) throws AuthException, UnavailableTaskException {
        if (taskID == null) {
            throw new RequiredArgumentException("taskID");
        }
        if (attachment == null) {
            throw new RequiredArgumentException("attachment");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("addAttachmentRequest");
                createElement(request, "taskId", taskID);
                request.addChild(AttachmentMarshaller.marshalAttachment(attachment));
                createElement(request, "participantToken", _participantToken);

                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "addAttachment");
    }

    public void removeAttachment(final String taskID, final URL attachmentPayloadURL) throws AuthException, UnavailableTaskException,
                    UnavailableAttachmentException {
        if (taskID == null) {
            throw new RequiredArgumentException("taskID");
        }
        if (attachmentPayloadURL == null) {
            throw new RequiredArgumentException("attachmentPayloadURL");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("addAttachmentRequest");
                createElement(request, "taskId", taskID);
                createElement(request, "attachmentUrl", attachmentPayloadURL.toString());
                createElement(request, "participantToken", _participantToken);

                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "removeAttachment");
    }

    public void reassign(final Task task) throws AuthException, UnavailableTaskException {
        if (task == null) {
            throw new RequiredArgumentException("task");
        }
        if (!(task instanceof PATask)) {
            throw new UnavailableTaskException("Task is not PATask");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("reassignRequest");
                createElement(request, "taskId", task.getID());
                for (String userOwner : task.getUserOwners()) {
                    createElement(request, "userOwner", userOwner);
                }
                for (String roleOwner : task.getRoleOwners()) {
                    createElement(request, "roleOwner", roleOwner);
                }
                createElement(request, "taskState", ((PATask) task).getState().toString());
                createElement(request, "participantToken", _participantToken);
                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "reassign");
    }

    public void reassign(final String taskID, final AuthIdentifierSet users, final AuthIdentifierSet roles, final TaskState state) throws AuthException,
                    UnavailableTaskException {
        if (taskID == null) {
            throw new RequiredArgumentException("taskID");
        }

        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("reassignRequest");
                createElement(request, "taskId", taskID);
                for (String userOwner : users) {
                    createElement(request, "userOwner", userOwner);
                }
                for (String roleOwner : roles) {
                    createElement(request, "roleOwner", roleOwner);
                }
                createElement(request, "taskState", state.toString());
                createElement(request, "participantToken", _participantToken);
                return request;
            }
        }.marshalRequest();

        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "reassign");
    }

    public void storePipa(final PIPATask task) throws AuthException, InvalidTaskException {
        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("storePipaRequest");
                OMElement pipa = createElement(request, TaskXMLConstants.TASK_LOCAL_NAME);
                new TaskMarshaller().marshalFullTask(task, pipa, null);
                createElement(request, "participantToken", _participantToken);
                return request;
            }
        }.marshalRequest();
        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "storePipa");
    }

    public PIPATask getPipa(final String formUrl) throws AuthException, UnavailableTaskException {
        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("getPipaRequest");
                createElement(request, "pipaurl", formUrl);
                createElement(request, "participantToken", _participantToken);

                return request;
            }
        }.marshalRequest();

        _log.info(request.toString());

        try {
            OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "getPipaTask");
            OMElement taskElement = response.getFirstElement();
            PIPATask task = (PIPATask) new TaskUnmarshaller().unmarshalFullTask(taskElement);
            return task;
        } catch (InvalidInputFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePipa(final String formUrl) throws AuthException, UnavailableTaskException {
        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("deletePipaRequest");
                createElement(request, "pipaurl", formUrl);
                createElement(request, "participantToken", _participantToken);
                return request;
            }
        }.marshalRequest();
        sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "deletePipa");
    }

    public Task[] getAvailableTasks(final String taskType, final String subQuery) throws AuthException {
        return getAvailableTasks(taskType, subQuery, null, null);
    }
    
    public Long countAvailableTasks(final String taskType, final String subQuery) throws AuthException {
        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("getTaskListRequest");
                createElement(request, "participantToken", _participantToken);
                createElement(request, "taskType", taskType);
                createElement(request, "subQuery", subQuery);
                return request;
            }
        }.marshalRequest();
        OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "countAvailableTasks");
        return Long.parseLong(response.getText());
    }

    public Task[] getAvailableTasks(final String taskType, final String subQuery, final String first, final String max) throws AuthException {
        return getAvailableTasks(taskType, subQuery, first, max, null);
    }

    public Task[] getAvailableTasks(final String taskType, final String subQuery, final String first, final String max, final String fetchMetaData) throws AuthException {
        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("getTaskListRequest");
                createElement(request, "participantToken", _participantToken);
                createElement(request, "taskType", taskType);
                createElement(request, "subQuery", subQuery);
                if(first!=null) createElement(request, "first", first);
                if(max!=null) createElement(request, "max", max);
                if(fetchMetaData!=null) createElement(request, "fetchMetaData", fetchMetaData);
                return request;
            }
        }.marshalRequest();
        OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "getAvailableTasks");

        List<Task> tasks = new ArrayList<Task>();
        OMElementQueue rootQueue = new OMElementQueue(response);
        while (true) {
            OMElement taskElement = expectElement(rootQueue, "task");
            if (taskElement == null)
                break;

            try {
                Task task = new TaskUnmarshaller().unmarshalTaskFromMetadata(taskElement, fetchMetaData);
                tasks.add(task);
            } catch (Exception e) {
                _log.error("Error reading task: " + taskElement, e);
            }
        }
        return tasks.toArray(new Task[tasks.size()]);
    }

    public void setHttpTimeOut(long _httpTimeOut) {
        this._httpTimeOut = _httpTimeOut;
    }

    public long getHttpTimeOut() {
        return _httpTimeOut;
    }

    @Override
    public List<String> getCustomColumns() throws AuthException{
        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                OMElement request = createElement("getCustomColumns");
                createElement(request, "participantToken", _participantToken);
                return request;
            }
        }.marshalRequest();
        OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "getCustomColumns");
        List<String> customColumns = new ArrayList<String>();
        OMElementQueue rootQueue = new OMElementQueue(response);
        while (true) {
            OMElement customColumnElement = expectElementCustomColumn(rootQueue, "customColumn");
            if (customColumnElement == null)
                break;

            try {
                String column = customColumnElement.getText();
                customColumns.add(column);
            } catch (Exception e) {
                _log.error("Error reading customColumns: "+e);
            }
        }
        return customColumns;
    }
    
    private OMElement expectElementCustomColumn(OMElementQueue queue, String name) {
        OMElement element = queue.getNextElement();
        if (element != null) {
            if (element.getQName().equals(TaskXMLConstants.CUSTOM_COLUMN_QNAME)) {
                return element;
            } else {
                queue.pushElementBack(element);
            }
        }
        return null;
    }
    
    public void insertVacation(final String fromDate, final String toDate, final String Desc, final String user, final String substitute) {
		OMElement request = new TMSMarshaller() {
			public OMElement marshalRequest() {
				OMElement request = createElement("insertVacationRequest");
				Calendar myCal = Calendar.getInstance();
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				try {
				    myCal.setTime(df.parse(fromDate));
				    createElement(request, "fromDate", ConverterUtil.convertToString(myCal));
				    myCal.setTime(df.parse(toDate));
				    createElement(request, "toDate", ConverterUtil.convertToString(myCal));
				} catch (ParseException e) {
				    _log.error("Error reading task: " + e);
				}
				createElement(request, "description", Desc);
				createElement(request, "userName", user);
				createElement(request, "participantToken", _participantToken);
				createElement(request, "substitute", substitute);
				return request;
			}
		}.marshalRequest();
		sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "insertVacation");
	}
    
    public void updateVacation(final Vacation vacation) {
		OMElement request = new TMSMarshaller() {
			public OMElement marshalRequest() {
				Calendar myCal = Calendar.getInstance();
				OMElement request = createElement("updateVacationRequest");
				createElement(request, "vacId", String.valueOf(vacation.getId()));
				myCal.setTime(vacation.getFromDate());
				createElement(request, "fromDate", ConverterUtil.convertToString(myCal));
				myCal.setTime(vacation.getToDate());
				createElement(request, "toDate", ConverterUtil.convertToString(myCal));
				createElement(request, "description", vacation.getDescription());
				createElement(request, "userName", vacation.getUser());
				createElement(request, "participantToken", _participantToken);
				createElement(request, "substitute", vacation.getSubstitute());
				return request;
			}
		}.marshalRequest();
		sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "updateVacation");
	}

	public List<Vacation> getUserVacation(final String user) {
		OMElement request = new TMSMarshaller() {
			public OMElement marshalRequest() {
				OMElement request = createElement("getUserVacationRequest");
				createElement(request, "user", user);
				createElement(request, "participantToken", _participantToken);
				return request;
			}
		}.marshalRequest();
		List<Vacation> listVac = new ArrayList<Vacation>();
		OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "getUserVacation");
		OMElementQueue rootQueue = new OMElementQueue(response);
		while (true) {
			OMElement taskElement = expectVacationElement(rootQueue);
			if (taskElement == null)
				break;
			else {
				try {
					Vacation vac = new VacationUnmarshaller().unmarshalVacation(taskElement);
					listVac.add(vac);
				} catch (Exception e) {
					_log.error("Error reading task: " + taskElement, e);
				}
			}
		}
		return listVac;
	}

	public List<Vacation> getVacationList() {
		OMElement request = new TMSMarshaller() {
			public OMElement marshalRequest() {
				OMElement request = createElement("getVacationListRequest");
				createElement(request, "participantToken", _participantToken);
				return request;
			}
		}.marshalRequest();
		List<Vacation> listVac = new ArrayList<Vacation>();
		OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "getVacationList");
		OMElementQueue rootQueue = new OMElementQueue(response);
		while (true) {
			OMElement taskElement = expectVacationElement(rootQueue);
			if (taskElement == null)
				break;
			else {
				try {
					Vacation vac = new VacationUnmarshaller().unmarshalVacation(taskElement);
					listVac.add(vac);
				} catch (Exception e) {
					_log.error("Error reading task: " + taskElement, e);
				}
			}
		}
		return listVac;
	}
	
	private OMElement expectVacationElement(OMElementQueue queue) {
		OMElement element = queue.getNextElement();
		if (element != null) {
			if (element.getQName().equals(TaskXMLConstants.Vacation_QName)) {
				return element;
				} else {
					queue.pushElementBack(element);
					}
			}
			return null;
	}
	
	public void deleteVacation(final String[] vacIDs) {
        if (vacIDs == null) {
            throw new RequiredArgumentException("vacIDs");
        }
        if (vacIDs.length == 0) {
            throw new IllegalArgumentException("Vacation ID array is empty");
        }
		OMElement request = new TMSMarshaller() {
			public OMElement marshalRequest() {
				OMElement request = createElement("deleteVacationRequest");
				for (String vacID : vacIDs) {
                    if (vacID == null) {
                        throw new RequiredArgumentException("One of the given vacation ID's is null");
                    }
                    createElement(request, "vacId", vacID);
                }
				createElement(request, "participantToken", _participantToken);
				return request;
			}
		}.marshalRequest();
		sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "deleteVacation");
	}
	
	
	public List<Vacation> getMatchedVacations(final String fromDate, final String toDate) {
		OMElement request = new TMSMarshaller() {
			public OMElement marshalRequest() {
				OMElement request = createElement("getMatchedVacationsRequest");
				Calendar myCal = Calendar.getInstance();
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				try {
				    myCal.setTime(df.parse(fromDate));
				    createElement(request, "fromDate", ConverterUtil.convertToString(myCal));
				    myCal.setTime(df.parse(toDate));
				    createElement(request, "toDate", ConverterUtil.convertToString(myCal));
				} catch (ParseException e) {
				    _log.error("Error reading task: " + e);
				}
				createElement(request, "participantToken", _participantToken);
				return request;
			}
		}.marshalRequest();
		List<Vacation> listVac = new ArrayList<Vacation>();
		OMElement response = sendRequest(request, TaskXMLConstants.TASK_NAMESPACE + "getMatchedVacations");
		OMElementQueue rootQueue = new OMElementQueue(response);
		while (true) {
			OMElement taskElement = expectVacationElement(rootQueue);
			if (taskElement == null)
				break;
			else {
				try {
					Vacation vac = new VacationUnmarshaller().unmarshalVacation(taskElement);
					listVac.add(vac);
				} catch (Exception e) {
					_log.error("Error reading task: " + taskElement, e);
				}
			}
		}
		return listVac;
	}

    @Override
    public final List<Vacation> getUserMatchedVacations(
            final String user, final String fromDate,
            final String toDate) {
        OMElement request = new TMSMarshaller() {
            public OMElement marshalRequest() {
                Calendar myCal = Calendar.getInstance();
                OMElement request
                = createElement("getUserMatchedVacationsRequest");
                createElement(request, "user", user);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    myCal.setTime(df.parse(fromDate));
                    createElement(request, "fromDate", ConverterUtil.convertToString(myCal));
                    myCal.setTime(df.parse(toDate));
                    createElement(request, "toDate", ConverterUtil.convertToString(myCal));
                } catch (ParseException e) {
                    _log.error("Error reading task: " + e);
                }
                return request;
            }
        } .marshalRequest();
        List<Vacation> listVac = new ArrayList<Vacation>();
        OMElement response = sendRequest(request,
                TaskXMLConstants.TASK_NAMESPACE
                        + "getUserMatchedVacations");
        OMElementQueue rootQueue = new OMElementQueue(response);
        while (true) {
            OMElement taskElement = expectVacationElement(rootQueue);
            if (taskElement == null) {
                break;
            } else {
                try {
                    Vacation vac = new VacationUnmarshaller()
                            .unmarshalVacation(taskElement);
                    listVac.add(vac);
                } catch (Exception e) {
                    _log.error("Error reading task: " + taskElement, e);
                }
            }
        }
        return listVac;
    }
    
}