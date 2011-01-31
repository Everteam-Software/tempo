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

import java.net.URL;
import java.util.HashMap;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.InvalidTaskException;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
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
import org.intalio.tempo.workflow.tms.server.permissions.TaskPermissions;
import org.intalio.tempo.workflow.util.jpa.TaskFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;

public class TMSServer implements ITMSServer {

    private static final Logger _logger = LoggerFactory.getLogger(TMSServer.class);
    private IAuthProvider _authProvider;
    private TaskPermissions _permissions;
    private int _httpTimeout = 10000;

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


/*JIRA WF-1466 Changes have been made to get the dao in the method 
instead of creating and opening connections in the method itself */
    public Task[] getTaskList(ITaskDAOConnection dao,String participantToken) throws AuthException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task[] result = dao.fetchAllAvailableTasks(credentials);
        return result;
    }

    public UserRoles getUserRoles(String participantToken) throws AuthException {
        return _authProvider.authenticate(participantToken);
    }

    public Task getTask(ITaskDAOConnection dao,String taskID, String participantToken) throws AuthException, UnavailableTaskException,
            AccessDeniedException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
         
        Task task = dao.fetchTaskIfExists(taskID);
        if ((task != null)) {
            checkIsAvailable(taskID, task, credentials);
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
        // some admin access user has been defined in the configuration file
        else if (_permissions.isAuthorized(TaskPermissions.ACTION_READ, task, credentials))
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
            ITaskWithOutput taskWithOutput = (ITaskWithOutput) task;
            taskWithOutput.setOutput(output);
            dao.updateTask(task);
            dao.commit();
            if (_logger.isDebugEnabled())
                _logger.debug(credentials.getUserID() + " has set output for Workflow Task " + task);
        } else
            throw new UnavailableTaskException(credentials.getUserID() + " cannot set output for Workflow Task " + task);
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
                if (_permissions.isAuthorized(TaskPermissions.ACTION_DELETE, task, credentials)) {
                    dao.deleteTask(task.getInternalId(), taskID);
                    dao.commit();
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
        } 
    }

    public void update(ITaskDAOConnection dao,TaskMetadata task, String participantToken) throws AuthException, TMSException, AxisFault {
        try {
            String id = task.getTaskId();
            Task previous = dao.fetchTaskIfExists(id);
            if (previous != null && (previous instanceof PATask)) {
                PATask paPrevious = (PATask) previous;

                // security ?
                UserRoles credentials = _authProvider.authenticate(participantToken);
                // checkIsAvailable(id, previous, credentials);

                try {
                    paPrevious.setPriority(task.getPriority());
                } catch (Exception e) {
                    _logger.debug("Ignoring invalid priority value:" + task.xgetPriority().toString());
                }
                String desc = task.getDescription();
                if (!StringUtils.isEmpty(desc))
                    paPrevious.setDescription(desc);

                dao.updateTask(previous);
                dao.commit();
                if (_logger.isDebugEnabled())
                    _logger.debug("Workflow Task " + task + " was updated");
            } else {
                throw new RuntimeException("No previous activity task with the given id:" + id + (previous != null)
                        + (previous instanceof PATask));
            }
        } catch (Exception e) {
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

        Options options = new Options();
        EndpointReference endpointReference = new EndpointReference(task.getProcessEndpoint().toString());
        options.setTo(endpointReference);
        options.setAction(task.getInitOperationSOAPAction());

        if (_logger.isDebugEnabled()) {
            _logger.debug(task + " was used to start the process with endpoint:" + task.getProcessEndpoint());
            _logger.debug("Request to Ode:\n" + omInitProcessRequest.toString());
        }

        ServiceClient client = getServiceClient();
        client.setOptions(options);
        try {
            options.setTimeOutInMilliSeconds(_httpTimeout);
            OMElement response = client.sendReceive(omInitProcessRequest);
            return xmlTooling.convertOMToDOM(response);
        } catch (Exception e) {
            _logger.error("Error while sending initProcessRequest:" + e.getClass(), e);
            throw AxisFault.makeFault(e);
        }

    }

    public Document initProcess(ITaskDAOConnection dao,String taskID, String user, String formUrl, Document input, String participantToken)
            throws AuthException, UnavailableTaskException, AccessDeniedException, AxisFault {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        Task task = dao.fetchTaskIfExists(taskID);
        checkIsAvailable(taskID, task, credentials);
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
                Attachment removedAttachment = taskWithAttachments.removeAttachment(attachmentURL);
                availableAttachment = (removedAttachment != null);
                if (availableAttachment) {
                    dao.updateTask(task);
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
            String participantToken) throws AuthException, UnavailableTaskException {
        // UserRoles credentials = _authProvider
        // TODO: this requires SYSTEM
        // role
        // to be present
        // .authenticate(participantToken); // for the Escalations to work. This
        // is
        // a security hole for now!

        Task task = null;
        boolean available = false;

        try {
            task = dao.fetchTaskIfExists(taskID);
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
        } catch (Exception e) {
            _logger.error("Cannot retrieve Workflow Tasks", e);
        } 
        if (!available) {
            throw new UnavailableTaskException("Error to ressign Workflow Task " + task);
        }
    }

    public void deletePipa(ITaskDAOConnection dao,String formUrl, String participantToken) throws AuthException, UnavailableTaskException {
        HashMap<String, Exception> problemTasks = new HashMap<String, Exception>();

        // DOTO due to all the service from wds is 'x'
        if (participantToken.equalsIgnoreCase("x")) {
            dao.deletePipaTask(formUrl);
            dao.commit();
        } else {
            UserRoles credentials = _authProvider.authenticate(participantToken);
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
            return dao.fetchAvailableTasks(parameters);
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
}
