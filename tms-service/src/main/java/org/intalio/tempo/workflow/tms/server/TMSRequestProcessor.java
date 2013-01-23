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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axis2.AxisFault;
import org.intalio.deploy.deployment.utils.DeploymentServiceRegister;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.PIPATaskState;
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
import org.intalio.tempo.workflow.task.xml.vacation.VacationMarshaller;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.jpa.TaskFetcher;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.OMElementQueue;
import org.intalio.tempo.workflow.util.xml.OMMarshaller;
import org.intalio.tempo.workflow.util.xml.OMUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnectionFactory;
import org.intalio.tempo.workflow.tms.server.dao.VacationDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.VacationDAOConnectionFactory;
import org.jasypt.util.text.BasicTextEncryptor;


import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;

public class TMSRequestProcessor extends OMUnmarshaller {
    final static Logger _logger = LoggerFactory.getLogger(TMSRequestProcessor.class);

    private ITMSServer _server;
    private PIPAComponentManager _pipa;
    private DeploymentServiceRegister _registerPipa;
    private static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();
    private ITaskDAOConnectionFactory _taskDAOFactory;
    private VacationDAOConnectionFactory _VacationDAOFactory;
    public TMSRequestProcessor(ITaskDAOConnectionFactory taskDAOFactory) {
        super(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
        assert taskDAOFactory != null : "ITaskDAOConnectionFactory implementation is absent!";
         _logger.debug("Created TMSRequestProcessor");
         setTaskDAOFactory(taskDAOFactory);
    }

      /*JIRA WF-1466 Changes have been made open the dao connection in TMSRequestProcessor
       instead of creating and opening connections in the TMSServer */
      public void setTaskDAOFactory(ITaskDAOConnectionFactory taskDAOFactory) {
         this._taskDAOFactory = taskDAOFactory;
           _logger.info("ITaskDAOConnectionFactory implementation : " + _taskDAOFactory.getClass());
       }
      
      /* For Vacation Management */
  	public void setVacationDAOFactory(VacationDAOConnectionFactory vacationDAOFactory) {
  		this._VacationDAOFactory = vacationDAOFactory;
  		_logger.info("VacationDAOConnectionFactory implementation : " + _VacationDAOFactory.getClass());
  	}
  	
    // unify desctroy pipa behaviour make it easy to be covered by test.
    protected void destroyRegisterPipa() {
        if (_registerPipa != null) {
            _registerPipa.destroy();
            _registerPipa = null;
        }
    }

    public void setServer(ITMSServer server) {
        // if (_registerPipa != null) {
        // _registerPipa.destroy();
        // }
        destroyRegisterPipa();
        _logger.debug("TMSRequestProcessor.setServer:" + server.getClass().getSimpleName());
        _server = server;
        _pipa = new PIPAComponentManager(_server,_taskDAOFactory);
        _registerPipa = new DeploymentServiceRegister(_pipa);
        _registerPipa.init();
    }
    
    public void clearCache(final OMElement requestElement){
    	if(_taskDAOFactory != null){
    		_taskDAOFactory.clearCache();
    	}
    }

    public OMElement getTaskList(final OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            final UserRoles user = _server.getUserRoles(participantToken);
            Task[] tasks = _server.getTaskList(dao,participantToken);
            return marshalTasksList(user, tasks, "getTaskListResponse");
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }
    
    public OMElement listTasksFromInstance( OMElement requestElement) throws AxisFault{
        ITaskDAOConnection dao=null;
       
        try {
            dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String participantToken = requireElementValue(rootQueue, "participantToken"); 
            final UserRoles user = _server.getUserRoles(participantToken);
            String instanceId = expectElementValue(rootQueue, "instanceId");
            if (instanceId == null || "".equals(instanceId))
            {     
                throw new InvalidInputFormatException("Instance id element must be present");
            }
            Task[] tasks = _server.listTasksFromInstance(dao, participantToken, instanceId);
            return marshalTasksList(user, tasks, "listTasksFromInstanceResponse");
        }
        catch (Exception e) {
            throw makeFault(e);
        }
        finally{
            if(dao!=null)
            dao.close();
        }
    }

    public OMElement getTask(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            final UserRoles user = _server.getUserRoles(participantToken);
            Task task = _server.getTask(dao,taskID, participantToken);
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
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }
    
    //Fix for WF-1493 and WF-1490. To return only userOwners and taskState
    public OMElement getTaskOwnerAndState(OMElement requestElement) throws AxisFault {
        ITaskDAOConnection dao=null;
        try {
            dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            Task task = _server.getTaskOwnerAndState(dao,taskID, participantToken);
            OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
                public OMElement marshalResponse(Task task) {
                    OMElement response = createElement("getTaskOwnerAndStateResponse");
                    response.addChild(new TaskMarshaller().marshalTaskPartially(task));
                    return response;
                }
            }.marshalResponse(task);
            return response;
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
            if(dao!=null)
            dao.close();
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
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            OMElement taskElement = requireElement(rootQueue, "task");
            Task task = new TaskUnmarshaller().unmarshalFullTask(taskElement);
            if (task instanceof PIPATask) {
                throw new InvalidInputFormatException("Not allowed to create() PIPA tasks");
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.create(dao,task, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }
    
    public OMElement update(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            OMElement taskElement = requireElement(rootQueue, "taskMetadata");
            TaskMetadata metadata = new TaskUnmarshaller().unmarshalPartialTask2(taskElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.update(dao,metadata, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement delete(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            List<String> taskIDs = new ArrayList<String>();
            while (true) {
                String taskID = expectElementValue(rootQueue, "taskId");
                if (taskID != null)
                    taskIDs.add(taskID);
                else
                    break;
            }
            if (taskIDs.isEmpty()) {
                throw new InvalidInputFormatException("At least one taskId element must be present");
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.delete(dao,taskIDs.toArray(new String[] {}), participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }
    
    public OMElement manageFromInstance(OMElement requestElement) throws AxisFault {
        ITaskDAOConnection dao=null;
        boolean throwFaultIfNoTask=true;
        try {
            dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String instanceid = expectElementValue(rootQueue, "instanceid");
             if (instanceid == null || "".equals(instanceid))
             {     
                    throw new InvalidInputFormatException("At least one instanceid element must be present");
             }
            boolean delete=Boolean.valueOf(requireElementValue(rootQueue, "delete"));
            String participantToken = requireElementValue(rootQueue, "participantToken");
            TaskState taskState=null;
            String taskStateStr = expectElementValue(rootQueue, "taskState");
            if(taskStateStr!=null && !"".equals(taskStateStr)){
               try {
                   taskState = TaskState.valueOf(taskStateStr.toUpperCase());
               } catch (IllegalArgumentException e) {
                   throw new InvalidInputFormatException("Unknown task state: '" + taskStateStr + "'");
               }
            }
           
            if(taskState!=null && delete)
            {
                throw new InvalidInputFormatException("Cannot delete the tasks and update the task state at same time");
            }
            if (!delete && taskState==null)
            {
                throw new InvalidInputFormatException("No delete or update taskState action specified");
            }
            //Throw Fault IF No Task is added because every instance may not have tasks associated with them.
            //The call is made from ODEEventListener so no exception should be logged for no tasks from instanceId
            //The call can be made using SOAP UI hence providing this option to user if he wants to get 
            //fault for no tasks associated with instance id
            throwFaultIfNoTask=Boolean.valueOf(requireElementValue(rootQueue, "throwFaultIfNoTask"));
            _server.manageFromInstance(dao,instanceid, participantToken,delete,taskState);
            return createOkResponse();
        }
       catch(UnavailableTaskException e){
             if(throwFaultIfNoTask){
                 throw makeFault(e);
             }
             else{
                 return createOkResponse();
             }
        }
         catch (Exception e) {
            throw makeFault(e);
        }
        finally{
            if(dao!=null)
            dao.close();
        }
    }
    
    

    public OMElement deleteAll(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskType = expectElementValue(rootQueue, "taskType");
            String subquery = expectElementValue(rootQueue, "subQuery");
            boolean fakeDelete = Boolean.valueOf(requireElementValue(rootQueue, "fakeDelete"));
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.deleteAll(dao,fakeDelete, subquery, taskType, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement setOutput(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            OMElement omOutputContainer = requireElement(rootQueue, "data");
            Document domOutput = null;
            if (omOutputContainer.getFirstElement() != null) {
                domOutput = new TaskUnmarshaller().unmarshalTaskOutput(omOutputContainer);
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.setOutput(dao,taskID, domOutput, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement setOutputAndComplete(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            OMElement omOutputContainer = requireElement(rootQueue, "data");
            Document domOutput = null;
            if (omOutputContainer.getFirstElement() != null) {
                domOutput = new TaskUnmarshaller().unmarshalTaskOutput(omOutputContainer);
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.setOutputAndComplete(dao,taskID, domOutput, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement complete(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.complete(dao,taskID, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement fail(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String failureCode = requireElementValue(rootQueue, "code");
            String failureReason = requireElementValue(rootQueue, "message");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.fail(dao,taskID, failureCode, failureReason, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement initProcess(final OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            OMElement omInputContainer = requireElement(rootQueue, "input");
            Document domInput = null;
            if (omInputContainer.getFirstElement() != null) {
                domInput = new TaskUnmarshaller().unmarshalTaskOutput(omInputContainer);
            }
            final String participantToken = requireElementValue(rootQueue, "participantToken");
            final UserRoles ur = _server.getUserRoles(participantToken);
            final String user = ur.getUserID();            
            final String formUrl = expectElementValue(rootQueue, "formUrl");

            Document userProcessResponse = _server.initProcess(dao,taskID, user, formUrl, domInput, participantToken);
            if (userProcessResponse == null)
                throw new RuntimeException("TMP did not return a correct message while calling init");
            OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
                public OMElement marshalResponse(Document userProcessResponse) {
                    OMElement response = createElement("initResponse");
                    OMElement userProcessResponseWrapper = createElement(response, "userProcessResponse");
                    userProcessResponseWrapper.addChild(new XmlTooling().convertDOMToOM(userProcessResponse, this.getOMFactory()));
                    return response;
                }
            }.marshalResponse(userProcessResponse);
            return response;
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement getAttachments(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            Attachment[] attachments = _server.getAttachments(dao,taskID, participantToken);

            return AttachmentMarshaller.marshalAttachments(attachments);
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement addAttachment(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	_logger.debug("addAttachment(OMElement requestElement) called ");
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            OMElement attachmentElement = requireElement(rootQueue, "attachment");
            Attachment attachment = new AttachmentUnmarshaller().unmarshalAttachment(attachmentElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.addAttachment(dao,taskID, attachment, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement removeAttachment(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String attachmentURL = requireElementValue(rootQueue, "attachmentUrl");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            try {
                _server.removeAttachment(dao,taskID, new URL(attachmentURL), participantToken);
            } catch (MalformedURLException e) {
                throw new InvalidInputFormatException(e);
            }
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement reassign(OMElement requestElement) throws AxisFault {
    		ITaskDAOConnection dao=null;
    		try {
    		dao=_taskDAOFactory.openConnection();
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
            //String userAction = requireElementValue(rootQueue, "userAction");
            String userAction = expectElementValue(rootQueue, "userAction");
            _server.reassign(dao,taskID, users, roles, taskState, participantToken, userAction);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
        
    }

    public OMElement getPipa(OMElement requestElement) throws AxisFault {
    	 ITaskDAOConnection dao=null;
     	try {
     		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "pipaurl");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            final UserRoles user = _server.getUserRoles(participantToken);
            Task task = _server.getPipa(dao,taskID, participantToken);
            OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
                public OMElement marshalResponse(Task task) {
                    OMElement response = createElement("getPipaResponse");
                    response.addChild(new TaskMarshaller().marshalFullTask(task, user));
                    return response;
                }
            }.marshalResponse(task);
            return response;
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement storePipa(OMElement requestElement) throws AxisFault {
	   	 ITaskDAOConnection dao=null;
	   	 try {
	  		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            OMElement taskElement = requireElement(rootQueue, "task");
            PIPATask task = (PIPATask) new TaskUnmarshaller().unmarshalFullTask(taskElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            // final UserRoles user = _server.getUserRoles(participantToken);
            _server.storePipa(dao,task, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement deletePipa(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		BasicTextEncryptor encryptor = new BasicTextEncryptor();
              // setPassword uses hash to decrypt password which should be same as hash of encryptor
      		encryptor.setPassword("IntalioEncryptedpasswordfortempo#123");
              // only undeploy if this is the last version of this assembly
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "pipaurl");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            
            // final UserRoles user = _server.getUserRoles(participantToken);
            _server.deletePipa(dao,taskID, encryptor.encrypt(participantToken));
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    public OMElement updatePipa(OMElement requestElement) throws AxisFault {
        ITaskDAOConnection dao=null;
        try {
            dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String pipaEndpoint = requireElementValue(rootQueue, "pipaEndpoint");
            PIPATaskState pipaTaskState = null;
            String state = requireElementValue(rootQueue, "state");
            if(state !=null && !"".equals(state)) {
                try {
                    pipaTaskState = PIPATaskState.valueOf(state.toUpperCase());
                } catch(IllegalArgumentException e) {
                    throw new InvalidInputFormatException("Unknown task state: '" + state + "'");
                }
            }
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.updatePipa(dao, pipaEndpoint, participantToken, pipaTaskState);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
            if(dao!=null)
            dao.close();
        }
    }

    public OMElement getAvailableTasks(final OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
        try {
        	dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            String taskType = requireElementValue(rootQueue, "taskType");
            String subQuery = requireElementValue(rootQueue, "subQuery");
            String first = expectElementValue(rootQueue, "first");
            String max = expectElementValue(rootQueue, "max");
            HashMap map = new HashMap();
            map.put(TaskFetcher.FETCH_CLASS_NAME, taskType);
            map.put(TaskFetcher.FETCH_SUB_QUERY, subQuery);
            map.put(TaskFetcher.FETCH_FIRST, first);
            map.put(TaskFetcher.FETCH_MAX, max);
            final UserRoles user = _server.getUserRoles(participantToken);
            Task[] tasks = _server.getAvailableTasks(dao,participantToken, map);
            return marshalTasksList(user, tasks, "getAvailableTasksResponse");
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
        
    }
    
    public OMElement countAvailableTasks(final OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
        try {
        	dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            String taskType = requireElementValue(rootQueue, "taskType");
            String subQuery = requireElementValue(rootQueue, "subQuery");
            String first = expectElementValue(rootQueue, "first");
            String max = expectElementValue(rootQueue, "max");
            HashMap map = new HashMap();
            map.put(TaskFetcher.FETCH_CLASS_NAME, taskType);
            map.put(TaskFetcher.FETCH_SUB_QUERY, subQuery);
            final UserRoles user = _server.getUserRoles(participantToken);
            final Long taskCount = _server.countAvailableTasks(dao,participantToken, map);
            return new TMSResponseMarshaller(OM_FACTORY) {
                public OMElement createOkResponse() {
                    OMElement response = createElement("countAvailableTasksResponse");
                    response.setText(Long.toString(taskCount));
                    return response;
                }
            }.createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }

    /**
     * This is used in both <code>getAvailableTasks</code> and
     * <code>getTaskList</code>
     */
    private OMElement marshalTasksList(final UserRoles user, final Task[] tasks, final String responseTag) {
        OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
            public OMElement marshalResponse(Task[] tasks) {
                OMElement response = createElement(responseTag);
                for (Task task : tasks) {
                    try {
                        response.addChild(new TaskMarshaller().marshalTaskMetadata(task, user));
                    } catch (Exception e) {
                        // marshalling of that task failed.
                        // let's not fail fast, but provide info in the logs.
                        _logger.error(task.getID() + "could not be serialized to xml", e);
                    }
                }
                return response;
            }
        }.marshalResponse(tasks);
        if (_logger.isDebugEnabled())
            _logger.debug(response.toString());
        return response;
    }

    private AxisFault makeFault(Exception e) {
        if (e instanceof TMSException) {
            if (_logger.isDebugEnabled())
                _logger.debug(e.getMessage(), e);
            OMElement response = null;
            if (e instanceof InvalidInputFormatException)
                response = OM_FACTORY.createOMElement(TMSConstants.INVALID_INPUT_FORMAT);
            else if (e instanceof AccessDeniedException)
                response = OM_FACTORY.createOMElement(TMSConstants.ACCESS_DENIED);
            else if (e instanceof UnavailableTaskException)
                response = OM_FACTORY.createOMElement(TMSConstants.UNAVAILABLE_TASK);
            else if (e instanceof UnavailableAttachmentException)
                response = OM_FACTORY.createOMElement(TMSConstants.UNAVAILABLE_ATTACHMENT);
            else if (e instanceof AuthException)
                response = OM_FACTORY.createOMElement(TMSConstants.INVALID_TOKEN);

            else
                return AxisFault.makeFault(e);

            response.setText(e.getMessage());
            AxisFault axisFault = new AxisFault(e.getMessage(), e);
            axisFault.setDetail(response);
            return axisFault;
        } else if (e instanceof AxisFault) {
            _logger.error(e.getMessage(), e);
            return (AxisFault) e;
        } else {
            _logger.error(e.getMessage(), e);
            return AxisFault.makeFault(e);
        }
    }

    public OMElement skip(OMElement requestElement) throws AxisFault {
    	ITaskDAOConnection dao=null;
    	try {
    		dao=_taskDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String taskID = requireElementValue(rootQueue, "taskId");
            String participantToken = requireElementValue(rootQueue, "participantToken");
            _server.skip(dao,taskID, participantToken);
            return createOkResponse();
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }
    
    public OMElement getCustomColumns(OMElement requestElement) throws AxisFault{
        ITaskDAOConnection dao=null;
        try {
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            dao=_taskDAOFactory.openConnection();
            List<String> customColumns = _server.getCustomColumns(dao, participantToken);
            return marshalColumnsList(customColumns, "getCustomColumnsResponse");
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
            if(dao!=null)
            dao.close();
        }
    }
    
    private OMElement marshalColumnsList( List<String> customColumns, final String responseTag){
        OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
            public OMElement marshalResponse(List<String> customColumns) {
                OMElement response = createElement(responseTag);
                for(int i=0; i < customColumns.size(); i++){
                    OMElement columnElement = createElement(response, TaskXMLConstants.CUSTOM_COLUMN_LOCAL_NAME);
                    columnElement.setText( customColumns.get(i));
                }
                return response;
            }
        }.marshalResponse(customColumns);
        if (_logger.isDebugEnabled())
            _logger.debug(response.toString());
        return response;
    }

    protected void finalize() throws Throwable {
        // if (_registerPipa != null) {
        // _registerPipa.destroy();
        // _registerPipa = null;
        // }
        destroyRegisterPipa();
        super.finalize();
    }
    
    public OMElement insertVacation(final OMElement requestElement) throws AxisFault {
		VacationDAOConnection dao = null;
		try {
			dao = _VacationDAOFactory.openConnection();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			OMElementQueue rootQueue = new OMElementQueue(requestElement);
			Vacation vac = new Vacation();
			vac.setFromDate(df.parse(requireElementValue(rootQueue, "fromDate")));
			vac.setToDate(df.parse(requireElementValue(rootQueue, "toDate")));
			vac.setDescription(requireElementValue(rootQueue, "description"));
			vac.setUser(requireElementValue(rootQueue, "userName"));
			String participantToken = requireElementValue(rootQueue, "participantToken");
			_server.insertVacation(dao, vac, participantToken);
			return createOkResponse();
		} catch (Exception e) {
			throw makeFault(e);
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	public OMElement getUserVacation(final OMElement requestElement) throws AxisFault {
		VacationDAOConnection dao = null;
		try {
			dao = _VacationDAOFactory.openConnection();
			OMElementQueue rootQueue = new OMElementQueue(requestElement);
			String user = requireElementValue(rootQueue, "user");
			String participantToken = requireElementValue(rootQueue, "participantToken");
			List<Vacation> vac = _server.getUserVacation(dao, user, participantToken);
			return marshalVacationList(vac, "getUserVacationResponse");
		} catch (Exception e) {
			throw makeFault(e);
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	public OMElement getVacationList(final OMElement requestElement) throws AxisFault {
		VacationDAOConnection dao = null;
		try {
			dao = _VacationDAOFactory.openConnection();
			OMElementQueue rootQueue = new OMElementQueue(requestElement);
			String participantToken = requireElementValue(rootQueue, "participantToken");
			List<Vacation> vac = _server.getVacationList(dao, participantToken);
			return marshalVacationList(vac, "getVacationListResponse");
		} catch (Exception e) {
			throw makeFault(e);
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	public OMElement deleteVacation(final OMElement requestElement) throws AxisFault {
		VacationDAOConnection dao = null;
		try {
			dao = _VacationDAOFactory.openConnection();
			OMElementQueue rootQueue = new OMElementQueue(requestElement);
			int vacId = Integer.parseInt(requireElementValue(rootQueue, "vacId"));
			String participantToken = requireElementValue(rootQueue, "participantToken");
			_logger.debug("vacation=" + vacId);
			_server.deleteVacation(dao, vacId, participantToken);
			return createOkResponse();
		} catch (Exception e) {
			throw makeFault(e);
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	private OMElement marshalVacationList(final List<Vacation> vac, final String responseTag) {
		OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
			public OMElement marshalResponse(List<Vacation> vac) {
				OMElement response = createElement(responseTag);
				for (Vacation vacation : vac) {
					try {
						response.addChild(new VacationMarshaller().marshalVacationData(vacation));
					} catch (Exception e) {
						_logger.error(vacation.getId() + "could not be serialized to xml", e);
					}
				}
				return response;
			}
		}.marshalResponse(vac);
		if (_logger.isDebugEnabled())
			_logger.debug(response.toString());
		return response;
	}
	
    private abstract class TMSResponseMarshaller extends OMMarshaller {
        public TMSResponseMarshaller(OMFactory omFactory) {
            super(omFactory, omFactory.createOMNamespace(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX));
        }
    }
}
