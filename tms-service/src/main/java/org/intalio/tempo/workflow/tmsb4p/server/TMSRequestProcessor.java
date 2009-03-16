package org.intalio.tempo.workflow.tmsb4p.server;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.taskb4p.TaskStatus;
import org.intalio.tempo.workflow.taskb4p.TaskType;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.TMSConstants;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.OMMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intalio.wsHT.api.TStatus;
import com.intalio.wsHT.api.TTask;
import com.intalio.wsHT.api.TTaskQueryResultRow;
import com.intalio.wsHT.api.TTaskQueryResultSet;
import com.intalio.wsHT.api.xsd.CreateDocument;
import com.intalio.wsHT.api.xsd.CreateResponseDocument;
import com.intalio.wsHT.api.xsd.GetMyTasksDocument;
import com.intalio.wsHT.api.xsd.GetMyTasksResponseDocument;
import com.intalio.wsHT.api.xsd.QueryDocument;
import com.intalio.wsHT.api.xsd.QueryResponseDocument;
import com.intalio.wsHT.api.xsd.CreateDocument.Create;
import com.intalio.wsHT.api.xsd.GetMyTasksDocument.GetMyTasks;
import com.intalio.wsHT.api.xsd.GetMyTasksResponseDocument.GetMyTasksResponse;
import com.intalio.wsHT.api.xsd.QueryDocument.Query;
import com.intalio.wsHT.api.xsd.QueryResponseDocument.QueryResponse;
import com.intalio.wsHT.protocol.THumanTaskContext;

public class TMSRequestProcessor {
	final static Logger _logger = LoggerFactory
			.getLogger(TMSRequestProcessor.class);
	private ITMSServer _server;
	private static final OMFactory OM_FACTORY = OMAbstractFactory
			.getOMFactory();
	public static ThreadLocal<String> participantToken = new ThreadLocal<String>();

	/**
	 * dumy function
	 * 
	 * @return
	 */
	public OMElement marshallResponse() {
		return new TMSResponseMarshaller(OM_FACTORY) {
			public OMElement createOkResponse() {
				OMElement okResponse = createElement("okResponse");
				return okResponse;
			}
		}.createOkResponse();
	}

	public OMElement createOkResponse() {
		return new TMSResponseMarshaller(OM_FACTORY) {
			public OMElement createOkResponse() {
				OMElement okResponse = createElement("okResponse");
				return okResponse;
			}
		}.createOkResponse();
	}

	private AxisFault makeFault(Exception e) {
		if (e instanceof TMSException) {
			if (_logger.isDebugEnabled())
				_logger.debug(e.getMessage(), e);
			OMElement response = null;
			if (e instanceof InvalidInputFormatException)
				response = OM_FACTORY
						.createOMElement(TMSConstants.INVALID_INPUT_FORMAT);
			else if (e instanceof AccessDeniedException)
				response = OM_FACTORY
						.createOMElement(TMSConstants.ACCESS_DENIED);
			else if (e instanceof UnavailableTaskException)
				response = OM_FACTORY
						.createOMElement(TMSConstants.UNAVAILABLE_TASK);
			else if (e instanceof UnavailableAttachmentException)
				response = OM_FACTORY
						.createOMElement(TMSConstants.UNAVAILABLE_ATTACHMENT);
			else if (e instanceof AuthException)
				response = OM_FACTORY
						.createOMElement(TMSConstants.INVALID_TOKEN);

			else
				return AxisFault.makeFault(e);

			response.setText(e.getMessage());
			AxisFault axisFault = new AxisFault(e.getMessage(), e);
			axisFault.setDetail(response);
			return axisFault;
		} else if (e instanceof AxisFault) {
			_logger.error(e.getMessage(), e);
			return (AxisFault) e;
		} else {
			_logger.error(e.getMessage(), e);
			return AxisFault.makeFault(e);
		}
	}

	public void setServer(ITMSServer server) {
		_logger.info("TMSRequestProcessor.setServer:"
				+ server.getClass().getSimpleName());
		_server = server;
	}

	// private String GenTaskId(){
	//        
	// }

	private static String getParticipantToken() {
		String participantToken = null;
		try {
			MessageContext inMsgCtxt = MessageContext
					.getCurrentMessageContext();
			SOAPEnvelope envelope = inMsgCtxt.getEnvelope();
			// Log.event("soap body:"+envelope.toString());
			SOAPHeader header = envelope.getHeader();
			// Log.log("soap header:" + header.toString());
			Iterator it = header.getChildElements();

			while (it.hasNext()) {
				OMElement ele = (OMElement) it.next();
				// Log.event("element:"+ele.toString());
				if (ele.getLocalName().equals("participantToken"))
					// Log.event("particpant:"+ele.getText());
					participantToken = ele.getText();

			}

			System.out.println("participantToken=" + participantToken);
			if (participantToken == null)
				throw new AxisFault(
						"participant token not found in soap header");
			else
				return participantToken;
			// do whatever you want with this envelope.
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// /////////////////////////
	// operations
	// /////////////////////////
	public OMElement create(OMElement requestElement) throws AxisFault {

		System.out.print("=======created\n");
		// check participant token
		// String participantToken =
		// "VE9LRU4mJnVzZXI9PWFkbWluJiZpc3N1ZWQ9PTExODA0NzY2NjUzOTMmJnJvbGVzPT1pbnRhbGlvXHByb2Nlc3NhZG1pbmlzdHJhdG9yLGV4YW1wbGVzXGVtcGxveWVlLGludGFsaW9ccHJvY2Vzc21hbmFnZXIsZXhhbXBsZXNcbWFuYWdlciYmZnVsbE5hbWU9PUFkbWluaW5pc3RyYXRvciYmZW1haWw9PWFkbWluQGV4YW1wbGUuY29tJiZub25jZT09NDMxNjAwNTE5NDM5MTk1MDMzMyYmdGltZXN0YW1wPT0xMTgwNDc2NjY1Mzk1JiZkaWdlc3Q9PTVmM1dQdDBXOEp2UlpRM2gyblJ6UkRrenRwTT0mJiYmVE9LRU4";
		String participantToken = getParticipantToken();
		if (participantToken == null)
			throw makeFault(new Exception(
					"Cannot get participant toke in soap header"));

		// Log.setFile("d:\\tempo.log");
		// Log.log("enter");

		try {
			// unmarshal request
			CreateDocument req = CreateDocument.Factory.parse(requestElement
					.getXMLStreamReader());
			Create r = req.getCreate();
			THumanTaskContext tasks[] = req.getCreate()
					.getHumanTaskContextArray();

			// call server
			for (int i = 0; i < tasks.length; i++) {
				// Log.log("task "+i);
				Task task = new Task();
				task.setId(UUID.randomUUID().toString()); // temporary solution
															// to generate task
															// id
				task.setName("test");
				task.setCreatedOn(new Date());
				task.setPriority(tasks[i].getPriority().intValue());
				task.setStatus(TaskStatus.CREATED);
				task.setInputMessage(req.getCreate().getIn());
				// task.setPotentialOwners(potentialOwners)(tasks[i].getPeopleAssignments().getPotentialOwnersArray()
				task.setSkipable(tasks[i].getIsSkipable());
				task.setTaskInitiator(tasks[i].getPeopleAssignments()
						.getTaskInitiatorArray().toString());
				task.setTaskType(TaskType.TASK);
				_server.create(task, participantToken);
			}

			// marshal response
			CreateResponseDocument resp = CreateResponseDocument.Factory
					.newInstance();
			resp.addNewCreateResponse().setOut("ok");

			// convert to OMElment
			return this.convertXML(resp);
			// return this.createOkResponse();

		} catch (Exception e) {
			throw makeFault(e);
		}

	}

	public OMElement query(OMElement requestElement) throws AxisFault {
		System.out.print("=======query\n");
		// check participant token
		String participantToken = getParticipantToken();
		if (participantToken == null)
			throw makeFault(new Exception(
					"Cannot get participant toke in soap header"));

		try {
			// unmarshal request
			Query req = QueryDocument.Factory.parse(
					requestElement.getXMLStreamReader()).getQuery();

			// call server
			List<Task> ret = _server.query(participantToken, req
					.getSelectClause(), req.getWhereClause(), req
					.getOrderByClause(), req.getMaxTasks(), req
					.getTaskIndexOffset());

			// marshal response
			QueryResponseDocument resp = QueryResponseDocument.Factory
					.newInstance();
			for (int i = 0; i < ret.size(); i++) {
				Task r = ret.get(i);
				QueryResponse qr = resp.addNewQueryResponse();
				TTaskQueryResultSet result = qr.addNewQuery();
				TTaskQueryResultRow row = result.addNewRow();
				Calendar ca = Calendar.getInstance();
				ca.setTime(r.getActivationTime());
				row.addActivationTime(ca);
				row.addActualOwner(r.getActualOwner());
				// ? row.addCompleteByExists(r.)
				// ...

			}
			return XmlTooling.convertDocument(resp);
		} catch (Exception e) {
			throw makeFault(e);
		}

	}

	public OMElement getMyTasks(OMElement requestElement) throws AxisFault {
		try {
			// check participant token
			String participantToken = getParticipantToken();
			if (participantToken == null)
				throw makeFault(new Exception(
						"Cannot get participant toke in soap header"));

			// unmarshal request
			GetMyTasksDocument reqDoc = GetMyTasksDocument.Factory
					.parse(requestElement.getXMLStreamReader());
			GetMyTasks req = reqDoc.getGetMyTasks();

			// process request
			List<Task> tasks = _server.getMyTasks(participantToken, req
					.getTaskType(), req.getGenericHumanRole(), req
					.getWorkQueue(), req.getStatusArray(),
					req.getWhereClause(), req.getCreatedOnClause(), req
							.getMaxTasks());
			if (tasks != null) {
				this._logger.info("tasks list size:" + tasks.size());
				System.out.println("tasks list size:" + tasks.size());
			} else {
				System.out.println("tasks list is null");
			}

			for (int i = 0; i < tasks.size(); i++) {
				this._logger.info("task " + i + " created on "
						+ tasks.get(i).getCreatedOn().toString());
			}

			// marshal response
			GetMyTasksResponseDocument retDoc = GetMyTasksResponseDocument.Factory
					.newInstance();
			GetMyTasksResponse ret = retDoc.addNewGetMyTasksResponse();
			for (int i = 0; i < tasks.size(); i++) {
				// GetMyTasksResponse node = ret.addNewGetMyTasksResponse();
				TTask tt = ret.addNewTaskAbstract();
				Task t = tasks.get(i);
				if (t.getActivationTime() != null) {
					Calendar at = Calendar.getInstance();
					at.setTime(t.getActivationTime());
					tt.setActivationTime(at);
				}
				tt.setActualOwner(t.getActualOwner());
				System.out.println("created by:" + t.getCreatedBy());
				tt.setCreatedBy(t.getCreatedBy());
				if (t.getCreatedOn() != null) {
					Calendar createdOn = Calendar.getInstance();
					createdOn.setTime(t.getCreatedOn());
					tt.setCreatedOn(createdOn);
				}
				tt.setId(t.getId());
				System.out.println("status:" + t.getStatus());
				if (t.getStatus() != null)
					tt.setStatus(TStatus.Enum.forString(t.getStatus()
							.toString()));
				tt.setTaskInitiator(t.getTaskInitiator());
				if (t.getTaskType() != null)
					tt.setTaskType(t.getTaskType().toString());
				_logger.info("task " + i + ": " + tt.xmlText());
				System.out.println("task " + i + ": " + tt.xmlText());

			}

			// _logger.info("xmltoolsing convert result:"+
			// convertXML(ret).toString());
			// _logger.info("xmlText:"+ret.xmlText());
			System.out.println("xmltoolsing convert result:"
					+ convertXML(ret).toString());
			System.out.println("xmlText:" + ret.xmlText());

			// convert to OMElment
			return convertXML(retDoc);

			// return this.createOkResponse();

		} catch (Exception e) {
			throw makeFault(e);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private abstract class TMSResponseMarshaller extends OMMarshaller {

		public TMSResponseMarshaller(OMFactory omFactory) {
			super(
					omFactory,
					omFactory
							.createOMNamespace(
									"http://www.intalio.com/BPMS/Workflow/HumanTaskOperationServices-20081209/",
									"tmsb4p"));
		}
	}

	private OMElement convertXML(XmlObject xmlObject) {
		if (xmlObject == null)
			return null;
		HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
		suggestedPrefixes.put(TaskXMLConstants.TASK_NAMESPACE,
				TaskXMLConstants.TASK_NAMESPACE_PREFIX);
		XmlOptions opts = new XmlOptions();
		opts.setSaveSuggestedPrefixes(suggestedPrefixes);
		OMElement dm = null;
		InputStream is = xmlObject.newInputStream(opts);
		StAXOMBuilder builder;
		try {
			builder = new StAXOMBuilder(is);
			dm = builder.getDocumentElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return dm;
	}
}
