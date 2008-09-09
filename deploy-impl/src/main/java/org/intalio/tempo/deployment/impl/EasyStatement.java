/**
 * Copyright (c) 2007-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.deployment.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

/**
 * Utility wrapper for PreparedStatements.
 * <p>
 * Note: This class is not thread-safe.
 */
public final class EasyStatement {

    /**
     * SQL statement.
     */
    private StringBuffer _sql;

    /**
     * Connection to use.
     */
    private Connection _connection;

    /**
     * Prepared statement, from SQL string.
     */
    private PreparedStatement _statement;

    /**
     * Current column index.
     */
    private int _column = 1;

    /**
     * Construct an EasyStatement.
     * 
     * @param connection
     *            Connection to use.
     */
    public EasyStatement(Connection connection) {
        this(connection, "");
    }

    /**
     * Construct an EasyStatement.
     * 
     * @param connection
     *            Connection to use.
     * @param sql
     *            SQL statement string.
     */
    public EasyStatement(Connection connection, String sql) {
        _connection = connection;
        _sql = new StringBuffer(sql);
        _column = 1;
    }

    /**
     * Get the SQL statement string.
     */
    public String getSQL() {
        return _sql.toString();
    }

    /**
     * Append the given string to the SQL statement.
     */
    public void append(String sql) throws SQLException {
        _column = 1;
        if (_sql == null) {
            _sql = new StringBuffer();
        }
        _sql.append(sql);
        _statement = null;
    }

    /**
     * Set the SQL statement.
     */
    public void setSQL(String sql) throws SQLException {
        _column = 1;
        _sql = new StringBuffer(sql);
        _statement = _connection.prepareStatement(_sql.toString());
    }

    /**
     * Adds a set of parameters to this <code>EasyStatement</code> object's batch of commands.
     * 
     * @exception SQLException
     *                if a database access error occurs
     */
    public void addBatch() throws SQLException {
        prepareStatement();
        _statement.addBatch();
        _column = 1;
    }

    /**
     * Executes the SQL statement in this <code>EasyStatement</code> object, which may be any kind of SQL statement.
     */
    public boolean execute() throws SQLException {
        prepareStatement();
        return _statement.execute();
    }

    /**
     * Execute the SQL statement as an update.
     * 
     * @return number of rows affected.
     */
    public int executeUpdate() throws SQLException {
        prepareStatement();
        return _statement.executeUpdate();
    }

    /**
     * Execute the SQL query and return an EasyResultSet.
     */
    public EasyResultSet executeQuery() throws SQLException {
        ResultSet result;

        prepareStatement();
        result = _statement.executeQuery();
        return new EasyResultSet(result);
    }

    /**
     * Release all underlying resources of this EasyStatement.
     */
    public void close() throws SQLException {
        _sql = null;
        _column = 1;
        if (_statement != null) {
            _statement.close();
            _statement = null;
        }
    }

    /**
     * Clear the current parameter values and reset the column index to 1.
     */
    public void clearParameters() throws SQLException {
        if (_statement != null) {
            _statement.clearParameters();
        }
        _column = 1;
    }

    /**
     * Set the current CHAR column value to the given String value.
     * 
     * @param value
     *            String value (optional).
     */
    public void write(String value) throws SQLException {
        prepareStatement();

        if (value == null || value.trim().length() == 0) {
            _statement.setNull(_column++, Types.CHAR);
        } else {
            _statement.setString(_column++, value.trim());
        }
    }

    /**
     * Set the current BIT column value to the given Boolean value.
     * 
     * @param value
     *            Boolean value (optional).
     */
    public void write(Boolean value) throws SQLException {
        prepareStatement();
        if (value == null) {
            _statement.setNull(_column++, Types.BIT);
        } else {
            _statement.setBoolean(_column++, value.booleanValue());
        }
    }

    /**
     * Set the current BIT column value to the given boolean value.
     * 
     * @param value
     *            boolean value.
     */
    public void write(boolean value) throws SQLException {
        prepareStatement();
        _statement.setBoolean(_column++, value);
    }

    /**
     * Set the current NUMBER column value to the given Integer's value
     * 
     * @param value
     *            Integer value (optional).
     */
    public void write(Integer value) throws SQLException {
        prepareStatement();
        if (value == null) {
            _statement.setNull(_column++, Types.INTEGER);
        } else {
            _statement.setInt(_column++, value.intValue());
        }
    }

    /**
     * Set the current NUMBER column value to the given int value
     * 
     * @param value
     *            integer value
     */
    public void write(int value) throws SQLException {
        prepareStatement();
        _statement.setInt(_column++, value);
    }

    /**
     * Set the current NUMBER column value to the given long value
     * 
     * @param value
     *            long value.
     */
    public void write(long value) throws SQLException {
        prepareStatement();
        _statement.setLong(_column++, value);
    }

    /**
     * Set the current NUMBER column value to the given Long value
     * 
     * @param value
     *            Long value (optional).
     */
    public void write(Long value) throws SQLException {
        prepareStatement();
        if (value == null) {
            _statement.setNull(_column++, Types.NUMERIC);
        } else {
            _statement.setLong(_column++, value.longValue());
        }
    }

    /**
     * Set the current DATE column value to the given Date value
     * 
     * @param value
     *            Date value (optional).
     */
    public void write(Date value) throws SQLException {
        prepareStatement();
        if (value == null) {
            _statement.setNull(_column++, Types.TIMESTAMP);
        } else {
            Timestamp timestamp = new Timestamp(value.getTime());
            _statement.setTimestamp(_column++, timestamp);
        }
    }

    /**
     * Set the current BINARY column to the given Object serialized value. If this method returns a lob helper, then
     * this means the caller must call the lobHelper to update the lob after
     */
    public String toString() {
        return _sql.toString();
    }

    /**
     * Prepare the SQL statement.
     */
    private void prepareStatement() throws SQLException {
        if (_sql == null) {
            throw new IllegalStateException("SQL statement string is null");
        }
        if (_statement == null) {
            _statement = _connection.prepareStatement(_sql.toString());
        }
    }

    public static void execute(Connection c, String sql) throws SQLException {
        EasyStatement stmt = null;
        try {
            stmt = new EasyStatement(c, sql);
            stmt.execute();
        } finally {
            stmt.close();
        }
    }
}