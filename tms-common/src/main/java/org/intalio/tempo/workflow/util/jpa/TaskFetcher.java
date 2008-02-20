package org.intalio.tempo.workflow.util.jpa;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
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
    private static final String FIND_BY_IDS = "select m from Task m where m._id IN ";

    private static final String SUBQUERY_FIND_USER = "m.USERS IN (SELECT SET_ID FROM TEMPO_BACKING_SET WHERE AUTH_ID IN  {0})";
    private static final String SUBQUERY_FIND_ROLE = "m.ROLES IN (SELECT SET_ID FROM TEMPO_BACKING_SET WHERE AUTH_ID IN  {0})";
    private static final String SUBQUERY_SELECT_FROM_TASKS = "SELECT tid FROM TEMPO_TASKS m WHERE " ; 
    private static final String FIND_BY_USERS = SUBQUERY_SELECT_FROM_TASKS+ SUBQUERY_FIND_USER;
    private static final String FIND_BY_ROLES = SUBQUERY_SELECT_FROM_TASKS + SUBQUERY_FIND_ROLE;

    final static Logger _logger = LoggerFactory.getLogger(TaskFetcher.class);
    private EntityManager _entityManager;
    private Query find_by_id;

    public TaskFetcher(EntityManager em) {
        this._entityManager = em;
        this.find_by_id = _entityManager.createNamedQuery(Task.FIND_BY_ID);
    }

    /**
     * Retrieve and load a task if it exists
     */
    public Task fetchTaskIfExists(String taskID) {
        Query q = find_by_id.setParameter(1, taskID);
        return (Task) (q.getResultList()).get(0);
    }

    /**
     * Fetch a PIPA task from its URL
     */
    public PIPATask fetchPipaFromUrl(String formUrl) {
        Query q = _entityManager.createNamedQuery(PIPATask.FIND_BY_URL).setParameter(1, formUrl);
        return (PIPATask) q.getSingleResult();
    }

    /**
     * Core method. retrieve all the tasks for the given <code>UserRoles</code>
     */
    public Task[] fetchAllAvailableTasks(UserRoles user) {
        String query = SUBQUERY_SELECT_FROM_TASKS+"("
                + MessageFormat.format(SUBQUERY_FIND_ROLE, user.getAssignedRoles().toString()) + ") OR ("
                + MessageFormat.format(SUBQUERY_FIND_USER, "('"+user.getUserID() + "')")+")";
        return fetchTasks(query, null);
    }

    /**
     * Fetch the tasks for a given user
     */
    public Task[] fetchTasksForUser(String user) {
        return fetchTasks(FIND_BY_USERS, new Object[] { "('" + user + "')" });
    }

    /**
     * Fetch the tasks for a given role
     */
    public Object[] fetchTasksForRole(String role) {
        return fetchTasks(FIND_BY_ROLES, new Object[] { "('" + role + "')" });
    }

    /**
     * Load all the task object from the list of ids
     */
    private List<Task> fetchTaskFromIds(List<String> l) {
        Query q2 = _entityManager.createQuery(getJPQLQueryFromIds(l));
        return (List<Task>) q2.getResultList();
    }

    private Task[] fetchTasks(String baseQuery) {
        if (_logger.isDebugEnabled())
            _logger.debug("fetchAllAvailableTasks query:" + baseQuery);
        Query q = _entityManager.createNativeQuery(baseQuery, String.class);
        List<String> listofIds = (List<String>) q.getResultList();
        if (listofIds.size() < 1)
            return new Task[0];
        List<Task> tasks = fetchTaskFromIds(listofIds);
        return (Task[]) new ArrayList(tasks).toArray(new Task[tasks.size()]);
    }

    /**
     * Execute the native query, retrieve the ids, and then load the tasks
     */
    private Task[] fetchTasks(String baseQuery, Object[] parameters) {
        String query = (parameters == null) ? baseQuery : MessageFormat.format(baseQuery, parameters);
        return fetchTasks(query);
    }

    /**
     * Generate the jpa query from the list of task ids we want to retrieve the
     * task object for
     */
    private String getJPQLQueryFromIds(List<String> ids) {
        StringBuffer buffer = new StringBuffer(FIND_BY_IDS + "( ");
        for (String id : ids)
            buffer.append("'" + id + "',");
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append(" )");
        if (_logger.isDebugEnabled())
            _logger.debug("Query:" + buffer.toString());
        return buffer.toString();
    }

}
