package org.intalio.tempo.workflow.tmsb4p.server;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.tms.TMSException;

import com.intalio.wsHT.api.TStatus;

public interface ITMSServer {
    public void create(Task task, String participantToken) throws TMSException;
    public void remove(String participantToken, String taskId) throws TMSException;
    public List<Task> getMyTasks(String participantToken, String taskType, String genericHumanRole, String workQueue, TStatus.Enum[] statusList, String whereClause, String createdOnClause, int maxTasks) throws TMSException;
    public List<Task> query(String participantToken, String selectClause, String whereClause, String orderByClause, int maxTasks, int taskIndexOffset) throws TMSException;
	public void Stop(String participantToken, String identifier);
	public void Start(String participantToken, String identifier);
	public void claim(String participantToken, String identifier);
	public void Release(String participantToken, String identifier);
	public void Complete(String participantToken, String identifier, XmlObject xmlObject);
	public void Fail(String participantToken, String identifier,
			String faultName, XmlObject faultData);
	public void Resume(String participantToken, String identifier);
}
