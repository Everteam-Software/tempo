/**
 * Copyright (c) 2005-2009 Intalio inc.
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

/**
A list of all the different task states that Workflow currently handles.

 <ul>
 <li>READY: the task can be viewed by potentially anyone, and has a task manager process to handle its lifecycle</li>
 <li>COMPLETED: the task has been completed, the task manager process has also finished</li>
 <li>FAILED: the task has somehow failed to complete, it has error info in its metadata, task maanger has finished</li>
 <li>CLAIMED: the task is just like in an ACTIVE states, except it can be accessed by only one user</li>
 <li>OBSOLETE: the task was asked to be skipped. TMP is stopped. You need to act on your user process to react accordingly </li> 
 </ul>
*/
public enum TaskState {
    READY,
    COMPLETED,
    FAILED,
    CLAIMED,
    OBSOLETE;
    
    public String getName() {
        return this.name();
    }
}
