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

package org.intalio.tempo.workflow.tms.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.xml.transform.TransformerException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.RollbackException;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.CustomColumn;
import org.intalio.tempo.workflow.task.InvalidTaskException;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.PIPATaskOutput;
import org.intalio.tempo.workflow.task.PIPATaskState;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.traits.ITaskWithAttachments;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.xml.TaskTypeMapper;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.InvalidTaskStateException;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.VacationDAOConnection;
import org.intalio.tempo.workflow.tms.server.permissions.TaskPermissions;
import org.intalio.tempo.workflow.util.jpa.TaskFetcher;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.intalio.bpms.common.AxisUtil;

import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;

public class TMSServer implements ITMSServer {

    private static final Logger _logger = LoggerFactory.getLogger(TMSServer.class);
    private IAuthProvider _authProvider;
    private TaskPermissions _permissions;
    private int _httpTimeout = 30000;
    private String _tasEndPoint;
    private String httpChunking = "true";
    private String internalPassword = "verylongpassword";
    
    public String getInternalPassword() {
		return internalPassword;
	}

	public void setInternalPassword(String internalPassword) {
		this.internalPassword = internalPassword;
	}

	public String getHttpChunking() {
		return httpChunking;
	}

	public void setHttpChunking(String httpChunking) {
		this.httpChunking = httpChunking;
	}

	public boolean isHTTPChunking(){
		return Boolean.parseBoolean(httpChunking);
	}
	
	private static final String TAS_NS= "http://www.intalio.com/BPMS/Workflow/TaskAttachmentService/";
      
    //Added the property for deleting file upload widget attachments
    private String _tasStorageStrategyEndPoint;
    public String tasStorageStrategyEndPoint() {
        return _tasStorageStrategyEndPoint;
    }

    public void settasStorageStrategyEndPoint(String _tasStorageStrategyEndPoint) {
        this._tasStorageStrategyEndPoint = _tasStorageStrategyEndPoint;
    }

    // Added the property for the process endpoint that are stored without ODE server URL in the database
    private String _odeServerURL;
	public String getOdeServerURL() {
		return _odeServerURL;
	}

	public void setOdeServerURL(String odeServerURL) {
		_odeServerURL = odeServerURL;
	}
    
	public TMSServer() {
    }

    public TMSServer(IAuthProvider authProvider, TaskPermissions permissions) {
        _logger.info("New TMS Instance");
        assert authProvider != null : "IAuthProvider implementation is absent!";
        setAuthProvider(authProvider);
        setPermissions(permissions);
    }

    public void setPermissions(TaskPermissions permissions) {
        this._permissions = permissions;
    }

    public int getHttpTimeout() {
        return _httpTimeout;
    }

    public void setHttpTimeout(int httpTimeout) {
        _httpTimeout = httpTimeout;
    }

    public void setAuthProvider(IAuthProvider authProvider) {
        this._authProvider = authProvider;
        _logger.info("IAuthProvider implementation : " + _authProvider.getClass());
    }
    
    public void setTasEndPoint(String tasEndPoint) {
        this._tasEndPoint = tasEndPoint;
    }

    public String getTasEndPoint() {
        return _tasEndPoint;
    }

    public Task[] getTaskList(ITaskDAOConnection dao,String participantToken) throws AuthException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task[] result = dao.fetchAllAvailableTasks(credentials);
        if(result != null && result.length > 0 && result[0] instanceof PIPATask){
        	for(Task task : result){
        		if(task instanceof PIPATask){
        			setOutputPIPA(dao, task, credentials.getUserID());
        		}
        	}
	    }
        return result;
    }
      
    public Task[] listTasksFromInstance(ITaskDAOConnection dao,
        String participantToken, String instanceId) throws AuthException, AccessDeniedException, UnavailableTaskException{
        UserRoles credentials = _authProvider.authenticate( participantToken );
        String userID = credentials.getUserID();
        if (!_permissions.isAuthroized(TaskPermissions.ACTION_DELETE,  credentials))
        {
            throw new AccessDeniedException("The user "+userID+" does not have permission to view tasks from an instance");
        }
        List<Task> tasksFromInstanceId = new ArrayList<Task>();
                        tasksFromInstanceId = dao.fetchTaskfromInstanceID(instanceId);
        Task[] result = (Task[]) tasksFromInstanceId.toArray(new Task[tasksFromInstanceId.size()]);
                        
        return result;
    }

    public UserRoles getUserRoles(String participantToken) throws AuthException {
        return _authProvider.authenticate(participantToken);
    }

    public Task getTask(ITaskDAOConnection dao,String taskID, String participantToken) throws AuthException, UnavailableTaskException,
            AccessDeniedException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task task = dao.fetchTaskIfExists(taskID);
        if(task instanceof PIPATask){
        	setOutputPIPA(dao, task, credentials.getUserID());
        }
        if ((task != null)) {
            checkIsAvailable(taskID, task, credentials);
            if (_logger.isDebugEnabled())
                _logger.debug("Workflow Task " + taskID + " for user " + credentials.getUserID());
            return task;
        } else {
            throw new UnavailableTaskException("No task with" + taskID + " has been found");
        }
    }
    
    //Fix for WF-1493 and WF-1490. To return only userOwners and taskState
    public Task getTaskOwnerAndState(ITaskDAOConnection dao, String taskID,
            String participantToken) throws AuthException, UnavailableTaskException{
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task task = dao.fetchTaskIfExists(taskID);
        if ((task != null)) {
//            checkIsAvailable(taskID, task, credentials);
            if (_logger.isDebugEnabled())
                _logger.debug("Workflow Task " + taskID + " for user " + credentials.getUserID());
            return task;
        } else {
            throw new UnavailableTaskException("No task with" + taskID + " has been found");
        }
    }


    protected ServiceClient getServiceClient() throws AxisFault {
        return new ServiceClient();
    }

    private void checkIsAvailable(String taskID, Task task, UserRoles credentials) throws AccessDeniedException {  	
        // the task has been assign to those credentials
        if (task.isAvailableTo(credentials))
            return;
        // some workflow admin access user has been defined in the security configuration file
        else if (credentials.isWorkflowAdmin())
            return;       
        
        // fire the exception, this user cannot read this task
        else
            throw new AccessDeniedException(credentials.getUserID() + " cannot access task:" + taskID);      
        
    }

    public void setOutput(ITaskDAOConnection dao,String taskID, Document output, String participantToken) throws AuthException,
            UnavailableTaskException, AccessDeniedException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task task = dao.fetchTaskIfExists(taskID);
        checkIsAvailable(taskID, task, credentials);
        if (task instanceof ITaskWithOutput) {
        	if(task instanceof PIPATask){
        		String userOwner = credentials.getUserID();
        		String taskId = task.getID();
        		PIPATaskOutput pipaTaskOutput = null;
        		pipaTaskOutput = dao.fetchPIPATaskOutput(taskId, userOwner);
        		if(pipaTaskOutput != null){
        			pipaTaskOutput.setOutput(output);
        			dao.updatePipaOutput(pipaTaskOutput);
            		dao.commit();
        		}else{
        			pipaTaskOutput = new PIPATaskOutput();
            		pipaTaskOutput.setOutput(output);
            		pipaTaskOutput.setUserOwner(userOwner);
            		pipaTaskOutput.setPipaTaskId(taskId);
            		dao.insertPipaOutput(pipaTaskOutput);
            		dao.commit();
        		}
	            if (_logger.isDebugEnabled())
	                _logger.debug(credentials.getUserID() + " has set output for Workflow Task " + task);
        	}else {
        		ITaskWithOutput taskWithOutput = (ITaskWithOutput) task;
	            taskWithOutput.setOutput(output);
	            dao.updateTask(task);
	            dao.commit();
	            if (_logger.isDebugEnabled())
	                _logger.debug(credentials.getUserID() + " has set output for Workflow Task "+  task);       
        	}
    	} else
    		throw new UnavailableTaskException(credentials.getUserID() + 
    				" cannot set output for Workflow Task " + task);
    }

    public void complete(ITaskDAOConnection dao,String taskID, String participantToken) throws AuthException, UnavailableTaskException,
            InvalidTaskStateException, AccessDeniedException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task task = dao.fetchTaskIfExists(taskID);
        checkIsAvailable(taskID, task, credentials);
        if (task instanceof ITaskWithState) {
            ITaskWithState taskWithState = (ITaskWithState) task;
            taskWithState.setState(TaskState.COMPLETED);
            dao.updateTask(task);
            dao.commit();
            if (_logger.isDebugEnabled())
                _logger.debug(credentials.getUserID() + " has completed the Workflow Task " + task);
        } else {
            throw new UnavailableTaskException(credentials.getUserID() + " cannot complete Workflow Task " + task);
        }
    }

    public void setOutputAndComplete(ITaskDAOConnection dao,String taskID, Document output, String participantToken) throws AuthException,
            UnavailableTaskException, InvalidTaskStateException, AccessDeniedException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task task = dao.fetchTaskIfExists(taskID);
        checkIsAvailable(taskID, task, credentials);
        if (task instanceof ITaskWithOutput && task instanceof ITaskWithState) {
            ((ITaskWithOutput) task).setOutput(output);
            ((ITaskWithState) task).setState(TaskState.COMPLETED);
            dao.updateTask(task);
            dao.commit();
            if (_logger.isDebugEnabled())
                _logger.debug(credentials.getUserID() + " has set output and completed Workflow Task " + task);
        } else {
            throw new UnavailableTaskException(credentials.getUserID()
                    + " cannot set output and complete Workflow Task " + task);
        }
    }

    public void fail(ITaskDAOConnection dao,String taskID, String failureCode, String failureReason, String participantToken)
            throws AuthException, UnavailableTaskException {
        boolean available = false;

        // UserRoles credentials = _authProvider.authenticate(participantToken);
        Task task = dao.fetchTaskIfExists(taskID);
        available = task instanceof ITaskWithState;
        // && task.isAvailableTo(credentials)
        if (available) {
            ITaskWithState taskWithState = (ITaskWithState) task;
            taskWithState.setState(TaskState.FAILED);
            taskWithState.setFailureCode(failureCode);
            taskWithState.setFailureReason(failureReason);
            dao.updateTask(task);
            dao.commit();
            _logger.debug(
            // credentials.getUserID() +
            " fails Workflow Task " + task + " with code: " + failureCode + " and reason: " + failureReason);
        }
        if (!available) {
            throw new UnavailableTaskException("Cannot set state as FAILED for Workflow Task " + task);
        }
    }

    public void delete(ITaskDAOConnection dao,String[] taskIDs, String participantToken) throws AuthException, UnavailableTaskException {
        HashMap<String, Exception> problemTasks = new HashMap<String, Exception>();
        UserRoles credentials = _authProvider.authenticate(participantToken);

        String userID = credentials.getUserID();
        for (String taskID : taskIDs) {
            try {
                Task task = dao.fetchTaskIfExists(taskID);
                Set<String> attachmentsdeletedurl=new HashSet<String>(); 
                if (_permissions.isAuthorized(TaskPermissions.ACTION_DELETE, task, credentials)) {
                    if (task instanceof ITaskWithAttachments && task instanceof PATask) {
                        ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
                        Collection<Attachment> attachments = taskWithAttachments
                                .getAttachments();
                        
                        for (Attachment attachment : attachments) {
                            if(!isAttachmentRelatedToOtherTask(attachment,(PATask)task, dao)){
                                deleteAttachmentTas(participantToken, attachment
                                        .getPayloadURL().toExternalForm());
                                attachmentsdeletedurl.add(attachment.getPayloadURL().toExternalForm());
                            }
                        }
                    }
                    //Change by Atul Starts for deleting attachments uploaded by file upload widget which does not have mapping
                    try{
                    if(task instanceof PATask){
                        PATask patask=(PATask) task;
                        ArrayList<Document> documents=new ArrayList<Document>();
                        if(patask.getOutput()!=null)
                            documents.add(patask.getOutput());
                        if(patask.getInput()!=null)
                            documents.add(patask.getInput());
                        //patask.getInput();
                        if(documents!=null){
                            Set<String> attachmentURL=getAttachmentsURL(documents);
                            if(attachmentURL!=null){
                                attachmentURL.removeAll(attachmentsdeletedurl);
                                Iterator<String> itr=attachmentURL.iterator();
                                while(itr.hasNext()){
                                     deleteAttachmentTas(participantToken,itr.next());
                                }
                            }
                        }
                    }
                    }catch(AxisFault e){
                        _logger.warn("The Attachment URL was not valid " + e.getMessage());
                        //No handling required as the url might have been picked from some text
                    }
                    //Change by Atul Ends
                    
                    dao.deleteTask(task.getInternalId(), taskID);
                    try {
                        dao.commit();
                    } catch (RollbackException  e) {
                        //TODO: Eating the exception here. As tempo-tms is running under ode context which implements its own transaction manager.
                        // Thus explicitly calling commit causes exception. The new jira is created for this fix WF-1590.
                    }
                    if (_logger.isDebugEnabled())
                        _logger.debug(userID + " has deleted Workflow Task " + task);
                } else {
                    problemTasks.put(taskID, new AuthException(userID + " cannot delete" + taskID));
                }
            } catch (Exception e) {
                _logger.error("Cannot retrieve Workflow Tasks", e);
                problemTasks.put(taskID, e);
            }
        }
        if (problemTasks.size() > 0) {
            throw new UnavailableTaskException(userID + " cannot delete Workflow Tasks: " + problemTasks.keySet());
        }
    }

    /**
     * Checks if the currentAttachment is being used by any other tasks. WF-1477 fix.
     * @throws UnavailableTaskException 
     */
    private boolean isAttachmentRelatedToOtherTask(Attachment currentAttachment, PATask currentTask, ITaskDAOConnection dao) throws UnavailableTaskException{
        
        String instanceId = currentTask.getInstanceId();
        String currentAttachmentURL = currentAttachment.getPayloadURL().toExternalForm();
        
        if(instanceId == null ){
            return false;
        }
                          
        List<Task> taskList = dao.fetchTaskfromInstanceID(instanceId);
        for(Task task : taskList ){
            if(task instanceof ITaskWithAttachments && !task.getID().equals(currentTask.getID())){
                ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
                Collection<Attachment> attachments = taskWithAttachments.getAttachments();
                for(Attachment attachment : attachments){
                    if(attachment.getPayloadURL().toExternalForm().equals(currentAttachmentURL)){
                        return true;    
                    }
                }
            }
        }       
        return false;
    }
    
    /**
     * Deletes attachment from the TEMPO_ITEM table through TAS delete operation. WF-1477 fix.
     */
    private void deleteAttachmentTas(String participantToken, String payloadURL) throws AxisFault {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();

        OMNamespace omNamespaceTas = omFactory.createOMNamespace(
                TAS_NS, "tas");
        OMElement deleteRequest = omFactory.createOMElement("deleteRequest",
                omNamespaceTas);
        OMElement authCredentials = omFactory.createOMElement(
                "authCredentials", omNamespaceTas, deleteRequest);
        OMElement participantTokenOM = omFactory.createOMElement(
                "participantToken", omNamespaceTas, authCredentials);
        participantTokenOM.setText(participantToken);
        OMElement attachmentUrlOM = omFactory.createOMElement("attachmentURL",
                omNamespaceTas, deleteRequest);
        attachmentUrlOM.setText(payloadURL);

        Options options = new Options();
        EndpointReference endpointReference = new EndpointReference(
                _tasEndPoint);
        options.setTo(endpointReference);
		// Disabling chunking as lighthttpd doesnt support it
        
		if (isHTTPChunking())
			options.setProperty(
					org.apache.axis2.transport.http.HTTPConstants.CHUNKED,
					Boolean.TRUE);
		else
			options.setProperty(
					org.apache.axis2.transport.http.HTTPConstants.CHUNKED,
					Boolean.FALSE);

        options.setAction("delete");
        
        if (_logger.isDebugEnabled()) {
            _logger.debug("Request to TAS:\n" + deleteRequest.toString());
        }
        
        ServiceClient client = getServiceClient();
        client.setOptions(options);

        try {
            try{
                options.setTimeOutInMilliSeconds(_httpTimeout);
                OMElement response = client.sendReceive(deleteRequest);
                response.build();
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Response from TAS:\n" + response.toString());
                }
            }finally{
                client.cleanupTransport();
            }

        }catch (Exception e) {
            _logger.error("Error while sending deleteRequest:" + e.getClass(), e);
            throw AxisFault.makeFault(e);
        }
    }
    
    public void manageFromInstance(ITaskDAOConnection dao,String instanceid, String participantToken,boolean delete,TaskState state) throws AuthException, UnavailableTaskException,AccessDeniedException {
        
        UserRoles credentials = _authProvider.authenticate(participantToken);  
        String userID=credentials.getUserID();
        if (!_permissions.isAuthroized(TaskPermissions.ACTION_DELETE,  credentials))
        {
            throw new AccessDeniedException("The user"+userID+"does not have delete permission");
        }
        
        if(delete){
            List<Task> taskInstanceId = new ArrayList<Task>();
                taskInstanceId = dao.fetchTaskfromInstanceID(instanceid);
                //Exceptions are not logged as the call will be from ODEEventListener and for some tasks instanceid is not there
                if (taskInstanceId != null && taskInstanceId.size() > 0) {
                    String[] taskIds = new String[taskInstanceId.size()];
                    for (int count = 0; count < taskInstanceId.size(); count++) {
                        taskIds[count] = taskInstanceId.get(count).getID();
                    }
                    delete(dao, taskIds, participantToken); //Delete is called so that tempo_item fix for delete task also works here
                }
           }
        else{
            List<Task> taskInstanceId = new ArrayList<Task>();
            taskInstanceId = dao.fetchTaskfromInstanceID(instanceid);
            if(taskInstanceId!=null && taskInstanceId.size()>0){
                for(int count=0;count <taskInstanceId.size();count++)
                {
                    if (taskInstanceId.get(count) instanceof ITaskWithState)
                    {
                        ITaskWithState taskWithState= (ITaskWithState)taskInstanceId.get(count);
                        taskWithState.setState(state);
                        dao.updateTask(taskInstanceId.get(count));
                        dao.commit();
                    }
                }
            }
        }
    }       
        
    public void create(ITaskDAOConnection dao,Task task, String participantToken) throws AuthException, TaskIDConflictException {
        // UserRoles credentials =
        // _authProvider.authenticate(participantToken);// FIXME: decide on this
        // issue

        try {
            dao.createTask(task);
            dao.commit();
            if (_logger.isDebugEnabled())
                _logger.debug("Workflow Task " + task + " was created");
            // TODO : Use credentials.getUserID() :vb
        } catch (Exception e) {
            _logger.error("Cannot create Workflow Tasks", e); // TODO :
            // TaskIDConflictException
            // must be rethrowed :vb
        } finally {
           // dao.close();
        }
    }

    public void update(ITaskDAOConnection dao,TaskMetadata task, String participantToken) throws AuthException, TMSException ,AxisFault{
       
        try {
            String id = task.getTaskId();
            Task previous = dao.fetchTaskIfExists(id);
            if (previous != null && (previous instanceof PATask)) {
                PATask paPrevious = (PATask) previous;

                // security ?
                UserRoles credentials = _authProvider.authenticate(participantToken);
                // checkIsAvailable(id, previous, credentials);
                
				if(task.isSetDeadline() && !"<xml-fragment/>".equals(task.xgetDeadline().toString())){
					paPrevious.setDeadline(new Date(task.getDeadline().getTimeInMillis()));
				}
                
                if(task.isSetPriority() && !"<xml-fragment/>".equals(task.xgetPriority().toString())){
                	paPrevious.setPriority(task.getPriority());
                }
                String desc = task.getDescription();
                if (!StringUtils.isEmpty(desc))
                    paPrevious.setDescription(desc);
                if(task.getDeadline() != null){
                	paPrevious.setDeadline(new Date(task.getDeadline().getTimeInMillis()));
                }
                dao.updateTask(previous);
                dao.commit();
                if (_logger.isDebugEnabled())
                    _logger.debug("Workflow Task " + task + " was updated");
            } else {
                throw new RuntimeException("No previous activity task with the given id:" + id + (previous != null)
                        + (previous instanceof PATask));
            }
        } 
        catch (Exception e) {
        	_logger.error("Error while sending updating:" + e.getClass(), e);
        	throw AxisFault.makeFault(e);
        }
       
    }

    private Document sendInitMessage(PIPATask task, String user, String formUrl, String participantToken, Document input)
            throws AxisFault {

        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMNamespace omNamespace = omFactory.createOMNamespace(task.getInitMessageNamespaceURI().toString(), "user");

        OMElement omInitProcessRequest = omFactory.createOMElement("initProcessRequest", omNamespace);
        OMElement omTaskId = omFactory.createOMElement("taskId", omNamespace, omInitProcessRequest);
        omTaskId.setText(task.getID());
        OMElement omParticipantToken = omFactory.createOMElement("participantToken", omNamespace, omInitProcessRequest);
        omParticipantToken.setText(participantToken);

        OMElement omUser = omFactory.createOMElement("user", omNamespace, omInitProcessRequest);
        omUser.setText(user);

        OMElement omFormUrl = omFactory.createOMElement("formUrl", omNamespace, omInitProcessRequest);
        omFormUrl.setText(formUrl);

        OMElement omTaskOutput = omFactory.createOMElement("taskOutput", omNamespace, omInitProcessRequest);

        XmlTooling xmlTooling = new XmlTooling();
        if (input != null)
            omTaskOutput.addChild(xmlTooling.convertDOMToOM(input, omFactory));

        //  Refer WF-1531 : Use ODE server url from tempo-tms.xml if process endpoint in the database does not contain the ODE server url.
        String processEndPoint= task.getProcessEndpoint().toString();
        processEndPoint=processEndPoint.startsWith("http:")||processEndPoint.startsWith("https:") ? processEndPoint : _odeServerURL+processEndPoint;


        if (_logger.isDebugEnabled()) {
            _logger.debug(task + " was used to start the process with endpoint:" + task.getProcessEndpoint());
            _logger.debug("Request to Ode:\n" + omInitProcessRequest.toString());
        }

        ServiceClient serviceClient = null;
        OMElement response = null;
        AxisUtil util = new AxisUtil();
        try {
            try{
                serviceClient = util.getServiceClient();
                serviceClient.getOptions().setTo(new EndpointReference(processEndPoint));
                serviceClient.getOptions().setAction(task.getInitOperationSOAPAction());
                if (isHTTPChunking())
                    serviceClient.getOptions()
                            .setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.TRUE);
                else
                    serviceClient.getOptions()
                            .setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
                serviceClient.getOptions().setTimeOutInMilliSeconds(_httpTimeout);
                response = serviceClient.sendReceive(omInitProcessRequest);
                response.build();
                return xmlTooling.convertOMToDOM(response);
            }
            finally{
                if (serviceClient != null){
                    try {
                        util.closeClient(serviceClient);
                    } catch (Exception e) {
                        _logger.error("Error while cleanup");
                    }
                }
            }
        } catch (Exception e) {
            _logger.error("Error while sending initProcessRequest:" + e.getClass(), e);
            throw AxisFault.makeFault(e);
        }

    }

    public Document initProcess(ITaskDAOConnection dao ,String taskID, String user, String formUrl, Document input, String participantToken)
            throws AuthException, UnavailableTaskException, AccessDeniedException, AxisFault {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task task = dao.fetchTaskIfExists(taskID);
        checkIsAvailable(taskID, task, credentials);
        PIPATaskOutput output = dao.fetchPIPATaskOutput(taskID, credentials.getUserID());
        if(output != null){
        	dao.deletePIPATaskOutput(output);
        	dao.commit();
        }        
        if (task instanceof PIPATask) {
            PIPATask pipaTask = (PIPATask) task;
            Document document = sendInitMessage(pipaTask, user, formUrl, participantToken, input);
            if (_logger.isDebugEnabled())
                _logger.debug(credentials.getUserID() + " has initialized process " + pipaTask.getProcessEndpoint()
                        + " with Workflow PIPA Task " + task);
            return document;
        } else {
            throw new UnavailableTaskException("Task (" + taskID
                    + ") is not a PIPA task cannot be used to initiate a process");
        }
    }

    public Attachment[] getAttachments(ITaskDAOConnection dao,String taskID, String participantToken) throws AuthException,
            UnavailableTaskException, AccessDeniedException {
    	_logger.debug("Calling "+this.getClass()+":getAttachments");
    	UserRoles credentials = _authProvider.authenticate(participantToken);
       Task task = dao.fetchTaskIfExists(taskID);
        checkIsAvailable(taskID, task, credentials);
        if (task instanceof ITaskWithAttachments) {
            ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
            return taskWithAttachments.getAttachments().toArray(new Attachment[] {});
        } else {
            throw new RuntimeException("Task (" + taskID + ") does not have attachment");
        }
    }

    public void addAttachment(ITaskDAOConnection dao,String taskID, Attachment attachment, String participantToken) throws AuthException,
            UnavailableTaskException, AccessDeniedException {
    	_logger.debug("Calling "+this.getClass()+":addAttachment");
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task task = dao.fetchTaskIfExists(taskID);
        checkIsAvailable(taskID, task, credentials);
        if (task instanceof ITaskWithAttachments == false) {
            throw new UnavailableTaskException("Task does not support attachments");
        }
        ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
        taskWithAttachments.addAttachment(attachment);
        dao.updateTask(task);
        dao.commit();
        if (_logger.isDebugEnabled())
            _logger.debug(credentials.getUserID() + " has added attachment " + attachment + "to Workflow Task " + task);
    }
    
    /**
     * deletes from attachment_map, attachment, attachment_metadata tables. WF-1479 fix.
     * */
    public void removeAttachment(ITaskDAOConnection dao,String taskID, URL attachmentURL, String participantToken) throws AuthException,
            UnavailableAttachmentException, UnavailableTaskException {

        Task task = null;
        boolean availableTask = false;
        boolean availableAttachment = false;

        UserRoles credentials = _authProvider.authenticate(participantToken);
        try {
            task = dao.fetchTaskIfExists(taskID);
            availableTask = task instanceof ITaskWithAttachments && task.isAvailableTo(credentials);
            if (availableTask) {
                ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
              //removes from the attachment_map table
                Attachment removedAttachment = taskWithAttachments.removeAttachment(attachmentURL);
                availableAttachment = (removedAttachment != null);
                if (availableAttachment) {
                    dao.updateTask(task);
                  //deletes from the attachment and attachment_metadata tables
                    dao.deleteAttachment(attachmentURL.toExternalForm());
                    dao.commit();
                    if (_logger.isDebugEnabled())
                        _logger.debug(credentials.getUserID() + " has removed attachment " + attachmentURL
                                + " for Workflow Task " + task);
                }
            }
        } catch (Exception e) {
            _logger.error("Error while delete attachment " + attachmentURL + " for Workflow Task " + taskID, e);
        } 
        if (!availableTask || !availableAttachment) {
            throw new UnavailableTaskException(credentials.getUserID() + " cannot remove attachment for Workflow Task "
                    + task + ", is problem with task? - " + availableTask + ", is problem with attachment? - "
                    + availableAttachment);
        }
    }

    public void reassign(ITaskDAOConnection dao,String taskID, AuthIdentifierSet users, AuthIdentifierSet roles, TaskState state,
            String participantToken, String userAction) throws AuthException, UnavailableTaskException, AccessDeniedException {
        // UserRoles credentials = _authProvider
        // TODO: this requires SYSTEM
        // role
        // to be present
        // .authenticate(participantToken); // for the Escalations to work. This
        // is
        // a security hole for now!

        Task task = null;
        boolean available = false;

            task = dao.fetchTaskIfExists(taskID);
            if(userAction !=null && !(userAction.equals("ESCALATE") && participantToken.equals(""))){
                UserRoles credentials = _authProvider.authenticate(participantToken);
                checkIsAvailable(taskID, task, credentials);
            }
            // if (task.isAvailableTo(credentials) && (task instanceof
            // ITaskWithState)) { // TODO: see above
            available = task instanceof ITaskWithState;
            if (available) {
                ((ITaskWithState) task).setState(state);
                task.setUserOwners(users);
                task.setRoleOwners(roles);

                dao.updateTask(task);
                dao.commit();
                if (_logger.isDebugEnabled())
                    _logger.debug(" changed to user owners " + users + " and role owners " + roles);
            } 
        if (!available) {
            throw new UnavailableTaskException("Error to ressign Workflow Task " + task);
        }
    }

    public void deletePipa(ITaskDAOConnection dao,String formUrl, String participantToken) throws AuthException, UnavailableTaskException {
        HashMap<String, Exception> problemTasks = new HashMap<String, Exception>();
        BasicTextEncryptor decryptor = new BasicTextEncryptor();
        
        // setPassword uses hash to decrypt password which should be same as hash of encryptor
		decryptor.setPassword("IntalioEncryptedpasswordfortempo#123");
        // DOTO due to all the service from wds is 'x'

        if (decryptor.decrypt(participantToken).equalsIgnoreCase(internalPassword)) {
        	// In some cases internal applicatons like WDS will just send value defined in internalPassword so we need to be careful
            try {
                dao.deletePipaTask(formUrl);
                dao.commit();
            } catch (NoResultException nre) {
                throw new UnavailableTaskException(nre.getMessage());
            }

        } else {
            UserRoles credentials = _authProvider.authenticate(decryptor.decrypt(participantToken));
            String userID = credentials.getUserID();
            try {
                Task task = dao.fetchPipa(formUrl);
                if (_permissions.isAuthorized(TaskPermissions.ACTION_DELETE, task, credentials)) {
                    dao.deletePipaTask(formUrl);
                    dao.commit();
                    if (_logger.isDebugEnabled())
                        _logger.debug(userID + " has deleted PIPA Task " + task);
                } else {
                    problemTasks.put(formUrl, new AuthException(userID + " cannot delete" + formUrl));
                }
            } catch (Exception e) {
                _logger.error("Cannot retrieve PIPA Tasks", e);
                problemTasks.put(formUrl, e);
            }
            if (problemTasks.size() > 0) {
                throw new UnavailableTaskException(userID + " cannot delete PIPA Tasks: " + problemTasks.keySet());
            }
        }
    }
    
    public boolean isPipaExist(ITaskDAOConnection dao, String formUrl){
    	try {
			dao.fetchPipa(formUrl);
		} catch (UnavailableTaskException e) {
			return false;
		}
    	return true;
    }
    
    public void updatePipa(ITaskDAOConnection dao,String formUrl, String participantToken, PIPATaskState state) throws AuthException, UnavailableTaskException {
        HashMap<String, Exception> problemTasks = new HashMap<String, Exception>();
        try {	
            	//Before updating pipa check whether the pipa exists in db?
        	    //So that if we delete pipa and try to update it doesn't produce exception.
        		if(isPipaExist(dao, formUrl)){        			
		            PIPATask pipaTask = dao.fetchPipa(formUrl);
		            pipaTask.setProcessState(state);
		            dao.updatePipaTask(pipaTask);
		            dao.commit();
        		}
            } catch (Exception e) {
                _logger.error("Cannot update PIPA Tasks", e);
                problemTasks.put(formUrl, e);
            }
    }

    public PIPATask getPipa(ITaskDAOConnection dao,String formUrl, String participantToken) throws AuthException, UnavailableTaskException {
        try {
            return dao.fetchPipa(formUrl);
        } catch (Exception e) {
            throw new UnavailableTaskException(e);
        }
    }

    public void storePipa(ITaskDAOConnection dao,PIPATask task, String participantToken) throws AuthException, InvalidTaskException {
        try {
        	dao.storePipaTask(task);
            dao.commit();
        } catch (Exception e) {
            throw new InvalidTaskException(e);
        }
    }

    public Task[] getAvailableTasks(ITaskDAOConnection dao,String participantToken, String taskType, String subQuery) throws AuthException {
        HashMap map = new HashMap(3);
        map.put(TaskFetcher.FETCH_CLASS_NAME, taskType);
        map.put(TaskFetcher.FETCH_SUB_QUERY, subQuery);
        return this.getAvailableTasks(dao,participantToken, map);
    }

    public Long countAvailableTasks(ITaskDAOConnection dao,String participantToken, HashMap parameters) throws AuthException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        try {
            parameters.put(TaskFetcher.FETCH_USER, credentials);
            String klass = (String) parameters.get(TaskFetcher.FETCH_CLASS_NAME);
            if (klass != null)
                parameters.put(TaskFetcher.FETCH_CLASS, TaskTypeMapper.getTaskClassFromStringName(klass));
            return dao.countAvailableTasks(parameters);
        } catch (Exception e) {
            _logger.error("Error while tasks list retrieval for user " + credentials.getUserID(), e);
            throw new RuntimeException(e);
        }
    }

    public Task[] getAvailableTasks(ITaskDAOConnection dao,String participantToken, HashMap parameters) throws AuthException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        try {
            parameters.put(TaskFetcher.FETCH_USER, credentials);
            String klass = (String) parameters.get(TaskFetcher.FETCH_CLASS_NAME);
            if (klass != null)
                parameters.put(TaskFetcher.FETCH_CLASS, TaskTypeMapper.getTaskClassFromStringName(klass));
                Task[] tasks = dao.fetchAvailableTasks(parameters);
                if(tasks != null && tasks.length > 0 && tasks[0] instanceof PIPATask){
                	for(Task task : tasks){
                		if(task instanceof PIPATask){
                			setOutputPIPA(dao, task, credentials.getUserID());
                		}
                	}
                	
                }
                return tasks;
        } catch (Exception e) {
            _logger.error("Error while tasks list retrieval for user " + credentials.getUserID(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the list of task ids to be deleted, and then delegate to the regular
     * delete method
     */
    public void deleteAll(ITaskDAOConnection dao,boolean fakeDelete, String subquery, String taskType, String participantToken)
            throws AuthException, UnavailableTaskException {
        Task[] tasks = null;
        if (taskType != null && taskType.length() != 0) {
            if (subquery == null || subquery.length() == 0)
                subquery = "";
            tasks = getAvailableTasks(dao,participantToken, taskType, subquery);
        } else {
            tasks = getTaskList(dao,participantToken);
        }
        int length = tasks.length;
        String[] ids = new String[length];
        for (int i = 0; i < length; i++) {
            _logger.info("Task:" + tasks[i].getClass() + ":" + tasks[i].getID() + " has been selected for deletion");
            ids[i] = tasks[i].getID();
        }
        if (fakeDelete) {
            _logger.info("Fake delete enabled. Not deleting any tasks");
        } else {
            delete(dao,ids, participantToken);
        }
    }

    public void skip(ITaskDAOConnection dao,String taskID, String participantToken) throws AuthException, UnavailableTaskException,
            InvalidTaskStateException, AccessDeniedException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task task = dao.fetchTaskIfExists(taskID);
        checkIsAvailable(taskID, task, credentials);
        if (task instanceof ITaskWithState) {
            ITaskWithState taskWithState = (ITaskWithState) task;
            taskWithState.setState(TaskState.OBSOLETE);
            dao.updateTask(task);
            dao.commit();
            if (_logger.isDebugEnabled())
                _logger.debug(credentials.getUserID() + " has skiped the Workflow Task " + task);
        } else {
            throw new UnavailableTaskException(credentials.getUserID() + " cannot skip Workflow Task " + task);
        }
    }
    
    private Set<String> getAttachmentsURL(ArrayList<Document> documents) {
        if(_tasStorageStrategyEndPoint==null||_tasStorageStrategyEndPoint ==""){
            _logger.warn(" _tasStorageStrategyEndPoint is not set in properties of TMSServer");
            return null;
        }
        String xpath = "//*[count(*)=0 and contains(.,'"
                + _tasStorageStrategyEndPoint + "')]";
        Set<String> fileuploadwidgetlinkset = new HashSet<String>();
        // Adding the string url only if it's a well formed url
        try {
            if (documents != null) {
                for (Document document : documents) {
                    NodeList nodelist = org.apache.xpath.XPathAPI
                            .selectNodeList(document, xpath);
                    if (nodelist != null) {
                        for (int i = 0; i < nodelist.getLength(); i++) {
                            Element elem = (Element) nodelist.item(i);
                            try {
                                URL url = new URL(elem.getTextContent().trim());
                                _logger.info("File upload widget attachments url" +elem.getTextContent().trim() );
                                fileuploadwidgetlinkset.add(elem
                                        .getTextContent().trim());
                            } catch (MalformedURLException mal) {

                                // The urls are malformed because the file
                                // upload widget makes the link like
                                // <a
                                // href=http://127.0.0.1:8080/wds/attachments/920d7155-ea9e-4ad4-94d5-ef3033d2cab0/x.xlsx>File</a
                                // href>

                                String hrefstring = elem.getTextContent()
                                        .trim();
                                if (hrefstring != null) {
                                    int startindex = hrefstring
                                            .indexOf(_tasStorageStrategyEndPoint);
                                    int endindex = hrefstring.indexOf(">");
                                    if ((startindex != -1 && endindex != -1)
                                            && startindex < endindex) {
                                        String ur = hrefstring.substring(
                                                startindex, endindex).trim();
                                        try {
                                            URL url = new URL(ur);
                                            _logger.info("File upload widget attachments url" +ur );
                                            fileuploadwidgetlinkset.add(ur);
                                        } catch (MalformedURLException ex) {
                                            _logger.warn("File upload widget attachments url not found" + ur );
                                            // Exception Handling not required

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
         } 
            catch (TransformerException e) {
            _logger.error("Exception  getAttachmentsURL for FileUpload"+e.getMessage());
         }
         return fileuploadwidgetlinkset;

    }

	@Override
	public void deleteCustomColumn(ITaskDAOConnection dao, String processName,String token) throws Exception {
		List<CustomColumn> customColumns = null;
		if (processName != null && processName.length() != 0) {
            customColumns = dao.fetchCustomColumnfromProcessName(processName);
        } 
        for (CustomColumn custCol: customColumns) {
            dao.deleteCustomColumn(custCol);
        }
	}

	@Override
	public void storeCustomColumn(ITaskDAOConnection dao,CustomColumn[] customColumn, String token) {
        for (CustomColumn custCol: customColumn) {
        	dao.storeCustomColumn(custCol);
        	dao.commit();           
        }
	}
	
	public List<String> getCustomColumns(ITaskDAOConnection dao, String token) throws AuthException{
	    UserRoles credentials = _authProvider.authenticate(token);
	    return dao.fetchCustomColumns();
	}


    private void setOutputPIPA(ITaskDAOConnection dao, Task task, String userOwner) {
		PIPATaskOutput pipaTaskOutput = dao.fetchPIPATaskOutput(task.getID(), userOwner);
		if(pipaTaskOutput != null){
			((PIPATask) task).setOutput(pipaTaskOutput.getOutput());
		}
	}
    
    public void insertVacation(VacationDAOConnection dao,Vacation vac, String participantToken) throws TMSException {
        try {
     	   	dao.insertVacationDetails(vac);
            dao.commit();
             if (_logger.isDebugEnabled())
                 _logger.debug("Vacation " + vac + " was created");
         } catch (Exception e) {
             _logger.error("Cannot create vacation", e);
             
         } finally {
            // dao.close();
         }
     }
    
     public List<Vacation> getUserVacation(VacationDAOConnection dao,String user, String participantToken) throws TMSException {
     		 List<Vacation> vacationOfUser = dao.getVacationDetails(user);
     		 _logger.debug("vac="+vacationOfUser.size());
     		 return vacationOfUser;
      }
     
     public List<Vacation> getVacationList(VacationDAOConnection dao,String participantToken) throws TMSException {
 		 List<Vacation> vacationOfUser = dao.getVacationDetails();
 		 _logger.debug("vac="+vacationOfUser.size());
 		 return vacationOfUser;
     }
     
     public void deleteVacation(VacationDAOConnection dao,int vacId, String participantToken) throws TMSException {
         try {
         	 dao.deleteVacationDetails(vacId);
             dao.commit();
              if (_logger.isDebugEnabled())
                  _logger.debug("Vacation " + vacId + " was deleted");
          } catch (Exception e) {
              _logger.error("Cannot delete vacation", e); // TODO :
          } finally {
             // dao.close();
          }
      }     
}
