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

package org.intalio.tempo.workflow.task;

import java.net.URI;
import java.net.URL;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentMap;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.traits.IChainableTask;
import org.intalio.tempo.workflow.task.traits.ICompleteReportingTask;
import org.intalio.tempo.workflow.task.traits.IInstanceBoundTask;
import org.intalio.tempo.workflow.task.traits.IProcessBoundTask;
import org.intalio.tempo.workflow.task.traits.ITaskWithAttachments;
import org.intalio.tempo.workflow.task.traits.ITaskWithCustomMetadata;
import org.intalio.tempo.workflow.task.traits.ITaskWithDeadline;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.traits.ITaskWithPriority;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.w3c.dom.Document;

/**
 * Activity task
 */
@Entity
@Table(name = "tempo_pa")
@PrimaryKeyJoinColumn(name="ID", referencedColumnName="ID")
@NamedQueries( {
        @NamedQuery(name = PATask.FIND_BY_STATES, query = "select m from PATask m where m._state=?1", hints = { @QueryHint(name = "openjpa.hint.OptimizeResultCount", value = "1") }),
        @NamedQuery(name= PATask.FIND_BY_INSTANCEID, query= "select m from PATask m where m._instanceId= ?1"),
        @NamedQuery(name=PATask.GET_PENDING_TASK_COUNT,
			query="select count(pa._id) from PATask pa where pa._lastAssignedDate >= (:since) and pa._lastAssignedDate <= (:until) and pa._state = TaskState.READY and (:userOwner MEMBER OF pa._userOwners or pa._roleOwners in (:roleOwners))"),
		@NamedQuery(name=PATask.GET_COMPLETED_TASK_COUNT_BY_USER,
            query="select count(pa._id) from PATask pa where pa._lastActiveDate >= (:since) and pa._lastActiveDate <= (:until) and pa._state = TaskState.COMPLETED and :userOwner MEMBER OF pa._userOwners"),
		@NamedQuery(name=PATask.GET_COMPLETED_TASK_COUNT_BY_USER_ASSIGNED_ROLES,
			query="select count(pa._id) from PATask pa where pa._lastActiveDate >= (:since) and pa._lastActiveDate <= (:until) and pa._state = TaskState.COMPLETED and pa._roleOwners in (:roleOwners)"),
		@NamedQuery(name=PATask.GET_CLAIMED_TASK_COUNT,
			query="select count(pa._id) from PATask pa where pa._lastAssignedDate >= (:since) and pa._lastAssignedDate <= (:until) and pa._state = TaskState.CLAIMED and :userOwner MEMBER OF pa._userOwners"),
		/*get pending or claimed task counts for all users*/
		@NamedQuery(name=PATask.GET_PENDING_CLAIMED_TASK_COUNT_FOR_ALL_USERS,
		    query="select count(pa._id) as total,pa._state as State , user from PATask pa, IN (pa._userOwners) as user where (pa._state = TaskState.READY or pa._state =TaskState.CLAIMED )" +
		        "group by pa._state, user "),

		/*get pending or claimed task counts for users*/
        @NamedQuery(name=PATask.GET_TASK_DISTRIBUTION_FOR_USERS_BASED_ON_TIME,
            query="select count(pa._id) as total,pa._state as State , user from PATask pa, IN (pa._userOwners) as user where pa._state IN (:states)" +
                    "and pa._lastAssignedDate >= (:since) and pa._lastAssignedDate <= (:until) and pa._userOwners in (:userOwners) group by pa._state, user "),

        /*get pending or claimed task counts for all users based on time*/
        @NamedQuery(name=PATask.GET_TASK_DISTRIBUTION_BY_USERS_BASED_ON_TIME,
            query="select count(pa._id) as total,pa._state as State , user from PATask pa, IN (pa._userOwners) as user where pa._state IN (:states) " +
                    "and pa._lastAssignedDate >= (:since) and pa._lastAssignedDate <= (:until) group by pa._state, user "),

        /* get pending or claimed task counts for all users based on time */
        @NamedQuery(name = PATask.GET_TASK_DISTRIBUTION_BY_ROLES_BASED_ON_TIME, query = "select count(pa._id) as total,pa._state as State , role from PATask pa, IN (pa._roleOwners) as role where pa._state IN (:states) "
                + "and pa._lastAssignedDate >= (:since) and pa._lastAssignedDate <= (:until) group by pa._state, role "),

        /* get pending or claimed task counts for users */
        @NamedQuery(name = PATask.GET_TASK_DISTRIBUTION_FOR_ROLES_BASED_ON_TIME, query = "select count(pa._id) as total, pa._state as State, role from PATask pa, IN (pa._roleOwners) as role where pa._state IN (:states)"
                + "and pa._lastAssignedDate >= (:since) and pa._lastAssignedDate <= (:until) and pa._roleOwners in (:roleOwners) group by pa._state, role "),

        @NamedQuery(name=PATask.GET_TASK_COUNT_BY_STATUS,
            query="select count(pa._id) as total, pa._state as State from PATask pa where (pa._state = TaskState.READY or pa._state =TaskState.CLAIMED or pa._state = TaskState.COMPLETED " +
                "or pa._state = TaskState.FAILED) and  pa._creationDate >= (:since) and pa._creationDate <= (:until) group by pa._state"),

        @NamedQuery(name=PATask.GET_TASK_COUNT_BY_PRIORITY,
            query="select count(pa._id) as total, pa._priority as Priority from PATask pa where (pa._state = TaskState.READY or pa._state = TaskState.CLAIMED or pa._state = TaskState.FAILED) " +
                "and pa._creationDate >= (:since) and pa._creationDate <= (:until) group by pa._priority"),

        @NamedQuery(name=PATask.GET_TASK_COUNT_BY_CREATION_DATE,
            query="select pa._creationDate from PATask pa where pa._creationDate >= (:since) and pa._creationDate <= (:until) "),

        @NamedQuery(name=PATask.GET_AVERAGE_COMPLETION_TIME_BY_USER,
            query="select pa._lastActiveDate, pa._lastAssignedDate, user from PATask pa, IN (pa._userOwners) as user where pa._creationDate >= (:since) and pa._creationDate <= (:until) " + 
                "and pa._state = TaskState.COMPLETED and pa._userOwners in (:userOwners)"),

        @NamedQuery(name = PATask.GET_COMPLETED_TASK_COUNT_FOR_USERS, query = "select count(pa._id), user from PATask pa, IN (pa._userOwners) as user where pa._lastActiveDate >= (:since)"
                + " and pa._lastActiveDate <= (:until) and pa._state = TaskState.COMPLETED group by user"),
})
public class PATask extends Task implements ITaskWithState, IProcessBoundTask, ITaskWithInput, ITaskWithOutput,
        ICompleteReportingTask, ITaskWithAttachments, IChainableTask, ITaskWithPriority, ITaskWithDeadline ,IInstanceBoundTask,ITaskWithCustomMetadata{

    public static final String FIND_BY_STATES = "find_by_ps_states";
    public static final String FIND_BY_PA_USER_ROLE = "find_by_pa_user_role";
    public static final String FIND_BY_PA_USER_ROLE_GENERIC = "find_by_pa_user_role_generic";
    public static final String FIND_BY_INSTANCEID="find_by_pa_instanceid";
    public static final String GET_PENDING_TASK_COUNT="get_pending_task_count";
    public static final String GET_COMPLETED_TASK_COUNT_BY_USER="get_completed_task_count_by_user";
    public static final String GET_COMPLETED_TASK_COUNT_BY_USER_ASSIGNED_ROLES="get_completed_task_count_by_user_assigned_roles";
    public static final String GET_CLAIMED_TASK_COUNT="get_claimed_task_count";
    public static final String GET_PENDING_CLAIMED_TASK_COUNT_FOR_ALL_USERS="get_pending_claimed_task_count_for_all_users";
    public static final String GET_TASK_DISTRIBUTION_FOR_USERS_BASED_ON_TIME="get_task_distribution_task_for_users_based_on_time";
    public static final String GET_TASK_DISTRIBUTION_BY_USERS_BASED_ON_TIME="get_task_distribution_task_by_users_based_on_time";
    public static final String GET_TASK_COUNT_BY_STATUS="get_task_count_by_status";
    public static final String GET_TASK_COUNT_BY_PRIORITY="get_task_count_by_priority";
    public static final String GET_TASK_COUNT_BY_CREATION_DATE="get_task_count_by_creation_date";
    public static final String GET_AVERAGE_COMPLETION_TIME_BY_USER="get_average_completion_time_by_user";
    public static final String GET_COMPLETED_TASK_COUNT_FOR_USERS="get_completed_task_count_for_users";
    public static final String GET_TASK_DISTRIBUTION_FOR_ROLES_BASED_ON_TIME="get_task_distribution_for_roles_based_on_time";
    public static final String GET_TASK_DISTRIBUTION_BY_ROLES_BASED_ON_TIME="get_task_distribution_by_roles_based_on_time";

    @Persistent
    @Column(name = "state")
    private TaskState _state = TaskState.READY;

    @Persistent(fetch=FetchType.LAZY)
    @Column(name = "failure_code")
    private String _failureCode = "";

    @Persistent(fetch=FetchType.LAZY)
    @Column(name = "failure_reason")
    private String _failureReason = "";

    @Persistent
    @Column(name = "complete_soap_action")
    private String _completeSOAPAction;

    @Persistent(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @Lob
    @Column(name = "input_xml")
    private String _input;

    @Persistent(cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    @Column(name = "output_xml")
    @Lob
    private String _output;

    @PersistentMap(keyCascade = CascadeType.ALL, elementCascade = CascadeType.ALL, keyType = String.class, elementType = Attachment.class)
    @MapKey(name = "payloadURLAsString")
    @ContainerTable(name = "tempo_attachment_map")
    private Map<String, Attachment> _attachments = new HashMap<String, Attachment>();

    @Persistent(fetch= FetchType.LAZY)
    @Column(name = "is_chained_before")
    private Boolean _isChainedBefore = false;

    @Persistent(fetch= FetchType.LAZY)
    @Column(name = "previous_task_id")
    private String _previousTaskID = null;

    @Persistent
    @Column(name = "deadline")
    private Date _deadline;

    @Persistent
    @Column(name = "priority")
    private Integer _priority;
    
    @Persistent
    @Column(name = "process_id")
    private String _processID;

    @Persistent
    @Column(name = "instance_id")
    private String _instanceId;
    
    @PersistentMap(keyCascade = CascadeType.ALL, elementCascade = CascadeType.ALL, keyType = String.class, 
            elementType=String.class, fetch=FetchType.LAZY)
    @ContainerTable(name="tempo_generic")
    private Map<String, String> _customMetadata;
    
    
    @Persistent(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @Lob
    @Column(name = "ctm_xml")
    private String _ctm;
    
    public PATask() {
        super();
    }

    public PATask(String id, URI formURL) {
        super(id, formURL);
    }

    public PATask(String id, URI formURL, String processID, String completeSOAPAction, Document input, Map<String, String> customMetadata) {
        super(id, formURL);
        this.setProcessID(processID);
        this.setCompleteSOAPAction(completeSOAPAction);
        if (input != null)
            this.setInput(input);
        this.setCustomMetadata(customMetadata);
    }  
        
    public void setCustomMetadata(Map<String, String> customMetadata) {
        this._customMetadata = customMetadata;
    }
    
    public Map<String, String> getCustomMetadata() {
        return _customMetadata;
    }
    
    
    
    public String getProcessID() {
        return _processID;
    }

    public void setProcessID(String processID) {
        if (processID == null) {
            throw new RequiredArgumentException("processID");
        }
        _processID = processID;
    }
    
    public TaskState getState() {
        return _state;
    }

    public void setState(TaskState state) {
        if (state == null) {
            throw new RequiredArgumentException("state");
        }
        _state = state;
    }

    public String getFailureCode() {
        if (_state.equals(TaskState.FAILED)) {
            return _failureCode;
        } else {
            throw new IllegalStateException("Task ID '" + getID() + "': "
                    + "Attempt to get the failure code at task state " + _state);
        }
    }

    public void setFailureCode(String failureCode) {
        if (failureCode == null) {
            throw new RequiredArgumentException("failureCode");
        }

        if (_state.equals(TaskState.FAILED)) {
            _failureCode = failureCode;
        } else {
            throw new IllegalStateException("Task ID '" + getID() + "': "
                    + "Attempt to set the failure code at task state " + _state);
        }
    }

    public String getFailureReason() {
        if (_state.equals(TaskState.FAILED)) {
            return _failureReason;
        } else {
            throw new IllegalStateException("Task ID '" + getID() + "': "
                    + "Attempt to get the failure reason at task state " + _state);
        }
    }

    public void setFailureReason(String failureReason) {
        if (failureReason == null) {
            throw new RequiredArgumentException("failureReason");
        }

        if (_state.equals(TaskState.FAILED)) {
            _failureReason = failureReason;
        } else {
            throw new IllegalStateException("Task ID '" + getID() + "': "
                    + "Attempt to set the failure reason at task state " + _state);
        }
    }

    public String getCompleteSOAPAction() {
        return _completeSOAPAction;
    }

    public void setCompleteSOAPAction(String soapAction) {
        if (soapAction == null) {
            throw new RequiredArgumentException("soapAction");
        }
        _completeSOAPAction = soapAction;
    }

    public boolean isInputAvailable() {
        return _input != null;
    }

    public Document getInput() {
        if (!this.isInputAvailable()) {
            throw new IllegalStateException("Task input not available (e.g. was not retrieved).");
        }
        return XmlTooling.deserializeDocument(_input);
    }

    public void setInput(Document inputDocument) {
        if (inputDocument == null) {
            throw new RequiredArgumentException("inputDocument");
        }
        _input = XmlTooling.serializeDocument(inputDocument);
    }

    public Document getOutput() {
        return XmlTooling.deserializeDocument(_output);
    }

    public String getInputAsXmlString() {
        return _input;
    }

    public String getOutputAsXmlString() {
        return _output;
    }

    public void setOutput(Document outputDocument) {
        if (outputDocument == null) {
            throw new RequiredArgumentException("outputDocument");
        }
        _output = XmlTooling.serializeDocument(outputDocument);
    }

	public Attachment addAttachment(Attachment attachment) {
        return _attachments.put(attachment.getPayloadURL().toExternalForm(), attachment);
    }

    public Attachment removeAttachment(URL attachmentURL) {
        return _attachments.remove(attachmentURL.toExternalForm());
    }

    public Map<String, Attachment> get_attachments() {
        return _attachments;
    }

    public void set_attachments(Map<String, Attachment> _attachments) {
        this._attachments = _attachments;
    }

    public Collection<Attachment> getAttachments() {
    	/*
    	 * IF attachment is null then return null
    	 */
    	if(_attachments == null)
    		return null;
        return Collections.unmodifiableCollection(_attachments.values());
    }

    public boolean isChainedBefore() {
        return _isChainedBefore;
    }

    public void setChainedBefore(boolean isChainedBefore) {
        if (!isChainedBefore) {
            _previousTaskID = null;
        } else {
            if (_previousTaskID == null) {
                throw new IllegalStateException("Set previousTaskID before setting isChainedBefore to true");
            }
        }
        _isChainedBefore = isChainedBefore;
    }

    public String getPreviousTaskID() {
        return _previousTaskID;
    }

    public void setPreviousTaskID(String previousTaskID) {
        if (previousTaskID == null) {
            throw new RequiredArgumentException("previousTaskID");
        }
        _previousTaskID = previousTaskID;
    }

    public void setInput(String input) {
        _input = input;
    }

    public void setOutput(String output) {
        _output = output;
    }

    public Integer getPriority() {
        return _priority;
    }

    public void setPriority(Integer priority) {
        _priority = priority;
    }

    public Date getDeadline() {
        return _deadline;
    }

    public void setDeadline(Date deadline) {
        _deadline = deadline;
    }
    
    public boolean isAvailableTo(UserRoles credentials) {
		if(_state.equals(TaskState.CLAIMED)) {
                for (String userOwner : this.getUserOwners()){
                    if (credentials.getUserID().equals(userOwner)
                            || credentials.getVacationUsers().contains(userOwner)) {
                        return true;
                    }
                }
			return false;
		} else {
			return super.isAvailableTo(credentials);
		}
    }

	public String getInstanceId() {
		return _instanceId;
	}

	public void setInstanceId(String instanceId) {
		_instanceId=instanceId;
	}
	
    public String getCustomTaskMetadata() {
        return _ctm;
    }

    public void setCustomTaskMetadata(String ctm) {
        this._ctm = ctm;
    }

    @Override
	public String toString() {
		return "PATask [_attachments=" + _attachments
				+ ", _completeSOAPAction=" + _completeSOAPAction
				+ ", _deadline=" + _deadline + ", _failureCode=" + _failureCode
				+ ", _failureReason=" + _failureReason + ", _input=" + _input
				+ ", _instanceId=" + _instanceId + ", _isChainedBefore="
				+ _isChainedBefore + ", _output=" + _output
				+ ", _previousTaskID=" + _previousTaskID + ", _priority="
				+ _priority + ", _processID=" + _processID + ", _state="
				+ _state + "]";
	}


}
