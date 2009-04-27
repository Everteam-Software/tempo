package org.intalio.tempo.workflow.tmsb4p.server;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.taskb4p.Attachment;
import org.intalio.tempo.workflow.taskb4p.AttachmentInfo;
import org.intalio.tempo.workflow.taskb4p.Comment;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tmsb4p.server.dao.GenericRoleType;

import com.intalio.wsHT.TOrganizationalEntity;
import com.intalio.wsHT.api.TStatus;
import com.intalio.wsHT.api.xsd.TTime;

public interface ITMSServer {
//  public static final int TASK_INITIATOR = "TASK_INITIATOR";
//  public static final int TASK_STAKEHOLDERS = "TASK_STAKEHOLDERS";
//  public static final int POTENTIAL_OWNERS = "POTENTIAL_OWNERS";
//  public static final int ACTUAL_OWNER = "ACTUAL_OWNER";
//  public static final int EXCLUDED_OWNERS = "EXCLUDED_OWNERS";
//  public static final int BUSINESSADMINISTRATORS = "BUSINESSADMINISTRATORS";
//  public static final int RECIPIENTS = "RECIPIENTS";
    
    public static final int TASK_INITIATOR = 0;
    public static final int TASK_STAKEHOLDERS = 1;
    public static final int POTENTIAL_OWNERS = 2;
    public static final int ACTUAL_OWNER = 3;
    public static final int EXCLUDED_OWNERS = 4;
    public static final int BUSINESSADMINISTRATORS = 5;
    public static final int RECIPIENTS = 6;
    
    public void create(Task task, String participantToken) throws TMSException;
    public void remove(String participantToken, String taskId) throws TMSException;
    public List<Task> getMyTasks(String participantToken, String taskType, String genericHumanRole, String workQueue, TStatus.Enum[] statusList, String whereClause, String createdOnClause, int maxTasks) throws TMSException;
    public Collection<Map<String, Object>> query(String participantToken, String selectClause, String whereClause, String orderByClause, int maxTasks, int taskIndexOffset) throws TMSException;
    public void stop(String participantToken, String identifier) throws TMSException;
    public void start(String participantToken, String identifier) throws TMSException;
    public void claim(String participantToken, String identifier) throws TMSException;
    public void release(String participantToken, String identifier) throws TMSException;
    public void complete(String participantToken, String identifier, XmlObject xmlObject) throws TMSException;
    public void fail(String participantToken, String identifier,
            String faultName, XmlObject faultData) throws TMSException;
    public void resume(String participantToken, String identifier) throws TMSException;
    public void skip(String participantToken, String identifier) throws TMSException;
    public void forward(String participantToken, String identifier, TOrganizationalEntity oe ) throws TMSException;
    public void delegate(String participantToken, String identifier, TOrganizationalEntity oe) throws TMSException;
    
    public void setPriority(String participantToken, String identifier, int priority) throws TMSException;
    public void addAttachment(String participantToken, String identifier, String attachmentName, String accessType, String value)
    throws TMSException;
    public List<AttachmentInfo> getAttachmentInfos(String participantToken, String identifier) throws TMSException;
    public List<Attachment> getAttachments(String participantToken, String identifier, String attachmentName) throws TMSException;
    public void deleteAttachments(String participantToken, String identifier, String attachmentName) throws TMSException;
    public void addComment(String participantToken, String identifier, String text) throws TMSException;
    public List<Comment> getComments(String participantToken, String identifier) throws TMSException;
    public Task getTaskByIdentifier(String participantToken, String identifier) throws TMSException;
    public void setOutput(String participantToken, String identifier, String partName, XmlObject data) throws TMSException;
    public void deleteOutput(String participantToken, String identifier) throws TMSException;
    public void setFault(String participantToken, String identifier, String faultName, XmlObject data) throws TMSException;
    public void deleteFault(String participantToken, String identifier) throws TMSException;
    public String getInput(String participantToken, String identifier, String partName) throws TMSException;
    public String getOutput(String participantToken, String identifier, String partName) throws TMSException;
    public String getFault(String participantToken, String identifier) throws TMSException;
    
    public void suspendUntil(String participantToken, String identifier,
            TTime time) throws TMSException;
    public void suspend(String participantToken, String identifier) throws TMSException;

    public void activate(String participantToken, String identifier) throws TMSException;
    public void nominate(String participantToken, String identifier, List<String> principals, boolean isUser) throws TMSException;
    public void setGenericHumanRole(String participantToken, String identifier, GenericRoleType roleType, List<String> principals, boolean isUser) throws TMSException;

}