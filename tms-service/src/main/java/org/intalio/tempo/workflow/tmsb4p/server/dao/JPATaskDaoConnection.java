package org.intalio.tempo.workflow.tmsb4p.server.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import org.intalio.tempo.workflow.tms.B4PPersistException;
import org.intalio.tempo.workflow.tms.InvalidQueryException;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tmsb4p.query.InvalidFieldException;
import org.intalio.tempo.workflow.tmsb4p.query.QueryOperator;
import org.intalio.tempo.workflow.tmsb4p.query.QueryUtil;
import org.intalio.tempo.workflow.tmsb4p.query.SQLStatement;
import org.intalio.tempo.workflow.tmsb4p.query.TaskFieldConverter;
import org.intalio.tempo.workflow.tmsb4p.query.TaskJPAStatement;
import org.intalio.tempo.workflow.tmsb4p.query.TaskView;

import edu.emory.mathcs.backport.java.util.Arrays;

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
    
public void addUserOrGroups(String taskId, String[] usersOrGroups, boolean isUser,
GenericRoleType role) throws UnavailableTaskException, B4PPersistException {
        if (GenericRoleType.task_initiator.equals(role) || GenericRoleType.actual_owner.equals(role)) {
        if ((usersOrGroups == null) || (usersOrGroups.length != 1)) {
        throw new B4PPersistException("Only one user can be the task initiator or actual owner");
        }
        if (isUser) {
        throw new B4PPersistException("Only one user can be the task initiator or actual owner");
        }
        
            Task task = this.fetchTaskIfExists(taskId);
            String newUser = usersOrGroups[0];
            if (GenericRoleType.task_initiator.equals(role)) {
                task.setTaskInitiator(newUser);
            } else {
                task.setActualOwner(newUser);
            }
            this.updateTask(task);
       } else {
       OrganizationalEntity curOrg = getOrgWithTaskId(taskId, role);
       OrganizationalEntity newOrg = null;

if (curOrg == null) {
if (isUser) {
newOrg = new UserOrganizationalEntity();
} else {
newOrg = new GroupOrganizationalEntity();
}
} else {
if (curOrg.getEntityType().equals(OrganizationalEntity.USER_ENTITY) && !isUser) {
newOrg = new GroupOrganizationalEntity();
} else if (curOrg.getEntityType().equals(OrganizationalEntity.GROUP_ENTITY) && isUser) {
newOrg = new UserOrganizationalEntity();
}
}

Set<Principal> ps = null;
if (newOrg != null) {
ps = new HashSet<Principal>();
newOrg.setPrincipals(ps);
} else {
ps = curOrg.getPrincipals();
if (ps == null) {
ps = new HashSet<Principal>();
curOrg.setPrincipals(ps);
}
}

for (int i = 0; i < usersOrGroups.length; i++) {
Principal p = new Principal();
p.setValue(usersOrGroups[i]);
ps.add(p);

if (newOrg != null) {
p.setOrgEntity(newOrg);
} else {
p.setOrgEntity(curOrg);
}
}

if (newOrg != null) {
            this.updateNewOrgWithTaskId(taskId, newOrg, role);
} else {
checkTransactionIsActive();
this.entityManager.merge(curOrg);
}
}
}

private OrganizationalEntity getOrgWithTaskId(String taskId, GenericRoleType role) {
    String qStr = null;
       if (GenericRoleType.business_administrators.equals(role)) {
       qStr = "select t.businessAdministrators from Task t where t.id=?1";
       } else if (GenericRoleType.task_stakeholders.equals(role)) {
       qStr = "select t.taskStakeholders from Task t where t.id=?1";
       } else if (GenericRoleType.potential_owners.equals(role)) {
       qStr = "select t.potentialOwners from Task t where t.id=?1";
       } else if (GenericRoleType.notification_recipients.equals(role)) {
       qStr = "select t.notificationRecipients from Task t where t.id=?1";
       } else if (GenericRoleType.excluded_owners.equals(role)) {
       qStr = "select t.excludedOwners from Task t where t.id=?1";
       } else {
       throw new IllegalArgumentException("Illegal Arguement role: " + role);
       }
       
       Query query = this.entityManager.createQuery(qStr);
       query.setParameter(1, taskId);
       
       return (OrganizationalEntity)query.getSingleResult();
}

private void updateNewOrgWithTaskId(String taskId, OrganizationalEntity newOrg, GenericRoleType role) throws UnavailableTaskException {
OrganizationalEntity curOrg = null;
        Task task = this.fetchTaskIfExists(taskId);
        if (GenericRoleType.business_administrators.equals(role)) {
            curOrg = task.getBusinessAdministrators();
            task.setBusinessAdministrators(newOrg);
        } else if (GenericRoleType.task_stakeholders.equals(role)) {
            curOrg = task.getTaskStakeholders();
            task.setTaskStakeholders(newOrg);
        } else if (GenericRoleType.potential_owners.equals(role)) {
            curOrg = task.getPotentialOwners();
            task.setPotentialOwners(newOrg);
        } else if (GenericRoleType.notification_recipients.equals(role)) {
            curOrg = task.getNotificationRecipients();
            task.setNotificationRecipients(newOrg);
        } else if (GenericRoleType.excluded_owners.equals(role)) {
            curOrg = task.getExcludedOwners();
            task.setExcludedOwners(newOrg);
        } else {
        throw new IllegalArgumentException("Illegal Arguement role: " + role);
        }

        this.updateTask(task);
        if (curOrg != null) {
            this.entityManager.remove(curOrg);
        }
}

public void removeUserOrGroups(String taskId, String[] usersOrGroups,
GenericRoleType role) throws UnavailableTaskException {
if (GenericRoleType.task_initiator.equals(role)
                || GenericRoleType.actual_owner.equals(role)) {
            // this shouldn't happen because there only one user can be the
            // initiator or actual owner
            throw new IllegalArgumentException("Illegal Arguement role: " + role);
        }
        
        OrganizationalEntity org = this.getOrgWithTaskId(taskId, role);
        if (org == null) {
            return;
        }
        
        long orgId = org.getInternalId();
        Query q = this.entityManager.createNamedQuery(Principal.DELETE_WITH_VALUE);
        q.setParameter(1, orgId);
        q.setParameter(2, Arrays.asList(usersOrGroups));
        
        this.checkTransactionIsActive();
        q.executeUpdate();
    }

    public void updateTaskRole(String taskId, GenericRoleType role, List<String> values, String orgType) throws UnavailableTaskException {
        if (GenericRoleType.task_initiator.equals(role) || GenericRoleType.actual_owner.equals(role)) {
            Task task = this.fetchTaskIfExists(taskId);
            String newUser = values.iterator().next();
            if (GenericRoleType.task_initiator.equals(role)) {
                task.setTaskInitiator(newUser);
            } else {
                task.setActualOwner(newUser);
            }
            this.updateTask(task);
        } else {
            OrganizationalEntity newOrg = null;

            // add all the principals to new org
            if ((values != null) && (!values.isEmpty())) {
                if (OrganizationalEntity.GROUP_ENTITY.equals(orgType)) {
                    newOrg = new GroupOrganizationalEntity();
                } else {
                    newOrg = new UserOrganizationalEntity();
                }

                for (String value : values) {
                    Principal p = new Principal();
                    p.setValue(value);
                    p.setOrgEntity(newOrg);
                    newOrg.addPrincipal(p);
                }
            }
            
            this.updateNewOrgWithTaskId(taskId, newOrg, role);

//            if (GenericRoleType.business_administrators.equals(role)) {
//                curOrg = task.getBusinessAdministrators();
//                task.setBusinessAdministrators(newOrg);
//            } else if (GenericRoleType.task_stakeholders.equals(role)) {
//                curOrg = task.getTaskStakeholders();
//                task.setTaskStakeholders(newOrg);
//            } else if (GenericRoleType.potential_owners.equals(role)) {
//                curOrg = task.getPotentialOwners();
//                task.setPotentialOwners(newOrg);
//            } else if (GenericRoleType.notification_recipients.equals(role)) {
//                curOrg = task.getNotificationRecipients();
//                task.setNotificationRecipients(newOrg);
//            } else if (GenericRoleType.excluded_owners.equals(role)) {
//                curOrg = task.getExcludedOwners();
//                task.setExcludedOwners(newOrg);
//            }
//
//            this.updateTask(task);
//            if (curOrg != null) {
//                this.entityManager.remove(curOrg);
//            }
        }
    }

    public Collection<Map<String, Object>> query(UserRoles ur, String selectClause, String whereClause, String orderByClause, int maxTasks, int taskIndexOffset)
                    throws InvalidQueryException {
        TaskJPAStatement taskStatement = new TaskJPAStatement(selectClause, whereClause, orderByClause);

        Query query = this.createJPAQuery(taskStatement);

        query.setFirstResult(taskIndexOffset);
        if (maxTasks > 0) {
            query.setMaxResults(maxTasks);
        }

        List queryRet = query.getResultList();
        // needs to convert the result with the query columns
        List<String> columns = taskStatement.getQueryColumns();

        Collection result = convertResult(queryRet, columns);
        return result;
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
        if (workQueue != null && workQueue.length() > 0) {

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
        if (maxTasks > 0) {
            query.setMaxResults(maxTasks);
        }

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
            for (String key : keys) {
                query.setParameter(key, data.get(key));
            }
        }

        return query;
    }

    public boolean isRoleMember(String taskId, UserRoles ur, GenericRoleType role) {
        boolean hasAssignedRoles = ((ur.getAssignedRoles() != null) && (!ur.getAssignedRoles().isEmpty()));
        String selectClause = "id";
        StringBuffer wClause = new StringBuffer();
        // user id clause
        if (hasAssignedRoles) {
            wClause.append("(");
        }
        wClause.append(TaskView.USERID).append("='").append(ur.getUserID()).append("'");

        if (hasAssignedRoles) {
            // group clause
            wClause.append(" or ").append(TaskView.GROUP).append("='").append(QueryUtil.joinString(ur.getAssignedRoles(), ",")).append("'").append(")");
        }
        // role info
        wClause.append(" and  ").append(TaskView.GENERIC_HUMAN_ROLE).append("='").append(role.name()).append("'");
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

    private Collection<Map<String, Object>> convertResult(List data, List<String> columns) {
        if ((data == null) || (data.isEmpty())) {
            return Collections.EMPTY_LIST;
        }

        boolean isArray = data.get(0).getClass().isArray();

        List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
        if (!isArray) {
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> row = new HashMap<String, Object>();
                // only one column in the search result
                row.put(columns.get(0), data.get(i));
                listData.add(row);
            }

            return listData;
        }

        if (isArray) {
            if (((Object[]) data.get(0)).length != columns.size()) {
                // the length should be equal
                // TODO: throw exception here.
            }
        }

        Map<String, Map<String, Object>> result = new LinkedHashMap<String, Map<String, Object>>();

        // it is because the attachments data in task is one collection, the
        // attachments
        // should be aggregated in one list.
        Map<String, List<Object>> attMap = new LinkedHashMap<String, List<Object>>();
        String idCol = null;
        String attachmentCol = null;

        try {
            idCol = TaskFieldConverter.getQueryColumn(TaskView.ID);
            attachmentCol = TaskFieldConverter.getQueryColumn(TaskView.ATTACHMENTS);
        } catch (InvalidFieldException e) {
            // ignore it
        }

        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = new HashMap<String, Object>();

            Object[] rowValues = (Object[]) data.get(i);
            for (int j = 0; j < columns.size(); j++) {
                row.put(columns.get(j), rowValues[j]);
            }

            String id = (String) row.get(idCol);
            Object att = row.get(attachmentCol);

            if (att != null) {
                if (attMap.get(id) == null) {
                    attMap.put(id, new ArrayList<Object>());
                }
                attMap.get(id).add(att);
            }

            if (result.get(id) == null) {
                result.put(id, row);
            }
        }

        // put in the attachments data
        if (!attMap.isEmpty()) {
            for (String id : attMap.keySet()) {
                Map<String, Object> row = result.get(id);
                row.put(attachmentCol, attMap.get(id));
            }
        }

        return result.values();
    }

    // TODO: below method will be removed
    public EntityManager getEntityManager() {
        return this.entityManager;
    }
}
