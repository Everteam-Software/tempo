package org.intalio.tempo.workflow.util.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.TaskType;
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
    Query q = _entityManager.createNamedQuery(PIPATask.FIND_BY_URL).setParameter(1, "%" + formUrl);
    return (PIPATask) q.getSingleResult();
  }

  /**
   * Core method. retrieve all the tasks for the given <code>UserRoles</code>
   */
  public Task[] fetchAllAvailableTasks(UserRoles user) {
    ArrayList userIdList = new ArrayList();
    userIdList.add(user.getUserID());
    Query q = _entityManager.createNamedQuery(Task.FIND_BY_ROLE_USER).setParameter(1, userIdList).setParameter(2, user.getAssignedRoles().toCollection());
    List result = q.getResultList();
    return (Task[]) result.toArray(new Task[result.size()]);
  }

  /**
   * Core method. retrieve all the tasks for the given <code>UserRoles</code>
   * and task state, task type
   */
  public Task[] fetchAvailableTasks(UserRoles user, TaskType taskType, TaskState taskState) {
    ArrayList userIdList = new ArrayList();
    userIdList.add(user.getUserID());
    Query q;
    switch (taskType) {
      case PA:
        if (taskState == null) {
          q = _entityManager.createNamedQuery(PATask.FIND_BY_PA_USER_ROLE).setParameter(1, userIdList).setParameter(2, user.getAssignedRoles().toCollection());
        } else {
          q = _entityManager.createNamedQuery(PATask.FIND_BY_PA_USER_ROLE_STATE).setParameter(1, userIdList).setParameter(2,
              user.getAssignedRoles().toCollection()).setParameter(3, taskState);
        }
        break;
      case PIPA:
        q = _entityManager.createNamedQuery(PIPATask.FIND_BY_PIPA_USER_ROLE).setParameter(1, userIdList)
            .setParameter(2, user.getAssignedRoles().toCollection());
        break;
      case Notification:
        q = _entityManager.createNamedQuery(Notification.FIND_BY_NOTI_USER_ROLE).setParameter(1, userIdList).setParameter(2,
            user.getAssignedRoles().toCollection());
        break;
      default:
        q = _entityManager.createNamedQuery(Task.FIND_BY_ROLE_USER).setParameter(1, userIdList).setParameter(2, user.getAssignedRoles().toCollection());
        break;
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
