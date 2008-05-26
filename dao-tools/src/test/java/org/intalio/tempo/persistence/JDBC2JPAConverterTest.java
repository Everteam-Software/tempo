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

import java.net.URI;
import java.util.Properties;

import org.intalio.tempo.workflow.task.Notification;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Confirm the proper usage of converter from JDBC to JPA
 */
public class JDBC2JPAConverterTest {
    final static Logger log = LoggerFactory.getLogger(JDBC2JPAConverterTest.class);

   
    @Test
    public void createAndCopyTask() throws Exception {
        Properties props = new Properties();
        props.load(this.getClass().getResourceAsStream("/jpa.properties"));
        
        JDBC2JPAConverter converter = new JDBC2JPAConverter(props);
        converter.copyAllTasks();
        converter.copyAllItems();
    }
}
