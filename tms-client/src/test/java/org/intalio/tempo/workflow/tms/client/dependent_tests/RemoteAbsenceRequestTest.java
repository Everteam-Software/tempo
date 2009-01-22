/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 * 
 */
package org.intalio.tempo.workflow.tms.client.dependent_tests;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.Assert;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.tms.client.RemoteTMSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;


/**
 * This supports a fully absence request samples through code and soap requests only
 */
public class RemoteAbsenceRequestTest extends TestCase {
	static final Logger _log = LoggerFactory.getLogger(RemoteAbsenceRequestTest.class);
	static final String TOKEN_SERVICE = "http://localhost:8080/axis2/services/TokenService";
	static final String TMS_SERVICE = "http://localhost:8080/axis2/services/TaskManagementServices";
	static final String TASK_MANAGEMENT_PROCESS = "http://localhost:8080/ode/processes/completeTask";
	static final String TASK_MANAGEMENT_PROCESS_WORKFLOW = "http://localhost:8080/fds/workflow/xform";
	
	static final XmlTooling xmlTooling = new XmlTooling();
	Configuration cfg;
	static final int SLEEP_TIME = 2000;
	
	public void setUp() {
		TemplateLoader loader = new ClassTemplateLoader(this.getClass(),"/");
		cfg = new Configuration();
		cfg.setTemplateLoader(loader);
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	}
	
	/**
	 * This sets the different parameters we'll use through this test
	 * @throws Exception
	 */
	public void testAbsenceRequest() throws Exception {
		// We'll use those parameters for login
		String paramUser = "examples\\ewilliams";
		String paramPassword = "password";
		
		// We'll use those parameters for initiating the process
		HashMap<String,Object> pipa = new HashMap<String,Object>();
		Map<String,String> employee = new HashMap<String,String>();
		employee.put("name", "Nicolas");
		employee.put("phone", "+1(650)596-1801");
		employee.put("email", "nico@examples.intalio.com");
		Map<String,String> contact = new HashMap<String,String>();
		contact.put("name", "George Michael");
		contact.put("phone", "+1(650)596-1802");
		contact.put("email", "george@examples.intalio.com");
		pipa.put("notes", "Those are some comments");
		pipa.put("employee", employee);
		pipa.put("contact", contact);
		List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		for(int i = 0 ; i <3 ; i ++) {
			HashMap<String,String> request = new HashMap<String,String>();
			request.put("from", "2008-12-04");
			request.put("to", "2008-12-28");
			request.put("type", "holidays");
			request.put("hours", "200");
			list.add(request);	
		}
		pipa.put("requests", list);
		
		// We'll use those parameters to complete the task
		HashMap complete = new HashMap();
		complete.put("comment", "It is a great day for some comments");
		complete.put("approved", "false");
		Map<String,String> person = new HashMap<String,String>();
		person.put("name", "George Michael");
		person.put("phone", "+1(650)596-1802");
		person.put("email", "george@examples.intalio.com");
		complete.put("contact", person);
		
		// Standard absence request
		runAbsenceRequest(paramUser, paramPassword, pipa, complete);		
		// Provoke a fail by calling TMS.fail while the task has been created
		runAbsenceRequestWithOptionalCall(paramUser, paramPassword, pipa, complete,"fail");
		// Provoke a skip by calling TMP.skip while the task has been created
		runAbsenceRequestWithOptionalCall(paramUser, paramPassword, pipa, complete,"skip");
		// Provoke a delete by calling TMS.delete while the task has been created.
		runAbsenceRequestWithOptionalCall("admin", "changeit", pipa, complete,"delete");
		// Provoke a deleteAll by calling TMS.deleteAll while the task has been created.
		runAbsenceRequestWithOptionalCall("admin", "changeit", pipa, complete,"deleteAll");
		// Provoke a reassign to a new user. If we reassign to a new user, it will disappear from the list, so we're good. (hint: hack!)
		runAbsenceRequestWithOptionalCall("admin", "changeit", pipa, complete,"reassign");
	}

    /**
	* Quite a few different call to check that the release is in good shape.
    */
	private void runAbsenceRequestWithOptionalCall(String paramUser, String paramPassword, HashMap pipa, HashMap complete, String optionalCall) throws Exception {
		_log.info("Running absence request with optional call to:"+optionalCall);
		
		_log.info("Get the token client and authenticate");
		TokenClient client = new TokenClient(TOKEN_SERVICE);
		String token = client.authenticateUser(paramUser, paramPassword);
		
		_log.info("get the tms client");
		RemoteTMSClient tms = new RemoteTMSClient(TMS_SERVICE, token);
		
		_log.info("get the absence request PIPA");
		Task[] ts = tms.getAvailableTasks("PIPATask", "T._description like '%Examples%'");
		String pipaID = ts[0].getID();
		
		_log.info("Init the process and wait");
		tms.init(pipaID, xmlTooling.parseXML(pipa(pipa)));
		Thread.sleep(SLEEP_TIME);
		
		_log.info("check the new task has appeared");
		Task[] paList = tms.getAvailableTasks("PATask", "T._state = TaskState.READY ORDER BY T._creationDate DESC");
		String id = paList[0].getID();
		
		_log.info("keep track of the current time");
		long currentTime = System.currentTimeMillis();
		
		_log.info("do the optional call.");
		
		// skip needs to be called on TMP first.
		if(optionalCall.equals("skip")) sendSoapToTMP(skip(token,id), "skipTask");
		// while the other ones call TMS straight. Note that those should rarely be called directly 
		// from a user process in a real life system. More like administration methods
		else if(optionalCall.equals("fail")) tms.fail(id,"0","Error message");
		else if(optionalCall.equals("delete")) tms.delete(new String[]{id});
		else if(optionalCall.equals("deleteAll")) tms.deleteAll("false", "T._state = TaskState.READY","PATask");
		else if(optionalCall.equals("reassign")) sendSoapToWorkflow(reassign(token,id, "examples/msmith"), "escalate");
		else throw new Exception("invalid optional call:"+optionalCall);
		
		_log.info("wait some more so that we process the optional call");
		Thread.sleep(SLEEP_TIME);
		
		_log.info("check the task has disappeared. All the call we've done make the task not showing the user task list anymore");
        Task[] ts2 = tms.getAvailableTasks("PATask", "T._state = TaskState.READY ORDER BY T._creationDate DESC");
		Assert.assertEquals(0, ts2.length);
		
		if(optionalCall.equals("skip")) {
			_log.info("for skip, we're doing one extra check to see if the state of the task is appropriately OBSOLETE");
			Task[] ts3 = tms.getAvailableTasks("PATask", "T._state = TaskState.OBSOLETE ORDER BY T._creationDate DESC");
			long time = currentTime - ts3[0].getCreationDate().getTime();
			Assert.assertTrue( time > 0);
			Assert.assertTrue( time < 5000); // 2*SLEEP_TIME + 1s of computer work margin
		}
	}

	/**
	 * Run the examples with the given parameters
	 */
	private void runAbsenceRequest(String paramUser, String paramPassword, HashMap pipa, HashMap complete) throws Exception {
		_log.info("Instanciate token service client");
		TokenClient client = new TokenClient(TOKEN_SERVICE);

		_log.info("We are trying to authenticate as user:"+paramUser+" with password:"+paramPassword);
		String token = client.authenticateUser(paramUser, paramPassword);
		_log.info("We have gained a token from the token service. We can use it to call authenticated tempo services");
		Property[] props = client.getTokenProperties(token);
		String user = (String)PropertyUtils.getProperty(props, AuthenticationConstants.PROPERTY_USER).getValue();
		_log.info("Decrypting the token properties. We have successfully logged in as:"+user);
		
		_log.info("Instanciate tms service client");
		RemoteTMSClient tms = new RemoteTMSClient(TMS_SERVICE, token);
		
		_log.info("get the pipa corresponding to the absence request, by making a query on our available tasks");
		Task[] ts = tms.getAvailableTasks("PIPATask", "T._description like '%Examples%'");
		
		
		String pipaID = ts[0].getID();
		_log.info("We have found the task to instanciate the process. This task has the following ID:"+pipaID);
		tms.init(pipaID, xmlTooling.parseXML(pipa(pipa)));
		_log.info("wait for the task to be initiated. Hopefully 2s is enough");
		Thread.sleep(SLEEP_TIME);
		
		_log.info("get our full activity task list");
		Task[] paList = tms.getAvailableTasks("PATask", "ORDER BY T._creationDate DESC");
		_log.info("get the id of the activity task");
		String id = paList[0].getID();
		_log.info("We're about to start using PATask with id:"+id);
		
		_log.info("We cannot get input and output of a task on a get task list call (see WSDL)");
		_log.info("Let's call TMS again to get the full input and output data of this PATask");
		PATask task = (PATask)tms.getTask(id);
		_log.info("" +
				"\nChecking the task metadata..." +
				"\nThe task has been created on:"+task.getCreationDate() +
				"\nIt has the following description:"+task.getDescription() +
				"\nIt is attached to the process with id:"+task.getProcessID() +
				"\nIt is attached to the following form:"+task.getFormURLAsString() +
				"\nIt is in the following state:"+task.getState() +
				"\nIt has the following input:\n"+task.getInputAsXmlString() + 
				"\nIt can be assigned to the following roles:"+task.getRoleOwners() +
				"\nIt can be assigned to the following users:"+task.getUserOwners());

		_log.info("Let's claim the task: no one else can access this task apart from user:"+user);
        sendSoapToTMP(claim(token,id, user), "claimTask");
        _log.info("Let's revoke the task:every one can access this task again");
        sendSoapToTMP(revoke(token,id).toString(),"revokeTask");
        _log.info("complete the PA task with some output");
        sendSoapToTMP(complete(token, id, complete).toString(),"completeTask");
        
        _log.info("sleep again to wait for the notification");
        Thread.sleep(SLEEP_TIME);
        Task[] ts2 = tms.getAvailableTasks("Notification", "ORDER BY T._creationDate DESC");
        String notificationId = ts2[0].getID();
        _log.info("We want to retrieve some more data on the notification with id:"+notificationId);
        Notification notification = (Notification) tms.getTask(notificationId);
        
        _log.info("The notification has the following:" +
        		"\nInput:"+xmlTooling.serializeXML(notification.getInput())+
        		"\nCreation Date:"+notification.getCreationDate() +
        		"\nAttached Form:"+notification.getFormURLAsString() +
        		"\nDescription:"+notification.getDescription());
        
        _log.info("Dismiss this notification");
		tms.complete(notificationId);
	}
	
	/**
	 * Send the WS request to TMP.
	 */
	private void sendSoapToWorkflow(String request, String soapAction) throws Exception {
		sendSoapTo(request, soapAction, TASK_MANAGEMENT_PROCESS_WORKFLOW);
	}
	
	private void sendSoapToTMP(String request, String soapAction) throws Exception {
		sendSoapTo(request, soapAction, TASK_MANAGEMENT_PROCESS);
	}
	
	private void sendSoapTo(String request, String soapAction, String endpoint) throws Exception {
		ServiceClient serviceClient = new ServiceClient();
		OMFactory factory = OMAbstractFactory.getOMFactory();
		Options options = new Options();
        options.setTo(new EndpointReference(endpoint));
        serviceClient.setOptions(options);
        options.setAction(soapAction);
        serviceClient.sendReceive(xmlTooling.convertDOMToOM(xmlTooling.parseXML(request), factory));
	}
	
	private String skip(String token, String taskId) throws Exception {
		HashMap<String,String> skip = new HashMap<String,String>();
		skip.put("taskId", taskId);
		skip.put("token", token);
		return templateMe("skip.ftl", skip);
	}

	private String reassign(String token, String taskId, String user) throws Exception {
		HashMap<String,String> escalate = new HashMap<String,String>();
		escalate.put("taskId", taskId);
		escalate.put("token", token);
		escalate.put("user", user);
		return templateMe("escalate.ftl", escalate);
	}	
	
	/**
	 * Generate a complete request.
	 * This also adds some output to the task. 
	 */
	private String complete(String token, String taskId, HashMap complete) throws Exception {
		complete.put("taskId", taskId);
		complete.put("token", token);
		return templateMe("complete.ftl", complete);
	}
	
	/**
	 * Generate a revoke request
	 */
	private String revoke(String token, String taskId) throws Exception {
		HashMap<String,String> root = new HashMap<String,String>();
		root.put("taskId",taskId);
		root.put("token",token);
		return templateMe("revoke.ftl", root);
	}
	
	/**
	 * Generate a claim request, for the given user
	 */
	private String claim(String token, String taskId, String user) throws Exception {
		HashMap<String,String> root = new HashMap<String,String>();
		root.put("taskId",taskId);
		root.put("user",user);
		root.put("token",token);
		return templateMe("claim.ftl", root);
	}
	
	/**
	 * Couldn't get a better name :)
	 * Prepare some parameters for a dynamic pipa input.
	 */
	private String pipa(HashMap pipa) throws Exception {
		return templateMe("initPipa.ftl", pipa);
	}

	/**
	 * Get the Result from templating operation
	 */
	private String templateMe(String template, Map params) throws Exception {
		Template temp = cfg.getTemplate(template);
		StringWriter writer = new StringWriter();
		temp.process(params, writer);
		return writer.toString();
	}
}