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
import java.util.Date;

import org.w3c.dom.Document;
import org.intalio.tempo.workflow.task.traits.ITaskWithDeadline;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithPriority;

import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public class Notification extends Task implements ITaskWithState, ITaskWithInput ,ITaskWithPriority{
    private TaskState _state = TaskState.READY;
    private String _failureCode;
    private String _failureReason;
    private Document _input;
    private Date _dealine=null;
    private Integer _priority=0;
    public Notification(String id, URI formURL, Document input) {
        super(id, formURL);
        _input = input;
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

    public Document getInput() {
        return _input;
    }

    public void setInput(Document input) {
        if (input == null) {
            throw new RequiredArgumentException("input");
        }
        _input = input;
    }

	
	public Integer getPriority() {
				return _priority;
	}

	
	public void setPriority(Integer priority) {
		_priority=priority;
		
	}


	public Date getDeadline() {
		
		return _dealine;
	}


	public void setDeadline(Date deadline) {
		 _dealine=deadline;
		
	}

	

	

	
}
