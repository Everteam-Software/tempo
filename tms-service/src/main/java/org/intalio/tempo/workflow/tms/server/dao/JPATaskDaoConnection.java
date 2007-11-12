package org.intalio.tempo.workflow.tms.server.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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

    public Task[] fetchAllAvailableTasks(UserRoles user) {
        user.getAssignedRoles();
        user.getUserID();
        return null;
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
