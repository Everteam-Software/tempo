package org.intalio.tempo.workflow.tmsb4p.server;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
import org.intalio.tempo.workflow.taskb4p.Attachment;
import org.intalio.tempo.workflow.taskb4p.AttachmentInfo;
import org.intalio.tempo.workflow.taskb4p.Comment;
import org.intalio.tempo.workflow.taskb4p.OrganizationalEntity;
import org.intalio.tempo.workflow.taskb4p.Principal;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.taskb4p.TaskStatus;
import org.intalio.tempo.workflow.taskb4p.TaskType;
import org.intalio.tempo.workflow.taskb4p.UserOrganizationalEntity;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.TMSConstants;
import org.intalio.tempo.workflow.tmsb4p.server.dao.GenericRoleType;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intalio.wsHT.TGroup;
import com.intalio.wsHT.TGrouplist;
import com.intalio.wsHT.TOrganizationalEntity;
import com.intalio.wsHT.TUser;
import com.intalio.wsHT.TUserlist;
import com.intalio.wsHT.api.TAttachment;
import com.intalio.wsHT.api.TAttachmentInfo;
import com.intalio.wsHT.api.TComment;
import com.intalio.wsHT.api.TStatus;
import com.intalio.wsHT.api.TTask;
import com.intalio.wsHT.api.TTaskQueryResultRow;
import com.intalio.wsHT.api.TTaskQueryResultSet;
import com.intalio.wsHT.api.xsd.ActivateDocument;
import com.intalio.wsHT.api.xsd.ActivateResponseDocument;
import com.intalio.wsHT.api.xsd.AddAttachmentDocument;
import com.intalio.wsHT.api.xsd.AddAttachmentResponseDocument;
import com.intalio.wsHT.api.xsd.AddCommentDocument;
import com.intalio.wsHT.api.xsd.AddCommentResponseDocument;
import com.intalio.wsHT.api.xsd.ClaimDocument;
import com.intalio.wsHT.api.xsd.ClaimResponseDocument;
import com.intalio.wsHT.api.xsd.CompleteDocument;
import com.intalio.wsHT.api.xsd.CompleteResponseDocument;
import com.intalio.wsHT.api.xsd.CreateDocument;
import com.intalio.wsHT.api.xsd.CreateResponseDocument;
import com.intalio.wsHT.api.xsd.DelegateDocument;
import com.intalio.wsHT.api.xsd.DelegateResponseDocument;
import com.intalio.wsHT.api.xsd.DeleteAttachmentsDocument;
import com.intalio.wsHT.api.xsd.DeleteAttachmentsResponseDocument;
import com.intalio.wsHT.api.xsd.FailDocument;
import com.intalio.wsHT.api.xsd.FailResponseDocument;
import com.intalio.wsHT.api.xsd.ForwardDocument;
import com.intalio.wsHT.api.xsd.ForwardResponseDocument;
import com.intalio.wsHT.api.xsd.GetAttachmentInfosDocument;
import com.intalio.wsHT.api.xsd.GetAttachmentInfosResponseDocument;
import com.intalio.wsHT.api.xsd.GetAttachmentsDocument;
import com.intalio.wsHT.api.xsd.GetAttachmentsResponseDocument;
import com.intalio.wsHT.api.xsd.GetCommentsDocument;
import com.intalio.wsHT.api.xsd.GetCommentsResposneDocument;
import com.intalio.wsHT.api.xsd.GetMyTasksDocument;
import com.intalio.wsHT.api.xsd.GetMyTasksResponseDocument;
import com.intalio.wsHT.api.xsd.GetTaskDescriptionDocument;
import com.intalio.wsHT.api.xsd.GetTaskDescriptionResponseDocument;
import com.intalio.wsHT.api.xsd.GetTaskInfoDocument;
import com.intalio.wsHT.api.xsd.GetTaskInfoResponseDocument;
import com.intalio.wsHT.api.xsd.NominateDocument;
import com.intalio.wsHT.api.xsd.NominateResponseDocument;
import com.intalio.wsHT.api.xsd.QueryDocument;
import com.intalio.wsHT.api.xsd.QueryResponseDocument;
import com.intalio.wsHT.api.xsd.ReleaseDocument;
import com.intalio.wsHT.api.xsd.ReleaseResponseDocument;
import com.intalio.wsHT.api.xsd.RemoveDocument;
import com.intalio.wsHT.api.xsd.RemoveResponseDocument;
import com.intalio.wsHT.api.xsd.ResumeDocument;
import com.intalio.wsHT.api.xsd.ResumeResponseDocument;
import com.intalio.wsHT.api.xsd.SetGenericHumanRoleDocument;
import com.intalio.wsHT.api.xsd.SetGenericHumanRoleResponseDocument;
import com.intalio.wsHT.api.xsd.SetPriorityDocument;
import com.intalio.wsHT.api.xsd.SetPriorityResponseDocument;
import com.intalio.wsHT.api.xsd.SkipDocument;
import com.intalio.wsHT.api.xsd.SkipResponseDocument;
import com.intalio.wsHT.api.xsd.StartDocument;
import com.intalio.wsHT.api.xsd.StartResponseDocument;
import com.intalio.wsHT.api.xsd.StopDocument;
import com.intalio.wsHT.api.xsd.StopResponseDocument;
import com.intalio.wsHT.api.xsd.ActivateDocument.Activate;
import com.intalio.wsHT.api.xsd.AddAttachmentDocument.AddAttachment;
import com.intalio.wsHT.api.xsd.AddCommentDocument.AddComment;
import com.intalio.wsHT.api.xsd.ClaimDocument.Claim;
import com.intalio.wsHT.api.xsd.ClaimResponseDocument.ClaimResponse;
import com.intalio.wsHT.api.xsd.CompleteDocument.Complete;
import com.intalio.wsHT.api.xsd.CompleteResponseDocument.CompleteResponse;
import com.intalio.wsHT.api.xsd.CreateDocument.Create;
import com.intalio.wsHT.api.xsd.DelegateDocument.Delegate;
import com.intalio.wsHT.api.xsd.DelegateResponseDocument.DelegateResponse;
import com.intalio.wsHT.api.xsd.DeleteAttachmentsDocument.DeleteAttachments;
import com.intalio.wsHT.api.xsd.FailDocument.Fail;
import com.intalio.wsHT.api.xsd.FailResponseDocument.FailResponse;
import com.intalio.wsHT.api.xsd.ForwardDocument.Forward;
import com.intalio.wsHT.api.xsd.ForwardResponseDocument.ForwardResponse;
import com.intalio.wsHT.api.xsd.GetAttachmentInfosDocument.GetAttachmentInfos;
import com.intalio.wsHT.api.xsd.GetAttachmentInfosResponseDocument.GetAttachmentInfosResponse;
import com.intalio.wsHT.api.xsd.GetAttachmentsDocument.GetAttachments;
import com.intalio.wsHT.api.xsd.GetAttachmentsResponseDocument.GetAttachmentsResponse;
import com.intalio.wsHT.api.xsd.GetCommentsDocument.GetComments;
import com.intalio.wsHT.api.xsd.GetCommentsResposneDocument.GetCommentsResposne;
import com.intalio.wsHT.api.xsd.GetMyTasksDocument.GetMyTasks;
import com.intalio.wsHT.api.xsd.GetMyTasksResponseDocument.GetMyTasksResponse;
import com.intalio.wsHT.api.xsd.GetTaskDescriptionDocument.GetTaskDescription;
import com.intalio.wsHT.api.xsd.GetTaskDescriptionResponseDocument.GetTaskDescriptionResponse;
import com.intalio.wsHT.api.xsd.GetTaskInfoDocument.GetTaskInfo;
import com.intalio.wsHT.api.xsd.GetTaskInfoResponseDocument.GetTaskInfoResponse;
import com.intalio.wsHT.api.xsd.NominateDocument.Nominate;
import com.intalio.wsHT.api.xsd.QueryDocument.Query;
import com.intalio.wsHT.api.xsd.QueryResponseDocument.QueryResponse;
import com.intalio.wsHT.api.xsd.ReleaseDocument.Release;
import com.intalio.wsHT.api.xsd.ReleaseResponseDocument.ReleaseResponse;
import com.intalio.wsHT.api.xsd.RemoveDocument.Remove;
import com.intalio.wsHT.api.xsd.RemoveResponseDocument.RemoveResponse;
import com.intalio.wsHT.api.xsd.ResumeDocument.Resume;
import com.intalio.wsHT.api.xsd.ResumeResponseDocument.ResumeResponse;
import com.intalio.wsHT.api.xsd.SetGenericHumanRoleDocument.SetGenericHumanRole;
import com.intalio.wsHT.api.xsd.SetPriorityDocument.SetPriority;
import com.intalio.wsHT.api.xsd.SkipDocument.Skip;
import com.intalio.wsHT.api.xsd.SkipResponseDocument.SkipResponse;
import com.intalio.wsHT.api.xsd.StartDocument.Start;
import com.intalio.wsHT.api.xsd.StartResponseDocument.StartResponse;
import com.intalio.wsHT.api.xsd.StopDocument.Stop;
import com.intalio.wsHT.api.xsd.StopResponseDocument.StopResponse;
import com.intalio.wsHT.protocol.THumanTaskContext;

import edu.emory.mathcs.backport.java.util.Arrays;

public class TMSRequestProcessor {
    final static Logger _logger = LoggerFactory.getLogger(TMSRequestProcessor.class);
    private ITMSServer _server;
    private static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();

    // public static ThreadLocal<String> participantToken = new
    // ThreadLocal<String>();

    // /**
    // * dumy function
    // *
    // * @return
    // */
    // public OMElement marshallResponse() {
    // return new TMSResponseMarshaller(OM_FACTORY) {
    // public OMElement createOkResponse() {
    // OMElement okResponse = createElement("okResponse");
    // return okResponse;
    // }
    // }.createOkResponse();
    // }

    // public OMElement createOkResponse() {
    // return new TMSResponseMarshaller(OM_FACTORY) {
    // public OMElement createOkResponse() {
    // OMElement okResponse = createElement("okResponse");
    // return okResponse;
    // }
    // }.createOkResponse();
    // }

    /************************************************************************
     * Internal function
     ************************************************************************/

    /**
     * @TODO need to be improved
     */
    private AxisFault makeFault(Exception e) {
        if (e instanceof TMSException) {
            if (_logger.isDebugEnabled())
                _logger.debug(e.getMessage(), e);
            OMElement response = null;
            if (e instanceof InvalidInputFormatException)
                response = OM_FACTORY.createOMElement(TMSConstants.INVALID_INPUT_FORMAT);
            else if (e instanceof AccessDeniedException)
                response = OM_FACTORY.createOMElement(TMSConstants.ACCESS_DENIED);
            else if (e instanceof UnavailableTaskException)
                response = OM_FACTORY.createOMElement(TMSConstants.UNAVAILABLE_TASK);
            else if (e instanceof UnavailableAttachmentException)
                response = OM_FACTORY.createOMElement(TMSConstants.UNAVAILABLE_ATTACHMENT);
            else if (e instanceof AuthException)
                response = OM_FACTORY.createOMElement(TMSConstants.INVALID_TOKEN);

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
        _logger.info("TMSRequestProcessor.setServer:" + server.getClass().getSimpleName());
        _server = server;
    }

    // private String GenTaskId(){
    //        
    // }

    private static String getParticipantToken() throws AxisFault {
        String participantToken = null;

        MessageContext inMsgCtxt = MessageContext.getCurrentMessageContext();
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

        // System.out.println("participantToken=" + participantToken);
        if (participantToken == null)
            throw new AxisFault("participant token not found in soap header");
        else
            return participantToken;
    }

    private void marshalTask(Task t, TTask tt) {
        if (t == null || tt == null)
            return;
        if (t.getActivationTime() != null) {
            tt.setActivationTime(convertDateToCalendar(t.getActivationTime()));
        }
        tt.setActualOwner(t.getActualOwner());

        // Business Administrators
        tt.setBusinessAdministrators(marshalOrgEntity(t.getBusinessAdministrators()));

        tt.setCreatedBy(t.getCreatedBy());
        if (t.getCreatedOn() != null) {
            tt.setCreatedOn(convertDateToCalendar(t.getCreatedOn()));
        }
        tt.setEscalated(t.isEscalated());

        tt.setExpirationTime(convertDateToCalendar(t.getExpirationTime()));
        
        if (t.getAttachments() != null && t.getAttachments().size() > 0) {
            tt.setHasAttachments(true);
        } else {
            tt.setHasAttachments(false);
        }

        if (t.getComments() != null && t.getComments().size() > 0) {
            tt.setHasComments(true);
        } else {
            tt.setHasComments(false);
        }
        if (t.getFaultMessage() != null){
            tt.setHasFault(true);
        }else{
            tt.setHasFault(false);
        }
        if (t.getOutputMessage() != null){
            tt.setHasOutput(true);
        }else{
            tt.setHasOutput(false);
        }
        if (t.getPotentialOwners() != null && t.getPotentialOwners().getPrincipals() != null && t.getPotentialOwners().getPrincipals().size() > 0) {
            tt.setHasPotentialOwners(true);
        } else {
            tt.setHasPotentialOwners(false);
        }
        tt.setId(t.getId());
        
        tt.setIsSkipable(t.isSkipable());
        tt.setName(new javax.xml.namespace.QName(t.getName()));
        tt.setNotificationRecipients(marshalOrgEntity(t.getNotificationRecipients()));
        tt.setPotentialOwners(marshalOrgEntity(t.getPotentialOwners()));
        tt.setPresentationName(t.getPresentationName());
        tt.setPresentationSubject(t.getPresentationSubject());
        tt.setPrimarySearchBy(t.getPrimarySearchBy());
        tt.setPriority(BigInteger.valueOf(t.getPriority()));
        
        if (t.getRenderingMethName() != null)
            tt.setRenderingMethodExists(true);
        else
            tt.setRenderingMethodExists(false);

        if (t.getStartBy() != null)
            tt.setStartByExists(true);
        else
            tt.setStartByExists(false);

        tt.setTaskInitiator(t.getTaskInitiator());
        tt.setTaskStakeholders(marshalOrgEntity(t.getTaskStakeholders()));

        if (t.getStatus() != null)
            tt.setStatus(TStatus.Enum.forString(t.getStatus().toString()));
        if (t.getTaskType() != null)
            tt.setTaskType(t.getTaskType().toString());
        
    }

    private void marshalAttachmentInfo(AttachmentInfo attInfo, TAttachmentInfo tAttInfo) {
        tAttInfo.setAccessType(attInfo.getAccessType().name());

        tAttInfo.setAttachedAt(convertDateToCalendar(attInfo.getAttachedAt()));

        tAttInfo.setAttachedBy(attInfo.getAttachedBy());
        tAttInfo.setContentType(attInfo.getContentType());
        tAttInfo.setName(attInfo.getName());
    }

    // private abstract class TMSResponseMarshaller extends OMMarshaller {
    //
    // public TMSResponseMarshaller(OMFactory omFactory) {
    // super(
    // omFactory,
    // omFactory
    // .createOMNamespace(
    // "http://www.intalio.com/BPMS/Workflow/HumanTaskOperationServices-20081209/",
    // "tmsb4p"));
    // }
    // }

    private OMElement convertXML(XmlObject xmlObject) {
        if (xmlObject == null)
            return null;
        HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
        suggestedPrefixes.put(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
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

    private TOrganizationalEntity marshalOrgEntity(OrganizationalEntity oe) {
        TOrganizationalEntity tOE = TOrganizationalEntity.Factory.newInstance();
        if (oe != null && oe.getEntityType() != null) {
            if (oe.getEntityType().equalsIgnoreCase(OrganizationalEntity.USER_ENTITY)) {
                TUserlist tUL = tOE.addNewUsers();
                Iterator<Principal> it = oe.getPrincipals().iterator();
                while (it != null && it.hasNext()) {
                    TUser tUser = tUL.addNewUser();
                    tUser.setStringValue(it.next().getValue());
                }
            } else {
                TGrouplist tGL = tOE.addNewGroups();
                Iterator<Principal> it = oe.getPrincipals().iterator();
                while (it != null && it.hasNext()) {
                    TGroup tGroup = tGL.addNewGroup();
                    tGroup.setStringValue(it.next().getValue());
                }
            }
        }
        return tOE;
    }

    private Calendar convertDateToCalendar(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        }else{
            return null;
        }

    }

    /************************************************************************
     * Main Participant operation (most will change task status)
     * ------------------------------------------------------------------------
     * Create/remove/start/stop/complete/fail/skip/forward/delegate/resume/
     * release/suspend/suspendUtil
     ************************************************************************/

    /**
     * Create a Task
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     * @author JackieJu
     */
    public OMElement create(OMElement requestElement) throws AxisFault {

        System.out.print("=======created\n");
        String taskID = null;
        // check participant token
        // String participantToken =
        // "VE9LRU4mJnVzZXI9PWFkbWluJiZpc3N1ZWQ9PTExODA0NzY2NjUzOTMmJnJvbGVzPT1pbnRhbGlvXHByb2Nlc3NhZG1pbmlzdHJhdG9yLGV4YW1wbGVzXGVtcGxveWVlLGludGFsaW9ccHJvY2Vzc21hbmFnZXIsZXhhbXBsZXNcbWFuYWdlciYmZnVsbE5hbWU9PUFkbWluaW5pc3RyYXRvciYmZW1haWw9PWFkbWluQGV4YW1wbGUuY29tJiZub25jZT09NDMxNjAwNTE5NDM5MTk1MDMzMyYmdGltZXN0YW1wPT0xMTgwNDc2NjY1Mzk1JiZkaWdlc3Q9PTVmM1dQdDBXOEp2UlpRM2gyblJ6UkRrenRwTT0mJiYmVE9LRU4";
        String participantToken = getParticipantToken();
        if (participantToken == null)
            throw makeFault(new Exception("Cannot get participant toke in soap header"));

        try {
            // unmarshal request
            CreateDocument req = CreateDocument.Factory.parse(requestElement.getXMLStreamReader());
            Create r = req.getCreate();
            
            THumanTaskContext tasks[] = req.getCreate().getHumanTaskContextArray();

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

                OrganizationalEntity potentialOwner = new UserOrganizationalEntity();
                Principal user = new Principal();
                user.setValue("some test user");
                user.setOrgEntity(potentialOwner);
                Set<Principal> users = new HashSet<Principal>();
                users.add(user);
                potentialOwner.setPrincipals(users);

                task.setPotentialOwners(potentialOwner);
                task.setInputMessage(req.getCreate().getIn());
                // task.setPotentialOwners(potentialOwners)(tasks[i].getPeopleAssignments().getPotentialOwnersArray()
                task.setSkipable(tasks[i].getIsSkipable());
                task.setTaskInitiator(tasks[i].getPeopleAssignments().getTaskInitiatorArray().toString());
                task.setTaskType(TaskType.TASK);
                taskID = task.getId();
                _server.create(task, participantToken);
            }

            // marshal response
            CreateResponseDocument resp = CreateResponseDocument.Factory.newInstance();
            resp.addNewCreateResponse().setOut("Task:" + taskID + " ---> ok");

            // convert to OMElment
            return this.convertXML(resp);
            // return this.createOkResponse();

        } catch (Exception e) {
            throw makeFault(e);
        }

    }

    /**
     * Remove a Task
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     * @author Jackie Ju
     */
    public OMElement remove(OMElement requestElement) throws AxisFault {
        RemoveResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            RemoveDocument reqDoc = RemoveDocument.Factory.parse(requestElement.getXMLStreamReader());
            Remove req = reqDoc.getRemove();

            // call TMSServer to process request
            this._server.remove(participantToken, req.getIdentifier());

            // marshal response
            retDoc = RemoveResponseDocument.Factory.newInstance();
            RemoveResponse ret = retDoc.addNewRemoveResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }

        return this.convertXML(retDoc);
    }

    /**
     * Claim responsibility for a task, i.e. set the task to status Reserved In
     * task identifier
     * 
     * @param requestElement
     *            task identifier
     * @return
     * @throws AxisFault
     * @author JackieJu
     */
    public OMElement claim(OMElement requestElement) throws AxisFault {
        ClaimResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            ClaimDocument reqDoc = ClaimDocument.Factory.parse(requestElement.getXMLStreamReader());
            Claim req = reqDoc.getClaim();

            // call TMSServer to process request
            this._server.claim(participantToken, req.getIdentifier());

            // marshal response
            retDoc = ClaimResponseDocument.Factory.newInstance();
            ClaimResponse ret = retDoc.addNewClaimResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }

        return this.convertXML(retDoc);
    }

    /**
     * Start the execution of the task, i.e. set the task to status InProgress.
     * In task identifier
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     */
    public OMElement start(OMElement requestElement) throws AxisFault {
        StartResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            StartDocument reqDoc = StartDocument.Factory.parse(requestElement.getXMLStreamReader());
            Start req = reqDoc.getStart();

            // call TMSServer to process request
            this._server.start(participantToken, req.getIdentifier());

            // marshal response
            retDoc = StartResponseDocument.Factory.newInstance();
            StartResponse ret = retDoc.addNewStartResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }

        return this.convertXML(retDoc);
    }

    /**
     * Cancel/stop the processing of the task. The task returns to the Reserved
     * state. In task identifier Out void
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     * @author Jackie Ju
     */
    public OMElement stop(OMElement requestElement) throws AxisFault {
        StopResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            StopDocument reqDoc = StopDocument.Factory.parse(requestElement.getXMLStreamReader());
            Stop req = reqDoc.getStop();

            // call TMSServer to process request
            this._server.stop(participantToken, req.getIdentifier());

            // marshal response
            retDoc = StopResponseDocument.Factory.newInstance();
            StopResponse ret = retDoc.addNewStopResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }

        return this.convertXML(retDoc);
    }

    /**
     * Release the task, i.e. set the task back to status Ready. In � task
     * identifier
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     * @author juweihua
     */
    public OMElement release(OMElement requestElement) throws AxisFault {
        ReleaseResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            ReleaseDocument reqDoc = ReleaseDocument.Factory.parse(requestElement.getXMLStreamReader());
            Release req = reqDoc.getRelease();

            // call TMSServer to process request
            this._server.release(participantToken, req.getIdentifier());

            // marshal response
            retDoc = ReleaseResponseDocument.Factory.newInstance();
            ReleaseResponse ret = retDoc.addNewReleaseResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }
        return this.convertXML(retDoc);
    }

    /**
     * Execution of the task finished successfully. If no output data is set the
     * operation returns illegalArgumentFault. In � task identifier � output
     * data of task Out � void Authorization � Actual Owner
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     */
    public OMElement complete(OMElement requestElement) throws AxisFault {
        CompleteResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            CompleteDocument reqDoc = CompleteDocument.Factory.parse(requestElement.getXMLStreamReader());
            Complete req = reqDoc.getComplete();

            // call TMSServer to process request
            this._server.complete(participantToken, req.getIdentifier(), req.getTaskData());

            // marshal response
            retDoc = CompleteResponseDocument.Factory.newInstance();
            CompleteResponse ret = retDoc.addNewCompleteResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }
        return this.convertXML(retDoc);
    }

    /**
     * Actual owner completes the execution of the task raising a fault. In �
     * task identifier � fault name � fault data Out � void Authorization �
     * Actual Owner The fault illegalOperationFault is returned if the task
     * interface defines no faults. If fault name or fault data is not set the
     * operation returns illegalArgumentFault.
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     */
    public OMElement fail(OMElement requestElement) throws AxisFault {
        FailResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            FailDocument reqDoc = FailDocument.Factory.parse(requestElement.getXMLStreamReader());
            Fail req = reqDoc.getFail();

            // call TMSServer to process request
            this._server.fail(participantToken, req.getIdentifier(), req.getFaultName(), req.getFaultData());

            // marshal response
            retDoc = FailResponseDocument.Factory.newInstance();
            FailResponse ret = retDoc.addNewFailResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }
        return this.convertXML(retDoc);
    }

    /**
     * Skip the task. If the task is not skipable then the fault
     * illegalOperationFault is returned. In � task identifier Out � void
     * 
     * Authorization Task Initiator Actual Owner Business Administrator
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     */
    public OMElement skip(OMElement requestElement) throws AxisFault {
        SkipResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            SkipDocument reqDoc = SkipDocument.Factory.parse(requestElement.getXMLStreamReader());
            Skip req = reqDoc.getSkip();

            // call TMSServer to process request
            this._server.skip(participantToken, req.getIdentifier());

            // marshal response
            retDoc = SkipResponseDocument.Factory.newInstance();
            SkipResponse ret = retDoc.addNewSkipResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }
        return this.convertXML(retDoc);
    }

    /**
     * Resume a suspended task. In � task identifier Out � void
     * 
     * Authorization Potential Owners (state Ready) Actual Owner Business
     * Administrator
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     */
    public OMElement resume(OMElement requestElement) throws AxisFault {
        ResumeResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            ResumeDocument reqDoc = ResumeDocument.Factory.parse(requestElement.getXMLStreamReader());
            Resume req = reqDoc.getResume();

            // call TMSServer to process request
            this._server.resume(participantToken, req.getIdentifier());

            // marshal response
            retDoc = ResumeResponseDocument.Factory.newInstance();
            ResumeResponse ret = retDoc.addNewResumeResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }
        return this.convertXML(retDoc);
    }

    /**
     * Forward the task to another organization entity. The caller has to
     * specify the receiving organizational entity. Potential owners can only
     * forward a task while the task is in the Ready state. In � task identifier
     * � organizational entity (htd:tOrganization alEntity) Out � void
     * Authorization Potential Owners Actual Owner Business Administrator
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     */
    public OMElement forward(OMElement requestElement) throws AxisFault {
        ForwardResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            ForwardDocument reqDoc = ForwardDocument.Factory.parse(requestElement.getXMLStreamReader());
            Forward req = reqDoc.getForward();

            // call TMSServer to process request
            this._server.forward(participantToken, req.getIdentifier());

            // marshal response
            retDoc = ForwardResponseDocument.Factory.newInstance();
            ForwardResponse ret = retDoc.addNewForwardResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }
        return this.convertXML(retDoc);
    }

    /**
     * Assign the task to one user and set the task to state Reserved. If the
     * recipient was not a potential owner then this person is added to the set
     * of potential owners. In � task identifier � organizational entity
     * (htd:tOrganization alEntity) Out � void Authorization Potential Owners
     * (only in Ready state) Actual Owner Business Administrator
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     */
    public OMElement delegate(OMElement requestElement) throws AxisFault {
        DelegateResponseDocument retDoc = null;
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            DelegateDocument reqDoc = DelegateDocument.Factory.parse(requestElement.getXMLStreamReader());
            Delegate req = reqDoc.getDelegate();

            // call TMSServer to process request
            this._server.delegate(participantToken, req.getIdentifier());

            // marshal response
            retDoc = DelegateResponseDocument.Factory.newInstance();
            DelegateResponse ret = retDoc.addNewDelegateResponse();

        } catch (Exception e) {

            throw makeFault(e);
        }
        return this.convertXML(retDoc);
    }

    /***********************************************************************
     * Task data operation (most will only change task property)
     * ------------------------------------------------------------------------
     * getXXX/setXXX
     ************************************************************************/

    /**
     * Change the priority of the task. The caller has to specify the integer
     * value of the new priority.
     */
    public OMElement setPriority(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            SetPriorityDocument spd = SetPriorityDocument.Factory.parse(requestElement.getXMLStreamReader());
            SetPriority sp = spd.getSetPriority();

            String identifier = sp.getIdentifier();
            int priority = sp.getPriority().intValue();

            _server.setPriority(participantToken, identifier, priority);

            SetPriorityResponseDocument ret = SetPriorityResponseDocument.Factory.newInstance();
            ret.addNewSetPriorityResponse();

            return convertXML(ret);
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    /**
     * Add attachment to the task
     */
    public OMElement addAttachment(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            AddAttachmentDocument aad = AddAttachmentDocument.Factory.parse(requestElement.getXMLStreamReader());
            AddAttachment aa = aad.getAddAttachment();

            _server.addAttachment(participantToken, aa.getIdentifier(), aa.getName(), aa.getAccessType(), aa.getAttachment().xmlText());
            // TODO process the attachment field

            AddAttachmentResponseDocument ret = AddAttachmentResponseDocument.Factory.newInstance();
            ret.addNewAddAttachmentResponse();

            return convertXML(ret);
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    /**
     * Get attachment information for all attachments associated with the task
     */
    public OMElement getAttachmentInfos(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            GetAttachmentInfosDocument gaid = GetAttachmentInfosDocument.Factory.parse(requestElement.getXMLStreamReader());
            GetAttachmentInfos gai = gaid.getGetAttachmentInfos();

            List<AttachmentInfo> attInfos = _server.getAttachmentInfos(participantToken, gai.getIdentifier());

            GetAttachmentInfosResponseDocument retDoc = GetAttachmentInfosResponseDocument.Factory.newInstance();
            GetAttachmentInfosResponse ret = retDoc.addNewGetAttachmentInfosResponse();
            Iterator<AttachmentInfo> it = attInfos.iterator();
            while (it.hasNext()) {
                TAttachmentInfo tAttInfo = ret.addNewInfo();
                AttachmentInfo attInfo = it.next();
                marshalAttachmentInfo(attInfo, tAttInfo);
            }

            return convertXML(retDoc);
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    /**
     * Get all attachments of a task with a given name
     */
    public OMElement getAttachments(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            GetAttachmentsDocument gad = GetAttachmentsDocument.Factory.parse(requestElement.getXMLStreamReader());
            GetAttachments ga = gad.getGetAttachments();

            List<Attachment> atts = _server.getAttachments(participantToken, ga.getIdentifier(), ga.getAttachmentName());
            GetAttachmentsResponseDocument retDoc = GetAttachmentsResponseDocument.Factory.newInstance();
            GetAttachmentsResponse gar = retDoc.addNewGetAttachmentsResponse();

            Iterator<Attachment> it = atts.iterator();
            while (it.hasNext()) {
                TAttachment tAtt = gar.addNewAttachment();
                TAttachmentInfo tAttInfo = tAtt.addNewAttachmentInfo();
                Attachment att = it.next();
                AttachmentInfo attInfo = att.getAttachmentInfo();

                marshalAttachmentInfo(attInfo, tAttInfo);
            }
            return convertXML(retDoc);
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    /**
     * Delete the attachments with the specified name from the task (if multiple
     * attachments with that name exist, all are deleted). Attachments provided
     * by the enclosing context are not affected by this operation.
     */
    public OMElement deleteAttachments(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            DeleteAttachmentsDocument dad = DeleteAttachmentsDocument.Factory.parse(requestElement.getXMLStreamReader());
            DeleteAttachments da = dad.getDeleteAttachments();

            _server.deleteAttachments(participantToken, da.getIdentifier(), da.getAttachmentName());

            DeleteAttachmentsResponseDocument dar = DeleteAttachmentsResponseDocument.Factory.newInstance();
            dar.addNewDeleteAttachmentsResponse();

            return convertXML(dar);
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    /**
     * Add a comment to a task.
     */
    public OMElement addComment(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            AddCommentDocument acd = AddCommentDocument.Factory.parse(requestElement.getXMLStreamReader());
            AddComment ac = acd.getAddComment();

            _server.addComment(participantToken, ac.getIdentifier(), ac.getText());

            AddCommentResponseDocument ard = AddCommentResponseDocument.Factory.newInstance();
            ard.addNewAddCommentResponse();

            return convertXML(ard);
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    /**
     * Get all comments of a task
     */
    public OMElement getComments(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            GetCommentsDocument gcd = GetCommentsDocument.Factory.parse(requestElement.getXMLStreamReader());
            GetComments gc = gcd.getGetComments();

            List<Comment> comments = _server.getComments(participantToken, gc.getIdentifier());
            Iterator<Comment> it = comments.iterator();

            GetCommentsResposneDocument gcrd = GetCommentsResposneDocument.Factory.newInstance();
            GetCommentsResposne gcr = gcrd.addNewGetCommentsResposne();
            while (it.hasNext()) {
                Comment comment = it.next();
                TComment tComment = gcr.addNewComment();
                tComment.setAddedBy(comment.getAddedBy());

                tComment.setAddedAt(convertDateToCalendar(comment.getAddedAt()));

                tComment.setText(comment.getText());
            }

            return convertXML(gcrd);
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    /**
     * Applies to both tasks and notifications. Returns a data object of type
     * tTask
     */
    public OMElement getTaskInfo(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            GetTaskInfoDocument gtid = GetTaskInfoDocument.Factory.parse(requestElement.getXMLStreamReader());
            GetTaskInfo gti = gtid.getGetTaskInfo();

            Task task = _server.getTaskByIdentifier(participantToken, gti.getIdentifier());

            GetTaskInfoResponseDocument gtird = GetTaskInfoResponseDocument.Factory.newInstance();
            GetTaskInfoResponse gtir = gtird.addNewGetTaskInfoResponse();

            TTask tTask = gtir.addNewTask();

            this.marshalTask(task, tTask);

            return convertXML(gtird);
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    /**
     * Applies to both tasks and notifications. Returns the presentation
     * description in the specified mime type.
     */
    public OMElement getTaskDescription(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            GetTaskDescriptionDocument gtdd = GetTaskDescriptionDocument.Factory.parse(requestElement.getXMLStreamReader());
            GetTaskDescription gtd = gtdd.getGetTaskDescription();

            Task task = _server.getTaskByIdentifier(participantToken, gtd.getIdentifier());

            GetTaskDescriptionResponseDocument gtdrd = GetTaskDescriptionResponseDocument.Factory.newInstance();
            GetTaskDescriptionResponse gtdr = gtdrd.addNewGetTaskDescriptionResponse();
            gtdr.setDescription(task.getPresentationName());
            // TODO replace the presentation name with the real description

            return convertXML(gtdrd);
        } catch (Exception e) {
            throw makeFault(e);
        }
    }

    /*****************************************
     * Query operation
     *****************************************/

    /**
     * Query tasks created/assigned/potential assigned to login user
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     * @author Jackie Ju, Michael Zhu
     */
    public OMElement getMyTasks(OMElement requestElement) throws AxisFault {
        try {
            // check participant token
            String participantToken = getParticipantToken();
            if (participantToken == null)
                throw makeFault(new Exception("Cannot get participant toke in soap header"));

            // unmarshal request
            GetMyTasksDocument reqDoc = GetMyTasksDocument.Factory.parse(requestElement.getXMLStreamReader());
            GetMyTasks req = reqDoc.getGetMyTasks();

            // process request
            List<Task> tasks = _server.getMyTasks(participantToken, req.getTaskType(), req.getGenericHumanRole(), req.getWorkQueue(), req.getStatusArray(), req
                            .getWhereClause(), req.getCreatedOnClause(), req.getMaxTasks());
            if (tasks != null) {
                this._logger.info("tasks list size:" + tasks.size());
                System.out.println("tasks list size:" + tasks.size());
            } else {
                System.out.println("tasks list is null");
            }

            for (int i = 0; i < tasks.size(); i++) {
                this._logger.info("task " + i + " created on " + tasks.get(i).getCreatedOn().toString());
            }

            // marshal response
            GetMyTasksResponseDocument retDoc = GetMyTasksResponseDocument.Factory.newInstance();
            GetMyTasksResponse ret = retDoc.addNewGetMyTasksResponse();
            for (int i = 0; i < tasks.size(); i++) {
                // GetMyTasksResponse node = ret.addNewGetMyTasksResponse();
                TTask tt = ret.addNewTaskAbstract();
                Task t = tasks.get(i);
                this.marshalTask(t, tt);
                _logger.info("task " + i + ": " + tt.xmlText());
                System.out.println("task " + i + ": " + tt.xmlText());

            }

            // _logger.info("xmltoolsing convert result:"+
            // convertXML(ret).toString());
            // _logger.info("xmlText:"+ret.xmlText());
            System.out.println("xmltoolsing convert result:" + convertXML(ret).toString());
            System.out.println("xmlText:" + ret.xmlText());

            // convert to OMElment
            return convertXML(retDoc);

        } catch (Exception e) {
            throw makeFault(e);
        }

    }

    /**
     * advanced query
     * 
     * @param requestElement
     * @return
     * @throws AxisFault
     * @author Jackie Ju, Michael Zhu
     */
    public OMElement query(OMElement requestElement) throws AxisFault {
        System.out.print("=======query\n");
        // check participant token
        String participantToken = getParticipantToken();
        if (participantToken == null)
            throw makeFault(new Exception("Cannot get participant toke in soap header"));

        try {
            // unmarshal request
            Query req = QueryDocument.Factory.parse(requestElement.getXMLStreamReader()).getQuery();

            // call server
            List<Task> ret = _server.query(participantToken, req.getSelectClause(), req.getWhereClause(), req.getOrderByClause(), req.getMaxTasks(), req
                            .getTaskIndexOffset());

            // marshal response
            QueryResponseDocument resp = QueryResponseDocument.Factory.newInstance();
            for (int i = 0; i < ret.size(); i++) {
                Task r = ret.get(i);
                QueryResponse qr = resp.addNewQueryResponse();
                TTaskQueryResultSet result = qr.addNewQuery();
                TTaskQueryResultRow row = result.addNewRow();
                row.addActivationTime(convertDateToCalendar(r.getActivationTime()));
                row.addActualOwner(r.getActualOwner());
                // ? row.addCompleteByExists(r.)
                // ...

            }
            return XmlTooling.convertDocument(resp);
        } catch (Exception e) {
            throw makeFault(e);
        }

    }

    /*****************************************
     * Administrative operation
     *****************************************/
    public OMElement activate(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            Activate req =  ActivateDocument.Factory.parse(requestElement.getXMLStreamReader()).getActivate();
            
            // call the server
            _server.activate(participantToken, req.getIdentifier());
            
            ActivateResponseDocument retDoc = ActivateResponseDocument.Factory.newInstance();
            retDoc.addNewActivateResponse();
            
            return this.convertXML(retDoc);
        } catch (Exception e) {
            throw makeFault(e);
        }        
    }
    
    public OMElement nominate(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            Nominate req =  NominateDocument.Factory.parse(requestElement.getXMLStreamReader()).getNominate();
            
            TOrganizationalEntity  orgEntity = req.getOrganizationalEntity();
            
            List<String> principals = null;
            if (orgEntity.isSetUsers()) {
                TUserlist userList = orgEntity.getUsers();
                principals = Arrays.asList(userList.getUserArray());
            } else {
                // should be groups
                TGrouplist groupList = orgEntity.getGroups();
                principals = Arrays.asList(groupList.getGroupArray());
            }
            // call the server
            _server.nominate(participantToken, req.getIdentifier(), principals, orgEntity.isSetUsers());
            
            NominateResponseDocument retDoc = NominateResponseDocument.Factory.newInstance();
            retDoc.addNewNominateResponse();
            
            return this.convertXML(retDoc);
        } catch (Exception e) {
            throw makeFault(e);
        }
    }
    
    public OMElement setGenericHumanRole(OMElement requestElement) throws AxisFault {
        String participantToken = getParticipantToken();
        try {
            SetGenericHumanRole req =  SetGenericHumanRoleDocument.Factory.parse(requestElement.getXMLStreamReader()).getSetGenericHumanRole();
            
            GenericRoleType roleType = GenericRoleType.valueOf(req.getGenericHumanRole());
            
            TOrganizationalEntity  orgEntity = req.getOrganizationalEntity();
            List<String> principals = null;
            
            if (orgEntity.isSetUsers()) {
                TUserlist userList = orgEntity.getUsers();
                principals = Arrays.asList(userList.getUserArray());
            } else {
                // should be groups
                TGrouplist groupList = orgEntity.getGroups();
                principals = Arrays.asList(groupList.getGroupArray());
            }
            
            _server.setGenericHumanRole(participantToken, req.getIdentifier(), roleType, principals, orgEntity.isSetUsers());

            SetGenericHumanRoleResponseDocument retDoc = SetGenericHumanRoleResponseDocument.Factory.newInstance();
            retDoc.addNewSetGenericHumanRoleResponse();
            
            return this.convertXML(retDoc);
        } catch (Exception e) {
            throw makeFault(e);
        }

    }
}
