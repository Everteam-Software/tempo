package org.intalio.tempo.workflow.tmsb4p.server.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.taskb4p.Attachment;
import org.intalio.tempo.workflow.taskb4p.AttachmentAccessType;
import org.intalio.tempo.workflow.taskb4p.AttachmentInfo;
import org.intalio.tempo.workflow.taskb4p.Comment;
import org.intalio.tempo.workflow.taskb4p.TaskStatus;
import org.intalio.tempo.workflow.tms.B4PPersistException;
import org.intalio.tempo.workflow.tms.InvalidQueryException;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTaskDAOConnection implements org.intalio.tempo.workflow.tmsb4p.server.dao.ITaskDAOConnection{
    private static final Logger _logger = LoggerFactory.getLogger(SimpleTaskDAOConnection.class);

    private boolean _closed = false;

    private Map<String, org.intalio.tempo.workflow.taskb4p.Task> _tasks;

    public SimpleTaskDAOConnection(Map<String, org.intalio.tempo.workflow.taskb4p.Task> tasks) {
      if (tasks == null) {
        throw new RequiredArgumentException("tasks");
      }
      _tasks = tasks;

      _logger.debug("Opened a simple DAO connection.");
    }

    public void addAttachment(String taskId, String attachmentName, AttachmentAccessType accessType, String contentType, String attachedBy, String value) {
        // TODO Auto-generated method stub
        
    }

    public void addComment(String taskId, String addedBy, String text) {
        // TODO Auto-generated method stub
        
    }

    public void addUserOrGroups(String taskId, String[] usersOrGroups, boolean isUser, GenericRoleType role) throws UnavailableTaskException,
                    B4PPersistException {
        // TODO Auto-generated method stub
        
    }

    public void close() {
        // TODO Auto-generated method stub
        
    }

    public void commit() {
        // TODO Auto-generated method stub
        
    }

    public void createTask(org.intalio.tempo.workflow.taskb4p.Task task) throws TaskIDConflictException {
        // TODO Auto-generated method stub
        
    }

    public boolean deleteAttachments(String taskId, String attachmentName) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean deleteTask(String taskId) throws UnavailableTaskException {
        // TODO Auto-generated method stub
        return false;
    }

    public org.intalio.tempo.workflow.taskb4p.Task fetchTaskIfExists(String taskID) throws UnavailableTaskException {
        return _tasks.get(taskID);
    }

    public List<AttachmentInfo> getAttachmentInfos(String taskId) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Attachment> getAttachments(String taskId, String attachmentName) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Comment> getComments(String taskId) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<org.intalio.tempo.workflow.taskb4p.Task> getMyTasks(UserRoles ur, String taskType, String genericHumanRole, String workQueue,
                    List<TaskStatus> statusList, String whereClause, String createdOnClause, int maxTasks) throws InvalidQueryException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<org.intalio.tempo.workflow.taskb4p.Task> getTasksWithName(String taskName) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isRoleMember(String taskId, UserRoles ur, GenericRoleType role) {
        // TODO Auto-generated method stub
        return false;
    }

    public Collection<Map<String, Object>> query(UserRoles ur, String selectClause, String whereClause, String orderByClause, int maxTasks, int taskIndexOffset)
                    throws InvalidQueryException {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeUserOrGroups(String taskId, String[] usersOrGroups, GenericRoleType role) throws UnavailableTaskException {
        // TODO Auto-generated method stub
        
    }

    public void updateTask(org.intalio.tempo.workflow.taskb4p.Task task) {
        // TODO Auto-generated method stub
        
    }

    public void updateTaskRole(String taskId, GenericRoleType role, List<String> value, String orgType) throws UnavailableTaskException {
        // TODO Auto-generated method stub
        
    }

    public void updateTaskStatus(String taskID, TaskStatus status) throws UnavailableTaskException {
        // TODO Auto-generated method stub
        
    }
    

}
