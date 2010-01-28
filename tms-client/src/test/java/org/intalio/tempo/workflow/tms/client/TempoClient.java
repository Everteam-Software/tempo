package org.intalio.tempo.workflow.tms.client;

import java.io.StringWriter;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * A client for most tempo endpoints: TMS, TMP, and workflow
 * 
 */
public class TempoClient extends RemoteTMSClient {

	static final Logger _log = LoggerFactory.getLogger(TempoClient.class);
	String tmpEndpoint;
	String workflowEndpoint;
	XmlTooling xmlTooling = new XmlTooling();
	Configuration cfg;
	private String token;
	TokenClient tokenClient;

	public TempoClient(String bpmsBaseUrl, String participantToken,
			TokenClient tokenClient) {
		super(bpmsBaseUrl + "/axis2/services/TaskManagementServices",
				participantToken);
		this.tmpEndpoint = bpmsBaseUrl + "/ode/processes/completeTask";
		workflowEndpoint = bpmsBaseUrl + "/fds/workflow/xform";
		this.tokenClient = tokenClient;
		this.token = participantToken;
		initTemplating();
	}

	private void initTemplating() {
		TemplateLoader loader = new ClassTemplateLoader(this.getClass(), "/");
		cfg = new Configuration();
		cfg.setTemplateLoader(loader);
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	}

	/**
	 * Send a WS request to Form Dispatcher Service (FDS). This is called on a
	 * reassign
	 */
	public void sendSoapToWorkflow(String request, String soapAction)
			throws Exception {
		sendSoapTo(request, soapAction, this.workflowEndpoint);
	}

	/**
	 * Send a WS request to The Task Management Process (TMP).
	 */
	public void sendSoapToTMP(String request, String soapAction)
			throws Exception {
		sendSoapTo(request, soapAction, this.tmpEndpoint);
	}

	/**
	 * Generic http method to send a soap request
	 */
	public void sendSoapTo(String request, String soapAction, String endpoint)
			throws Exception {
		ServiceClient serviceClient = new ServiceClient();
		OMFactory factory = OMAbstractFactory.getOMFactory();
		Options options = new Options();
		options.setTo(new EndpointReference(endpoint));
		serviceClient.setOptions(options);
		options.setAction(soapAction);
		OMElement response = serviceClient.sendReceive(xmlTooling
				.convertDOMToOM(xmlTooling.parseXML(request), factory));
		_log.info(MessageFormat.format("Answer from endpoint {0}: {1}",
				endpoint, response.toString()));
	}

	/**
	 * Prepare a skip message
	 */
	public String skip(String taskId) throws Exception {
		HashMap<String, String> skip = new HashMap<String, String>();
		skip.put("taskId", taskId);
		skip.put("token", token);
		sendSoapToTMP(templateMe("skip.ftl", skip), "skip");
		return StringUtils.EMPTY;
	}

	/**
	 * Prepare a reassign message
	 */
	public String reassign(String taskId, String user) throws Exception {
		HashMap<String, String> escalate = new HashMap<String, String>();
		escalate.put("taskId", taskId);
		escalate.put("token", token);
		escalate.put("user", user);
		sendSoapToWorkflow(templateMe("escalate.ftl", escalate), "escalate");
		return StringUtils.EMPTY;
	}

	/**
	 * Generate a revoke request
	 */
	public String revoke(String taskId) throws Exception {
		HashMap<String, String> root = new HashMap<String, String>();
		root.put("taskId", taskId);
		root.put("token", token);
		sendSoapToTMP(templateMe("revoke.ftl", root), "revokeTask");
		return StringUtils.EMPTY;
	}

	/**
	 * Generate a claim request, for the given user
	 */
	public String claim(String taskId, String user) throws Exception {
		HashMap<String, String> root = new HashMap<String, String>();
		root.put("taskId", taskId);
		root.put("user", user);
		root.put("token", token);
		sendSoapToTMP(templateMe("claim.ftl", root), "claimTask");
		return StringUtils.EMPTY;
	}

	/**
	 * Generate a complete request. This also adds some output to the task.
	 */
	public String complete(String taskId, HashMap complete,
			String outputTemplate) throws Exception {
		complete.put("taskId", taskId);
		complete.put("token", token);
		complete.put("user", getCurrentUser());
		complete.put("output", templateMe(outputTemplate, complete));
		String fullCompleteMessage = templateMe("complete.ftl", complete);
		_log.info("COMPLETE:"+complete.toString());
		sendSoapToTMP(fullCompleteMessage, "completeTask");
		return StringUtils.EMPTY;
	}

	public String getCurrentUser() throws RemoteException, AuthenticationException {
		Property[] props = tokenClient.getTokenProperties(token);
		String user = (String) PropertyUtils.getProperty(props,
				AuthenticationConstants.PROPERTY_USER).getValue();
		return user;
	}

	/**
	 * Get the Result from templating operation
	 */
	public String templateMe(String template, Map params) throws Exception {
		Template temp = cfg.getTemplate(template);
		StringWriter writer = new StringWriter();
		temp.process(params, writer);
		return writer.toString();
	}

	public Document createMessageAsDocument(HashMap map, String template)
			throws Exception {
		return new XmlTooling().parseXML(createMessage(map, template));
	}

	public String createMessage(HashMap map, String template) throws Exception {
		return templateMe(template, map);
	}

}
