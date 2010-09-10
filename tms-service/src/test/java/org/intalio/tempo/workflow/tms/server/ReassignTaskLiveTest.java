package org.intalio.tempo.workflow.tms.server;

import java.util.Arrays;
import java.util.HashMap;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.intalio.tempo.security.provider.SecurityProvider;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.provider.RBACProvider;
import org.intalio.tempo.security.simple.SimpleSecurityProvider;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this class to test the life-cycle of Task Re-assign.
 * 
 * <P>
 * - get the list of users from the RBACQuery class<BR>
 * - get a task that is in our inbox<BR>
 * - select one of the user, and re-assign it to the above task<BR>
 * - check that the task is not in our inbox anymore<BR>
 * - check the task is in the inbox of the newly assigned user.
 * </P>
 * 
 * 
 * @author James Hu
 * @date 12/9/2007
 * 
 */
public class ReassignTaskLiveTest extends TestCase {

    private final static String REALM = "test";

    private final static String USER_CURRENT = "test\\user1";
    private final static String ROLE_TARGET = "role4";

    private final static String TOKEN_CURRENT = "token1";
    private final static String TOKEN_TARGET = "token3";

    private SecurityProvider securityProvider;
    private RBACProvider rbacProvider;
    private TMSRequestProcessor requestProcessor;
    private ITMSServer tmsServer;

    private static final Logger _logger = LoggerFactory.getLogger(ReassignTaskLiveTest.class);

    protected void setUp() throws Exception {
        super.setUp();

        initSecurityProvider();
        initRBACProvider();
        initTMSServer();
        initTMSRequestProcessor();
        createTask();
    }

    public void testReassginLive() throws Exception {

        /*
         * Get available users
         */
        _logger.debug("Get the users from role: " + ROLE_TARGET);
        String[] users = queryAssignedUsers(ROLE_TARGET);
        _logger.debug("Get the users: " + Arrays.asList(users));

        /*
         * Get task list of current user
         */
        _logger.debug("Get the task list from token: " + TOKEN_CURRENT);
        Task[] tasks = tmsServer.getTaskList(TOKEN_CURRENT);

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
        String targetUserId = users[0];
        _logger.debug("Select one target user = " + targetUserId);
        targetUserSet.add(targetUserId);

        /*
         * Re-assign task
         */
        tmsServer.reassign(selectTask.getID(), targetUserSet, new AuthIdentifierSet(), TaskState.READY, "token1");
        _logger.debug("Reassign task[" + selectTaskId + "] to " + targetUserId);

        /*
         * check that the task is not in the inbox of current user
         */
        boolean task_nowuser_inbox = false;
        Task[] tasks2 = tmsServer.getTaskList(TOKEN_CURRENT);
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
        boolean task_targetuser_inbox = false;
        Task[] tasks3 = tmsServer.getTaskList(TOKEN_TARGET);
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

    /**
     * Get Simple SecurityProvider.
     * 
     * @return SecurityProvider
     * @throws Exception
     */
    void initSecurityProvider() throws Exception {
        HashMap<String, String> props;
        props = new HashMap<String, String>();
        props.put("configFile", "testSimpleSecurityForReassign.xml");
        securityProvider = new SimpleSecurityProvider();
        securityProvider.initialize(props);
    }

    void initRBACProvider() throws RBACException {
        rbacProvider = securityProvider.getRBACProvider(REALM);
    }

    void initTMSServer() throws Exception {
        tmsServer = Utils.createTMSServer();
    }

    void initTMSRequestProcessor() throws Exception {
        requestProcessor = new TMSRequestProcessor();
        requestProcessor.setServer(tmsServer);
    }

    void createTask() throws Exception {
        OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
        requestProcessor.create(createTaskRequest);
    }

    public String[] queryAssignedUsers(String role) throws Exception {
        role = REALM + "\\" + role;
        RBACQuery query = rbacProvider.getQuery();
        return query.assignedUsers(role);
    }
}
