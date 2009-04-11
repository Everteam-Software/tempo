package org.intalio.tempo.workflow.tmsb4p.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.taskb4p.Attachment;
import org.intalio.tempo.workflow.taskb4p.AttachmentAccessType;
import org.intalio.tempo.workflow.taskb4p.AttachmentInfo;
import org.intalio.tempo.workflow.taskb4p.Comment;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.taskb4p.TaskStatus;
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
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + taskId, e);
        } finally {
            dao.close();
        }

        return;

    }

    public void Release(String participantToken, String identifier) {
        // TODO Auto-generated method stub

    }

    public void Resume(String participantToken, String identifier) {
        // TODO Auto-generated method stub

    }

    public void Start(String participantToken, String identifier) throws Exception {
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
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw e;
        } finally {
            dao.close();
        }
    }

    public void Stop(String participantToken, String identifier) throws TMSException {

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
        } catch (Exception e) {
            _logger.error("remove task failed, task id " + identifier, e);
            throw new B4PPersistException(e);
        } finally {
            dao.close();
        }

    }

    public void claim(String participantToken, String identifier) {
        // TODO Auto-generated method stub

    }

    public void delegate(String participantToken, String identifier) {
        // TODO Auto-generated method stub

    }

    public void forward(String participantToken, String identifier) {
        // TODO Auto-generated method stub

    }

    public void release(String participantToken, String identifier) {
        // TODO Auto-generated method stub

    }

    public void resume(String participantToken, String identifier) {
        // TODO Auto-generated method stub

    }

    public void skip(String participantToken, String identifier) {
        // TODO Auto-generated method stub

    }

    public void start(String participantToken, String identifier) {
        // TODO Auto-generated method stub

    }

    public void stop(String participantToken, String identifier) {
        // TODO Auto-generated method stub

    }

    public void fail(String participantToken, String identifier, String faultName, XmlObject faultData) {
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

    public void complete(String participantToken, String identifier, XmlObject xmlObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public void activate(String participantToken, String identifier) throws AuthException, InvalidTaskStateException, UnavailableTaskException{
        UserRoles ur = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        
        // check whether the user has the business administrator role
        boolean isRightPerson = dao.isRoleMember(identifier, ur, GenericRoleType.business_administrators);
        if (!isRightPerson) {
            throw new AuthException("Only business administrator can activate the task.");
        }
        
        try {
            Task task = dao.fetchTaskIfExists(identifier);
            
            if (!TaskStatus.CREATED.equals(task.getStatus())) {
                throw new InvalidTaskStateException("Only created status's Task can be activated!");
            }
            
            task.setActivationTime(new Date(System.currentTimeMillis()));
            task.setStatus(TaskStatus.READY);
            
            dao.updateTask(task);
            dao.commit();           
        } catch (UnavailableTaskException e) {
            _logger.error("Cannot activate Task: ", e);
        } finally {
            dao.close();
        }

    }

    @Override
    public void nominate(String participantToken, String identifier) throws AuthException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setGenericHumanRole(String participantToken, String identifier) throws AuthException {
        // TODO Auto-generated method stub
        
    }

}
