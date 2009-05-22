package org.intalio.tempo.workflow.tms.client.dependent_tests;

import junit.framework.TestCase;

import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.client.RemoteTMSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SitaTaskListTest extends TestCase {
	static final String TOKEN_SERVICE = "http://localhost:8080/axis2/services/TokenService";
    static final String TMS_SERVICE = "http://localhost:8080/axis2/services/TaskManagementServices";

	static final String paramUser = "admin";
	static final String paramPassword = "changeit";
	
	static final Logger _log = LoggerFactory.getLogger(SitaTaskListTest.class);
	
	public void testGetFullTaskList() throws Exception {
		TokenClient client = new TokenClient(TOKEN_SERVICE);
		
		String token = client.authenticateUser(paramUser, paramPassword);
		RemoteTMSClient tms = new RemoteTMSClient(TMS_SERVICE, token);
		String taskType = "PATask";
		String subQuery = "";
		String first = "0";
		String max = "5";
		boolean full = Boolean.TRUE;
		
		Task[] tasks = tms.getAvailableTasks(taskType, subQuery, first, max, full);
		for(Task t : tasks) {
			_log.error(((PATask)t).getInputAsXmlString());
			_log.error(((PATask)t).getOutputAsXmlString());
		}
	}
}
