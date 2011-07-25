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
 * $Id: XFormsManager.java 2764 2006-03-16 18:34:41Z ozenzin $
 * $Log:$
 */
package org.intalio.tempo.uiframework.model;

import org.intalio.tempo.uiframework.actions.TasksCollector;
import org.intalio.tempo.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskHolder<T extends Task> {
    private T _task;
    private static final Logger _log = LoggerFactory.getLogger(TasksCollector.class);
    private String _formManagerURL;

    public TaskHolder(T task, String formManagerURL) {
        super();
        _task = task;
        _formManagerURL = formManagerURL;
        //_log.debug("Constructor Call: task = "+_task+"/nDescription="+_task.getDescription()+"\n");
    }

    public String getFormManagerURL() {
        return _formManagerURL;
    }

    public void setFormManagerURL(String formManagerURL) {
        _formManagerURL = formManagerURL;
    }

    public T getTask() {
        //_log.debug("getTask()call : task = "+_task+"/nDescription="+_task.getDescription()+"\n");
        return _task;
    }

    public void setTask(T task) {
        this._task = task;
    }

	@Override
	public String toString() {
		return "TaskHolder [_formManagerURL=" + _formManagerURL + ", _task="
				+ _task + "]";
	}

}
