package org.intalio.tempo.workflow.tmsb4p.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
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
import org.intalio.tempo.workflow.tms.InvalidTaskStateException;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.permissions.TaskPermissions;
import org.intalio.tempo.workflow.tmsb4p.server.dao.GenericRoleType;
import org.intalio.tempo.workflow.tmsb4p.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tmsb4p.server.dao.ITaskDAOConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**************************************
     * flow-related participant operations
     ***************************************/

    public void create(Task task, String participantToken) throws TMSException {
        System.out.println("tmsserver-> create task");
        try {
            UserRoles ur = _authProvider.authenticate(participantToken);
            System.out.println("userid:" + ur.getUserID());
            task.setCreatedBy(ur.getUserID());
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
        } catch (Exception e) {
            _logger.error("Cannot create Workflow Tasks", e); // TODO :
            // TaskIDConflictException
            // must be rethrowed :vb
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }

    }

    public void remove(String participantToken, String taskId) throws TMSException {
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }

        // do query
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {
            dao.deleteTask(taskId);
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + taskId, e);
        } finally {
            dao.close();
        }

        return;

    }


    public void start(String participantToken, String identifier) throws TMSException {
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);
            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            return;
        }

        // @TODO check permision (action owner / potential owner can start task

        // @TODO check status ( should be ready )

        // update task status
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
            dao.updateTaskStatus(identifier, TaskStatus.IN_PROGRESS);
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }
    }

    public void stop(String participantToken, String identifier) throws TMSException {

        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);
            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }

        // @TODO check permission (action owner / business administrators can
        // stop task

        // @TODO check status ( should be ready )

        // update task status
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
            dao.updateTaskStatus(identifier, TaskStatus.RESERVED);
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }

    }

    private boolean checkPermission(UserRoles ur, OrganizationalEntity oe){
    	int i = 0;
   	    Principal[] s = (Principal[])oe.getPrincipals().toArray();
    	if (oe.getEntityType().equalsIgnoreCase(OrganizationalEntity.USER_ENTITY)){
		
			 for (i = 0; i < s.length; i++){
				 if (ur.getUserID().equalsIgnoreCase(s[i].getValue()))
					 return true;
			 }
		}else { // group entity
			 for (i = 0; i < s.length; i++)
				if  (ur.getAssignedRoles().contains(s[i].getValue()))
						return true; 
		
		}
    	return false;
    }
    
    protected List<Integer> checkUserTaskRoles(Task task, UserRoles ur){
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
     
   			return ret;
   			
}
    
    protected void checkPermission(Task task, UserRoles ur, int[] roles) throws IllegalAccessException{
    	
    	for (int i = 0; i< roles.length; i++){
    		int role = roles[i];
    		OrganizationalEntity oe = null;
    		switch (role){
    		case ITMSServer.ACTUAL_OWNER:
    			String ao = task.getActualOwner();
    			if (ao == null || ao.length()==0)
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
    			throw new IllegalAccessException("unknow role "+ role);
    		}
    	}
    	throw new IllegalAccessException("user "+ ur.getUserID() +" have not permission for actino on task " + task.getId());
    }
    
    public void claim(String participantToken, String identifier) throws TMSException {
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);

            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
        	Task task = dao.fetchTaskIfExists(identifier);
        	if (task == null)
        		throw new IllegalArgumentException("cannot find task "+ identifier);
        	
        // check permission (actual owner / business administrators can claim tasks
        checkPermission(task, ur, new int[]{ITMSServer.ACTUAL_OWNER, ITMSServer.BUSINESSADMINISTRATORS});
        
        // @TODO check status ( should be ready )

        // update task status

            dao.updateTaskStatus(identifier, TaskStatus.RESERVED);
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }


    }

    public void delegate(String participantToken, String identifier) throws TMSException{
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);

            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
        	Task task = dao.fetchTaskIfExists(identifier);
        	
        	// check permission (actual owner / business administrators / potential owner(only in ready states)
        	List<Integer> r = checkUserTaskRoles(task, ur);
        	if (r.size() == 1 && r.get(0) == ITMSServer.POTENTIAL_OWNERS && task.getStatus() == TaskStatus.READY)
        		throw new IllegalAccessException("Potential owner can delegate task only in ready states");
        	if (!r.contains(ITMSServer.ACTUAL_OWNER) && !r.contains(ITMSServer.BUSINESSADMINISTRATORS))
        		throw new IllegalAccessException("User must be potential owner or business adiministrator");
        
        	// @TODO check status ( should be ready )

        	// update task status
        	
        	//TODO assign task to user and put him into potential owner if he is not
            dao.updateTaskStatus(identifier, TaskStatus.RESERVED);
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }


    }

    public void forward(String participantToken, String identifier) throws TMSException{
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);

            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }

        // @TODO check permission (action owner / business administrators can claim tasks

        // @TODO check status ( should be ready )

        // update task 
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
        	// TODO impl logic
        	
        	// update status
            dao.updateTaskStatus(identifier, TaskStatus.RESERVED);
            
            // commit
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }

    }

    public void release(String participantToken, String identifier) throws TMSException{
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);

            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }

        // @TODO check permission (action owner / business administrators can claim tasks

        // @TODO check status ( should be ready )

        // update task 
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
        	// TODO impl logic
        	
        	// update status
            dao.updateTaskStatus(identifier, TaskStatus.READY);
            
            // commit
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }

    }

    public void resume(String participantToken, String identifier) throws TMSException{
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);

            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }

        // @TODO check permission (action owner / business administrators can claim tasks

        // @TODO check status ( should be ready )

        // update task 
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
        	// TODO impl logic
        	
        	// update status
            dao.updateTaskStatus(identifier, TaskStatus.IN_PROGRESS);
            
            // commit
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }

    }

    public void skip(String participantToken, String identifier) throws TMSException{
    	 // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);

            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }

        // @TODO check permission (action owner / business administrators can claim tasks

        // @TODO check status ( should be ready )

        // update task 
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
        	// TODO impl logic
        	
        	// update status
            dao.updateTaskStatus(identifier, TaskStatus.OBSOLETE);
            
            // commit
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }


    }


    public void fail(String participantToken, String identifier, String faultName, XmlObject faultData) throws TMSException{
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);

            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }

        // @TODO check permission (action owner / business administrators can claim tasks

        // @TODO check status ( should be ready )

        // update task 
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
        	// TODO impl logic
        	
        	// update status
            dao.updateTaskStatus(identifier, TaskStatus.FAILED);
            
            // commit
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }

    }
    
    public void complete(String participantToken, String identifier, XmlObject taskData) throws TMSException{
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);

            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }

        // @TODO check permission (action owner / business administrators can claim tasks

        // @TODO check status ( should be ready )

        // update task 
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
        	// TODO impl logic
            Task task = dao.fetchTaskIfExists(identifier);
            if (task == null){
            	_logger.warn("task "+ identifier+" not found");
            	return;
            }
        	// update status
            task.setStatus(TaskStatus.COMPLETED);
            // update output data
            task.setOutputMessage(taskData.toString()); //?
            dao.updateTask(task);
       
            // commit
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }
    }

	public void suspend(String participantToken, String identifier) throws TMSException{
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);

            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            throw new AuthException(e);
        }

        // @TODO check permission (action owner / business administrators can claim tasks

        // @TODO check status ( should be ready )

        // update task 
        ITaskDAOConnection dao = this._taskDAOFactory.openConnection();
        try {
        	// TODO impl logic
        	
        	// update status
            dao.updateTaskStatus(identifier, TaskStatus.SUSPENDED);
            
            // commit
            dao.commit();
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }
		
	}

	public void suspendUntil(String participantToken, String identifier,
			TTime time) {
		// TODO Auto-generated method stub
		
	}


    /**************************************
     * data operation operations
     * 
     * @throws AuthException
     ***************************************/

    public void setPriority(String participantToken, String identifier, int priority) throws AuthException, UnavailableTaskException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        Task task = dao.fetchTaskIfExists(identifier);
        String actualOwner = task.getActualOwner();

        // OrganizationalEntity = task.getBusinessAdministrators();
        // TODO auth check
        if (true) {
            // if (ur.getUserID().equalsIgnoreCase(actualOwner)) {
            task.setPriority(priority);
            dao.updateTask(task);
            dao.commit();
            dao.close();
        } else {
            dao.close();
            throw new AuthException("User:" + ur.getUserID() + "does not authorized to setPriority");
        }
    }

    public void addAttachment(String participantToken, String identifier, String attachmentName, String accessType, String value) throws AuthException,
                    UnavailableTaskException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {
            // TODO auth check
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

    public List<AttachmentInfo> getAttachmentInfos(String participantToken, String identifier) throws AuthException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        //TODO auth check
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();

        List<AttachmentInfo> ret = dao.getAttachmentInfos(identifier);
        dao.close();
        return ret;
    }
    
    public List<Attachment> getAttachments(String participantToken, String identifier, String attachmentName) throws AuthException{
        UserRoles ur = _authProvider.authenticate(participantToken);
        //TODO auth check
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        List<Attachment> ret = dao.getAttachments(identifier, attachmentName);
        dao.close();
        return ret;
    }
    
    public void deleteAttachments(String participantToken, String identifier, String attachmentName) throws AuthException{
        UserRoles ur = _authProvider.authenticate(participantToken);
        //TODO auth check
        
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        dao.deleteAttachments(identifier, attachmentName);
        dao.commit();
        dao.close();
    }
    
    public void addComment(String participantToken, String identifier, String text) throws AuthException{
        UserRoles ur = _authProvider.authenticate(participantToken);
        //TODO auth check
        
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        dao.addComment(identifier, ur.getUserID(), text);
        dao.commit();
        dao.close();
    }

    public List<Comment> getComments(String participantToken, String identifier) throws AuthException{
        UserRoles ur = _authProvider.authenticate(participantToken);
        //TODO auth check
        
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        List<Comment> comments = dao.getComments(identifier);
        dao.close();
        return comments;
    }
    
    public Task getTaskByIdentifier(String participantToken, String identifier) throws AuthException, UnavailableTaskException{
        UserRoles ur = _authProvider.authenticate(participantToken);
        //TODO auth check
        
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        Task task = dao.fetchTaskIfExists(identifier);
        dao.close();
        return task;
    }
    
    
    /*****************************************
     * administrative operation
     *****************************************/

    /*****************************************
     * Query operation
     *****************************************/

    public List<Task> query(String participantToken, String selectClause, String whereClause, String orderByClause, int maxTasks, int taskIndexOffset)
                    throws TMSException {
        // get user
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);

            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            return null;
        }

        // do query
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {
            return dao.query(ur, selectClause, whereClause, orderByClause, maxTasks, taskIndexOffset);
        } catch (Exception e) {
            _logger.error("Query failed", e);
        } finally {
            dao.close();
        }

        return null;
    }

    public List<Task> getMyTasks(String participantToken, String taskType, String genericHumanRole, String workQueue, TStatus.Enum[] statusList,
                    String whereClause, String createdOnClause, int maxTasks) throws TMSException {
        System.out.println("tmsserver->getTasks");
        UserRoles ur = null;
        try {
            ur = _authProvider.authenticate(participantToken);
            System.out.println("userid:" + ur.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            this._logger.error("authenticate user failed", e);
            return null;
        }

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {
            List<TaskStatus> statuses = new ArrayList<TaskStatus>();
            for (int i = 0; i < statusList.length; i++) {
                statuses.add(TaskStatus.valueOf(statusList[i].toString()));
            }
            System.out.println("==>call dao.getMyTasks");
            List<Task> tasks = dao.getMyTasks(ur, taskType, genericHumanRole, workQueue, statuses, whereClause, createdOnClause, maxTasks);
            _logger.info("return " + tasks.size() + " tasks.");

            return tasks;

            // if (_logger.isDebugEnabled())
            // _logger.debug("Workflow Task " + task + " was created");
            // TODO : Use credentials.getUserID() :vb
        } catch (Exception e) {
            _logger.error("Cannot create Workflow Tasks", e);
            System.out.println("exception raised," + e.getMessage());
            // TODO :
            // TaskIDConflictException
            // must be rethrowed :vb
        } finally {
            dao.close();
        }

        return null;

    }



    public void activate(String participantToken, String identifier) throws AuthException, InvalidTaskStateException, UnavailableTaskException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();

        Task task = checkAdminOperation(dao, ur, identifier);
        try {
            task.setActivationTime(new Date(System.currentTimeMillis()));
            task.setStatus(TaskStatus.READY);

            dao.updateTask(task);
            dao.commit();
        } catch (Exception e) {
            _logger.error("Cannot activate Task: ", e);
        } finally {
            dao.close();
        }
    }


    public void nominate(String participantToken, String identifier, List<String> principals, boolean isUser) throws AuthException, InvalidTaskStateException,
                    UnavailableTaskException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        
        Task task = checkAdminOperation(dao, ur, identifier);
        OrganizationalEntity orgEntity = task.getPotentialOwners();
        if (isUser) {
           // user type
            if (principals.size() == 1) {
                // status -> reserved
                task.setStatus(TaskStatus.RESERVED);
            } else {
                task.setStatus(TaskStatus.READY);
            }
            
            if ((orgEntity == null) || (!OrganizationalEntity.USER_ENTITY.equalsIgnoreCase(orgEntity.getEntityType()))) {
                orgEntity = new UserOrganizationalEntity();
            }

        } else {
           // should be group type
            task.setStatus(TaskStatus.READY);
            if ((orgEntity == null) || (!OrganizationalEntity.GROUP_ENTITY.equalsIgnoreCase(orgEntity.getEntityType()))) {
                orgEntity = new GroupOrganizationalEntity();
            }
        }
        
        orgEntity.getPrincipals().clear();
        for (String principal: principals) {
            Principal p = new Principal();
            p.setValue(principal);
            p.setOrgEntity(orgEntity);
        }
        task.setPotentialOwners(orgEntity);
        
        try {
            dao.updateTask(task);
            dao.commit();
        } catch (Exception e) {
            _logger.error("Cannot nominate Task: ", e);
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
                    throws AuthException, UnavailableTaskException {
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        
        boolean isRightPerson = dao.isRoleMember(identifier, ur, GenericRoleType.business_administrators);
        if (!isRightPerson) {
            throw new AuthException("Only business administrator can update roles.");
        }
        
        Task task = dao.fetchTaskIfExists(identifier);
        
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
            if ((isUser) && (principals.size() == 1)){                
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
        } finally {
            dao.close();
        }
    }
    
    /**
     * check whether the user belongs to one set of principals.
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
