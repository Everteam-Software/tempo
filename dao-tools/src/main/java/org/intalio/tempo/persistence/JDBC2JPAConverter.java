/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 */

package org.intalio.tempo.persistence;

import java.sql.Connection;
import java.util.Map;

import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.JDBCTaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.JPATaskDaoConnectionFactory;
import org.intalio.tempo.workflow.wds.Item;
import org.intalio.tempo.workflow.wds.JPAItemDaoConnection;
import org.intalio.tempo.workflow.wds.JPAItemDaoConnectionFactory;
import org.intalio.tempo.workflow.wds.core.JdbcItemDaoConnection;

public class JDBC2JPAConverter {

    private JPAItemDaoConnectionFactory JPAItemFactory;
    private JPATaskDaoConnectionFactory JPATaskFactory;
    private JdbcItemDaoConnection jdbcItemConnection;
    private JDBCTaskDAOConnection jdbcTaskConnection;
    private JPAItemDaoConnection jpaItemConnection;
    private ITaskDAOConnection jpaTaskConnection;

    public JDBC2JPAConverter(Map<String,Object> properties) throws Exception {
        JPAItemFactory = new JPAItemDaoConnectionFactory(properties);
        JPATaskFactory = new JPATaskDaoConnectionFactory(properties);

        Connection itemConnection = JPATaskFactory.getUnderlyingJDBCConnectionFromEntityManager();
        Connection taskConnection = JPATaskFactory.getUnderlyingJDBCConnectionFromEntityManager();
        jdbcItemConnection = new JdbcItemDaoConnection(itemConnection);
        jdbcTaskConnection = new JDBCTaskDAOConnection(taskConnection);
        
        jpaItemConnection = JPAItemFactory.openConnection();
        jpaTaskConnection = JPATaskFactory.openConnection();
        
    }
    
    public void copyItem(String uri) throws Exception {
        Item item = jdbcItemConnection.retrieveItem(uri);
        jpaItemConnection.storeItem(item);
    }
    
    public void copyTask(String id) throws Exception {
        Task task = jdbcTaskConnection.fetchTaskIfExists(id);
        
    }

}