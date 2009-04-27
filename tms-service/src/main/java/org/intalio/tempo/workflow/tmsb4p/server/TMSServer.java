package org.intalio.tempo.workflow.tmsb4p.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.openjpa.persistence.NoResultException;
import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.taskb4p.Attachment;
import org.intalio.tempo.workflow.taskb4p.AttachmentAccessType;
import org.intalio.tempo.workflow.taskb4p.AttachmentInfo;
import org.intalio.tempo.workflow.taskb4p.Comment;
import org.intalio.tempo.workflow.taskb4p.OrganizationalEntity;
import org.intalio.tempo.workflow.taskb4p.Principal;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.taskb4p.TaskStatus;
import org.intalio.tempo.workflow.tms.B4PPersistException;
import org.intalio.tempo.workflow.tms.InvalidQueryException;
import org.intalio.tempo.workflow.tms.InvalidTaskStateException;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.permissions.TaskPermissions;
import org.intalio.tempo.workflow.tmsb4p.message.MessageData;
import org.intalio.tempo.workflow.tmsb4p.server.dao.GenericRoleType;
import org.intalio.tempo.workflow.tmsb4p.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tmsb4p.server.dao.ITaskDAOConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intalio.wsHT.TGrouplist;
import com.intalio.wsHT.TOrganizationalEntity;
import com.intalio.wsHT.TUserlist;
import com.intalio.wsHT.api.TStatus;
import com.intalio.wsHT.api.xsd.TTime;

public class TMSServer implements ITMSServer {

    private static final Logger _logger = LoggerFactory.getLogger(TMSServer.class);
    private IAuthProvider _authProvider;
    private ITaskDAOConnectionFactory _taskDAOFactory;
    private TaskPermissions _permissions;

    public IAuthProvider getAuthProvider() {
        return _authProvider;
    }

    public void setAuthProvider(IAuthProvider provider) {
        _authProvider = provider;
    }

    public ITaskDAOConnectionFactory getTaskDAOFactory() {
        return _taskDAOFactory;
    }

    public void setTaskDAOFactory(ITaskDAOConnectionFactory factory) {
        _taskDAOFactory = factory;
    }

    public TaskPermissions getPermissions() {
        return _permissions;
    }

    public void setPermissions(TaskPermissions _permissions) {
        this._permissions = _permissions;
    }

    private String makeString(Object[] ar) {
        if (ar == null)
            return "";

        StringBuffer buf = new StringBuffer("[");
        for (int i = 0; i < ar.length; i++)
            if (i == 0)
                buf.append(ar[i].toString());
            else
                buf.append(", " + ar[i].toString());
        buf.append("]");
        return buf.toString();
    }

    private String makeString(int[] ar) {
        if (ar == null)
            return "";

        StringBuffer buf = new StringBuffer("[");
        for (int i = 0; i < ar.length; i++)
            if (i == 0)
                buf.append(ar[i]);
            else
                buf.append(", " + ar[i]);
        buf.append("]");
        return buf.toString();
    }

    private boolean checkPermission(UserRoles ur, OrganizationalEntity oe) {
        int i = 0;
        if (oe == null)
            return false;
        Set<Principal> sp = oe.getPrincipals();
        Principal[] s = new Principal[1];
        if (sp == null)
            return false;
        s = sp.toArray(s);
        if (oe.getEntityType().equalsIgnoreCase(OrganizationalEntity.USER_ENTITY)) {
            System.out.println("===>s size=" + s.length);

            for (i = 0; i < s.length; i++) {
                System.out.println("s[" + i + "]=" + s[i].getValue());
                if (ur.getUserID().equalsIgnoreCase(s[i].getValue()))
                    return true;
            }
        } else { // group entity
            for (i = 0; i < s.length; i++)
                if (ur.getAssignedRoles().contains(s[i].getValue()))
                    return true;

        }
        return false;
    }

    protected List<Integer> checkUserTaskRoles(Task task, UserRoles ur) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        OrganizationalEntity oe = null;

        String ao = task.getActualOwner();
        if (ao != null && ao.equalsIgnoreCase(ur.getUserID()))
            ret.add(ITMSServer.ACTUAL_OWNER);

        oe = task.getPotentialOwners();
        if (checkPermission(ur, oe))
            ret.add(ITMSServer.POTENTIAL_OWNERS);

        oe = task.getBusinessAdministrators();
        if (checkPermission(ur, oe))
            ret.add(ITMSServer.BUSINESSADMINISTRATORS);

        oe = task.getExcludedOwners();
        if (checkPermission(ur, oe))
            ret.add(ITMSServer.EXCLUDED_OWNERS);

        oe = task.getNotificationRecipients();
        if (checkPermission(ur, oe))
            ret.add(ITMSServer.RECIPIENTS);

        String u = task.getTaskInitiator();
        if (u.equalsIgnoreCase(ur.getUserID()))
            ret.add(ITMSServer.TASK_INITIATOR);

        oe = task.getTaskStakeholders();
        if (checkPermission(ur, oe))
            ret.add(ITMSServer.TASK_STAKEHOLDERS);

        System.out.println("user in roles " + ret.toString());
        return ret;

    }

    protected void checkPermission(Task task, UserRoles ur, int[] roles) throws IllegalAccessException {

        for (int i = 0; i < roles.length; i++) {
            int role = roles[i];
            OrganizationalEntity oe = null;
            switch (role) {
                case ITMSServer.ACTUAL_OWNER:
                    String ao = task.getActualOwner();
                    if (ao == null || ao.length() == 0)
                        continue;
                    if (ao.equalsIgnoreCase(ur.getUserID()))
                        return;
                    break;
                case ITMSServer.POTENTIAL_OWNERS:
                    oe = task.getPotentialOwners();
                    if (checkPermission(ur, oe))
                        return;
                    break;
                case ITMSServer.BUSINESSADMINISTRATORS:
                    oe = task.getBusinessAdministrators();
                    if (checkPermission(ur, oe))
                        return;
                    break;
                case ITMSServer.EXCLUDED_OWNERS:
                    oe = task.getExcludedOwners();
                    if (checkPermission(ur, oe))
                        return;
                    break;
                case ITMSServer.RECIPIENTS:
                    oe = task.getNotificationRecipients();
                    if (checkPermission(ur, oe))
                        return;
                    break;
                case ITMSServer.TASK_INITIATOR:
                    String u = task.getTaskInitiator();
                    if (u.equalsIgnoreCase(ur.getUserID()))
                        return;

                    break;
                case ITMSServer.TASK_STAKEHOLDERS:
                    oe = task.getTaskStakeholders();
                    if (checkPermission(ur, oe))
                        return;

                    break;
                default:
                    throw new IllegalAccessException("unknow role " + role);
            }
        }
        throw new IllegalAccessException("user " + ur.getUserID() + " have not permission for actino on task " + task.getId() + "you must be role of "
                        + makeString(roles));
    }

    protected void checkPermission(String identifier, UserRoles ur, int[] roles) throws IllegalAccessException, B4PPersistException {
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
            Task task = dao.fetchTaskIfExists(identifier);
            checkPermission(task, ur, roles);
        } catch (UnavailableTaskException e) {
            _logger.error("error when retrieving task, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }
    }

    /**
     * check status, if suspended and timeout, will change status back to states before suspending.
     * Please notice that task status maybe changed after calling checkTaskStatus
     * @param taskId
     * @param status
     * @return task status
     * @throws TMSException
     */
    TaskStatus checkTaskStatus(String taskId, TaskStatus[] status) throws TMSException{
    	ITaskDAOConnection dao = _taskDAOFactory.openConnection();
    	Task task = null;
    	try{
    	 task = dao.fetchTaskIfExists(taskId);
    	}catch(Exception e){
    		throw new B4PPersistException(e);
    	}finally{
    		dao.close();
    	}
    	 if (task==  null)
    		 throw new IllegalArgumentException("fetch task failed");
    	TaskStatus s = task.getStatus();
    	if (s == TaskStatus.SUSPENDED){
    		Date st = task.getSuspendStartTime();
    		System.out.println("suspend start:"+ st.toString() + ", suspend period: " + task.getSuspendPeriod());
    		if (st != null){
    			Date until = new Date(st.getTime()+task.getSuspendPeriod()*1000);
    			Date t = new Date(); // current time
    			System.out.println("current time:"+ t.toString() + ", suspend until: " + until.toString());
    			if (t.after(until)){  // expired 
    				try{
    					task.setStatus(task.getOriginalStatus());
    					//task.setOriginalStatus(null);
    					dao.updateTask(task);
    					dao.commit();
    					s = task.getOriginalStatus();
    				}catch(Exception e){
    					throw new B4PPersistException(e);
    		    	}finally{
    		    		dao.close();
    		    	}
    			}else
    				throw new IllegalStateException("Task suspended");
    		}else // unlimited suspend
    		throw new IllegalStateException("Task suspended");
    	}
    	
    	for (int i =0; i< status.length; i++){
    		if (s == status[i])
    			return s;
    	}
    	
    	throw new IllegalStateException("status error, task must be in (" + makeString(status)+")");
    	
    }
    
	/**************************************
	 * flow-related participant operations
	 ***************************************/

	public void create(Task task, String participantToken) throws TMSException {
		System.out.println("tmsserver-> create task");
		try {
			UserRoles ur = _authProvider.authenticate(participantToken);
			System.out.println("userid:" + ur.getUserID());
			task.setCreatedBy(ur.getUserID());
			task.setTaskInitiator(ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthException(e);
		}

		
		// TODO check if this user in task initialtor
		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
			dao.createTask(task);
			dao.commit();
			if (_logger.isDebugEnabled())
				_logger.debug("Workflow Task " + task + " was created");
			System.out.println("Workflow Task " + task + " was created");
			// TODO : Use credentials.getUserID() :vb
		} catch (NoResultException e) {
			_logger.error("Cannot create Workflow Tasks", e); // TODO :
			// TaskIDConflictException
			// must be rethrowed :vb
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}

	public void remove(String participantToken, String taskId)
			throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {

			Task task = dao.fetchTaskIfExists(taskId);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ taskId);
			
			// check permission (notification recipient)
			checkPermission(task, ur, new int[] { ITMSServer.RECIPIENTS});

			// do query
			dao.deleteTask(taskId);
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + taskId, e);
			
		} finally {
			dao.close();
		}

		return;

	}

	public void start(String participantToken, String identifier)
			throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);
			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			return;
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {
			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (actual owner / potential owner(only in ready states)
			List<Integer> r = checkUserTaskRoles(task, ur);
			if (r.size() == 1 && r.get(0) == ITMSServer.POTENTIAL_OWNERS
					&& task.getStatus() != TaskStatus.READY)
				throw new IllegalAccessException(
						"Potential owner can start task only in ready states");
			if (!(r.contains(ITMSServer.POTENTIAL_OWNERS) && task.getStatus() == TaskStatus.READY)
					&& !r.contains(ITMSServer.ACTUAL_OWNER))
				throw new IllegalAccessException(
						"User must be potential owner (only in ready states) or actual owner");

		// check status ( should be ready )
		checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.READY, TaskStatus.RESERVED});
		
		// update task status
			dao.updateTaskStatus(identifier, TaskStatus.IN_PROGRESS);
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}
	}

	public void stop(String participantToken, String identifier)
			throws TMSException {

		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);
			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {
			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (actual owner / business administrators can
			// stop tasks
			checkPermission(task, ur, new int[] { ITMSServer.ACTUAL_OWNER,
					ITMSServer.BUSINESSADMINISTRATORS });

		// stop task

		// @TODO check status ( should be ready )
		checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.IN_PROGRESS});
		
		// update task status
			dao.updateTaskStatus(identifier, TaskStatus.RESERVED);
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}



	public void claim(String participantToken, String identifier)
			throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);

			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {
			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (actual owner / business administrators can

			checkPermission(task, ur, new int[] { ITMSServer.ACTUAL_OWNER,
					ITMSServer.BUSINESSADMINISTRATORS });

			// check status ( should be ready )
			checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.READY});
			
			// set actual owner
			//task.setActualOwner(ur.getUserID());
			ArrayList<String> users = new ArrayList<String>();
			users.add(ur.getUserID());
			dao.updateTaskRole(identifier, GenericRoleType.actual_owner, users, OrganizationalEntity.USER_ENTITY); 
			dao.updateTask(task);
			
			// update task status
			task.setStatus(TaskStatus.RESERVED);
			
			dao.updateTask(task);
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}

	public void delegate(String participantToken, String identifier, TOrganizationalEntity oe)
			throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);

			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {
			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (actual owner / business administrators /
			// potential owner(only in ready states)
			List<Integer> r = checkUserTaskRoles(task, ur);
			if (r.size() == 1 && r.get(0) == ITMSServer.POTENTIAL_OWNERS
					&& task.getStatus() != TaskStatus.READY)
				throw new IllegalAccessException(
						"Potential owner can delegate task only in ready states");
			if (!r.contains(ITMSServer.ACTUAL_OWNER)
					&& !r.contains(ITMSServer.BUSINESSADMINISTRATORS))
				throw new IllegalAccessException(
						"User must be potential(only in ready states) owner or business adiministrator");

			// check status ( should be ready )
			checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.READY, TaskStatus.IN_PROGRESS, TaskStatus.RESERVED});
			


			// assign task to user and put him into potential owner if he is not
			if (oe.isSetUsers()){
				ArrayList<String> values = new ArrayList<String>();
				TUserlist users = oe.getUsers();
				for (int i = 0; i< users.sizeOfUserArray(); i ++){
					values.add(users.getUserArray(i));
				}
				dao.updateTaskRole(identifier, GenericRoleType.actual_owner, values, OrganizationalEntity.USER_ENTITY);
				
				for (int i=0; i < values.size(); i++){
					if  (!dao.isRoleMember(identifier, new UserRoles(values.get(i),  new String[0]), GenericRoleType.actual_owner)){
						// TODO add to potential owners
					}
					
				}
					
			}
			else{
/*				ArrayList<String> values = new ArrayList<String>();
				TGrouplist groups = oe.getGroups();
				for (int i = 0; i< groups.sizeOfGroupArray(); i ++){
					values.add(groups.getGroupArray(i));
				}
				dao.updateTaskRole(identifier, GenericRoleType.actual_owner, values, OrganizationalEntity.GROUP_ENTITY);
	
				for (int i=0; i < values.size(); i++){
//					if  (!dao.isRoleMember(identifier, new UserRoles(values.get(i),  new String[0]), GenericRoleType.actual_owner)){
//						// TODO add to potential owners
//					}			
				}
*/		
			throw new IllegalArgumentException("Cannot delegate task to a group");
			}
				
			// update task status
			task.setStatus(TaskStatus.RESERVED);
			dao.updateTask(task);
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}

	public void forward(String participantToken, String identifier, TOrganizationalEntity oe )
			throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);

			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();

		try {
			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (actual owner / potential owner / business
			// administrators can forward tasks
			checkPermission(task, ur, new int[] { ITMSServer.ACTUAL_OWNER,
					ITMSServer.BUSINESSADMINISTRATORS });

			//  check status ( should be ready )
			checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.READY, TaskStatus.IN_PROGRESS, TaskStatus.RESERVED});

			// impl logic
			if (oe.isSetUsers()){
				ArrayList<String> values = new ArrayList<String>();
				TUserlist users = oe.getUsers();
				for (int i = 0; i< users.sizeOfUserArray(); i ++){
					values.add(users.getUserArray(i));
				}
				dao.updateTaskRole(identifier, GenericRoleType.actual_owner, values, OrganizationalEntity.USER_ENTITY);
				
				for (int i=0; i < values.size(); i++){
					if  (!dao.isRoleMember(identifier, new UserRoles(values.get(i),  new String[0]), GenericRoleType.actual_owner)){
						// TODO add to potential owners
					}
					
				}
					
			}
			else{
				ArrayList<String> values = new ArrayList<String>();
				TGrouplist groups = oe.getGroups();
				for (int i = 0; i< groups.sizeOfGroupArray(); i ++){
					values.add(groups.getGroupArray(i));
				}
				dao.updateTaskRole(identifier, GenericRoleType.actual_owner, values, OrganizationalEntity.GROUP_ENTITY);
	
				for (int i=0; i < values.size(); i++){
//					if  (!dao.isRoleMember(identifier, new UserRoles(values.get(i),  new String[0]), GenericRoleType.actual_owner)){
//						// TODO add to potential owners
//					}
					
				}
		
				
			}
				
			
			// update status
			task.setStatus(TaskStatus.RESERVED);
			dao.updateTask(task);
			// commit
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}

	public void release(String participantToken, String identifier)
			throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);

			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {
			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);
			// check permission (action owner / business administrators can
			// claim tasks
			checkPermission(task, ur, new int[] { ITMSServer.ACTUAL_OWNER,
					ITMSServer.BUSINESSADMINISTRATORS,
					ITMSServer.POTENTIAL_OWNERS });
			// @TODO check status ( should be ready )
			checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.READY, TaskStatus.IN_PROGRESS});
			
			// update task

			// TODO impl logic

			// update status
			dao.updateTaskStatus(identifier, TaskStatus.READY);

			// commit
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}

	public void resume(String participantToken, String identifier)
			throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);

			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {
			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (action owner / business administrators can
			// claim tasks
			// check permission (actual owner / business administrators /
			// potential owner(only in ready states)
			List<Integer> r = checkUserTaskRoles(task, ur);
			if (r.size() == 1 && r.get(0) == ITMSServer.POTENTIAL_OWNERS
					&& task.getStatus() != TaskStatus.READY)
				throw new IllegalAccessException(
						"Potential owner can delegate task only in ready states");
			if (!r.contains(ITMSServer.ACTUAL_OWNER)
					&& !r.contains(ITMSServer.BUSINESSADMINISTRATORS))
				throw new IllegalAccessException(
						"User must be potential owner or business adiministrator");

			// check status 
			if (task.getStatus() != TaskStatus.SUSPENDED)
				throw new IllegalStateException("Task is not suspended.");
			
			// update task

			// TODO impl logic

			// update status
			dao.updateTaskStatus(identifier, task.getOriginalStatus());

			// commit
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}

	public void skip(String participantToken, String identifier)
			throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);

			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {
			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (actual owner/ initiator / business
			// administrators can skip tasks
			checkPermission(task, ur, new int[] { ITMSServer.ACTUAL_OWNER,
					ITMSServer.BUSINESSADMINISTRATORS,
					ITMSServer.TASK_INITIATOR });

			// check status ( should be ready )
			checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.READY, TaskStatus.IN_PROGRESS, TaskStatus.CREATED, TaskStatus.RESERVED});
			
			// update task

			// TODO impl logic

			// update status
			dao.updateTaskStatus(identifier, TaskStatus.OBSOLETE);

			// commit
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}

	public void fail(String participantToken, String identifier,
			String faultName, XmlObject faultData) throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);

			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {
			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (actual owner)
			checkPermission(task, ur, new int[] { ITMSServer.ACTUAL_OWNER });

			//  check status
			checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.READY, TaskStatus.IN_PROGRESS, TaskStatus.CREATED, TaskStatus.RESERVED});

			// update task

			// TODO impl logic

			// update status
			dao.updateTaskStatus(identifier, TaskStatus.FAILED);

			// commit
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}

	public void complete(String participantToken, String identifier,
			XmlObject taskData) throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);

			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {
			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (actual owner)
			checkPermission(task, ur, new int[] { ITMSServer.ACTUAL_OWNER });

			// check status ( should be ready )
			checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.READY, TaskStatus.IN_PROGRESS, TaskStatus.CREATED, TaskStatus.RESERVED});

			// update task
			// TODO impl logic

			// update status
			task.setStatus(TaskStatus.COMPLETED);
			// update output data
			task.setOutputMessage(taskData.toString()); // ?
			dao.updateTask(task);

			// commit
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}
	}

	public void suspend(String participantToken, String identifier)
			throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);

			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {

			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (actual owner / business administrators /
			// potential owner(only in ready states)
			List<Integer> r = checkUserTaskRoles(task, ur);
			if (r.size() == 1 && r.get(0) == ITMSServer.POTENTIAL_OWNERS
					&& task.getStatus() != TaskStatus.READY)
				throw new IllegalAccessException(
						"Potential owner can delegate task only in ready states");
			if (!r.contains(ITMSServer.ACTUAL_OWNER)
					&& !r.contains(ITMSServer.BUSINESSADMINISTRATORS))
				throw new IllegalAccessException(
						"User must be potential owner or business adiministrator");

			// check status 
			checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.READY, TaskStatus.IN_PROGRESS, TaskStatus.RESERVED});
			
			// impl logic
			task.setOriginalStatus(task.getStatus());
			
			// update status
			dao.updateTaskStatus(identifier, TaskStatus.SUSPENDED);
	
			// commit
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}

	public void suspendUntil(String participantToken, String identifier,
			TTime time) throws TMSException {
		// get user
		UserRoles ur = null;
		try {
			ur = _authProvider.authenticate(participantToken);

			System.out.println("userid:" + ur.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("authenticate user failed", e);
			throw new AuthException(e);
		}
		ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
		try {

			Task task = dao.fetchTaskIfExists(identifier);
			if (task == null)
				throw new IllegalArgumentException("cannot find task "
						+ identifier);

			// check permission (actual owner / business administrators /
			// potential owner(only in ready states)
			List<Integer> r = checkUserTaskRoles(task, ur);
			if (r.size() == 1 && r.get(0) == ITMSServer.POTENTIAL_OWNERS
					&& task.getStatus() != TaskStatus.READY)
				throw new IllegalAccessException(
						"Potential owner can delegate task only in ready states");
			if (!r.contains(ITMSServer.ACTUAL_OWNER)
					&& !r.contains(ITMSServer.BUSINESSADMINISTRATORS))
				throw new IllegalAccessException(
						"User must be potential owner or business adiministrator");

			// check status ( should be ready )
			checkTaskStatus(identifier, new TaskStatus[]{TaskStatus.READY, TaskStatus.IN_PROGRESS, TaskStatus.RESERVED});
		
			// impl logic
			// update task
			Date t_now = new Date();
			int t_p = 0;
			if (time.isSetPointOfTime()){
				Date tt = time.getPointOfTime().getTime();	
				t_p =  (int)(tt.getTime() - System.currentTimeMillis());
			}else if (time.isSetTimePeriod()){
				// convert stupid GDuration to second
				GDuration gd = time.getTimePeriod();
				System.out.println("gd="+gd.toString());
				GDuration gDuration = new GDuration(gd);  
				Calendar cal = Calendar.getInstance();  
				cal.setTimeInMillis(0);  
				GDate base = new GDate(cal);
				System.out.println("base="+base.toString());
				System.out.println("base="+base.getDate().getTime());
				GDate d = base.add(gDuration);
				System.out.println("d="+d.toString());
				System.out.println("d="+d.getDate().getTime());
				t_p = (int)d.getDate().getTime()/1000;
			}
			task.setSuspendStartTime(t_now);
			task.setSuspendPeriod(t_p);
			
	
			task.setOriginalStatus(task.getStatus());
			
			// update status
			task.setStatus(TaskStatus.SUSPENDED);

			dao.updateTask(task);
			
			// commit
			dao.commit();
		} catch (NoResultException e) {
			_logger.error("remove task failed, task id " + identifier, e);
			throw new B4PPersistException(e);
		} finally {
			dao.close();
		}

	}

	/**************************************
     * data operation operations
     * 
     * @throws AuthException
     * @throws IllegalAccessException
     ***************************************/

    public void setPriority(String participantToken, String identifier, int priority) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        Task task = dao.fetchTaskIfExists(identifier);
        // String actualOwner = task.getActualOwner();

        checkPermission(task, ur, new int[] { ITMSServer.ACTUAL_OWNER, ITMSServer.BUSINESSADMINISTRATORS });

        // if (ur.getUserID().equalsIgnoreCase(actualOwner)) {
        task.setPriority(priority);
        dao.updateTask(task);
        dao.commit();
        dao.close();
    }

    public void addAttachment(String participantToken, String identifier, String attachmentName, String accessType, String value) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {
            checkPermission(identifier, ur, new int[] { ITMSServer.ACTUAL_OWNER, ITMSServer.BUSINESSADMINISTRATORS });
            // TODO get contentType
            String contentType = "plain/text";
            if (accessType.equalsIgnoreCase("inline")) {
                dao.addAttachment(identifier, attachmentName, AttachmentAccessType.INLINE, contentType, ur.getUserID(), value);
            } else if (accessType.equalsIgnoreCase("url")) {
                dao.addAttachment(identifier, attachmentName, AttachmentAccessType.URL, contentType, ur.getUserID(), value);
            } else {
                dao.addAttachment(identifier, attachmentName, AttachmentAccessType.OTHER, contentType, ur.getUserID(), value);
            }
            dao.commit();
        } finally {
            dao.close();
        }
    }

    public List<AttachmentInfo> getAttachmentInfos(String participantToken, String identifier) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.POTENTIAL_OWNERS, ITMSServer.ACTUAL_OWNER, ITMSServer.BUSINESSADMINISTRATORS });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();

        List<AttachmentInfo> ret = dao.getAttachmentInfos(identifier);
        dao.close();
        return ret;
    }

    public List<Attachment> getAttachments(String participantToken, String identifier, String attachmentName) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.POTENTIAL_OWNERS, ITMSServer.ACTUAL_OWNER, ITMSServer.BUSINESSADMINISTRATORS });
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        List<Attachment> ret = dao.getAttachments(identifier, attachmentName);
        dao.close();
        return ret;
    }

    public void deleteAttachments(String participantToken, String identifier, String attachmentName) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.ACTUAL_OWNER, ITMSServer.BUSINESSADMINISTRATORS });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        dao.deleteAttachments(identifier, attachmentName);
        dao.commit();
        dao.close();
    }

    public void addComment(String participantToken, String identifier, String text) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.POTENTIAL_OWNERS, ITMSServer.ACTUAL_OWNER, ITMSServer.BUSINESSADMINISTRATORS });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        dao.addComment(identifier, ur.getUserID(), text);
        dao.commit();
        dao.close();
    }

    public List<Comment> getComments(String participantToken, String identifier) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.POTENTIAL_OWNERS, ITMSServer.ACTUAL_OWNER, ITMSServer.BUSINESSADMINISTRATORS });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        List<Comment> comments = dao.getComments(identifier);
        dao.close();
        return comments;
    }

    public Task getTaskByIdentifier(String participantToken, String identifier) throws TMSException {
        // UserRoles ur = _authProvider.authenticate(participantToken);

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        Task task = dao.fetchTaskIfExists(identifier);
        dao.close();
        return task;
    }

    public void setOutput(String participantToken, String identifier, String partName, XmlObject data) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.ACTUAL_OWNER });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        Task task = dao.fetchTaskIfExists(identifier);

        try {
            if (partName == null || partName.trim().length() == 0) {
                task.setOutputMessage(data.xmlText());
            } else {
                try {
                    QName partQName = new QName("http://schemas.xmlsoap.org/wsdl/", "part");
                    StAXOMBuilder builder = new StAXOMBuilder(task.getOutputMessage());
                    OMElement parts = builder.getDocumentElement();

                    MessageData messageData = new MessageData(partQName, parts);

                    messageData.setData(partName, data.xmlText());
                    task.setOutputMessage(messageData.toXML());
                } catch (Exception e) {
                    new IllegalAccessException("Error when parsing the output data");
                }
            }
        } finally {
            dao.updateTask(task);
            dao.commit();
            dao.close();
        }

    }

    public void deleteOutput(String participantToken, String identifier) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.ACTUAL_OWNER });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        Task task = dao.fetchTaskIfExists(identifier);

        try {
            task.setOutputMessage(null);
        } finally {
            dao.updateTask(task);
            dao.commit();
            dao.close();
        }
    }

    public void setFault(String participantToken, String identifier, String faultName, XmlObject data) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.ACTUAL_OWNER });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        Task task = dao.fetchTaskIfExists(identifier);

        try {
            if (faultName == null || faultName.trim().length() == 0) {
                task.setFaultMessage(data.xmlText());
            } else {
                try {
                    QName partQName = new QName("http://schemas.xmlsoap.org/wsdl/", "part");
                    StAXOMBuilder builder = new StAXOMBuilder(task.getFaultMessage());
                    OMElement parts = builder.getDocumentElement();

                    MessageData messageData = new MessageData(partQName, parts);

                    messageData.setData(faultName, data.xmlText());
                    task.setFaultMessage(messageData.toXML());
                } catch (Exception e) {
                    new IllegalAccessException("Error when parsing the fault data");
                }
            }
        } finally {
            dao.updateTask(task);
            dao.commit();
            dao.close();
        }

    }

    public void deleteFault(String participantToken, String identifier) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.ACTUAL_OWNER });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        Task task = dao.fetchTaskIfExists(identifier);

        try {
            task.setFaultMessage(null);
        } finally {
            dao.updateTask(task);
            dao.commit();
            dao.close();
        }
    }

    public String getInput(String participantToken, String identifier, String partName) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.ACTUAL_OWNER, ITMSServer.POTENTIAL_OWNERS, ITMSServer.BUSINESSADMINISTRATORS });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {
            Task task = dao.fetchTaskIfExists(identifier);

            return getMessagePart(task.getInputMessage(), partName);
        } finally {
            dao.close();
        }
    }

    public String getOutput(String participantToken, String identifier, String partName) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.ACTUAL_OWNER, ITMSServer.POTENTIAL_OWNERS, ITMSServer.BUSINESSADMINISTRATORS });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {

            Task task = dao.fetchTaskIfExists(identifier);

            return getMessagePart(task.getOutputMessage(), partName);
        } finally {
            dao.close();
        }
    }

    public String getFault(String participantToken, String identifier) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        checkPermission(identifier, ur, new int[] { ITMSServer.ACTUAL_OWNER, ITMSServer.POTENTIAL_OWNERS, ITMSServer.BUSINESSADMINISTRATORS });

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try{
            Task task = dao.fetchTaskIfExists(identifier);
            
            return task.getFaultMessage();
        }finally {
            dao.close();
        }
    }

    private String getMessagePart(String message, String partName) {
        if (partName == null || partName.trim().length() == 0) {
            return message;
        } else {
            try {
                QName partQName = new QName("http://schemas.xmlsoap.org/wsdl/", "part");
                StAXOMBuilder builder = new StAXOMBuilder(message);
                OMElement parts = builder.getDocumentElement();

                MessageData messageData = new MessageData(partQName, parts);
                return messageData.getData(partName).toString();
            } catch (Exception e) {
                new IllegalAccessException("Error when parsing the fault data");
            }
        }
        return null;
    }

    /*****************************************
     * Query operation
     *****************************************/

    public Collection<Map<String, Object>> query(String participantToken, String selectClause, String whereClause, String orderByClause, int maxTasks,
                    int taskIndexOffset) throws TMSException {
        // get user
        UserRoles ur = _authProvider.authenticate(participantToken);

        // do query
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {
            return dao.query(ur, selectClause, whereClause, orderByClause, maxTasks, taskIndexOffset);
        } catch (Exception e) {
            _logger.error("Query failed", e);
            throw new InvalidQueryException(e);
        } finally {
            dao.close();
        }
    }

    public List<Task> getMyTasks(String participantToken, String taskType, String genericHumanRole, String workQueue, TStatus.Enum[] statusList,
                    String whereClause, String createdOnClause, int maxTasks) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);

        List<TaskStatus> statuses = new ArrayList<TaskStatus>();
        for (int i = 0; i < statusList.length; i++) {
            statuses.add(TaskStatus.valueOf(statusList[i].toString()));
        }
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {
            List<Task> tasks = dao.getMyTasks(ur, taskType, genericHumanRole, workQueue, statuses, whereClause, createdOnClause, maxTasks);

            return tasks;
        } catch (Exception e) {
            _logger.error("Cannot get MyTasks: ", e);
            throw new InvalidQueryException(e);
        } finally {
            dao.close();
        }
    }

    /*****************************************
     * administrative operation
     *****************************************/
    public void activate(String participantToken, String identifier) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();

        Task task = checkAdminOperation(dao, ur, identifier);
        task.setActivationTime(new Date(System.currentTimeMillis()));
        task.setStatus(TaskStatus.READY);
        try {
            dao.updateTask(task);
            dao.commit();
        } catch (Exception e) {
            _logger.error("Cannot activate Task: ", e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }
    }

    public void nominate(String participantToken, String identifier, List<String> principals, boolean isUser) throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();

        Task task = checkAdminOperation(dao, ur, identifier);
        if (isUser) {
            // user type
            if (principals.size() == 1) {
                // status -> reserved
                task.setStatus(TaskStatus.RESERVED);
            } else {
                task.setStatus(TaskStatus.READY);
            }
        }

        String orgType = isUser ? OrganizationalEntity.USER_ENTITY : OrganizationalEntity.GROUP_ENTITY;
        try {
            dao.updateTaskRole(identifier, GenericRoleType.potential_owners, principals, orgType);
            dao.commit();
        } catch (Exception e) {
            _logger.error("Cannot nominate Task: ", e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }
    }

    private Task checkAdminOperation(ITaskDAOConnection dao, UserRoles ur, String identifier) throws AuthException, UnavailableTaskException,
                    InvalidTaskStateException {
        // check whether the user has the business administrator role
        boolean isRightPerson = dao.isRoleMember(identifier, ur, GenericRoleType.business_administrators);
        if (!isRightPerson) {
            throw new AuthException("Only business administrator can activate the task.");
        }

        Task task = null;
        try {
            task = dao.fetchTaskIfExists(identifier);

            if (!TaskStatus.CREATED.equals(task.getStatus())) {
                throw new InvalidTaskStateException("Only created status's Task can be activated or nominated!");
            }
        } catch (UnavailableTaskException e) {
            _logger.error("Cannot activate Task: ", e);
            throw e;
        }

        return task;
    }

    public void setGenericHumanRole(String participantToken, String identifier, GenericRoleType roleType, List<String> principals, boolean isUser)
                    throws TMSException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();

        boolean isRightPerson = dao.isRoleMember(identifier, ur, GenericRoleType.business_administrators);
        if (!isRightPerson) {
            throw new AuthException("Only business administrator can update roles.");
        }

        // Task task = dao.fetchTaskIfExists(identifier);

        if (GenericRoleType.business_administrators.equals(roleType)) {
            // the operator must be one member of the business parter
            boolean isMember = isMemeber(ur, principals, isUser);
            if (!isMember) {
                if (isUser) {
                    // add the operator the user list auto
                    principals.add(ur.getUserID());
                } else {
                    throw new AuthException("The user should be one memeber of the Business Parter.");
                }
            }
        }

        // update the generic role data to task
        if (GenericRoleType.task_initiator.equals(roleType) || GenericRoleType.actual_owner.equals(roleType)) {
            // can be one user
            if ((isUser) && (principals.size() == 1)) {
            } else {
                throw new AuthException("Only one user can be: " + roleType.name());
            }
        }

        String orgType = isUser ? OrganizationalEntity.USER_ENTITY : OrganizationalEntity.GROUP_ENTITY;
        try {
            dao.updateTaskRole(identifier, roleType, principals, orgType);
            dao.commit();
        } catch (Exception e) {
            _logger.error("Cannot set generic role: ", e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }
    }

    /**
     * check whether the user belongs to one set of principals.
     * 
     * @param ur
     * @param principals
     * @param isUser
     * @return
     */
    private boolean isMemeber(UserRoles ur, List<String> principals, boolean isUser) {
        String uId = ur.getUserID();
        if (isUser) {
            return principals.contains(uId);
        }

        Set<String> roles = ur.getAssignedRoles();
        for (String role : roles) {
            if (principals.contains(role)) {
                return true;
            }
        }

        return false;
    }
}