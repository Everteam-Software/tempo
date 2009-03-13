package org.intalio.tempo.workflow.tmsb4p.server.dao;

import java.util.List;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.taskb4p.Attachment;
import org.intalio.tempo.workflow.taskb4p.AttachmentAccessType;
import org.intalio.tempo.workflow.taskb4p.AttachmentInfo;
import org.intalio.tempo.workflow.taskb4p.Comment;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.taskb4p.TaskStatus;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;

public interface ITaskDAOConnection {
	public void commit();

	public void close();

	public void createTask(Task task) throws TaskIDConflictException;

	public Task fetchTaskIfExists(String taskID)
			throws UnavailableTaskException;

	public void updateTaskStatus(String taskID, TaskStatus status)
			throws UnavailableTaskException;

	public boolean deleteTask(String taskId) throws UnavailableTaskException;
	
	public void updateTask(Task task);

	public void addAttachment(String taskId, String attachmentName,
			AttachmentAccessType accessType, String contentType, String attachedBy, String value);
	
	public List<AttachmentInfo> getAttachmentInfos(String taskId);
	
	public List<Attachment> getAttachments(String taskId);
	
	public boolean deleteAttachments(String taskId, String attachmentName);
	
	public void addComment(String taskId, String addedBy, String text);
	
	public List<Comment> getComments(String taskId);
	
	public List<Task>  getMyTasks(UserRoles ur, String taskType, String genericHumanRole, String workQueue, List<TaskStatus> statusList, String whereClause, String createdOnClause, int maxTasks);
	
	public List<Task>  query(UserRoles ur, String selectClause, String whereClause, String orderByClause, int maxTasks, int taskIndexOffset);
	
}
