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

package org.intalio.tempo.workflow.tms.server.dao;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class JDBCTaskDAOConnection implements ITaskDAOConnection {
	
    private XmlTooling xmltooling = new XmlTooling();
	
    private static final Logger _logger = LoggerFactory.getLogger(JDBCTaskDAOConnection.class);

    private static final String _SELECT_TASK_FIELDS = " tasks.id, tasks.task_id, "
            + "tasks.process_id, task_types.code, task_states.code, tasks.description, "
            + "tasks.creation_date, tasks.form_url, tasks.failure_code, tasks.failure_reason, tasks.input_xml, "
            + "tasks.output_xml, tasks.endpoint, tasks.namespace, "
            + "tasks.init_soap_action, tasks.complete_soap_action, tasks.is_chained_before, "
            + "tasks.previous_task_id ";

    private static final String _INSERT_TASK_FIELDS = " tasks.task_id, "
            + "tasks.process_id, tasks.type_id, tasks.state_id, tasks.description, "
            + "tasks.creation_date, tasks.form_url, tasks.failure_code, tasks.failure_reason, tasks.input_xml, "
            + "tasks.output_xml, tasks.endpoint, tasks.namespace, "
            + "tasks.init_soap_action, tasks.complete_soap_action, tasks.is_chained_before, "
            + "tasks.previous_task_id ";

    private Connection _con;

    public JDBCTaskDAOConnection(Connection con) throws SQLException {
        if (con == null) throw new IllegalArgumentException("Connection is null");
        _con = con;
        _con.setAutoCommit(false);
    }

    public void commit() {
        try {
            if(_con.getAutoCommit()) _con.setAutoCommit(false);
            _con.commit();
            if(_logger.isDebugEnabled()) _logger.debug("committed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (_con != null) {
            try {
                _con.close();
                if(_logger.isDebugEnabled()) _logger.debug("Closed connection");
                _con = null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* @disabled(alex) for performance reasons
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        if (_con != null) {
            _logger.warn("SQL connection was not closed.");
            _con.close();
            _con = null;
        }
    }
    */

   

    private Task mapToTaskWithoutOwners(ResultSet resultSet) {
        try {
            Task resultTask;

            int i = 1;
            int internalId = resultSet.getInt(i++);
            String taskID = resultSet.getString(i++);
            String processID = resultSet.getString(i++);
            String type = resultSet.getString(i++);
            String state = resultSet.getString(i++);
            String description = resultSet.getString(i++);
            Date creationDate = resultSet.getTimestamp(i++);
            URI formURL = new URI(resultSet.getString(i++));
            String failureCode = resultSet.getString(i++);
            String failureReason = resultSet.getString(i++);
            Document inputXML = xmltooling.parseXML(resultSet.getString(i++));
            Document outputXML = xmltooling.parseXML(resultSet.getString(i++));
            String processEndpoint = resultSet.getString(i++);
            String namespace = resultSet.getString(i++);
            String initSOAPAction = resultSet.getString(i++);
            String completeSOAPAction = resultSet.getString(i++);
            String isChainedBefore = resultSet.getString(i++);
            String previousTaskID = resultSet.getString(i++);

            if (type.equalsIgnoreCase("init")) {
                PIPATask pipaTask = new PIPATask(taskID, formURL, new URI(processEndpoint), new URI(namespace),
                        initSOAPAction);
                resultTask = pipaTask;
            } else if (type.equalsIgnoreCase("activity")) {
                PATask paTask = new PATask(taskID, formURL, processID, completeSOAPAction, inputXML);
                if (outputXML != null) {
                    paTask.setOutput(outputXML);
                }
                TaskState stateEnum = TaskState.valueOf(state.toUpperCase());
                paTask.setState(stateEnum);
                if (stateEnum.equals(TaskState.FAILED)) {
                    paTask.setFailureCode(failureCode);
                    if (failureReason != null) {
                        paTask.setFailureReason(failureReason);
                    }
                }

                if ("1".equals(isChainedBefore)) {
                    paTask.setPreviousTaskID(previousTaskID);
                    paTask.setChainedBefore(true);
                }

                resultTask = paTask;
            } else if (type.equalsIgnoreCase("notification")) {
                Notification notification = new Notification(taskID, formURL, inputXML);

                // FIXME: this is a copy/paste from above
                TaskState stateEnum = TaskState.valueOf(state.toUpperCase());
                notification.setState(stateEnum);
                if (stateEnum.equals(TaskState.FAILED)) {
                    notification.setFailureCode(failureCode);
                    if (failureReason != null) {
                        notification.setFailureReason(failureReason);
                    }
                }

                resultTask = notification;
            } else {
                throw new RuntimeException("TMS inconsistency: unknown task type: '" + type + "'");
            }
            resultTask.setInternalID(internalId);
            resultTask.setDescription(description == null ? "" : description);
            resultTask.setCreationDate(creationDate);

            if(_logger.isDebugEnabled()) _logger.debug("Workflow Task " + resultTask.getID() + " has been read from TMS DB");
            return resultTask;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Task fetchTask(String taskID) {
        try {
            Task resultTask;
            {
                PreparedStatement select;
                select = _con.prepareStatement("SELECT " + _SELECT_TASK_FIELDS
                        + "FROM tasks, task_types, task_states WHERE tasks.type_id = task_types.id "
                        + "AND tasks.state_id = task_states.id AND tasks.task_id = ?");
                select.setString(1, taskID);
                if(_logger.isDebugEnabled()) _logger.debug("About to retrieve Workflow Task with ID " + taskID);
                ResultSet taskResultSet = select.executeQuery();
                try {
                    if (! taskResultSet.next()) {
                        if(_logger.isDebugEnabled()) _logger.debug("No such Workflow Task!");
                        return null;
                    }
                    resultTask = mapToTaskWithoutOwners(taskResultSet);
                } finally {
                    close(taskResultSet);
                }
                close(select);
            }

            // userOwners
            {
                PreparedStatement select = _con.prepareStatement("SELECT task_user_owners.user_id "
                        + "FROM tasks, task_user_owners WHERE tasks.id = ? "
                        + "AND task_user_owners.task_id = tasks.id");
                select.setInt(1, resultTask.getInternalId());
                if(_logger.isDebugEnabled()) _logger.debug("About to retrieve user owners for task " + taskID);
                ResultSet userOwnerResultSet = select.executeQuery();
                while (userOwnerResultSet.next()) {
                    String userID = userOwnerResultSet.getString(1);
                    if(_logger.isDebugEnabled()) _logger.debug("User Owner : " + userID);
                    resultTask.getUserOwners().add(userID);
                }
                close(userOwnerResultSet);
            }
            
            // roleOwners
            {
                PreparedStatement select = _con.prepareStatement("SELECT task_role_owners.role_id "
                        + "FROM tasks, task_role_owners WHERE tasks.id = ? "
                        + "AND task_role_owners.task_id = tasks.id");
                select.setInt(1, resultTask.getInternalId());
                if(_logger.isDebugEnabled()) _logger.debug("About to retrieve role owners for task " + taskID);
                ResultSet roleOwnerResultSet = select.executeQuery();
                while (roleOwnerResultSet.next()) {
                    String roleID = roleOwnerResultSet.getString(1);
                    if(_logger.isDebugEnabled()) _logger.debug("Role Owner : " + roleID);
                    resultTask.getRoleOwners().add(roleID);
                }
                close(roleOwnerResultSet);
            }
            
            // user actions
            {
                PreparedStatement select = _con.prepareStatement(
                        "SELECT action_id, user_id FROM task_user_actions WHERE task_id = ? ");
                select.setInt(1, resultTask.getInternalId());
                ResultSet result = select.executeQuery();
                while (result.next()) {
                    String action = result.getString(1);
                    String user = result.getString(2);
                    resultTask.authorizeActionForUser(action, user);
                }
                close(result);
                close(select);
            }

            // role actions
            {
                PreparedStatement select = _con.prepareStatement(
                        "SELECT action_id, role_id FROM task_role_actions WHERE task_id = ? ");
                select.setInt(1, resultTask.getInternalId());
                ResultSet result = select.executeQuery();
                try {
                    while (result.next()) {
                        String action = result.getString(1);
                        String role = result.getString(2);
                        resultTask.authorizeActionForRole(action, role);
                    }
                } finally {
                    close(result);
                }
                close(select);
            }

            // attachments
            if (resultTask instanceof PATask) {
                PreparedStatement select = _con.prepareStatement("SELECT "
                        + "ta.payload_url, ta.file_name, ta.mime_type, ta.widget, ta.creation_date, ta.title, ta.description "
                        + "FROM tasks t, task_attachments ta " + "WHERE ta.task_id = t.id AND t.task_id = ?");
                select.setString(1, taskID);
                if(_logger.isDebugEnabled()) _logger.debug("About to retrieve attachments for task " + taskID);
                ResultSet rs = select.executeQuery();
                try {
                    while (rs.next()) {
                        int i = 1;
                        String payloadURLStr = rs.getString(i++);
                        String fileName = rs.getString(i++);
                        String mimeType = rs.getString(i++);
                        String widget = rs.getString(i++);
                        java.sql.Timestamp creationDate = rs.getTimestamp(i++);
                        String title = rs.getString(i++);
                        String description = rs.getString(i++);
    
                        AttachmentMetadata metadata = new AttachmentMetadata();
                        metadata.setFileName(fileName);
                        metadata.setMimeType(mimeType);
                        metadata.setWidget(widget);
                        if (creationDate != null) metadata.setCreationDate(new Date(creationDate.getTime()));
                        if (title != null) metadata.setTitle(title);
                        if (description != null) metadata.setDescription(description);
    
                        Attachment attachment = new Attachment(metadata, new URL(payloadURLStr));
                        if(_logger.isDebugEnabled()) _logger.debug("Attachment : " + attachment.getPayloadURL());
                        ((PATask) resultTask).addAttachment(attachment);
                    }
                } finally {
                    close(rs);
                }
                close(select);
            }
            return resultTask;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Task[] fetchAllAvailableTasks(UserRoles user) {
        try {
            List<Task> resultTasks = new ArrayList<Task>();
            Set<String> taskIDs = new HashSet<String>();

            PreparedStatement selectByUserStatement = _con.prepareStatement("SELECT tasks.task_id "
                    + "FROM tasks, task_user_owners "
                    + "WHERE tasks.id = task_user_owners.task_id AND task_user_owners.user_id = ? ");
            selectByUserStatement.setString(1, user.getUserID());

            if(_logger.isDebugEnabled()) _logger.debug("About to fetch all Workflow Tasks for user owner : " + user.getUserID());
            ResultSet resultSet = selectByUserStatement.executeQuery();
            while (resultSet.next()) {
                String taskID = resultSet.getString(1);
                taskIDs.add(taskID);
            }
            if(_logger.isDebugEnabled()) _logger.debug("Workflow Tasks : " + taskIDs);
            selectByUserStatement.close();

            for (String role : user.getAssignedRoles()) {
                PreparedStatement selectByRoleStatement = _con.prepareStatement("SELECT tasks.task_id "
                        + "FROM tasks, task_role_owners "
                        + "WHERE tasks.id = task_role_owners.task_id AND task_role_owners.role_id = ? ");
                selectByRoleStatement.setString(1, role);

                if(_logger.isDebugEnabled()) _logger.debug("About to fetch all Workflow Tasks for role owner : " + role);
                ResultSet roleResultSet = selectByRoleStatement.executeQuery();
                while (roleResultSet.next()) {
                    String taskID = roleResultSet.getString(1);
                    taskIDs.add(taskID);
                }

                if(_logger.isDebugEnabled()) _logger.debug("Workflow Tasks now : " + taskIDs);
                selectByRoleStatement.close();
            }

            for (String taskID : taskIDs) {
                Task task = fetchTask(taskID);
                resultTasks.add(task);
            }

            return resultTasks.toArray(new Task[] {});
        } catch (SQLException e) {
            _logger.error("Error while retrieving task list",e);
            throw new RuntimeException(e);
        }
    }

    public Task fetchTaskIfExists(String taskID) {
        return fetchTask(taskID);
    }

    public void updateTask(Task task) {
        if (!deleteTask(task.getInternalId(), task.getID())) {
            throw new RuntimeException("Attempt to update a nonexistent task (ID: '" + task.getID() + "'");
        }
        try {
            createTask(task);
        } catch (TaskIDConflictException e) {
            throw new RuntimeException(e);
        }
    }

    private String selectSingleValue(String query, String[] parameters) {
        try {
            PreparedStatement selectStatement = _con.prepareStatement(query);
            for (int i = 0; i < parameters.length; ++i) {
                selectStatement.setString(i + 1, parameters[i]);
            }
            ResultSet results = selectStatement.executeQuery();
            if (results.next()) {
                String value = results.getString(1);
                selectStatement.close();
                return value;
            } else {
                throw new RuntimeException("Expected a result for query: '" + query
                        + "', but an empty set was returned");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void insertAttachments(int taskID, Collection<Attachment> attachments) {
        try {
            PreparedStatement insertAttachmentStatement = _con
                    .prepareStatement("INSERT INTO "
                            + "task_attachments (task_id, payload_url, file_name, mime_type, widget, creation_date, title, description)"
                            + " VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            insertAttachmentStatement.setInt(1, taskID);

            for (Attachment attachment : attachments) {
                int i = 2;
                insertAttachmentStatement.setString(i++, attachment.getPayloadURL().toString()); // TODO: add props
                AttachmentMetadata metadata = attachment.getMetadata();
                insertAttachmentStatement.setString(i++, metadata.getFileName());
                insertAttachmentStatement.setString(i++, metadata.getMimeType());
                insertAttachmentStatement.setString(i++, metadata.getWidget());
                insertAttachmentStatement.setTimestamp(i++, new Timestamp(metadata.getCreationDate().getTime()));
                insertAttachmentStatement.setString(i++, metadata.getTitle());
                insertAttachmentStatement.setString(i++, metadata.getDescription());

                insertAttachmentStatement.executeUpdate();
                if(_logger.isDebugEnabled()) 
                    _logger.debug(" attachment filename=" + metadata.getFileName()
                        + " title=" + metadata.getTitle()
                        + " widget=" + metadata.getWidget());
            }

            insertAttachmentStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    private boolean removeAttachment(String internalTaskID, String attachmentPayloadURL) {
//        try {
//            PreparedStatement deleteStatement = _sqlConnection.prepareStatement("DELETE FROM task_attachments "
//                    + "WHERE task_id = ? AND payload_url = ?");
//            deleteStatement.setString(1, internalTaskID);
//            deleteStatement.setString(2, attachmentPayloadURL);
//
//            return deleteStatement.executeUpdate() > 0;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    // FIXME: this method is hardwired on task types (PATask, PIPATask, Notification)
    // It should use task traits (ITaskWithStatus, ITaskWithAttachments) instead.
    public void createTask(Task task) throws TaskIDConflictException {
        try {
            if (task instanceof PATask) {
                PATask paTask = (PATask) task;

                String count = selectSingleValue("SELECT COUNT(*) FROM tasks WHERE task_id = ?",
                        new String[] { paTask.getID() });
                if (! count.equals("0")) {
                    throw new TaskIDConflictException("Task with ID '" + paTask.getID() + "' already exists");
                }

                String typeID = selectSingleValue("SELECT id FROM task_types WHERE code = ?",
                        new String[] { "activity" });
                String stateID = selectSingleValue("SELECT id FROM task_states WHERE code = ?",
                        new String[] { paTask.getState().toString().toLowerCase() });

                PreparedStatement createTaskStatement = _con.prepareStatement("INSERT INTO tasks ("
                        + _INSERT_TASK_FIELDS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL, ?, ?, ?)");
                int i = 1;
                createTaskStatement.setString(i++, paTask.getID());
                createTaskStatement.setString(i++, paTask.getProcessID());
                createTaskStatement.setString(i++, typeID);
                createTaskStatement.setString(i++, stateID);
                createTaskStatement.setString(i++, paTask.getDescription());
                createTaskStatement.setTimestamp(i++, new Timestamp(paTask.getCreationDate().getTime()));
                createTaskStatement.setString(i++, paTask.getFormURL().toString());
                if (paTask.getState().equals(TaskState.FAILED)) {
                    createTaskStatement.setString(i++, paTask.getFailureCode());
                    createTaskStatement.setString(i++, paTask.getFailureReason());
                } else {
                    createTaskStatement.setString(i++, null);
                    createTaskStatement.setString(i++, null);
                }
                createTaskStatement.setString(i++, xmltooling.serializeXML(paTask.getInput()));
                createTaskStatement.setString(i++, xmltooling.serializeXML(paTask.getOutput()));
                
                createTaskStatement.setString(i++, paTask.getCompleteSOAPAction());
                createTaskStatement.setString(i++, paTask.isChainedBefore() ? "1" : "0");
                createTaskStatement.setString(i++, paTask.getPreviousTaskID());

                if(_logger.isDebugEnabled()) _logger.debug("Workflow PA Task " + paTask.getID() + " is about to be registered in TMS DB");
                createTaskStatement.executeUpdate();
                createTaskStatement.close();

                String id = selectSingleValue("SELECT id FROM tasks WHERE task_id = ?",
                        new String[] {paTask.getID()});
                int internalTaskID = Integer.parseInt(id);

                if(_logger.isDebugEnabled()) _logger.debug("Workflow PA Task " + paTask.getID() + " registered with ID=" + internalTaskID);
                if (!task.getUserOwners().isEmpty()) {
                    insertUserOwner(task, internalTaskID);
                }
                
                if (!task.getRoleOwners().isEmpty()) {
                    insertRoleOwner(task, internalTaskID);
                }

                for (String action : task.getAuthorizedActions()) {
                    insertActionUsers(task, internalTaskID, action);
                    insertActionRoles(task, internalTaskID, action);
                }
                
                if (! paTask.getAttachments().isEmpty()) {
                    insertAttachments(internalTaskID, paTask.getAttachments());
                }
            } else if (task instanceof Notification) {
                Notification notification = (Notification) task;

                String count = selectSingleValue("SELECT COUNT(*) FROM tasks WHERE task_id = ?",
                        new String[] { notification.getID() });
                if (! count.equals("0")) {
                    throw new TaskIDConflictException("Task with ID '" + notification.getID() + "' already exists");
                }

                String typeID = selectSingleValue("SELECT id FROM task_types WHERE code = ?",
                        new String[] { "notification" });
                String stateID = selectSingleValue("SELECT id FROM task_states WHERE code = ?",
                        new String[] { notification.getState().toString().toLowerCase() });

                PreparedStatement createTaskStatement = _con.prepareStatement("INSERT INTO tasks ("
                        + _INSERT_TASK_FIELDS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL, ?, ?, ?)");
                int i = 1;
                createTaskStatement.setString(i++, notification.getID());
                createTaskStatement.setString(i++, null);
                createTaskStatement.setString(i++, typeID);
                createTaskStatement.setString(i++, stateID);
                createTaskStatement.setString(i++, notification.getDescription());
                createTaskStatement.setTimestamp(i++, new Timestamp(notification.getCreationDate().getTime()));
                createTaskStatement.setString(i++, notification.getFormURL().toString());
                if (notification.getState().equals(TaskState.FAILED)) {
                    createTaskStatement.setString(i++, notification.getFailureCode());
                    createTaskStatement.setString(i++, notification.getFailureReason());
                } else {
                    createTaskStatement.setString(i++, null);
                    createTaskStatement.setString(i++, null);
                }
                createTaskStatement.setString(i++, xmltooling.serializeXML(notification.getInput()));
                createTaskStatement.setString(i++, null);
                createTaskStatement.setString(i++, null);
                createTaskStatement.setString(i++, null);
                createTaskStatement.setString(i++, null);

                if(_logger.isDebugEnabled()) _logger.debug("Workflow Notification Task " + notification.getID() + " is about to be registered in TMS DB");
                createTaskStatement.executeUpdate();
                createTaskStatement.close();

                String internalTaskID = selectSingleValue("SELECT id FROM tasks WHERE task_id = ?",
                        new String[] {notification.getID()});

                if(_logger.isDebugEnabled()) _logger.debug("Workflow Notification Task " + notification.getID() + " registered with ID=" + internalTaskID);
                if (! task.getUserOwners().isEmpty()) {
                    PreparedStatement insertTaskUserOwnerStatement = _con.prepareStatement("INSERT INTO "
                            + "task_user_owners (task_id, user_id) VALUES (?, ?)");
                    insertTaskUserOwnerStatement.setString(1, internalTaskID);
                    for (String userOwnerID : task.getUserOwners()) {
                        insertTaskUserOwnerStatement.setString(2, userOwnerID);
                        insertTaskUserOwnerStatement.executeUpdate();
                        if(_logger.isDebugEnabled()) _logger.debug(" and User Owner : " + userOwnerID);
                    }
                    insertTaskUserOwnerStatement.close();
                }
                if (! task.getRoleOwners().isEmpty()) {
                    PreparedStatement insertTaskRoleOwnerStatement = _con.prepareStatement("INSERT INTO "
                            + "task_role_owners (task_id, role_id) VALUES (?, ?)");
                    insertTaskRoleOwnerStatement.setString(1, internalTaskID);
                    for (String roleOwnerID : task.getRoleOwners()) {
                        insertTaskRoleOwnerStatement.setString(2, roleOwnerID);
                        insertTaskRoleOwnerStatement.executeUpdate();
                        if(_logger.isDebugEnabled()) _logger.debug(" and Role Owner : " + roleOwnerID);
                    }
                    insertTaskRoleOwnerStatement.close();
                }
            } else {
                throw new RuntimeException("Storing tasks of type " + task.getClass().getSimpleName() 
                        + " is not supported");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertRoleOwner(Task task, int internalTaskID) throws SQLException {
        PreparedStatement insertTaskRoleOwnerStatement = _con.prepareStatement("INSERT INTO "
                + "task_role_owners (task_id, role_id) VALUES (?, ?)");
        insertTaskRoleOwnerStatement.setInt(1, internalTaskID);
        for (String roleOwnerID : task.getRoleOwners()) {
            insertTaskRoleOwnerStatement.setString(2, roleOwnerID);
            insertTaskRoleOwnerStatement.executeUpdate();
            if(_logger.isDebugEnabled()) _logger.debug(" and Role Owner : " + roleOwnerID);
        }
        insertTaskRoleOwnerStatement.close();
    }

    private void insertUserOwner(Task task, int internalTaskID) throws SQLException {
        PreparedStatement insertTaskUserOwnerStatement = _con.prepareStatement("INSERT INTO "
                + "task_user_owners (task_id, user_id) VALUES (?, ?)");
        insertTaskUserOwnerStatement.setInt(1, internalTaskID);
        for (String userOwnerID : task.getUserOwners()) {
            insertTaskUserOwnerStatement.setString(2, userOwnerID);
            insertTaskUserOwnerStatement.executeUpdate();
            if(_logger.isDebugEnabled()) _logger.debug(" and User Owner : " + userOwnerID);
        }
        insertTaskUserOwnerStatement.close();
    }

    private void insertActionUsers(Task task, int internalTaskID, String action) throws SQLException {
        PreparedStatement insert = _con.prepareStatement("INSERT INTO "
                + "task_user_actions (task_id, action_id, user_id) VALUES (?, ?, ?)");
        insert.setInt(1, internalTaskID);
        for (String user : task.getAuthorizedUsers(action)) {
            insert.setString(2, action);
            insert.setString(3, user);
            insert.executeUpdate();
            if(_logger.isDebugEnabled()) _logger.debug(" and user "+user+" for action"+action);
        }
        insert.close();
    }

    private void insertActionRoles(Task task, int internalTaskID, String action) throws SQLException {
        PreparedStatement insert = _con.prepareStatement("INSERT INTO "
                + "task_role_actions (task_id, action_id, role_id) VALUES (?, ?, ?)");
        insert.setInt(1, internalTaskID);
        for (String role : task.getAuthorizedRoles(action)) {
            insert.setString(2, action);
            insert.setString(3, role);
            insert.executeUpdate();
            if(_logger.isDebugEnabled()) _logger.debug(" and role "+role+" for action"+action);
        }
        insert.close();
    }

    public boolean deleteTask(int internalTaskId, String taskID) {
        try {
            if(_logger.isDebugEnabled()) _logger.debug("Attempt to delete Workflow Task " + taskID);
            
            // delete role actions
            {
                PreparedStatement delete = _con.prepareStatement("DELETE FROM task_user_actions WHERE task_id = ?");
                delete.setInt(1, internalTaskId);
                delete.executeUpdate();
                delete.close();
            }

            // delete role actions
            {
                PreparedStatement delete = _con.prepareStatement("DELETE FROM task_role_actions WHERE task_id = ?");
                delete.setInt(1, internalTaskId);
                delete.executeUpdate();
                delete.close();
            }
            
            // task itself
            {
                PreparedStatement delete = _con.prepareStatement("DELETE FROM tasks WHERE tasks.task_id = ?");
                delete.setString(1, taskID);
                int results = delete.executeUpdate();
                delete.close();
                return results > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Throwable t2) {
                // ignore
            }
        }
    }

    static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Throwable t2) {
                // ignore
            }
        }
    }
}
