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

package org.intalio.tempo.workflow.task.xml;

import java.util.HashMap;
import java.util.Map;

import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;

final class TaskTypeMapper {

    private static final Map<Class<? extends Task>, String> _typeMap = new HashMap<Class<? extends Task>, String>();

    static {
        _typeMap.put(PIPATask.class, "INIT");
        _typeMap.put(PATask.class, "ACTIVITY");
        _typeMap.put(Notification.class, "NOTIFICATION");
    }

    public static String getTypeClassName(Class<? extends Task> taskClass) {
        if (taskClass == null) {
            throw new RequiredArgumentException("taskClass");
        }

        String result = _typeMap.get(taskClass);
        if (result == null) {
            throw new IllegalArgumentException("Unknown class type: " + taskClass.getName());
        }
        return result;
    }

    public static Class<? extends Task> getTypeClassByName(String name) throws InvalidInputFormatException {
        if (name == null) {
            throw new RequiredArgumentException("name");
        }

        Class<? extends Task> typeClass = null;
        for (Map.Entry<Class<? extends Task>, String> entry : _typeMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) {
                typeClass = entry.getKey();
                break;
            }
        }
        if (typeClass == null) {
            throw new InvalidInputFormatException("Invalid task type name: '" + name + "'");
        }
        return typeClass;
    }

    private TaskTypeMapper() {

    }
}
