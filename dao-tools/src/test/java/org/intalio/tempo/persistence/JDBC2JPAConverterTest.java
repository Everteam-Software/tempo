/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.intalio.tempo.persistence;

import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Confirm the proper usage of converter from JDBC to JPA
 */
public class JDBC2JPAConverterTest {
    final static Logger log = LoggerFactory.getLogger(JDBC2JPAConverterTest.class);
    private static JDBC2JPAConverter j2j;

    public static junit.framework.Test suite() throws Exception {
        // mappings required for JPA and internal derby
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openjpa.jdbc.SynchronizeMappings", "buildSchema");
        map.put("openjpa.ConnectionDriverName", "org.apache.derby.jdbc.EmbeddedDriver");
        map.put("openjpa.ConnectionURL", "jdbc:derby:target/BPMSDB;create=true");
        map.put("openjpa.Log", "DefaultLevel=TRACE");

        j2j = new JDBC2JPAConverter(map);

        return new JUnit4TestAdapter(JDBC2JPAConverterTest.class);
    }

    @Test
    public void createAndCopyTask() throws Exception {
        final String taskId = Long.toString(System.currentTimeMillis());
        // PATask task = new PATask(taskId,
        // URI.create("http://hellonico.net"),"123","123",null);
        // j2j.getJdbcTaskConnection().createTask(task);
        // j2j.copyAllTasks();
        // PATask task2 = (PATask)
        // j2j.getJpaTaskConnection().fetchTaskIfExists(taskId);
        // TaskEquality.areTasksEquals(task, task2);
    }

}