package org.intalio.tempo.workflow.tms.client.dependent_tests;

import java.text.MessageFormat;

import junit.framework.TestCase;

import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.tms.client.TempoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SomeSampleClientCode extends TestCase {
	
	static final Logger _log = LoggerFactory.getLogger(SomeSampleClientCode.class);
	static final String TOKEN_SERVICE = "http://localhost:8080/axis2/services/TokenService";
	static final XmlTooling xmlTooling = new XmlTooling();

	public void testSimpleCode() throws Exception {

		String paramUser = "admin";
		String paramPassword = "changeit";
		
		// prepare the token
		TokenClient client = new TokenClient(TOKEN_SERVICE);
		String token = client.authenticateUser(paramUser, paramPassword);
		// testing client, including call to TMS, TMP 
		TempoClient tempoClient = new TempoClient("http://localhost:8080", token, client);

		
		// search PIPA tasks using wild card description
		Task[] ts = tempoClient.getAvailableTasks("PIPATask", "T._description like '%Examples%'");
		
		// search PA tasks, using state, wildcard description, order by date
		StringBuilder query = new StringBuilder();
		query.append("(T._state = TaskState.READY OR T._state = TaskState.CLAIMED)");
		query.append(" AND ");
		query.append("T._description like '%Examples%'");
		query.append(" ");
		query.append("ORDER BY T._creationDate DESC");
		
		Long countTasks = tempoClient.countAvailableTasks(PATask.class.getSimpleName(), query.toString());
		_log.info(MessageFormat.format("Found {0} tasks with description like %Examples%", countTasks));
		// retrieve all the tasks
		Task[] paList = tempoClient.getAvailableTasks("PATask", query.toString());
		// retrieve only a few tasks by setting min and max
		Task[] aFewTasks = tempoClient.getAvailableTasks("PATask", query.toString(), String.valueOf(0), String.valueOf(5));
		_log.info(MessageFormat.format("Collecting {0} tasks with description like %Examples%", aFewTasks.length));

	}
}
