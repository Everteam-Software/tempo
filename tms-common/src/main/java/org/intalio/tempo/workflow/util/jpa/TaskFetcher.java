package org.intalio.tempo.workflow.util.jpa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The code to retrieve task is decomposed into two calls to the persistence:
 * <ul>
 * <li>One call to retrieve the tasks ids. The query cannot be expressed through
 * JPQL AND load the proper classes so we are using simple SQL</li>
 * <li>The second call uses JPQL to load the tasks, and load the proper
 * inheritance tree</li>
 * </ul>
 */
@SuppressWarnings("unchecked")
public class TaskFetcher {
	public static final String FETCH_MAX = ":max";
	public static final String FETCH_FIRST = ":first";
	public static final String FETCH_SUB_QUERY = ":subQuery";
	public static final String FETCH_CLASS = ":class";
	public static final String FETCH_CLASS_NAME = ":classname";
	public static final String FETCH_USER = ":user";
	public static final String FETCH_COUNT = ":count";
	public static final String FETCH_FILTER = ":filter";

	final static Logger _logger = LoggerFactory.getLogger(TaskFetcher.class);
	private EntityManager _entityManager;
	private Query find_by_id;
	private final String QUERY_GENERIC1 = "select DISTINCT T from ";
	private final String QUERY_GENERIC_COUNT = "select COUNT(DISTINCT T) from ";
	private final String QUERY_GENERIC2 = " T where (T._userOwners in (?1) or T._roleOwners in (?2)) ";
	/**
	 * display if atd!=null and atd>now-8h [2:35:16 AM] Pierre Pavageau: or if
	 * atd==null and std>now-8h [2:35:27 AM] Pierre Pavageau: or if atd==null
	 * and std==null
	 */
	private final String SITA_FILTER1 = "(T._ActualDeparture IS NOT NULL AND (T._ActualDeparture > (?3)))";
	private final String SITA_FILTER2 = "(T._ActualDeparture IS NULL AND (T._ScheduledDeparture > (?3)))";
	private final String SITA_FILTER3 = "(T._ActualDeparture IS NULL AND T._ScheduledDeparture IS NULL)";
	private final String QUERY_WITH_FILTER = " AND (" + SITA_FILTER1 + " OR "
			+ SITA_FILTER2 + " OR " + SITA_FILTER3 + ")";
	// private final String DELETE_TASKS =
	// "delete from Task m where m._userOwners in (?1) or m._roleOwners in (?2) "
	// ;
	private final String DELETE_ALL_TASK_WITH_ID = "delete from Task m where m._id = (?1) ";

	public TaskFetcher(EntityManager em) {
		this._entityManager = em;
		this.find_by_id = _entityManager.createNamedQuery(Task.FIND_BY_ID);
	}

	/**
	 * Retrieve and load a task if it exists
	 * 
	 * @throws UnavailableTaskException
	 */
	public Task fetchTaskIfExists(String taskID)
			throws UnavailableTaskException {
		try {
			Query q = find_by_id.setParameter(1, taskID);
			List resultList = q.getResultList();
			if (resultList.size() < 1)
				throw new UnavailableTaskException("Task does not exist"
						+ taskID);
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
		Query q = _entityManager.createNamedQuery(PIPATask.FIND_BY_URL)
				.setParameter(1, "%" + formUrl);
		return (PIPATask) q.getResultList().get(0);
	}

	/**
	 * Core method. retrieve all the tasks for the given <code>UserRoles</code>
	 */
	public Task[] fetchAllAvailableTasks(UserRoles user) {
		ArrayList userIdList = new ArrayList();
		userIdList.add(user.getUserID());
		Query q = _entityManager.createNamedQuery(Task.FIND_BY_ROLE_USER)
				.setParameter(1, userIdList).setParameter(2,
						user.getAssignedRoles());
		List result = q.getResultList();
		return (Task[]) result.toArray(new Task[result.size()]);
	}

	/**
	 * Core method. retrieve all the tasks for the given <code>UserRoles</code>
	 * and task state, task type
	 */
	public Task[] fetchAvailableTasks(UserRoles user, Class taskClass,
			String subQuery) {
		HashMap params = new HashMap(3);
		params.put(FETCH_USER, user);
		params.put(FETCH_CLASS, taskClass);
		params.put(FETCH_SUB_QUERY, subQuery);
		return fetchAvailableTasks(params);
	}

	public Task[] fetchAvailableTasks_With_Filter(UserRoles user,
			Class taskClass, String subQuery) {
		HashMap params = new HashMap(3);
		params.put(FETCH_USER, user);
		params.put(FETCH_CLASS, taskClass);
		params.put(FETCH_SUB_QUERY, subQuery);
		return fetchAvailableTasks(params);
	}

	public Long countTasks(Map parameters) {
		parameters.put(TaskFetcher.FETCH_COUNT, StringUtils.EMPTY);
		Query q = buildQuery(parameters);
		return (Long) q.getSingleResult();
	}

	private Query buildQuery(Map parameters) {
		UserRoles user = (UserRoles) parameters.get(FETCH_USER);
		Class taskClass = (Class) parameters.get(FETCH_CLASS);
		boolean filter = (Boolean) parameters.get(FETCH_FILTER);
		String subQuery = MapUtils.getString(parameters, FETCH_SUB_QUERY, "");

		ArrayList userIdList = new ArrayList();
		userIdList.add(user.getUserID());
		String baseQuery = parameters.containsKey(FETCH_COUNT) ? QUERY_GENERIC_COUNT
				: QUERY_GENERIC1;
		Query q;
		if (StringUtils.isEmpty(subQuery)) {
			if (taskClass.equals(PATask.class) && filter) {

				Date dateBefore8Hours = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateBefore8Hours);
				calendar.add(Calendar.HOUR, -8);
				dateBefore8Hours = calendar.getTime();

				q = _entityManager.createQuery(
						baseQuery + taskClass.getSimpleName() + QUERY_GENERIC2
								+ QUERY_WITH_FILTER)
						.setParameter(1, userIdList).setParameter(2,
								user.getAssignedRoles()).setParameter(3,
								dateBefore8Hours, TemporalType.TIMESTAMP);
			} else {
				q = _entityManager.createQuery(
						baseQuery + taskClass.getSimpleName() + QUERY_GENERIC2)
						.setParameter(1, userIdList).setParameter(2,
								user.getAssignedRoles());
			}
		} else {
			StringBuffer buffer = new StringBuffer();
			if (taskClass.equals(PATask.class)) {
				buffer.append(baseQuery).append(taskClass.getSimpleName())
						.append(QUERY_GENERIC2 + QUERY_WITH_FILTER);
			} else {
				buffer.append(baseQuery).append(taskClass.getSimpleName())
						.append(QUERY_GENERIC2);
			}
			String trim = subQuery.toLowerCase().trim();
			int orderIndex = trim.indexOf("order");
			if (orderIndex == -1) {
				buffer.append(" and ").append(" ( ").append(subQuery).append(
						" ) ");
			} else {
				if (!trim.startsWith("order"))
					buffer.append(" and (").append(
							subQuery.substring(0, orderIndex)).append(") ")
							.append(subQuery.substring(orderIndex));
				else {
					buffer.append(subQuery);
				}
			}
			if (_logger.isDebugEnabled())
				_logger.debug(buffer.toString());
			if (taskClass.equals(PATask.class) && filter) {

				Date dateBefore8Hours = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateBefore8Hours);
				calendar.add(Calendar.HOUR, -8);
				dateBefore8Hours = calendar.getTime();
				q = _entityManager.createQuery(buffer.toString()).setParameter(
						1, userIdList).setParameter(2, user.getAssignedRoles())
						.setParameter(3, dateBefore8Hours,
								TemporalType.TIMESTAMP);
			} else {
				q = _entityManager.createQuery(buffer.toString()).setParameter(
						1, userIdList).setParameter(2, user.getAssignedRoles());
			}

		}
		return q;
	}

	public Task[] fetchAvailableTasks(Map parameters) {
		Query q = buildQuery(parameters);
		int first = MapUtils.getIntValue(parameters, FETCH_FIRST, -1);
		int max = MapUtils.getIntValue(parameters, FETCH_MAX, -1);
		if (first >= 0 && max > 0) {
			q.setFirstResult(first);
			q.setMaxResults(max);
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
		Query q = _entityManager.createNamedQuery(Task.FIND_BY_USER)
				.setParameter(1, params);
		List result = q.getResultList();
		return (Task[]) result.toArray(new Task[result.size()]);
	}

	/**
	 * Fetch the tasks for a given role
	 */
	public Object[] fetchTasksForRole(String role) {
		List<String> params = new ArrayList<String>();
		params.add(role);
		Query q = _entityManager.createNamedQuery(Task.FIND_BY_ROLE)
				.setParameter(1, params);
		List result = q.getResultList();
		return (Task[]) result.toArray(new Task[result.size()]);
	}

}
