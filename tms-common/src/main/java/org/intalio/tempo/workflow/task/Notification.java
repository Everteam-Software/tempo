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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.Lob;

import org.apache.openjpa.persistence.Persistent;
import org.intalio.tempo.workflow.task.traits.IProcessBoundTask;
import org.intalio.tempo.workflow.task.traits.IInstanceBoundTask;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithPriority;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.w3c.dom.Document;

/**
 * Notification tasks
 */
@Entity
@Table(name = "tempo_notification")
public class Notification extends Task implements ITaskWithState, ITaskWithInput, IProcessBoundTask,IInstanceBoundTask{

    @Column(name = "state")
    @Persistent
    private TaskState _state = TaskState.READY;

    @Column(name = "failure_code")
    @Persistent(fetch = FetchType.LAZY)
    private String _failureCode;

    @Column(name = "failure_reason")
    @Persistent(fetch = FetchType.LAZY)
    private String _failureReason;

    @Persistent(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "input_xml")
    @Lob
    private String _input;

    @Persistent
    @Column(name = "priority")
    private Integer _priority;

    @Persistent (fetch = FetchType.LAZY)
    @Column(name = "instanceId")
    private String _instanceId;
    
    @Persistent
    @Column(name = "process_id")
    private String _processID;
    
    public Notification() {
        super();
    }

    @Deprecated
    public Notification(String id, URI formURL, Document input) {
        super(id, formURL);
        if (input != null)
            setInput(input);
    }

    public Notification(String id, URI formURL) {
        super(id, formURL);
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
            throw new IllegalStateException("Task ID '" + getID() + "': " + "Attempt to get the failure code at task state " + _state);
        }
    }

    public void setFailureCode(String failureCode) {
        if (failureCode == null) {
            throw new RequiredArgumentException("failureCode");
        }

        if (_state.equals(TaskState.FAILED)) {
            _failureCode = failureCode;
        } else {
            throw new IllegalStateException("Task ID '" + getID() + "': " + "Attempt to set the failure code at task state " + _state);
        }
    }

    public String getFailureReason() {
        if (_state.equals(TaskState.FAILED)) {
            return _failureReason;
        } else {
            throw new IllegalStateException("Task ID '" + getID() + "': " + "Attempt to get the failure reason at task state " + _state);
        }
    }

    public void setFailureReason(String failureReason) {
        if (failureReason == null) {
            throw new RequiredArgumentException("failureReason");
        }

        if (_state.equals(TaskState.FAILED)) {
            _failureReason = failureReason;
        } else {
            throw new IllegalStateException("Task ID '" + getID() + "': " + "Attempt to set the failure reason at task state " + _state);
        }
    }

    public Document getInput() {
        return XmlTooling.deserializeDocument(_input);
    }

    public void setInput(Document input) {
        _input = XmlTooling.serializeDocument(input);
    }

    public void setInput(String input) {
        _input = input;
    }

    public Integer getPriority() {
        return _priority;
    }

    public void setPriority(Integer _priority) {
        this._priority = _priority;
    }

	public String getInstanceId() {
		
		return _instanceId;
	}

	public void setInstanceId(String instanceId) {
		_instanceId=instanceId;
		
	}
}
