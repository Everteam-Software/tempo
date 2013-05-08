/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.workflow.tms.server;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.CustomColumn;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.PIPATaskState;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.VacationDAOConnection;
import org.w3c.dom.Document;

import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;

//Added ITaskDAOConnection in every method signature for JIRA WF-1466
public interface ITMSServer {

    Task[] getTaskList(ITaskDAOConnection dao,String participantToken) throws TMSException;

    Task[] getAvailableTasks(ITaskDAOConnection dao,String participantToken, String taskType, String subQuery) throws TMSException;

    void skip(ITaskDAOConnection dao,String taskID, String participantToken) throws TMSException;
    
    Task getTask(ITaskDAOConnection dao,String taskID, String participantToken) throws TMSException;
    
    Task getTaskOwnerAndState(ITaskDAOConnection dao,String taskID, String participantToken) throws TMSException;

    UserRoles getUserRoles(String participantToken) throws TMSException;

    PIPATask getPipa(ITaskDAOConnection dao,String formUrl, String participantToken) throws TMSException;

    Document initProcess(ITaskDAOConnection dao,String taskID, String user, String formUrl, Document input, String participantToken) throws TMSException, AxisFault;

    Attachment[] getAttachments(ITaskDAOConnection dao,String taskID, String participantToken) throws TMSException;

    void addAttachment(ITaskDAOConnection dao,String taskID, Attachment attachment, String participantToken) throws TMSException;

    void removeAttachment(ITaskDAOConnection dao,String taskID, URL attachmentURL, String participantToken) throws TMSException;

    void reassign(ITaskDAOConnection dao,String taskID, AuthIdentifierSet users, AuthIdentifierSet roles, TaskState state, String participantToken, String userAction) throws TMSException;

    void storePipa(ITaskDAOConnection dao,PIPATask task, String participantToken) throws TMSException;

    void deletePipa(ITaskDAOConnection dao,String formUrl, String participantToken) throws TMSException;

    void setOutput(ITaskDAOConnection dao,String taskID, Document output, String participantToken) throws TMSException;

    void complete(ITaskDAOConnection dao,String taskID, String participantToken) throws TMSException;

    void setOutputAndComplete(ITaskDAOConnection dao,String taskID, Document output, String participantToken) throws TMSException;

    void fail(ITaskDAOConnection dao,String taskID, String failureCode, String failureReason, String participantToken) throws TMSException;

    void delete(ITaskDAOConnection dao,String[] taskIDs, String participantToken) throws TMSException;
    
    void manageFromInstance(ITaskDAOConnection dao,String instanceId, String participantToken,boolean delete,TaskState state) throws TMSException;
    
    Task[] listTasksFromInstance(ITaskDAOConnection dao,String participantToken, String instanceId)throws AuthException, AccessDeniedException, UnavailableTaskException;
    
    void create(ITaskDAOConnection dao,Task task, String participantToken) throws TMSException;
    
    void update(ITaskDAOConnection dao,TaskMetadata task, String participantToken) throws TMSException,AxisFault;

    void deleteAll(ITaskDAOConnection dao,boolean fakeDelete, String subquery, String subqueryClass, String participantToken) throws TMSException;

    Task[] getAvailableTasks(ITaskDAOConnection dao,String participantToken, HashMap parameters) throws Exception;

    Long countAvailableTasks(ITaskDAOConnection dao,String participantToken, HashMap map) throws AuthException;
    
    List<String> getCustomColumns( ITaskDAOConnection dao, String token) throws AuthException;

    void updatePipa(ITaskDAOConnection dao,String formUrl, String participantToken, PIPATaskState state) throws AuthException, UnavailableTaskException ;

	void storeCustomColumn(ITaskDAOConnection dao, CustomColumn[] customColumn,
			String token);

	void deleteCustomColumn(ITaskDAOConnection dao, String processName,
			String token) throws Exception;
	
	/**
	 * Inserts Vacation Details
	 */
	void insertVacation(VacationDAOConnection dao,Vacation vac,String participantToken)throws TMSException;
	
	/**
	 * Updates Vacation Details
	 */
	void updateVacation(VacationDAOConnection dao,Vacation vac,String participantToken)throws TMSException;
    
	/**
	 * Gets the vacation details of a particular user
	 */
    List<Vacation> getUserVacation(VacationDAOConnection dao,String user,String participantToken)throws TMSException;
    
    /**
	 * Gets the vacation details of all users
	 */
    List<Vacation> getVacationList(VacationDAOConnection dao,String participantToken)throws TMSException;
    
    /**
	 * delete Vacation Details of given id
	 */
    void deleteVacation(VacationDAOConnection dao,int vacId,String participantToken)throws TMSException;
    
    /**
	 * Gets matched vacation details of given dates
	 */
    List<Vacation> getMatchedVacations(VacationDAOConnection dao,Date fromDate, Date toDate, String participantToken)throws TMSException;
}