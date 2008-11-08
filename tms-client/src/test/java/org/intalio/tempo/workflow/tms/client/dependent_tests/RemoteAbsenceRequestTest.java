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

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.tms.client.RemoteTMSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class RemoteAbsenceRequestTest extends TestCase {
	static final Logger _log = LoggerFactory.getLogger(RemoteAbsenceRequestTest.class);

	private static final String TOKEN_SERVICE = "http://localhost:8080/axis2/services/TokenService";
	private static final String TMS_SERVICE = "http://localhost:8080/axis2/services/TaskManagementServices";
	private static final String TASK_MANAGEMENT_PROCESS = "http://localhost:8080/ode/processes/completeTask";
	final XmlTooling xmlTooling = new XmlTooling();
	
	public void testAll() throws Exception {
		_log.info("Instanciate token service client");
		TokenClient client = new TokenClient(TOKEN_SERVICE);

		String paramUser = "examples\\ewilliams";
		String paramPassword = "password";
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
		Document response = tms.init(pipaID, Utils.createXMLDocument("/initPipa.xml"));
		
		_log.info("wait for the task to be initiated. Hopefully 2s is enough");
		Thread.sleep(2000);
		
		_log.info("get our full activity task list");
		Task[] pa = tms.getAvailableTasks("PATask", "ORDER BY T._creationDate DESC");
		_log.info("get the task we want");
		String id = pa[0].getID();
		_log.info("We're about to start using PATask with id:"+id);
		
		_log.info("We cannot get input and output of a task on a get task list call (see WSDL)");
		_log.info("Let's call TMS again to get the full data of this PATask");
		PATask task = (PATask)tms.getTask(id);
		_log.info("Task has the following input:\n"+task.getInputAsXmlString());

		_log.info("Let's claim the task: no one else can access this task apart from user:"+user);
        sendSoapToTMP(claim(token,id, user), "claimTask");
        _log.info("Let's revoke the task:every one can access this task again");
        sendSoapToTMP(revoke(token,id),"revokeTask");
        _log.info("complete the PA task with some output");
        sendSoapToTMP(complete(token, id),"completeTask");
        
        _log.info("sleep again to wait for the notification");
        Thread.sleep(2000);
        Task[] ts2 = tms.getAvailableTasks("Notification", "ORDER BY T._creationDate DESC");
        String notificationId = ts2[0].getID();
        _log.info("Dismiss the notification with id:"+notificationId);
		tms.complete(notificationId);
	}
	
	private void sendSoapToTMP(StringBuffer request, String soapAction) throws Exception {
		// options to talk to TMP
		ServiceClient serviceClient = new ServiceClient();
		OMFactory factory = OMAbstractFactory.getOMFactory();
		Options options = new Options();
        options.setTo(new EndpointReference(TASK_MANAGEMENT_PROCESS));
        serviceClient.setOptions(options);
        options.setAction(soapAction);
        serviceClient.sendReceive(xmlTooling.convertDOMToOM(xmlTooling.parseXML(request.toString()), factory));
	}
	
	private StringBuffer complete(String token, String taskId) throws Exception {
		StringBuffer complete = new StringBuffer();
		complete.append("<b4p:taskMetaData><b4p:taskId>"+taskId+"</b4p:taskId></b4p:taskMetaData>");
		complete.append("<b4p:participantToken>"+token+"</b4p:participantToken>");
		complete.append("<b4p:taskOutput>");
		complete.append("<output ");
		complete.append("xmlns=\"http://www.intalio.com/workflow/forms/AbsenceRequest/AbsenceRequest\" ");
		complete.append("xmlns:f=\"http://orbeon.org/oxf/xml/formatting\" ");
		complete.append("xmlns:xxforms=\"http://orbeon.org/oxf/xml/xforms\" ");
		complete.append("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		complete.append("xmlns:fe=\"http://www.intalio.com/workflow/forms/AbsenceRequest/AbsenceRequest\" ");
		complete.append("taskId=\""+taskId+"\">");
		// looks like we don't need token and user here, as it is send usually from form manager
		complete.append("<approved>true</approved>" +
        		"<comment/>" +
        		"<contactWhileAway>" +
        		"<name>Emily Williams</name>" +
        		"<phone>+1(650)596-1800</phone>" +
        		"<email>ewilliams@examples.intalio.com</email>" +
        		"</contactWhileAway>" +
        		"</output>");
        complete.append("</b4p:taskOutput>");
        return parseTMPRequest(token, taskId, "completeTaskRequest", complete);
	}
	
	private StringBuffer revoke(String token, String taskId) throws Exception {
		StringBuffer revoke = new StringBuffer();
		revoke.append("<b4p:taskId>"+taskId+"</b4p:taskId>");
		revoke.append("<b4p:participantToken>"+token+"</b4p:participantToken>");
		return parseTMPRequest(token, taskId, "revokeTaskRequest", revoke);
	}
	
	private StringBuffer claim(String token, String taskId, String user) throws Exception {
		StringBuffer claim = new StringBuffer();
		claim.append("<b4p:taskId>"+taskId+"</b4p:taskId>");
		claim.append("<b4p:claimerUser>"+user+"</b4p:claimerUser>");
		claim.append("<b4p:participantToken>"+token+"</b4p:participantToken>");
		return parseTMPRequest(token, taskId, "claimTaskRequest", claim);
	}

	private StringBuffer parseTMPRequest(String token, String taskId, String requestType, StringBuffer xml){
		StringBuffer request = new StringBuffer();
		request.append("<b4p:");
		request.append(requestType);
		request.append(" xmlns:b4p=\"http://www.intalio.com/bpms/workflow/ib4p_20051115\"");
		request.append(" xmlns:ev=\"http://www.w3.org/2001/xml-events\"");
	    request.append(" xmlns:oxf=\"http://www.orbeon.com/oxf/processors\"");
		request.append(" xmlns:p=\"http://www.orbeon.com/oxf/pipeline\"");
		request.append(" xmlns:xforms=\"http://www.w3.org/2002/xforms\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\">");
		request.append(xml);
		request.append("</b4p:"+requestType+">");
		return request;
	}
}