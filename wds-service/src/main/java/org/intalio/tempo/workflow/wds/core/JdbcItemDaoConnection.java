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
 */
package org.intalio.tempo.workflow.wds.core;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A JDBC implementation of {@link org.intalio.tempo.workflow.wds.core.ItemDaoConnection}.
 */
public class JdbcItemDaoConnection implements ItemDaoConnection {
    private static final String CHECK = "SELECT COUNT(*) FROM items WHERE uri = ?";
    private static final String STORE = "INSERT INTO items (uri, content_type, data) VALUES (?, ?, ?)";
    private static final String RETRIEVE = "SELECT content_type, data FROM items " + "WHERE uri = ?";
    private static final String DELETE = "DELETE FROM items WHERE uri = ?";
    
    private Connection _jdbcConnection;

    private PreparedStatement _checkStatement;
    private PreparedStatement _storeStatement;
    private PreparedStatement _retrieveStatement;
    private PreparedStatement _deleteStatement;

    /**
     * Instance constructor.
     */
    public JdbcItemDaoConnection(Connection jdbcConnection) {
        initConnection(jdbcConnection);
        prepareStatements();
    }

    /**
     * Initializes the underlying JDBC connection and sets its parameters.
     */
    private void initConnection(Connection jdbcConnection) {
        _jdbcConnection = jdbcConnection;
        try {
            _jdbcConnection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prepares a set of reusable SQL statements.
     */
    private void prepareStatements() {
        try {
            _checkStatement = _jdbcConnection.prepareStatement(CHECK);
            _storeStatement = _jdbcConnection.prepareStatement(STORE);
            _retrieveStatement = _jdbcConnection.prepareStatement(RETRIEVE);
            _deleteStatement = _jdbcConnection.prepareStatement(DELETE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void commit() {
        try {
            _jdbcConnection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            _jdbcConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeItem(Item item)
            throws UnavailableItemException {
        String uri = item.getURI();
        if (itemExists(uri)) {
            throw new UnavailableItemException("Item with URI '" + uri + "' already exists.");
        } else {
            try {
                int i = 1;
                _storeStatement.setString(i++, uri);
                _storeStatement.setString(i++, item.getContentType());
                _storeStatement.setBytes(i++, item.getPayload());
                _storeStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteItem(String uri)
            throws UnavailableItemException {
        if (!itemExists(uri)) {
            throw new UnavailableItemException("Item with URI '" + uri + "' does not exist.");
        } else {
            try {
                _deleteStatement.setString(1, uri);
                _deleteStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Item retrieveItem(String uri)
            throws UnavailableItemException {
        if (!itemExists(uri)) {
            throw new UnavailableItemException("Item with URI '" + uri + "' does not exist.");
        } else {
            try {
                _retrieveStatement.setString(1, uri);
                ResultSet resultSet = _retrieveStatement.executeQuery();
                resultSet.next();
                int i = 1;
                String contentType = resultSet.getString(i++);
                Blob dataBlob = resultSet.getBlob(i++);
                return new Item(uri, contentType, dataBlob.getBytes(1, (int) dataBlob.length()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean itemExists(String uri) {
        try {
            _checkStatement.setString(1, uri);
            ResultSet resultSet = _checkStatement.executeQuery();
            resultSet.next();
            return (resultSet.getInt(1) != 0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

	public Connection getJDBCConnection() {
		return _jdbcConnection;
	}
}
