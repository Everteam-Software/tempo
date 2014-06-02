package org.intalio.tempo.workflow.util.jpa;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.CustomColumn;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.PIPATaskOutput;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
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

	final static Logger _logger = LoggerFactory.getLogger(TaskFetcher.class);
	private EntityManager _entityManager;
	private Session session;
	private Query find_by_id;
	private Query find_pipa_task_output_by_task_id;
	private final String QUERY_GENERIC1 = "select T from ";
	private final String QUERY_GENERIC_DISTINCT = "select DISTINCT T from ";
	private final String QUERY_GENERIC_GROUPBY = " GROUP BY T._id ";
	private final String QUERY_GENERIC_COUNT = "select COUNT(DISTINCT T) from ";
//	private final String QUERY_GENERIC2 = " T where (T._userOwners = (?1) or T._roleOwners = (?2)) ";
	private final String QUERY_GENERIC2_FOR_ADMIN = " T ";
	private final String QUERY_GENERIC2_FOR_CUSTOM_COLUMN_SEARCH = " T JOIN T._customMetadata C ";
	private final String QUERY_GENERIC2 = " LEFT JOIN T._userOwners UO LEFT JOIN T._roleOwners RO where (UO in (:users) or RO in (:roles) or RO in ('*')) ";
	
	// private final String DELETE_TASKS =
	// "delete from Task m where m._userOwners in (?1) or m._roleOwners in (?2) "
	// ;
	private final String DELETE_ALL_TASK_WITH_ID = "delete from Task m where m._id = (?) ";

	private final String GET_CUSTOM_COL_SORT_PREFIX = "SELECT DISTINCT t0.ID,t0.complete_soap_action,t0.deadline,t0.instance_id,t0.priority,t0.process_id,t0.state,t1.creation_date,t1.description,t1.form_url,t1.taskid,t1.internal_id,t1.last_active_date,t1.last_assigned_date, t4.value FROM tempo_pa t0 INNER JOIN tempo_task t1 ON t0.ID = t1.id LEFT OUTER JOIN tempo_generic t4 ON t0.ID = t4.PATASK_ID and t4.key0 = ";
	private final String GET_CUSTOM_COL_SORT_POSTFIX = " (t0.state = 0 OR t0.state = 3) ORDER BY t4.value ";
	private final String GET_CUSTOM_COL_SORT_ADMIN_USER_CONDITION = " LEFT OUTER JOIN tempo_user t2 ON t1.id = t2.TASK_ID LEFT OUTER JOIN tempo_role t3 ON t1.id = t3.TASK_ID WHERE ( t2.element IN (?) ";
	private final String GET_CUSTOM_COL_SORT_ADMIN_ROLE_CONDITION = " OR t3.element = ?";
	private static String DATABASE_PRODUCT_NAME = "";
	private static String DATABASE_MYSQL = "MySQL";

	public TaskFetcher(EntityManager em) {
		this._entityManager = em;
		this.session = em.unwrap(Session.class);
		this.find_by_id = session.getNamedQuery(Task.FIND_BY_ID);
		this.find_pipa_task_output_by_task_id = session.getNamedQuery(PIPATaskOutput.FIND_BY_TASK_ID_AND_USER);
		/*
		 * Fix for CON-794
		 * Getting database name from JPA Entity Manager.
		 * For using Group By clause for MYSQL and DISTINCT for other databases
		 */
		if("".equals(DATABASE_PRODUCT_NAME)) {
		    Connection conn = null;
		    try {
		        conn = session.connection();
		        java.sql.DatabaseMetaData dbmd = conn.getMetaData();
		        DATABASE_PRODUCT_NAME = dbmd.getDatabaseProductName();
		    }catch (Exception e) {
		        _logger.error("Error while getting Database name ", e);
		    }finally{
		        if(conn != null)
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        _logger.error("Error while closing connection ", e);
                    }
		    }
		}
	}

	/**
	 * Retrieve and load a task if it exists
	 * 
	 * @throws UnavailableTaskException
	 */
	public Task fetchTaskIfExists(String taskID)
			throws UnavailableTaskException {
		try {
			Query q = find_by_id.setParameter(0, taskID);
			List resultList = q.list();
			if (resultList.size() < 1)
				throw new UnavailableTaskException("Task does not exist"
						+ taskID);
			return (Task) resultList.get(0);
		} catch (NoResultException nre) {
			throw new UnavailableTaskException("Task does not exist" + taskID);
		}
	}
	
	   public List<Task> fetchTaskIfExistsfrominstanceID(String instanceid)
       throws UnavailableTaskException {
	       try {
       Query q = session.getNamedQuery(PATask.FIND_BY_INSTANCEID);
       q.setParameter(0, instanceid);
       List<Task> resultList = q.list();
       if (resultList.size() < 1)
           throw new UnavailableTaskException("Task does not exist with InstanceID"
                   + instanceid);
       return  resultList;
	       } catch (NoResultException nre) {
       throw new UnavailableTaskException("Task does not exist" + instanceid);
   }
}

	public int deleteTasksWithID(String taskID) {
		Query q = session.createQuery(DELETE_ALL_TASK_WITH_ID);
		q.setParameter(0, taskID);
		return q.executeUpdate();
	}

	/**
	 * Fetch a PIPA task from its URL
	 * 
	 * @throws UnavailableTaskException
	 */
	public PIPATask fetchPipaFromUrl(String formUrl)
			throws UnavailableTaskException {
        if(formUrl == null) throw new UnavailableTaskException();
		Query q = session.getNamedQuery(PIPATask.FIND_BY_URL)
				.setParameter(0, formUrl);
		try {
			return (PIPATask) q.uniqueResult();
		} catch (NoResultException nre) {
			throw new UnavailableTaskException(
					"Task with following endpoint does not exist:" + formUrl);
		}
	}
	
	/**
	 * Core method. retrieve all the tasks for the given <code>UserRoles</code>
	 */
	public Task[] fetchAllAvailableTasks(UserRoles user) {
		ArrayList userIdList = new ArrayList();
		userIdList.add(user.getUserID());
		Query q = session.getNamedQuery(Task.FIND_BY_ROLE_USER)
				.setParameter(0, userIdList).setParameter(1,
				                user.getAssignedRoles());
		List result = q.list();
		return (Task[]) result.toArray(new Task[result.size()]);
	}

        /**
         * Core method. retrieve all the tasks for the given <code>Users</code>
         * @param users List<String>
         * @return tasks List<Task>
         */
        public final List<Task> fetchAllAvailableTasks(
                                final List<String> users) {
            Query q = session.getNamedQuery(Task.FIND_BY_USER)
                    .setParameter(0, users);
            List<Task> result = q.list();
            return result;
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

	public Long countTasks(Map parameters) {
//	    Task[] tasks = fetchAvailableTasks(parameters);
//	    Long count = (long) tasks.length;
//		return count;
	    parameters.put(TaskFetcher.FETCH_COUNT, StringUtils.EMPTY);
	    Query q = buildQuery(parameters);
	    return (Long) q.uniqueResult();
	}

	private Query buildQuery(Map parameters) {
		// _entityManager.clear();
		UserRoles user = (UserRoles) parameters.get(FETCH_USER);
		Class taskClass = (Class) parameters.get(FETCH_CLASS);		
		String subQuery = MapUtils.getString(parameters, FETCH_SUB_QUERY, "");
		ArrayList userIdList = new ArrayList();
		userIdList.add(user.getUserID());
		userIdList.addAll(user.getVacationUsers());
		String baseQuery = null;
		boolean isGroupByRequired = false;
		if(parameters.containsKey(FETCH_COUNT)){
		    baseQuery =  QUERY_GENERIC_COUNT;
		}else if(DATABASE_MYSQL.equalsIgnoreCase(DATABASE_PRODUCT_NAME)) {
		    //we are not using DISTINCT for MYSQL database to improve performance
		    baseQuery = QUERY_GENERIC1;
		    isGroupByRequired = true;
		}else {
		    baseQuery = QUERY_GENERIC_DISTINCT;
		}
		Query q;
		boolean isWorkflowAdmin=user.isWorkflowAdmin();
		//Constructing native query for sorting on custom metadata column.
		String orderClause = "order by t.";
		int customMetaDataIndex = subQuery.indexOf("_customMetadata");
		int OrderByIndex = subQuery.toLowerCase().indexOf(orderClause);
		if (!parameters.containsKey(FETCH_COUNT)
                    &&  customMetaDataIndex > 0 && OrderByIndex > 0 && customMetaDataIndex > OrderByIndex ) {
                    String sortOrder = subQuery.toLowerCase().indexOf(
                            "_custommetadata desc") > 0 ? "DESC" : "ASC";
                    String sortCol = subQuery.substring(OrderByIndex + orderClause.length(), customMetaDataIndex);
                    String[] roles = (String[]) user.getAssignedRoles().toArray(
                            new String[user.getAssignedRoles().size()]);
                    String roleCondition = "";
                    if (!isWorkflowAdmin) {
                        for (int i = 0; roles != null && i < roles.length; i++) {
                            roleCondition += GET_CUSTOM_COL_SORT_ADMIN_ROLE_CONDITION;
                        }
                    }
                    StringBuffer customQuery = new StringBuffer();
                    customQuery.append(GET_CUSTOM_COL_SORT_PREFIX).append(
                            "'" + sortCol + "'");
                    if (!isWorkflowAdmin) {
                        customQuery.append(GET_CUSTOM_COL_SORT_ADMIN_USER_CONDITION)
                                .append(roleCondition).append(" ) AND");
                    } else {
                        customQuery.append(" WHERE ");
                    }
                    customQuery.append(GET_CUSTOM_COL_SORT_POSTFIX).append(sortOrder);
                    SQLQuery q1 = session.createSQLQuery(customQuery.toString());
                    q1.addEntity(PATask.class);
                    if (!isWorkflowAdmin) {
                        q1.setParameter(0, user.getUserID());
                        for (int i = 0; i < roles.length; i++) {
                            q1.setParameter(i + 1, roles[i]);
                        }
                    }
                    return q1;
                   }
		if (StringUtils.isEmpty(subQuery)) {
			if(isWorkflowAdmin) {
				q = session.createQuery(
					baseQuery + taskClass.getSimpleName() + QUERY_GENERIC2_FOR_ADMIN);
			}else{
				q = session.createQuery(
						baseQuery + taskClass.getSimpleName() + QUERY_GENERIC2_FOR_ADMIN + QUERY_GENERIC2).setParameterList("users", userIdList).setParameterList("roles",user.getAssignedRoles());
				
			}
				
		} else {
				StringBuffer buffer = new StringBuffer();
				
				if(isWorkflowAdmin){
					buffer.append(baseQuery).append(taskClass.getSimpleName());
					if (subQuery.toLowerCase().indexOf("key(c)") >= 0 || subQuery.toLowerCase().indexOf("value(c)") >= 0) {
					    buffer.append(QUERY_GENERIC2_FOR_CUSTOM_COLUMN_SEARCH);
					} else {
					    buffer.append(QUERY_GENERIC2_FOR_ADMIN);
					}
					String trim = subQuery.toLowerCase().trim();
					int orderIndex = trim.indexOf("order");
					if (orderIndex == -1) {
						buffer.append(" where ").append(subQuery);
					} else {
						if (!trim.startsWith("order")){
							buffer.append(" where ").append(subQuery);
						}
						else {
							buffer.append(subQuery);
						}
				}
			}else{
				buffer.append(baseQuery).append(taskClass.getSimpleName());
				if (subQuery.toLowerCase().indexOf("key(c)") >= 0 || subQuery.toLowerCase().indexOf("value(c)") >= 0) {
				    buffer.append(QUERY_GENERIC2_FOR_CUSTOM_COLUMN_SEARCH);
				} else {
				    buffer.append(QUERY_GENERIC2_FOR_ADMIN);
				}
				buffer.append(QUERY_GENERIC2);
				String trim = subQuery.toLowerCase().trim();
                int orderIndex = trim.indexOf("order");
                int groupIndex = trim.indexOf("group");
                if (orderIndex == -1 && groupIndex == -1) {
                    buffer.append(" and ").append(" ( ").append(subQuery)
                            .append(" ) ");
                    if(isGroupByRequired) {
                        //Apply Group By clause only for MySQL database
                        buffer.append(QUERY_GENERIC_GROUPBY);
                    }
                } else if (groupIndex == -1) {
                    if (!trim.startsWith("order")) {
                        buffer.append(" and (")
                                .append(subQuery.substring(0, orderIndex))
                                .append(") ");
                        if(isGroupByRequired) {
                            buffer.append(QUERY_GENERIC_GROUPBY);
                        }
                        buffer.append(subQuery.substring(orderIndex));
                    }
                    else {
                        if(isGroupByRequired) {
                            buffer.append(QUERY_GENERIC_GROUPBY);
                        }
                        buffer.append(subQuery);
                    }
                } else if (orderIndex == -1) {
                    if (!trim.startsWith("group"))
                        buffer.append(" and (")
                                .append(subQuery.substring(0, groupIndex))
                                .append(") ")
                                .append(subQuery.substring(groupIndex));
                    else {
                        buffer.append(subQuery);
                    }
                } else {
                    int index = (groupIndex < orderIndex) ? groupIndex
                            : orderIndex;
                    if (!(trim.startsWith("group") || trim.startsWith("order")))
                        buffer.append(" and (")
                                .append(subQuery.substring(0, index))
                                .append(") ").append(subQuery.substring(index));
                    else {
                        buffer.append(subQuery);
                    }
                }
			}	
			
			if (_logger.isDebugEnabled()){
				_logger.debug(buffer.toString());
				_logger.debug("Parameter 1:" + userIdList);				
				_logger.debug("Parameter 2:" + user.getAssignedRoles());
				_logger.debug("isWorkflowAdmin" + isWorkflowAdmin);
			}			
		
			if(isWorkflowAdmin) {
				q = session.createQuery(buffer.toString());
			} else {
				q = session.createQuery(buffer.toString()).setParameterList("users",userIdList).setParameterList("roles", user.getAssignedRoles());
			}
		}
		return q;
	}
	
//	private Query buildQueryForSingleRole(Map parameters, String role){
//	       UserRoles user = (UserRoles) parameters.get(FETCH_USER);
//	        Class taskClass = (Class) parameters.get(FETCH_CLASS);
//	        String subQuery = MapUtils.getString(parameters, FETCH_SUB_QUERY, "");
//	        String baseQuery = parameters.containsKey(FETCH_COUNT) ? QUERY_GENERIC_COUNT
//	                : QUERY_GENERIC1;
//	        Query q;
//	        if (StringUtils.isEmpty(subQuery)) {
//	            q = _entityManager.createQuery(
//	                    baseQuery + taskClass.getSimpleName() + QUERY_GENERIC2)
//	                    .setParameter(1, user.getUserID()).setParameter(2,
//	                            role);
//	        } else {
//	            StringBuffer buffer = new StringBuffer();
//	            buffer.append(baseQuery).append(taskClass.getSimpleName()).append(
//	                    QUERY_GENERIC2);
//
//	            String trim = subQuery.toLowerCase().trim();
//	            int orderIndex = trim.indexOf("order");
//	            if (orderIndex == -1) {
//	                buffer.append(" and ").append(" ( ").append(subQuery).append(
//	                        " ) ");
//	            } else {
//	                if (!trim.startsWith("order"))
//	                    buffer.append(" and (").append(
//	                            subQuery.substring(0, orderIndex)).append(") ")
//	                            .append(subQuery.substring(orderIndex));
//	                else {
//	                    buffer.append(subQuery);
//	                }
//	            }
//	            if (_logger.isDebugEnabled()){
//	                _logger.debug(buffer.toString());
//	                _logger.debug("Parameter 1:" + user.getUserID());
//	                _logger.debug("Parameter 2:" + role);
//	            }
//	            q = _entityManager.createQuery(buffer.toString()).setParameter(1,
//	                    user.getUserID()).setParameter(2, role);
//	        }
//	        return q;
//	}

	public Task[] fetchAvailableTasks(Map parameters) {
//	    UserRoles user = (UserRoles) parameters.get(FETCH_USER);
//	    Set<String> roles = user.getAssignedRoles();
//	    Set result = new HashSet();
//	    for(String role : roles){
//	        Query q = buildQueryForSingleRole(parameters, role);
//	        int first = MapUtils.getIntValue(parameters, FETCH_FIRST, -1);
//	        int max = MapUtils.getIntValue(parameters, FETCH_MAX, -1);
//
//	        // WARNING: there is a bug in OpenJPA 2.0. You need to call setMax #before# setFirst
//	        if (max > 0)
//	            q.setMaxResults(max);
//	        if (first >= 0)
//	            q.setFirstResult(first);
//	        List singleRoleResult = q.getResultList();
//	        for( Object object : singleRoleResult){
//	            result.add(object);
//	        }
//	    }
		Query q = buildQuery(parameters);
		int first = MapUtils.getIntValue(parameters, FETCH_FIRST, -1);
		int max = MapUtils.getIntValue(parameters, FETCH_MAX, -1);

		// WARNING: there is a bug in OpenJPA 2.0. You need to call setMax #before# setFirst
		if (max > 0)
			q.setMaxResults(max);
		if (first >= 0)
			q.setFirstResult(first);
		if(_logger.isDebugEnabled())
            _logger.debug("Executing query: "+q.toString());
		List result = q.list();
		if(_logger.isDebugEnabled())
            _logger.debug("Returning the resultSetList");
		return (Task[]) result.toArray(new Task[result.size()]);
	}

	/**
	 * Fetch the tasks for a given user
	 */
	public Task[] fetchTasksForUser(String user) {
		List<String> params = new ArrayList<String>();
		params.add(user);
		Query q = session.getNamedQuery(Task.FIND_BY_USER)
				.setParameter(0, params);
		List result = q.list();
		return (Task[]) result.toArray(new Task[result.size()]);
	}

	/**
	 * Fetch the tasks for a given role
	 */
	public Object[] fetchTasksForRole(String role) {
		List<String> params = new ArrayList<String>();
		params.add(role);
		Query q = session.getNamedQuery(Task.FIND_BY_ROLE)
				.setParameter(0, params);
		List result = q.list();
		return (Task[]) result.toArray(new Task[result.size()]);
	}

	/**
	 * Fetch Custom Columns from its Process Name.
	 * 
	 * @throws UnavailableTaskException
	 */
	public List<CustomColumn> fetchCustomColumnIfExistsfromprocessname(
			String processName) throws UnavailableTaskException {
       try {
           Query q = session.getNamedQuery(CustomColumn.FIND_BY_PROCESS_NAME);
           q.setParameter(0, processName);
           List<CustomColumn> resultList = q.list();
//           if (resultList.size() < 1)
//               throw new UnavailableTaskException("Custom Column does not exist with ProcessName"
//                       + processName);
           return  resultList;
    	       } catch (NoResultException nre) {
           throw new UnavailableTaskException("Custom Column does not exist " + processName + nre);
       }
	}
	
	public List<String> fetchCustomColumns(){
	        Query q = session.getNamedQuery( CustomColumn.FIND_ALL_CUSTOM_COLUMNS);
	        List<String> resultList = q.list();
	        return resultList;
	}
	
	public PIPATaskOutput fetchPIPATaskOutput(String taskId, String userOwner) {
		   Query query = find_pipa_task_output_by_task_id.setParameter(0, taskId).setParameter(1, userOwner);
		   List resultList = query.list();
		   PIPATaskOutput pipaTaskOutput=null;
		   if(resultList!= null && !resultList.isEmpty()){
			   pipaTaskOutput = (PIPATaskOutput) resultList.get(0);
			   
		   }
		   return pipaTaskOutput;
	}
	/**
     * Fetch the pending and claimed task count for all users.
     */
    public List<Object> fetchTaskCountForUsers() {
        Query q = session.getNamedQuery(PATask.GET_PENDING_CLAIMED_TASK_COUNT_FOR_ALL_USERS);
        List result = q.list();
        List <Object>resultSet = new ArrayList<Object>();
        
        Iterator<Object> iterator = result.iterator();
        while(iterator.hasNext())
        {
            Object[] row = (Object[]) iterator.next();
            HashMap <String,Object>rowData = new HashMap<String,Object>();
            rowData.put("Count",row[0]);
            rowData.put("State",((TaskState)row[1]).getName());
            rowData.put("User",row[2]);
            resultSet.add(rowData);
        }
        
        return resultSet;
    }

    /**
     * Fetch the pending and claimed task count for all users.
     */
    public List<Object> fetchTaskDistributionByUserStatusAndTime(Date since, Date until,
            List<String> users, List<String> statusList) {
        List<TaskState> taskStatusList = new ArrayList<TaskState>();

        if (statusList == null) {
            taskStatusList.add(TaskState.READY);
            taskStatusList.add(TaskState.CLAIMED);
            taskStatusList.add(TaskState.COMPLETED);
        } else {
            for (String status : statusList) {
                taskStatusList.add(TaskState.valueOf(status));
            }
        }

        Query q = null;

        if (users != null) {
            q = session.getNamedQuery(PATask.GET_TASK_DISTRIBUTION_FOR_USERS_BASED_ON_TIME);
            q.setParameter("since", since).setParameter("until", until).setParameterList("userOwners", users)
                    .setParameterList("states", taskStatusList);
        } else {
            q = session.getNamedQuery(PATask.GET_TASK_DISTRIBUTION_BY_USERS_BASED_ON_TIME);
            q.setParameter("since", since).setParameter("until", until).setParameterList("states",
                    taskStatusList);
        }

        List result = q.list();
        List<Object> resultSet = new ArrayList<Object>();

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            HashMap<String, Object> rowData = new HashMap<String, Object>();
            rowData.put("Count", row[0]);
            rowData.put("State", ((TaskState) row[1]).getName());
            rowData.put("User", row[2]);
            resultSet.add(rowData);
        }

        return resultSet;
    }

    /**
     * Fetch the pending and claimed task count for all users.
     */
    public List<Object> fetchTaskDistributionByRoleStatusAndTime(Date since, Date until,
            List<String> roles, List<String> statusList) {
        List<TaskState> taskStatusList = new ArrayList<TaskState>();

        if (statusList == null) {
            taskStatusList.add(TaskState.READY);
            taskStatusList.add(TaskState.CLAIMED);
            taskStatusList.add(TaskState.COMPLETED);
        } else {
            for (String status : statusList) {
                taskStatusList.add(TaskState.valueOf(status));
            }
        }

        Query q = null;

        if (roles != null) {
            q = session.getNamedQuery(PATask.GET_TASK_DISTRIBUTION_FOR_ROLES_BASED_ON_TIME);
            q.setParameter("since", since).setParameter("until", until).setParameterList("roleOwners", roles)
                    .setParameterList("states", taskStatusList);
        } else {
            q = session.getNamedQuery(PATask.GET_TASK_DISTRIBUTION_BY_ROLES_BASED_ON_TIME);
            q.setParameter("since", since).setParameter("until", until).setParameterList("states",
                    taskStatusList);
        }

        List result = q.list();
        List<Object> resultSet = new ArrayList<Object>();

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            HashMap<String, Object> rowData = new HashMap<String, Object>();
            rowData.put("Count", row[0]);
            rowData.put("State", ((TaskState) row[1]).getName());
            rowData.put("Role", row[2]);
            resultSet.add(rowData);
        }

        return resultSet;
    }

    /**
     * Fetch the average completion time for tasks assigned to given users.
     */
    public List<Object> fetchAverageTaskCompletionTime(Date since, Date until,
            List<String> users) {

        Query q = null;

        q = session.getNamedQuery(PATask.GET_AVERAGE_COMPLETION_TIME_BY_USER);
        q.setParameter("since", since).setParameter("until", until)
                .setParameterList("userOwners", users);

        List result = q.list();
        List<Object> resultSet = new ArrayList<Object>();

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            HashMap<String, Object> rowData = new HashMap<String, Object>();
            rowData.put("EndDate", row[0]);
            rowData.put("StartDate", row[1]);
            rowData.put("User", row[2]);
            resultSet.add(rowData);
        }

        return resultSet;
    }

    /**
     * Fetch the task count for all states.
     */
    public List<Object> fetchTaskCountByStatus(Date since, Date until) {
        Query q = session.getNamedQuery(PATask.GET_TASK_COUNT_BY_STATUS);
        q.setParameter("since", since);
        q.setParameter("until", until);

        List result = q.list();
        List <Object>resultSet = new ArrayList<Object>();

        Iterator<Object> iterator = result.iterator();
        while(iterator.hasNext())
        {
            Object[] row = (Object[]) iterator.next();
            HashMap <String,Object>rowData = new HashMap<String,Object>();
            rowData.put("Count",row[0]);
            rowData.put("State",((TaskState)row[1]).getName());
            resultSet.add(rowData);
        }

        return resultSet;
    }

    /**
     * Fetch the task count for all priorities.
     */
    public List<Object> fetchTaskCountByPriority(Date since, Date until) {
        Query q = session.getNamedQuery(PATask.GET_TASK_COUNT_BY_PRIORITY);
        q.setParameter("since", since);
        q.setParameter("until", until);

        List result = q.list();
        List <Object>resultSet = new ArrayList<Object>();

        Iterator<Object> iterator = result.iterator();
        while(iterator.hasNext())
        {
            Object[] row = (Object[]) iterator.next();
            HashMap <String,Object>rowData = new HashMap<String,Object>();
            rowData.put("Count",row[0]);
            rowData.put("Priority",row[1]);
            resultSet.add(rowData);
        }

        return resultSet;
    }

    /**
     * Fetch the task count by creation date.
     */
    public Map<String, Date> fetchTaskCountByCreationDate(Date since, Date until) {
        Query q = session.getNamedQuery(PATask.GET_TASK_COUNT_BY_CREATION_DATE);
        q.setParameter("since", since);
        q.setParameter("until", until);

        Map<String, Date> taskCreationMap = new HashMap<String, Date>();

        List result = q.list();
        Iterator<Object> iterator = result.iterator();

        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            String taskId = (String) row[0];
            Date taskCreationDate = (Date) row[1];
            taskCreationMap.put(taskId, taskCreationDate);
        }

        return taskCreationMap;
    }

    public Map<String, Long> getMaxTaskCompletionForUsers(Date since,
            Date until) {

        Map<String, Long> taskSummary = new HashMap<String, Long>();
        Query q = null;

        q = session.getNamedQuery(PATask.GET_COMPLETED_TASK_COUNT_FOR_USERS);
        q.setParameter("since", since).setParameter("until", until);

        List result = q.list();

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            taskSummary.put((String) row[1], (Long)row[0]);
        }

        return taskSummary;
    }
}
