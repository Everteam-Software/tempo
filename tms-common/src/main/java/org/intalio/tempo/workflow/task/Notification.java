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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import org.apache.openjpa.persistence.Persistent;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.w3c.dom.Document;

@Entity
public class Notification extends Task implements ITaskWithState, ITaskWithInput {
   
    @Column(name="state")
    @Persistent
    private TaskState _state = TaskState.READY;
    
    @Column(name="failure_code")
    @Persistent
    private String _failureCode;
    
    @Column(name="failure_reason")
    @Persistent
    private String _failureReason;

    @Column(name="input_xml")
    @Lob
    private String  _input;

    public Notification() {
        super();
    }
        
    public Notification(String id, URI formURL, Document input) {
        super(id, formURL);
        if(input!=null)setInput(input);
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
        return XmlTooling.deserializeDocument(_input);
    }

    public void setInput(Document input) {
        if (input == null) {
            throw new RequiredArgumentException("input");
        }
        _input = XmlTooling.serializeDocument(input);
    }
}
