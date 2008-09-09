package org.intalio.tempo.deployment.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Utility wrapper for ResultSet.
 */
public final class EasyResultSet {

    /**
     * Wrapped ResultSet.
     */
    private ResultSet _resultSet;

    /**
     * Current column.
     */
    private int _column = 1;

    /**
     * Construct an EasyResultSet from an existing ResultSet.
     * 
     * @exception SQLException
     *                if SQL exception occurs during execution of the prepared statement.
     */
    public EasyResultSet(ResultSet resultSet) throws SQLException {
        _resultSet = resultSet;
    }

    /**
     * Get the underlying ResultSet
     */
    public ResultSet getResultSet() {
        return _resultSet;
    }

    /**
     * Move the result set cursor to the next row, if any.
     * 
     * @return true if there is a next row, false otherwise.
     */
    public boolean next() throws SQLException {
        _column = 1;
        return _resultSet.next();
    }

    /**
     * Close the underlying ResultSet.
     */
    public void close() throws SQLException {
        if (_resultSet != null) {
            _resultSet.close();
        }
    }

    /**
     * Read the next column as a String.
     * 
     * @return String read after being trimmed, or null if value is SQL <code>NULL</code>.
     */
    public String readString() throws SQLException {
        String value = _resultSet.getString(_column++);

        if (value != null) {
            value = value.trim();
        }

        return value;
    }

    /**
     * Read the next column as an "int" java type.
     * 
     * @return The column value, or 0 if column value is SQL <code>NULL</code>.
     */
    public int readInt() throws SQLException {
        int value = _resultSet.getInt(_column++);
        return value;
    }

    /**
     * Read the next column as an Integer object.
     * 
     * @return Value read converted to an Integer, or null if value is SQL <code>NULL</code>.
     */
    public Integer readIntegerObject() throws SQLException {
        BigDecimal value = _resultSet.getBigDecimal(_column++);

        if (value == null) {
            return null;
        } else {
            return new Integer(value.intValue());
        }
    }

    /**
     * Read the next column as a Long object.
     * 
     * @return Value read converted to an Long, or null if value is SQL <code>NULL</code>.
     */
    public Long readLongObject() throws SQLException {
        BigDecimal value = _resultSet.getBigDecimal(_column++);

        if (value == null) {
            return null;
        } else {
            return new Long(value.longValue());
        }
    }

    /**
     * Read the next column as a Date object.
     * 
     * @return Value read converted to a Date, or null if value is SQL <code>NULL</code>.
     */
    public Date readDateObject() throws SQLException {
        Timestamp value = _resultSet.getTimestamp(_column++);

        if (value == null) {
            return null;
        } else {
            return new Date(value.getTime());
        }
    }

    /**
     * Read the next column as a boolean Java type.
     * 
     * @return Value boolean value of the column, or false if value is SQL <code>NULL</code>.
     */
    public boolean readBoolean() throws SQLException {
        boolean value = _resultSet.getBoolean(_column++);

        return value;
    }

}