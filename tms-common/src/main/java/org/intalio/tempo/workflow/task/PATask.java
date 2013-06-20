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
        @NamedQuery(name= PATask.FIND_BY_INSTANCEID, query= "select m from PATask m where m._instanceId= ?1")})
public class PATask extends Task implements ITaskWithState, IProcessBoundTask, ITaskWithInput, ITaskWithOutput,
        ICompleteReportingTask, ITaskWithAttachments, IChainableTask, ITaskWithPriority, ITaskWithDeadline ,IInstanceBoundTask,ITaskWithCustomMetadata{

    public static final String FIND_BY_STATES = "find_by_ps_states";
    public static final String FIND_BY_PA_USER_ROLE = "find_by_pa_user_role";
    public static final String FIND_BY_PA_USER_ROLE_GENERIC = "find_by_pa_user_role_generic";
    public static final String FIND_BY_INSTANCEID="find_by_pa_instanceid";

    
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
			for (String userOwner : this.getUserOwners()) if (credentials.getUserID().equals(userOwner)) return true;
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
