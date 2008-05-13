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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.JDBCTaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.JPATaskDaoConnectionFactory;
import org.intalio.tempo.workflow.wds.core.Item;
import org.intalio.tempo.workflow.wds.core.JPAItemDaoConnection;
import org.intalio.tempo.workflow.wds.core.JPAItemDaoConnectionFactory;
import org.intalio.tempo.workflow.wds.core.JdbcItemDaoConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convert task and items from jdbc to jpa
 * 
 */
public class JDBC2JPAConverter implements ConverterInterface {
    private static final Logger log = LoggerFactory.getLogger(JDBC2JPAConverter.class);
    private static final String SELECT_ITEMS = "SELECT uri FROM items";
    private static final String SELECT_TASKS = "SELECT task_id FROM tasks";

    private JPAItemDaoConnectionFactory JPAItemFactory;
    private JPATaskDaoConnectionFactory JPATaskFactory;
    private JdbcItemDaoConnection jdbcItemConnection;
    private JDBCTaskDAOConnection jdbcTaskConnection;
    private JPAItemDaoConnection jpaItemConnection;
    private ITaskDAOConnection jpaTaskConnection;
    private Connection itemConnection;
    private Connection taskConnection;

    /**
     * Create the jpa factories, and retrieve the jdbc tempo connection and the
     * barebone jdbc connections
     */
    public JDBC2JPAConverter(Map<String, Object> properties) throws Exception {
        // JPAItemFactory = new JPAItemDaoConnectionFactory(properties);
        JPATaskFactory = new JPATaskDaoConnectionFactory(properties);

        // jpaItemConnection = JPAItemFactory.openConnection();
        jpaTaskConnection = JPATaskFactory.openConnection();

        String userName = "niko";
        UserRoles ur = new UserRoles(userName, new AuthIdentifierSet());

        // itemConnection =
        // JPAItemFactory.getUnderlyingJDBCConnectionFromEntityManager();
        taskConnection = JPATaskFactory.getUnderlyingJDBCConnectionFromEntityManager();

        // jdbcItemConnection = new JdbcItemDaoConnection(itemConnection);
        // jdbcTaskConnection = new JDBCTaskDAOConnection(taskConnection);
        jdbcTaskConnection = new JDBCTaskDAOConnection(getJDBCConnection(properties));
        jdbcItemConnection = new JdbcItemDaoConnection(getJDBCConnection(properties));

    }

    private static final Connection getJDBCConnection(Map<String, Object> properties) throws SQLException {
        return DriverManager.getConnection(
                        (String) properties.get("jdbc.ConnectionURL"), 
                        (String) properties.get("jdbc.ConnectionUsername"),
                        (String) properties.get("jdbc.ConnectionPassword"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.intalio.tempo.persistence.ConverterInterface#copyItem(java.lang.String)
     */
    public void copyItem(String uri) throws Exception {
        Item item = jdbcItemConnection.retrieveItem(uri);
        jpaItemConnection.storeItem(item);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.intalio.tempo.persistence.ConverterInterface#copyTask(java.lang.String)
     */
    public void copyTask(String id) throws Exception {
        Task task = jdbcTaskConnection.fetchTaskIfExists(id);
        jpaTaskConnection.createTask(task);
    }

    private List<String> find(Connection c, String sql) throws Exception {
        PreparedStatement ps = c.prepareCall(sql);
        ResultSet set = ps.executeQuery();
        List<String> list = new ArrayList<String>();
        while (set.next())
            list.add(set.getString(1));
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.intalio.tempo.persistence.ConverterInterface#findAllItems()
     */
    public List<String> findAllItems() throws Exception {
        return find(itemConnection, SELECT_ITEMS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.intalio.tempo.persistence.ConverterInterface#findAllTasks()
     */
    public List<String> findAllTasks() throws Exception {
        return find(taskConnection, SELECT_TASKS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.intalio.tempo.persistence.ConverterInterface#copyAllTasks()
     */
    public void copyAllTasks() throws Exception {
        List<String> ids = findAllTasks();
        for (String id : ids) {
            log.info("Copying task:" + ids);
            copyTask(id);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.intalio.tempo.persistence.ConverterInterface#copyAllItems()
     */
    public void copyAllItems() throws Exception {
        List<String> ids = findAllItems();
        for (String id : ids) {
            log.info("Copying item:" + ids);
            copyItem(id);
        }
    }

    public JdbcItemDaoConnection getJdbcItemConnection() {
        return jdbcItemConnection;
    }

    public JDBCTaskDAOConnection getJdbcTaskConnection() {
        return jdbcTaskConnection;
    }

    public JPAItemDaoConnection getJpaItemConnection() {
        return jpaItemConnection;
    }

    public ITaskDAOConnection getJpaTaskConnection() {
        return jpaTaskConnection;
    }

    public Connection getItemConnection() {
        return itemConnection;
    }

    public Connection getTaskConnection() {
        return taskConnection;
    }

}