package org.intalio.tempo.workflow.tmsb4p.server.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.taskb4p.Attachment;
import org.intalio.tempo.workflow.taskb4p.AttachmentAccessType;
import org.intalio.tempo.workflow.taskb4p.AttachmentInfo;
import org.intalio.tempo.workflow.taskb4p.Comment;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.taskb4p.TaskStatus;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;

public class JPATaskDaoConnection extends AbstractJPAConnection implements
		ITaskDAOConnection {

	public JPATaskDaoConnection(EntityManager createEntityManager) {
		super(createEntityManager);
	}

	public void createTask(Task task) throws TaskIDConflictException {
        checkTransactionIsActive();
        task.setCreatedOn(new Date(System.currentTimeMillis()));
        entityManager.persist(task);
	}

	public void addAttachment(String taskId, String attachmentName,
			AttachmentAccessType accessType, String contentType, String attachedBy, String value) {
		checkTransactionIsActive();
		
		Task task = new Task();
		task.setId(taskId);
		
		Attachment attachment = new Attachment();
		attachment.setValue(value);
		
		AttachmentInfo attachmentInfo = new AttachmentInfo();
		attachmentInfo.setAccessType(accessType);
		attachmentInfo.setAttachedAt(new Date(System.currentTimeMillis()));
		attachmentInfo.setAttachedBy(attachedBy);
		attachmentInfo.setContentType(contentType);
		
		attachment.setTask(task);
		attachment.setAttachmentInfo(attachmentInfo);
		
		entityManager.persist(attachment);		
	}

	public void addComment(String taskId, String addedBy, String text) {
		checkTransactionIsActive();

		Task task = new Task();
		task.setId(taskId);

		Comment comment = new Comment();
		comment.setAddedAt(new Date(System.currentTimeMillis()));
		comment.setText(text);
		comment.setAddedBy(addedBy);

		entityManager.persist(comment);
	}

	public boolean deleteAttachments(String taskId, String attachmentName) {
        Query query = entityManager.createNamedQuery(Attachment.DELETE_WITH_NAME);
        query.setParameter(1, taskId);
        query.setParameter(2, attachmentName);
        
        checkTransactionIsActive();
        int deleted = query.executeUpdate();
        
		return true;
	}

	public boolean deleteTask(String taskId) throws UnavailableTaskException {
		Task task = new Task();
		task.setId(taskId);
		checkTransactionIsActive();
		Task newTask = entityManager.merge(task);
		entityManager.remove(newTask);
		
		return true;
	}

	public Task fetchTaskIfExists(String taskId)
			throws UnavailableTaskException {
        Query query = entityManager.createNamedQuery(Task.FIND_BY_ID);
        query.setParameter(1, taskId);
        
        // set the value for hasAttachments and hasComments
        Task task = (Task)query.getSingleResult();
        task.setHasAttachments(this.hasAttachment(taskId));
        task.setHasComments(this.hasComment(taskId));
        
        return task; 		
	}

	public List<AttachmentInfo> getAttachmentInfos(String taskId) {
		Query query = entityManager
				.createNamedQuery(Attachment.QUERY_ALL_INFOS);
		query.setParameter(1, taskId);

		List<AttachmentInfo> infos = query.getResultList();
		return infos;
	}

	public List<Attachment> getAttachments(String taskId) {
		Query query = entityManager
				.createNamedQuery(Attachment.QUERY_ALL_INFOS);
		query.setParameter(1, taskId);

		List<Attachment> attachments = query.getResultList();
		return attachments;
	}

	public List<Comment> getComments(String taskId) {
        Query  query = entityManager.createNamedQuery(Comment.QUERY_ALL_COMMENTS);
        query.setParameter(1, taskId);
        List<Comment> comments = query.getResultList();
        
		return comments;
	}

	public void updateTask(Task task) {
		checkTransactionIsActive();
		entityManager.merge(task);
	}

	public void updateTaskStatus(String taskId, TaskStatus status)
			throws UnavailableTaskException {
		checkTransactionIsActive();
		Task task = this.fetchTaskIfExists(taskId);
		
		task.setStatus(status);
	}
	
	private boolean hasAttachment(String taskId) {
        String queryString = "select count(m) from Attachment m where m.task.id=:taskId";
        Query hasAttachmentQury = this.entityManager.createQuery(queryString);
        hasAttachmentQury.setParameter("taskId", taskId);

        Long count = (Long)hasAttachmentQury.getSingleResult();
        return (count.longValue() > 0);
	}
	
	private boolean hasComment(String taskId) {
        String queryString = "select count(m) from Comment m where m.task.id=:taskId";
        Query hasCommentQury = this.entityManager.createQuery(queryString);
        hasCommentQury.setParameter("taskId", taskId);

        Long count = (Long)hasCommentQury.getSingleResult();
        return (count.longValue() > 0);
	}
	

	
	public List<Task>  query(UserRoles ur, String selectClause, String whereClause, String orderByClause, int maxTasks, int taskIndexOffset){
	    String queryString = "select " + selectClause + " from tempob4p_task where " + whereClause + " " + orderByClause;
	    _logger.info(queryString);
	    
	    Query q = this.entityManager.createQuery(queryString);
	    q.setMaxResults(maxTasks);
	    List r = q.getResultList();
//	    
//	    ArrayList<Task> ret = new ArrayList<Task>();
//	    int m = 0;
//	    _logger.info("result size = " + r.size());
//	    while (m < maxTasks &&  m < r.size()){
//	        Task t = (Task)r.get(m);
//	        _logger.info("task["+m+"]:"+t);
//	        m++;	    
//	    }
	    
	    return r;    
	}

    public List<Task> getMyTasks(UserRoles ur, String taskType, String genericHumanRole, String workQueue, Enum[] statusList, String whereClause,
                    String createdOnClause, int maxTasks) {
        // TODO Auto-generated method stub
        return null;
    }

   

}
