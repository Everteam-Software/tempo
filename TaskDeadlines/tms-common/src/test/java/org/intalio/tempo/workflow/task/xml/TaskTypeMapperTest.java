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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.xml.TaskTypeMapper;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;

public class TaskTypeMapperTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TaskTypeMapperTest.class);
    }

    public void testTaskTypeMapper() throws Exception {
        Assert.assertEquals(PATask.class, TaskTypeMapper.getTypeClassByName("ACTIVITY"));
        Assert.assertEquals(PATask.class, TaskTypeMapper.getTypeClassByName("AcTiViTy"));
        Assert.assertEquals(PIPATask.class, TaskTypeMapper.getTypeClassByName("INIT"));
        Assert.assertEquals(PIPATask.class, TaskTypeMapper.getTypeClassByName("iNiT"));
        try {
            TaskTypeMapper.getTypeClassByName(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
        try {
            TaskTypeMapper.getTypeClassByName("badbadname");
            Assert.fail("InvalidInputFormatException expected");
        } catch (InvalidInputFormatException e) {

        }

        Assert.assertEquals("ACTIVITY", TaskTypeMapper.getTypeClassName(PATask.class));
        Assert.assertEquals("INIT", TaskTypeMapper.getTypeClassName(PIPATask.class));
        try {
            TaskTypeMapper.getTypeClassName(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
        try {
            Task task = new Task("id", new URI("http://localhost/")) {};
            TaskTypeMapper.getTypeClassName(task.getClass());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {

        }
    }
}
