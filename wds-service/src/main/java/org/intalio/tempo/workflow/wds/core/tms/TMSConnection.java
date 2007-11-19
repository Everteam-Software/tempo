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
package org.intalio.tempo.workflow.wds.core.tms;

import java.sql.*;

/**
 * This class represents a connection to TMS database.<br />
 * It is used for creating PIPA tasks.
 * 
 * @see TMSConnectionFactory
 */
public class TMSConnection implements TMSConnectionInterface {
    private Connection _jdbcConnection;
    private PreparedStatement _insertTaskStatement;
    private PreparedStatement _insertTaskUserOwnerStatement;
    private PreparedStatement _insertTaskRoleOwnerStatement;
    private PreparedStatement _deletePipaTaskStatement;
    private PreparedStatement _justCreatedTaskId;

    /**
     * Prepares all reusable statements.
     */
    private void prepareStatements() {
        try {
            _insertTaskStatement = prepare(
                "INSERT INTO tasks "
                + "(task_id, process_id, type_id, state_id, description, creation_date, form_url, "
                + " failure_code, failure_reason, input_xml,  output_xml, endpoint, namespace, init_soap_action) "
                + "VALUES (?, '', 1, 1, ?, ?, ?, NULL, NULL, NULL, NULL, ?, ?, ?)");
            
            _insertTaskUserOwnerStatement = prepare(
                "INSERT INTO task_user_owners (task_id, user_id) VALUES (?, ?)");
            
            _insertTaskRoleOwnerStatement = prepare(
                "INSERT INTO task_role_owners (task_id, role_id) VALUES (?, ?)");
            
            _deletePipaTaskStatement = prepare(
                    "DELETE FROM tasks WHERE type_id=1 AND form_url = ?");
                
            _justCreatedTaskId = prepare("SELECT id FROM tasks WHERE task_id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Instance constructor. 
     * <p/>
     * Use {@link TMSConnectionFactory} to obtain TMSConnectioninstances.
     */
    TMSConnection(Connection connection) {
        if (connection == null) throw new NullPointerException("connection");
        _jdbcConnection = connection;
        try {
            _jdbcConnection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        prepareStatements();
    }


    /**
     * Stores a PIPA task in TMS database.
     */
    public void storePipaTask(PipaTask task) {
        if (! task.isValid()) {
            throw new RuntimeException("Attempt to store an invalid pipa task:\n" + task.toString());
        }

        try {
            int i = 1;
            _insertTaskStatement.setString(i++, task.getId());
            _insertTaskStatement.setString(i++, task.getDescription());
            _insertTaskStatement.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
            _insertTaskStatement.setString(i++, task.getFormURL());
            _insertTaskStatement.setString(i++, task.getProcessEndpoint());
            _insertTaskStatement.setString(i++, task.getFormNamespace());
            _insertTaskStatement.setString(i++, task.getInitSoapAction());
            _insertTaskStatement.execute();

            _justCreatedTaskId.setString(1, task.getId());
            ResultSet rs = _justCreatedTaskId.executeQuery();
            if (rs.next()) {
                String justInsertedTaskId = rs.getString(1);

                for (String user : task.getUserOwners()) {
                    _insertTaskUserOwnerStatement.setString(1, justInsertedTaskId);
                    _insertTaskUserOwnerStatement.setString(2, user);
                    _insertTaskUserOwnerStatement.execute();
                }

                for (String role : task.getRoleOwners()) {
                    _insertTaskRoleOwnerStatement.setString(1, justInsertedTaskId);
                    _insertTaskRoleOwnerStatement.setString(2, role);
                    _insertTaskRoleOwnerStatement.execute();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes all PIPA tasks which use the specified form URL, from TMS database.
     *
     * @param formUrl The form URL. Tasks which use this form URL will be deleted by this method.
     */
    public void deletePipaTask(String formUrl) {
        try {
            _deletePipaTaskStatement.setString(1, formUrl);
            _deletePipaTaskStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getQParam(String value) {
        return value != null && !"".equals(value.trim()) ? value : "%";
    }
    
    private PreparedStatement prepare(String statement) throws SQLException {
        return _jdbcConnection.prepareStatement(statement);
    }
    /**
     * Commits the changes done to TMS database using this connection.
     */
    public void commit() {
        try {
            _jdbcConnection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes this connection.
     * <p/>
     * If {@link #commit()} was not called before this method, all changes to TMS database 
     * using this connection instance will be lost.
     */
    public void close() {
        try {
            _jdbcConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
