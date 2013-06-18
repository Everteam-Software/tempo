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

import java.net.URI;

import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.util.map.InvertibleMap;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;

public final class TaskTypeMapper {

    private static final InvertibleMap<Class<? extends Task>, Object> _typeMap = new InvertibleMap<Class<? extends Task>, Object>();

    public static enum TaskType {
        INIT, ACTIVITY, NOTIFICATION
    }

    static {
        _typeMap.put(PIPATask.class, TaskType.INIT.name());
        _typeMap.put(PATask.class, TaskType.ACTIVITY.name());
        _typeMap.put(Notification.class, TaskType.NOTIFICATION.name());
    }

    public static String getTypeClassName(Class<? extends Task> taskClass) {
        if (taskClass == null) {
            throw new RequiredArgumentException("taskClass");
        }
        Object result = _typeMap.get(taskClass);
        if (result == null) {
            throw new IllegalArgumentException("Unknown class type: " + taskClass.getName());
        }
        return result.toString();
    }
    
    /**
     * To be backward compatible, we support both finding the class from its simple name, and from the reverse map
     */
    public static Class<? extends Task> getTaskClassFromStringName(String klass) {
        try {
            return (Class<? extends Task>)Class.forName("org.intalio.tempo.workflow.task." + klass);    
        } catch (Exception e) {
            return TaskTypeMapper.getTypeClassByName(klass); 
        }
    }

    public static Class<? extends Task> getTypeClassByName(String name) throws InvalidInputFormatException {
        if (name == null) {
            throw new RequiredArgumentException("name");
        }
        Class<? extends Task> typeClass = _typeMap.getInverse(name.toUpperCase());
        if (typeClass == null) {
            throw new InvalidInputFormatException("Invalid task type name: '" + name + "'");
        }
        return typeClass;
    }

    private TaskTypeMapper() {

    }

    public static Task getNewInstance(Class<? extends Task> taskClass, String taskID, URI formURL) {
        try {
            return (Task) taskClass.getConstructor(String.class, URI.class).newInstance(taskID, formURL);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot instanciate class type: " + taskClass.getName());
        }
    }

    public static Task getNewInstance(Class<? extends Task> taskClass) {
        try {
            return (Task) taskClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot instanciate class type: " + taskClass.getName());
        }
    }
}
