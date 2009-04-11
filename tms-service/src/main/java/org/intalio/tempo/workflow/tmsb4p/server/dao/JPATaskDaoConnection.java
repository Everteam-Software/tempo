package org.intalio.tempo.workflow.tmsb4p.server.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.intalio.tempo.workflow.taskb4p.GroupOrganizationalEntity;
import org.intalio.tempo.workflow.taskb4p.OrganizationalEntity;
import org.intalio.tempo.workflow.taskb4p.Principal;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.taskb4p.TaskStatus;
import org.intalio.tempo.workflow.taskb4p.UserOrganizationalEntity;
import org.intalio.tempo.workflow.tms.InvalidQueryException;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tmsb4p.query.QueryOperator;
import org.intalio.tempo.workflow.tmsb4p.query.QueryUtil;
import org.intalio.tempo.workflow.tmsb4p.query.SQLStatement;
import org.intalio.tempo.workflow.tmsb4p.query.TaskJPAStatement;
import org.intalio.tempo.workflow.tmsb4p.query.TaskView;

//import com.ibm.db2.jcc.a.l;

public class JPATaskDaoConnection extends AbstractJPAConnection implements ITaskDAOConnection {

    public JPATaskDaoConnection(EntityManager createEntityManager) {
        super(createEntityManager);
    }

    public void createTask(Task task) throws TaskIDConflictException {
        checkTransactionIsActive();
        task.setCreatedOn(new Date(System.currentTimeMillis()));
        entityManager.persist(task);
    }

    public void addAttachment(String taskId, String attachmentName, AttachmentAccessType accessType, String contentType, String attachedBy, String value) {
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
        comment.setTask(task);

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

    public Task fetchTaskIfExists(String taskId) throws UnavailableTaskException {
        Query query = entityManager.createNamedQuery(Task.FIND_BY_ID);
        query.setParameter(1, taskId);

        Task task = (Task) query.getSingleResult();
        return task;
    }

    public List<AttachmentInfo> getAttachmentInfos(String taskId) {
        Query query = entityManager.createNamedQuery(Attachment.QUERY_ALL_INFOS);
        query.setParameter(1, taskId);

        List<AttachmentInfo> infos = query.getResultList();
        return infos;
    }

    public List<Attachment> getAttachments(String taskId, String attachmentName) {
        Query query = entityManager.createNamedQuery(Attachment.QUERY_ALL_ATTACHMENTS);
        query.setParameter(1, taskId);
        query.setParameter(2, attachmentName);

        List<Attachment> attachments = query.getResultList();
        return attachments;
    }

    public List<Comment> getComments(String taskId) {
        Query query = entityManager.createNamedQuery(Comment.QUERY_ALL_COMMENTS);
        query.setParameter(1, taskId);
        List<Comment> comments = query.getResultList();

        return comments;
    }

    public void updateTask(Task task) {
        checkTransactionIsActive();
        entityManager.merge(task);
    }

    public void updateTaskStatus(String taskId, TaskStatus status) throws UnavailableTaskException {
        checkTransactionIsActive();
        Task task = this.fetchTaskIfExists(taskId);

        task.setStatus(status);
    }

    private boolean hasAttachment(String taskId) {
        String queryString = "select count(m) from Attachment m where m.task.id=:taskId";
        Query hasAttachmentQury = this.entityManager.createQuery(queryString);
        hasAttachmentQury.setParameter("taskId", taskId);

        Long count = (Long) hasAttachmentQury.getSingleResult();
        return (count.longValue() > 0);
    }

    private boolean hasComment(String taskId) {
        String queryString = "select count(m) from Comment m where m.task.id=:taskId";
        Query hasCommentQury = this.entityManager.createQuery(queryString);
        hasCommentQury.setParameter("taskId", taskId);

        Long count = (Long) hasCommentQury.getSingleResult();
        return (count.longValue() > 0);
    }

    public List<Task> getTasksWithName(String taskName) {
        Query query = entityManager.createNamedQuery(Task.FIND_BY_NAME);
        query.setParameter(1, taskName);

        List<Task> tasks = query.getResultList();
        return tasks;
    }
    
    public void updateTaskRole(String taskId, GenericRoleType role, Set<String> values, String orgType) throws UnavailableTaskException {
        Task task = this.fetchTaskIfExists(taskId);
        if (GenericRoleType.task_initiator.equals(role) || GenericRoleType.actual_owner.equals(role)) {            
            String newUser = values.iterator().next();
            if (GenericRoleType.task_initiator.equals(role)) {
                task.setTaskInitiator(newUser);
            } else {
                task.setActualOwner(newUser);
            }
        } else {
            OrganizationalEntity orgEntity = null;
            if (GenericRoleType.business_administrators.equals(role)) {
                orgEntity = task.getBusinessAdministrators();
            } else if (GenericRoleType.task_stakeholders.equals(role)) {
                orgEntity = task.getTaskStakeholders();
            } else if (GenericRoleType.potential_owners.equals(role)) {
                orgEntity = task.getPotentialOwners();
            } else if (GenericRoleType.notification_recipients.equals(role)) {
                orgEntity = task.getNotificationRecipients();
            } else if (GenericRoleType.excluded_owners.equals(role)) {
                orgEntity = task.getExcludedOwners();
            }
            
            boolean isNewOrg = false;
            if ((orgEntity == null) || (!orgEntity.equals(orgType))) {
                // create one organization
                isNewOrg = true;
                if (OrganizationalEntity.GROUP_ENTITY.equals(orgType)) {
                    orgEntity = new GroupOrganizationalEntity();
                } else {
                    orgEntity = new UserOrganizationalEntity();
                }                
            }
            
            // update the principal to the organization
            orgEntity.getPrincipals().clear();
            
            for (String value : values) {
                Principal p = new Principal();
                p.setValue(value);
                p.setOrgEntity(orgEntity);
            }
            
            if (isNewOrg) {
                if (GenericRoleType.business_administrators.equals(role)) {
                    task.setBusinessAdministrators(orgEntity);
                } else if (GenericRoleType.task_stakeholders.equals(role)) {
                    task.setTaskStakeholders(orgEntity);
                } else if (GenericRoleType.potential_owners.equals(role)) {
                    task.setPotentialOwners(orgEntity);
                } else if (GenericRoleType.notification_recipients.equals(role)) {
                    task.setNotificationRecipients(orgEntity);
                } else if (GenericRoleType.excluded_owners.equals(role)) {
                    task.setExcludedOwners(orgEntity);
                }
            }
        }
        
        this.updateTask(task);
    }

    public List<Task> query(UserRoles ur, String selectClause, String whereClause, String orderByClause, int maxTasks, int taskIndexOffset)
                    throws InvalidQueryException {
        TaskJPAStatement taskStatement = new TaskJPAStatement(selectClause, whereClause, orderByClause);

        Query query = this.createJPAQuery(taskStatement);

        query.setFirstResult(taskIndexOffset);
        query.setMaxResults(maxTasks);

        return (List<Task>) query.getResultList();
    }

    public List<Task> getMyTasks(UserRoles ur, String taskType, String genericHumanRole, String workQueue, List<TaskStatus> statusList, String whereClause,
                    String createdOnClause, int maxTasks) throws InvalidQueryException {

        List<String> genRoles = QueryUtil.parseString(genericHumanRole, ",");
        if (genRoles == null) {
            throw new InvalidQueryException("No role has been specifed");
        }

        // Avoid the duplicate roles.
        Set<String> queryRoles = new HashSet<String>();
        queryRoles.addAll(genRoles);

        TaskJPAStatement taskStatement = null;
        if (workQueue != null) {
            List<String> groups = new ArrayList<String>();
            // check whether the user belongs the work queue, if not, empty
            // result returned.
            AuthIdentifierSet urRoles = ur.getAssignedRoles();
            List<String> inputRoles = QueryUtil.parseString(workQueue, ",");

            for (Iterator<String> iterator = inputRoles.iterator(); iterator.hasNext();) {
                String role = iterator.next();
                if (urRoles.contains(role)) {
                    groups.add(role);
                }
            }

            if (groups.isEmpty()) {
                return Collections.EMPTY_LIST;
            }

            // query with the group Organizational entity
            String newWhereClause = assembleWhereClause(taskType, statusList, whereClause, createdOnClause);

            taskStatement = new TaskJPAStatement(queryRoles, groups, OrganizationalEntity.GROUP_ENTITY, newWhereClause);
        } else {
            // query with the user Organizational entity
            String newWhereClause = assembleWhereClause(taskType, statusList, whereClause, createdOnClause);
            List<String> users = new ArrayList<String>();
            users.add(ur.getUserID());
            taskStatement = new TaskJPAStatement(queryRoles, users, OrganizationalEntity.USER_ENTITY, newWhereClause);
        }

        Query query = this.createJPAQuery(taskStatement);

        query.setMaxResults(maxTasks);

        return (List<Task>) query.getResultList();
    }

    private String assembleWhereClause(String taskType, List<TaskStatus> statusList, String whereClause, String createdOnClause) {
        StringBuffer result = new StringBuffer();

        if ((whereClause != null) && (whereClause.length() > 0)) {
            result.append(whereClause);
        }

        if ((createdOnClause != null) && (createdOnClause.length() > 0)) {
            if (result.length() != 0) {
                result.append(" and ");
            }
            result.append(createdOnClause);
        }

        if (statusList != null && !statusList.isEmpty()) {
            if (result.length() != 0) {
                result.append(" and ");
            }

            result.append(TaskView.STATUS).append(" in (");
            for (int i = 0; i < statusList.size(); i++) {
                if (i > 0) {
                    result.append(",");
                }
                result.append(statusList.get(i));
            }
            result.append(")");
        }

        if ((taskType != null) && (taskType.length() > 0)) {
            if (result.length() != 0) {
                result.append(" and ");
            }
            result.append(TaskView.TASK_TYPE).append(QueryOperator.EQUALS).append("'" + taskType + "'");
        }

        return result.toString();
    }

    private Query createJPAQuery(TaskJPAStatement taskStatement) throws InvalidQueryException {

        SQLStatement sqlStatement = null;
        try {
            sqlStatement = taskStatement.getStatement();
        } catch (Exception e) {
            throw new InvalidQueryException(e);
        }

        String jql = sqlStatement.toString();

        Query query = this.entityManager.createQuery(jql);
        Map<String, Object> data = sqlStatement.getParaValues();

        if (data != null) {
            Set<String> keys = data.keySet();
            for (String key: keys) {
                query.setParameter(key, data.get(key));
            }
        }

        return query;
    }

    public boolean isRoleMember(String taskId, UserRoles ur, GenericRoleType role) {
        String selectClause = "id";
        StringBuffer wClause = new StringBuffer();
        // user id clause
        wClause.append("(").append(TaskView.USERID).append("='").append(ur.getUserID()).append("'");
        // group clause
        wClause.append(" or ").append(TaskView.GROUP).append("='")
                .append(QueryUtil.joinString(ur.getAssignedRoles(), ","))
                .append("'").append(")");
        // role info
        wClause.append(" and  ").append(TaskView.GENERIC_HUMAN_ROLE).append("='")
                .append(role.name()).append("'");
        // task id clause
        wClause.append(" and  ").append("id='").append(taskId).append("'");
         
        TaskJPAStatement taskStatement = new TaskJPAStatement(selectClause, wClause.toString(), null);
        
        Query query = null;
        try {
            query = this.createJPAQuery(taskStatement);
        } catch (InvalidQueryException e) {
            // ignore it, it shouldn't happen.
        }
        int size = query.getResultList().size();
        
        return (size > 0);
    }
    
    // TODO: below method will be removed
    public EntityManager getEntityManager() {
        return this.entityManager;
    }
}
