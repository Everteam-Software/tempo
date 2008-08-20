package org.intalio.tempo.workflow.util.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The code to retrieve task is decomposed into two calls to the persistence:
 * <ul>
 * <li>One call to retrieve the tasks ids. The query cannot be expressed
 * through JPQL AND load the proper classes so we are using simple SQL</li>
 * <li>The second call uses JPQL to load the tasks, and load the proper
 * inheritance tree</li>
 * </ul>
 */
@SuppressWarnings("unchecked")
public class TaskFetcher {
    final static Logger _logger = LoggerFactory.getLogger(TaskFetcher.class);
    private EntityManager _entityManager;
    private Query find_by_id;
    private final String QUERY_GENERIC1 = "select DISTINCT T from ";
    private final String QUERY_GENERIC2 = " T where (T._userOwners in (?1) or T._roleOwners in (?2))";
    private final String DELETE_TASKS = "delete from Task m where m._userOwners in (?1) or m._roleOwners in (?2)";
    private final String DELETE_ALL_TASK_WITH_ID = "delete from Task m where m._id = (?1)";

    public TaskFetcher(EntityManager em) {
        this._entityManager = em;
        this.find_by_id = _entityManager.createNamedQuery(Task.FIND_BY_ID);
    }

    /**
     * Retrieve and load a task if it exists
     * 
     * @throws UnavailableTaskException
     */
    public Task fetchTaskIfExists(String taskID) throws UnavailableTaskException {
        try {
            Query q = find_by_id.setParameter(1, taskID);
            List resultList = q.getResultList();
            if (resultList.size() < 1)
                throw new UnavailableTaskException("Task does not exist" + taskID);
            return (Task) resultList.get(0);
        } catch (NoResultException nre) {
            throw new UnavailableTaskException("Task does not exist" + taskID);
        }
    }

    public int deleteTasksWithID(String taskID) {
        Query q = _entityManager.createQuery(DELETE_ALL_TASK_WITH_ID);
        q.setParameter(1, taskID);
        return q.executeUpdate();
    }

    /**
     * Fetch a PIPA task from its URL
     */
    public PIPATask fetchPipaFromUrl(String formUrl) {
        Query q = _entityManager.createNamedQuery(PIPATask.FIND_BY_URL).setParameter(1, "%" + formUrl);
        return (PIPATask) q.getResultList().get(0);
    }

    /**
     * Core method. retrieve all the tasks for the given <code>UserRoles</code>
     */
    public Task[] fetchAllAvailableTasks(UserRoles user) {
        ArrayList userIdList = new ArrayList();
        userIdList.add(user.getUserID());
        Query q = _entityManager.createNamedQuery(Task.FIND_BY_ROLE_USER).setParameter(1, userIdList).setParameter(2, user.getAssignedRoles());
        List result = q.getResultList();
        return (Task[]) result.toArray(new Task[result.size()]);
    }

    /**
     * Core method. retrieve all the tasks for the given <code>UserRoles</code>
     * and task state, task type
     */
    public Task[] fetchAvailableTasks(UserRoles user, Class taskClass, String subQuery) {
        ArrayList userIdList = new ArrayList();
        userIdList.add(user.getUserID());
        Query q;

        if (StringUtils.isEmpty(subQuery)) {
            q = _entityManager.createQuery(QUERY_GENERIC1 + taskClass.getSimpleName() + QUERY_GENERIC2).setParameter(1, userIdList).setParameter(2,
                            user.getAssignedRoles());
        } else {
            StringBuffer buffer = new StringBuffer();
            buffer.append(QUERY_GENERIC1).append(taskClass.getSimpleName()).append(QUERY_GENERIC2);
            if (!subQuery.startsWith("ORDER"))
                buffer.append(" and ");
            buffer.append(subQuery);
            q = _entityManager.createQuery(buffer.toString()).setParameter(1, userIdList).setParameter(2, user.getAssignedRoles());
        }
        List result = q.getResultList();
        return (Task[]) result.toArray(new Task[result.size()]);
    }

    /**
     * Fetch the tasks for a given user
     */
    public Task[] fetchTasksForUser(String user) {
        List<String> params = new ArrayList<String>();
        params.add(user);
        Query q = _entityManager.createNamedQuery(Task.FIND_BY_USER).setParameter(1, params);
        List result = q.getResultList();
        return (Task[]) result.toArray(new Task[result.size()]);
    }

    /**
     * Fetch the tasks for a given role
     */
    public Object[] fetchTasksForRole(String role) {
        List<String> params = new ArrayList<String>();
        params.add(role);
        Query q = _entityManager.createNamedQuery(Task.FIND_BY_ROLE).setParameter(1, params);
        List result = q.getResultList();
        return (Task[]) result.toArray(new Task[result.size()]);
    }

}
