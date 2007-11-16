package org.intalio.tempo.workflow.tms.server.dao;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;

public class JPATaskDaoConnection implements ITaskDAOConnection {

    private EntityManager entityManager;
    private Query find_by_id;

    public JPATaskDaoConnection(EntityManager createEntityManager) {
        this.entityManager = createEntityManager;
        this.find_by_id = entityManager.createNamedQuery("find_by_id");
    }

    public void close() {
        entityManager.close();
    }

    public void commit() {
        entityManager.getTransaction().commit();
    }

    public void createTask(Task task) throws TaskIDConflictException {
        entityManager.persist(task);
    }

    public boolean deleteTask(int internalTaskId, String taskID) {
        Task t = null;
        synchronized (find_by_id) {
            Query q = find_by_id.setParameter(1, taskID);
            t = (Task) (q.getResultList()).get(0);
        }
        entityManager.remove(t);
        return true;
    }

    @SuppressWarnings("unchecked")
    public Task[] fetchAllAvailableTasks(UserRoles user) {
        AuthIdentifierSet roles = user.getAssignedRoles();
        String userid = user.getUserID();
        String s = MessageFormat.format(Task.FIND_BY_USER_AND_ROLES, new Object[]{roles.toString(),userid});
        Query q = entityManager.createNativeQuery(s,Task.class);
        List<Task> l = q.getResultList();
        return (Task[])new ArrayList(l).toArray(new Task[l.size()]);
    }

    public Task fetchTaskIfExists(String taskID) {
        synchronized (find_by_id) {
            Query q = find_by_id.setParameter(1, taskID);
            return (Task) (q.getResultList()).get(0);
        }
    }

    public void updateTask(Task task) {
        entityManager.persist(task);
    }

}
