package org.intalio.tempo.workflow.tms.server.dependent_tests;

import java.io.File;
import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.intalio.tempo.security.provider.SecurityProvider;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.provider.RBACProvider;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

/**
 * Use this class to test the life-cycle of Task Re-assign from remote.
 * 
 * This class only be tested with Live variable. Also you should set "org.intalio.tempo.configDirectory" system property.
 * 
 * <P>
 * - get the list of users from the RBACQuery class<BR>
 * - get a task that is in our inbox<BR>
 * - select one of the user, and re-assign it to the above task<BR>
 * - check that the task is not in our inbox anymore<BR>
 * - check the task is in the inbox of the newly assigned user.
 * </P>
 * 
 * @author James Hu
 *
 */
public class RemoteReassginTaskTest extends TestCase {

	private static final Logger _logger = LoggerFactory.getLogger(RemoteReassginTaskTest.class);
    
    private final static String USER_CURRENT = "examples\\msmith";
    private final static String ROLE_TARGET = "examples\\manager";
    
    // examples\msmith
    private static final String TOKEN_CURRENT = "VE9LRU4mJnVzZXI9PWV4YW1wbGVzXG1zbWl0aCYmaXNzdWVkPT0xMTk3MjkxMDI4ODU5JiZyb2xlcz09ZXhhbXBsZXNcZW1wbG95ZWUmJmZ1bGxOYW1lPT1NaWNoYWVsIFNtaXRoJiZlbWFpbD09bXNtaXRoQGV4YW1wbGVzLmludGFsaW8uY29tJiZub25jZT09MjE3OTg2Njc4OTg4NzUwNTk2MiYmdGltZXN0YW1wPT0xMTk3MjkxMDI4ODU5JiZkaWdlc3Q9PUZNWUNtM0tkYVNzTnJZMVFHTWtqTjNmRVFNND0mJiYmVE9LRU4=";

    // examples\ewilliams
    private static final String TOKEN_TARGET = "VE9LRU4mJnVzZXI9PWV4YW1wbGVzXGV3aWxsaWFtcyYmaXNzdWVkPT0xMTk3Mjg5NzgzNTYyJiZyb2xlcz09ZXhhbXBsZXNcZW1wbG95ZWUsZXhhbXBsZXNcbWFuYWdlciYmZnVsbE5hbWU9PUVtaWx5IFdpbGxpYW1zJiZlbWFpbD09ZXdpbGxpYW1zQGV4YW1wbGVzLmludGFsaW8uY29tJiZub25jZT09LTYxNzAzMDk5ODE2MjkzNDA0MTAmJnRpbWVzdGFtcD09MTE5NzI4OTc4MzU2MiYmZGlnZXN0PT1jbDlaV1Rmd0JrRkZQcGRQVHlPYk9LdXNpOXM9JiYmJlRPS0VO";

    
    //private static final String TOKEN = "VE9LRU4mJnVzZXI9PWludGFsaW9cYWRtaW4mJmlzc3VlZD09MTE5NjI5NjM1MzQ4MyYmcm9sZXM9PWludGFsaW9ccHJvY2Vzc2FkbWluaXN0cmF0b3IsZXhhbXBsZXNcZW1wbG95ZWUsaW50YWxpb1xwcm9jZXNzbWFuYWdlcixleGFtcGxlc1xtYW5hZ2VyJiZmdWxsTmFtZT09QWRtaW5pbmlzdHJhdG9yJiZlbWFpbD09YWRtaW5AZXhhbXBsZS5jb20mJm5vbmNlPT0tODI1MzI1NjkwNzg0MzU2NTk0JiZ0aW1lc3RhbXA9PTExOTYyOTYzNTM0ODUmJmRpZ2VzdD09WnVLd2JWaDUxeWdMZ2FqSjVhTDlITk02anh3PSYmJiZUT0tFTg==";
    
    private SecurityProvider securityProvider;
    private RBACProvider rbacProvider;
    
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(RemoteReassginTaskTest.class);
    }	
	
	protected void setUp() throws Exception {
		super.setUp();
		initSecurityProvider();
        initRBACProvider();
        
        
        
	}
	
	public void testReassginTaskLifecycle() throws Exception {
		ITaskManagementService tms = new RemoteTMSFactory(
                "http://localhost:8080/axis2/services/TaskManagementServices", TOKEN_CURRENT).getService();

		
		/*
         * Get available users
         */
        _logger.debug("Get the users from role: " + ROLE_TARGET);
        RBACQuery query = rbacProvider.getQuery();
        String[] users = query.assignedUsers(ROLE_TARGET);
        _logger.debug("Get the users: " + Arrays.asList(users));

        /*
         * Get task list of current user
         */
        _logger.debug("Get the task list from token: " + TOKEN_CURRENT);
        Task[] tasks = tms.getTaskList();
        
        /*
         * Select one task to re-assign
         */
        Task selectTask = tasks[0];
        String selectTaskId = selectTask.getID();
        _logger.debug("Select one task: " + selectTaskId);
        
        /*
         * Select a user to be re-assigned.
         */
        AuthIdentifierSet targetUserSet = new AuthIdentifierSet();
        String targetUserId = "examples\\ewilliams";
        _logger.debug("Select one target user = " + targetUserId);
        targetUserSet.add(targetUserId);
        
        /*
         * Re-assign task
         */
        AuthIdentifierSet uOwners = selectTask.getUserOwners();
        uOwners.clear();
        selectTask.getUserOwners().addAll(targetUserSet);
        tms.reassign(selectTask);
        _logger.debug("Reassign task[" + selectTaskId + "] to " + targetUserId);
        
        
        /*
         * check that the task is not in the inbox of current user
         */
        boolean task_nowuser_inbox = false;
        Task[] tasks2 = tms.getTaskList();
        for (int i = 0; i < tasks2.length; i++) {
            if (selectTaskId.equalsIgnoreCase(tasks2[i].getID())) {
                task_nowuser_inbox = true;
            }
        }
        if (!task_nowuser_inbox) {
            _logger.debug("The task " + selectTaskId + " is not in the current user[" + USER_CURRENT + "]");
        }
        Assert.assertEquals(false, task_nowuser_inbox);

        /*
         * check that the task is not in the inbox of target user
         */
        ITaskManagementService tms_target = new RemoteTMSFactory(
                "http://localhost:8080/axis2/services/TaskManagementServices", TOKEN_TARGET).getService();
        boolean task_targetuser_inbox = false;
        Task[] tasks3 = tms_target.getTaskList();
        for (int i = 0; i < tasks3.length; i++) {
            if (selectTaskId.equalsIgnoreCase(tasks3[i].getID())) {
                task_targetuser_inbox = true;
            }
        }
        if (task_targetuser_inbox) {
            _logger.debug("The task " + selectTaskId + " is in the target user[" + targetUserId + "]");
        }
        Assert.assertEquals(true, task_targetuser_inbox);
        
	}
	
	void initSecurityProvider() throws Exception {
		try {
            synchronized (RemoteReassginTaskTest.class) {
            	_logger.debug("Initializing configuration.");
                String configDir = System.getProperty("org.intalio.tempo.configDirectory");
                if (configDir == null) {
                    throw new RuntimeException("System property org.intalio.tempo.configDirectory not defined.");
                }
                File _configDir = new File(configDir);
                if (!_configDir.exists()) {
                    throw new RuntimeException("Configuration directory " + _configDir.getAbsolutePath()
                            + " doesn't exist.");
                }
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                FileSystemResource config = new FileSystemResource(new File(_configDir, "securityConfig.xml"));
                XmlBeanFactory factory = new XmlBeanFactory(config);
    
                PropertyPlaceholderConfigurer propsCfg = new PropertyPlaceholderConfigurer();
                propsCfg.setSearchSystemEnvironment(true);
                propsCfg.postProcessBeanFactory(factory);
                securityProvider = (SecurityProvider) factory.getBean("securityProvider");

            }
        } catch (RuntimeException except) {
        	_logger.error("Error during initialization of security service", except);
            throw except;
        }
	}
	
	void initRBACProvider() throws RBACException {
        rbacProvider = securityProvider.getRBACProvider("examples");
    }

}
