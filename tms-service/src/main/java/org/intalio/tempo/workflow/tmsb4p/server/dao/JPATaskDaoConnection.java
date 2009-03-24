package org.intalio.tempo.workflow.tmsb4p.server.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.taskb4p.Attachment;
import org.intalio.tempo.workflow.taskb4p.AttachmentAccessType;
import org.intalio.tempo.workflow.taskb4p.AttachmentInfo;
import org.intalio.tempo.workflow.taskb4p.Comment;
import org.intalio.tempo.workflow.taskb4p.OrganizationalEntity;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.taskb4p.TaskStatus;
import org.intalio.tempo.workflow.taskb4p.TaskType;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;

//import com.ibm.db2.jcc.a.l;

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
        attachmentInfo.setName(attachmentName);
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
        
        Task task = (Task)query.getSingleResult();
        return task;         
    }

    public List<AttachmentInfo> getAttachmentInfos(String taskId) {
        Query query = entityManager
                .createNamedQuery(Attachment.QUERY_ALL_INFOS);
        query.setParameter(1, taskId);

        List<AttachmentInfo> infos = query.getResultList();
        return infos;
    }

    public List<Attachment> getAttachments(String taskId, String attachmentName) {
        Query query = entityManager
                .createNamedQuery(Attachment.QUERY_ALL_ATTACHMENTS);
        query.setParameter(1, taskId);
        query.setParameter(2, attachmentName);

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
    
    public List<Task> getTasksWithName(String taskName) {
        Query query = entityManager.createNamedQuery(Task.FIND_BY_NAME);
        query.setParameter(1, taskName);

        List<Task> tasks = query.getResultList();
        return tasks;
    }

    
    public List<Task>  query(UserRoles ur, String selectClause, String whereClause, String orderByClause, int maxTasks, int taskIndexOffset){
        String queryString = "select " + selectClause + " from tempob4p_task where " + whereClause + " " + orderByClause;
        _logger.info(queryString);
        
        Query q = this.entityManager.createQuery(queryString);
        q.setMaxResults(maxTasks);
        List r = q.getResultList();
//        
//        ArrayList<Task> ret = new ArrayList<Task>();
//        int m = 0;
//        _logger.info("result size = " + r.size());
//        while (m < maxTasks &&  m < r.size()){
//            Task t = (Task)r.get(m);
//            _logger.info("task["+m+"]:"+t);
//            m++;        
//        }
        
        return r;    
    }

    public List<Task> getMyTasks(UserRoles ur, String taskType, String genericHumanRole, String workQueue, List<TaskStatus> statusList, String whereClause,
                    String createdOnClause, int maxTasks) {
        SQLStatement statement = new SQLStatement();
        System.out.println("====>getMyTasks");
        // for the task self
        statement.addSelectClause("t");
        statement.addFromClause("Task t");

        // for the generic roles
        List<String> queryGenRoleTypes = this.parseString(genericHumanRole, ",");
        if (queryGenRoleTypes != null) {
        	 System.out.println("====>1");
            // check the work queue first.
            if (workQueue != null) {
                List<String> resultRoles = new ArrayList<String>();
                System.out.println("====>2");
                // check whether the user belongs the work queue, if not, empty
                // result returned.
                AuthIdentifierSet urRoles = ur.getAssignedRoles();
                List<String> inputRoles = parseString(workQueue, ",");

                for (Iterator<String> iterator = inputRoles.iterator(); iterator
                        .hasNext();) {
                    String role = iterator.next();
                    if (urRoles.contains(role)) {
                        resultRoles.add(role);
                    }
                }

                // if no workqueue specified, should retrieve personal tasks
//                if (resultRoles.isEmpty()) {
//                	System.out.println("====>3");
//                    return Collections.EMPTY_LIST;
//                }
                
 //               populateSQLWithRole(queryGenRoleTypes, resultRoles, statement);
            } else {
            	System.out.println("====>4");
                populateSQLWithUser(queryGenRoleTypes, ur.getUserID(), statement);
            }
        }
        System.out.println("====>5");
        
        // for the task type
        if (taskType != null) {
            if (TaskQueryType.ALL.name().equalsIgnoreCase(taskType)) {
                // no filter
            } else if (TaskQueryType.NOTIFICATIONS.name().equalsIgnoreCase(taskType)) {
                statement.addWhereClause("t.taskType=?20");
                statement.addParaValue(20, TaskType.NOTIFICATION);
            } else if (TaskQueryType.TASKS.name().equalsIgnoreCase(taskType)) {
                statement.addWhereClause("t.taskType=?20");
                statement.addParaValue(20, TaskType.TASK);
            }
        }
        System.out.println("====>6");
        // for the status
        if ((statusList != null) && (statusList.size() > 0)) {
            statement.addWhereClause("t.status in (?30)");
            statement.addParaValue(30, statusList);
            System.out.println("statuslist="+statusList);
        }
        
        if (createdOnClause != null && createdOnClause.length()>0){
        	statement.addWhereClause("t.createdOn " + createdOnClause);
        }
        System.out.println("====>7");
        String sql = statement.toString();
        System.out.println(sql);
        Query query = this.entityManager.createQuery(sql);
        
        if (statement.getParaValues() != null) {
            Set<Integer> keys = statement.getParaValues().keySet();
            for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
                Integer key = iterator.next();
                query.setParameter(key, statement.getParaValues().get(key));
            }
        }
        
        // for the query size
        query.setFirstResult(0);
        query.setMaxResults(maxTasks);
        

        return query.getResultList();
    }
    
    private void populateSQLWithRole(List<String> queryGenRoleTypes,
            List<String> queryRoles, SQLStatement statement) {
        
        // Task alias is t
        for (int i = 0; i < queryGenRoleTypes.size(); i++) {
            String type = queryGenRoleTypes.get(i);

            if (type.equalsIgnoreCase(GenericRoleType.task_initiator.name())) {
                // ignore it
            } else if (type.equalsIgnoreCase(GenericRoleType.task_stakeholders
                    .name())) {
                statement.addFromClause("in (t.taskStakeholders.principals) p1");
                statement.addWhereClause("(p1.value in (?1) and t.taskStakeholders.entityType=?2)");
                
                statement.addParaValue(1, queryRoles);
                statement.addParaValue(2, OrganizationalEntity.GROUP_ENTITY);
            } else if (type.equalsIgnoreCase(GenericRoleType.potential_owners
                    .name())) {
                statement.addFromClause("in (t.potentialOwners.principals) p3");
                statement.addWhereClause("(p3.value in (?3) and t.potentialOwners.entityType=?4)");

                statement.addParaValue(3, queryRoles);
                statement.addParaValue(4, OrganizationalEntity.GROUP_ENTITY);
            } else if (type.equalsIgnoreCase(GenericRoleType.actual_owner
                    .name())) {
                // ignore it
            } else if (type.equalsIgnoreCase(GenericRoleType.excluded_owners
                    .name())) {
                // ignore it, should model this role in the task entity?
            } else if (type
                    .equalsIgnoreCase(GenericRoleType.business_administrators
                            .name())) {
                statement.addFromClause("in (t.businessAdministrators.principals) p5");
                statement.addWhereClause("(p5.value in (?5) and t.businessAdministrators.entityType=?6)");

                statement.addParaValue(5, queryRoles);
                statement.addParaValue(6, OrganizationalEntity.GROUP_ENTITY);
            } else if (type
                    .equalsIgnoreCase(GenericRoleType.notification_recipients
                            .name())) {
                statement.addFromClause("in (t.notificationRecipients.principals) p7");
                statement.addWhereClause("(p7.value in (?7) and t.notificationRecipients.entityType=?8)");

                statement.addParaValue(7, queryRoles);
                statement.addParaValue(8, OrganizationalEntity.GROUP_ENTITY);
            }
        }
    }
    
    private void populateSQLWithUser(List<String> queryGenRoleTypes,
            String userId, SQLStatement statement) {
        // Task alias is t
        for (int i = 0; i < queryGenRoleTypes.size(); i++) {
            String type = queryGenRoleTypes.get(i);

            if (type.equalsIgnoreCase(GenericRoleType.task_initiator.name())) {
                statement.addWhereClause("(t.taskInitiator=?9)");
                statement.addParaValue(9, userId);
            } else if (type.equalsIgnoreCase(GenericRoleType.task_stakeholders
                    .name())) {
                statement.addFromClause("in (t.taskStakeholders.principals) p1");
                statement.addWhereClause("(p1.value =?1 and t.taskStakeholders.entityType=?2)");
                
                statement.addParaValue(1, userId);
                statement.addParaValue(2, OrganizationalEntity.USER_ENTITY);
            } else if (type.equalsIgnoreCase(GenericRoleType.potential_owners
                    .name())) {
                statement.addFromClause("in (t.potentialOwners.principals) p3");
                statement.addWhereClause("(p3.value =?3 and t.potentialOwners.entityType=?4)");

                statement.addParaValue(3, userId);
                statement.addParaValue(4, OrganizationalEntity.USER_ENTITY);
            } else if (type.equalsIgnoreCase(GenericRoleType.actual_owner
                    .name())) {
                statement.addWhereClause("(t.actualOwner=?10)");
                statement.addParaValue(10, userId);
            } else if (type.equalsIgnoreCase(GenericRoleType.excluded_owners
                    .name())) {
                // TODO: ignore it
            } else if (type
                    .equalsIgnoreCase(GenericRoleType.business_administrators
                            .name())) {
                statement.addFromClause("in (t.businessAdministrators.principals) p5");
                statement.addWhereClause("p5.value =?5 and t.businessAdministrators.entityType=?6");

                statement.addParaValue(5, userId);
                statement.addParaValue(6, OrganizationalEntity.USER_ENTITY);
            } else if (type
                    .equalsIgnoreCase(GenericRoleType.notification_recipients
                            .name())) {
                statement.addFromClause("in (t.notificationRecipients.principals) p7");
                statement.addWhereClause("p7.value =?7 and t.notificationRecipients.entityType=?8");

                statement.addParaValue(7, userId);
                statement.addParaValue(8, OrganizationalEntity.USER_ENTITY);
            }
        }
    }

    // TODO: below method will be removed
   public EntityManager getEntityManager() {
       return this.entityManager;
   }
   
   private List<String> parseString(String data, String seperator) {
       if (data == null) {
           return null;
       }
       String[] arrs = data.split(seperator);
       
       List<String> result = new ArrayList<String>();
       for (int i = 0; i < arrs.length; i++) {
           result.add(arrs[i]);
       }
       
       return result;
   }
}
