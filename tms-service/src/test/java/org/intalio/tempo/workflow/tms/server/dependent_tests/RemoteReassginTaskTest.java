package org.intalio.tempo.workflow.tms.server.dependent_tests;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.intalio.tempo.security.provider.SecurityProvider;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.provider.RBACProvider;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.intalio.tempo.workflow.tms.server.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

/**
 * Use this class to test the life-cycle of Task Re-assign from remote.
 * 
 * This class only be tested with Live variable. Also you should set
 * "org.intalio.tempo.configDirectory" system property.
 * 
 * <P> - get the list of users from the RBACQuery class<BR> - get a task that
 * is in our inbox<BR> - select one of the user, and re-assign it to the above
 * task<BR> - check that the task is not in our inbox anymore<BR> - check the
 * task is in the inbox of the newly assigned user.
 * </P>
 * 
 * @author James Hu
 * 
 */
public class RemoteReassginTaskTest extends TestCase {

    private static final Logger _logger = LoggerFactory.getLogger(RemoteReassginTaskTest.class);

    private final static String REALM_INTALIO = "intalio";
    private final static String REALM_EXAMPLES = "examples";

    private final static String USER_CURRENT = "examples\\msmith";
    private final static String ROLE_TARGET = "examples\\manager";
    private final static String ROLE_TARGET2 = "intalio\\eng";

    // examples\msmith
    private static final String TOKEN_CURRENT = "VE9LRU4mJnVzZXI9PWV4YW1wbGVzXG1zbWl0aCYmaXNzdWVkPT0xMTk3MjkxMDI4ODU5JiZyb2xlcz09ZXhhbXBsZXNcZW1wbG95ZWUmJmZ1bGxOYW1lPT1NaWNoYWVsIFNtaXRoJiZlbWFpbD09bXNtaXRoQGV4YW1wbGVzLmludGFsaW8uY29tJiZub25jZT09MjE3OTg2Njc4OTg4NzUwNTk2MiYmdGltZXN0YW1wPT0xMTk3MjkxMDI4ODU5JiZkaWdlc3Q9PUZNWUNtM0tkYVNzTnJZMVFHTWtqTjNmRVFNND0mJiYmVE9LRU4=";

    // examples\ewilliams

    // intalio\eng1
    private static final String TOKEN_TARGET2 = "VE9LRU4mJnVzZXI9PWludGFsaW9cZW5nMSYmaXNzdWVkPT0xMTk3MzYzMzk4MzQzJiZyb2xlcz09aW50YWxpb1xlbmcmJmZ1bGxOYW1lPT1FbmdpbmVlciAjMSYmZW1haWw9PWVuZzFAaW50YWxpby5jb20mJm5vbmNlPT0tNzc0MzkyOTM1NjE1MDQxMjU1MiYmdGltZXN0YW1wPT0xMTk3MzYzMzk4MzQzJiZkaWdlc3Q9PUFBMHRlZ0dTbEhaMzI1VGUyNHNnSnRyQ3orUT0mJiYmVE9LRU4=";

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
        _logger.debug("Get the users from role: " + ROLE_TARGET2);
        RBACQuery query = rbacProvider.getQuery();
        String[] users = query.assignedUsers(ROLE_TARGET2);
        _logger.debug("Get the users: " + Arrays.asList(users));

        /*
         * Create a new task for current user
         */
        PATask task1 = new PATask(String.valueOf(new Random().nextInt(10000)), new URI("http://localhost/1"),
                "processID", "urn:completeSOAPAction", Utils.createXMLDocument());
        task1.getUserOwners().add(USER_CURRENT);
        tms.create(task1);

        /*
         * Get task list of current user
         */
        _logger.debug("Get the task list from token: " + TOKEN_CURRENT);
        Task[] tasks = tms.getTaskList();

        /*
         * Select one task to re-assign
         */
        PATask selectTask = null;
        for (Task task : tasks) {
            _logger.debug(task.getClass().getName());
            if (task instanceof PATask) {
                selectTask = (PATask) task;
                break;
            }
        }
        if (selectTask == null) {
            throw new Exception("There is no PATask in the current user[examples\\msmith] task list");
        }
        String selectTaskId = selectTask.getID();
        _logger.debug("Select one task: " + selectTaskId);

        /*
         * Select a user to be re-assigned.
         */
        AuthIdentifierSet targetUserSet = new AuthIdentifierSet();
        String targetUserId = users[0];
        _logger.debug("Select one target user = " + targetUserId);
        targetUserSet.add(targetUserId);

        /*
         * Re-assign task
         */
        Collection<String> uOwners = selectTask.getUserOwners();
        uOwners.clear();
        selectTask.getRoleOwners().clear();
        selectTask.getUserOwners().addAll(targetUserSet);

        tms.reassign(new String[] {selectTask.getID()}, (AuthIdentifierSet)selectTask.getUserOwners(), (AuthIdentifierSet) selectTask.getRoleOwners(), selectTask.getState());
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
                "http://localhost:8080/axis2/services/TaskManagementServices", TOKEN_TARGET2).getService();
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
        rbacProvider = securityProvider.getRBACProvider(REALM_INTALIO);
    }

}
