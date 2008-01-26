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

import org.intalio.tempo.workflow.task.Task;

public class TaskHolder<T extends Task> {
    private T _task;

    private String _formManagerURL;

    public TaskHolder(T task, String formManagerURL) {
        super();
        _task = task;
        _formManagerURL = formManagerURL;
    }

    public String getFormManagerURL() {
        return _formManagerURL;
    }

    public void setFormManagerURL(String formManagerURL) {
        _formManagerURL = formManagerURL;
    }

    public T getTask() {
        return _task;
    }

    public void setTask(T task) {
        this._task = task;
    }

}
