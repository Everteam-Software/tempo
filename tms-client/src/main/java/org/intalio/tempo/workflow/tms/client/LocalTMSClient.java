package org.intalio.tempo.workflow.tms.client;

import java.net.URL;
import java.util.HashMap;

import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.InvalidTaskException;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.InvalidTaskStateException;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.TMSServer;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnectionFactory;
import org.intalio.tempo.workflow.util.jpa.TaskFetcher;
import org.w3c.dom.Document;

public class LocalTMSClient implements ITaskManagementService{

	private String participantToken;
	private ITaskDAOConnectionFactory taskDAOFactory;
	private TMSServer server ;
 
	public TMSServer getServer() {
		return server;
	}

	public void setServer(TMSServer server) {
		this.server = server;
	}

	public void setParticipantToken(String participantToken) {
		this.participantToken = participantToken;
	}

	public void setTaskDAOFactory(ITaskDAOConnectionFactory taskDAOFactory) {
		this.taskDAOFactory = taskDAOFactory;
	}

	public void addAttachment(String taskID, Attachment attachment)
			throws AuthException, UnavailableTaskException {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public void complete(String taskID) throws AuthException,
			UnavailableTaskException, InvalidTaskStateException {
		// TODO Auto-generated method stub
		
	}

	public Long countAvailableTasks(String taskType, String subQuery)
			throws AuthException {
		long count = 0;
		 
		HashMap map = new HashMap();
		map.put(TaskFetcher.FETCH_CLASS_NAME, taskType);
		map.put(TaskFetcher.FETCH_SUB_QUERY, subQuery);
		ITaskDAOConnection connection = taskDAOFactory.openConnection();
		try {
			count = server.countAvailableTasks(connection,
					this.participantToken, map);
		} finally {
			connection.close();
		}
		return count;
	}

	public void create(Task task) throws AuthException, TaskIDConflictException {
		// TODO Auto-generated method stub
		
	}

	public void delete(String[] taskIDs) throws AuthException,
			UnavailableTaskException {
		// TODO Auto-generated method stub
		
	}

	public void deleteAll(String fakeDelete, String subQuery, String taskType)
			throws AuthException, UnavailableTaskException {
		// TODO Auto-generated method stub
		
	}

	public void deletePipa(String formUrl) throws AuthException,
			UnavailableTaskException {
		// TODO Auto-generated method stub
		
	}

	public void fail(String taskID, String failureCode, String failureReason)
			throws AuthException, UnavailableTaskException,
			InvalidTaskStateException {
		// TODO Auto-generated method stub
		
	}

	public Attachment[] getAttachments(String taskID) throws AuthException,
			UnavailableTaskException {
		// TODO Auto-generated method stub
		return null;
	}

	public Task[] getAvailableTasks(String taskType, String subQuery)
			throws AuthException {
		// TODO Auto-generated method stub
		return null;
	}

	public Task[] getAvailableTasks(String taskType, String subQuery,
			String first, String max) throws AuthException {
		Task[] tasks;
		HashMap map = new HashMap();
		map.put(TaskFetcher.FETCH_CLASS_NAME, taskType);
		map.put(TaskFetcher.FETCH_SUB_QUERY, subQuery);
		map.put(TaskFetcher.FETCH_FIRST, first);
		map.put(TaskFetcher.FETCH_MAX, max);
		//final UserRoles user = server.getUserRoles(participantToken);
		ITaskDAOConnection connection = taskDAOFactory.openConnection();
		try {
			tasks = server.getAvailableTasks(connection,
					participantToken, map);
		} finally {
			connection.close();
		}
		return tasks;
	}

	public PIPATask getPipa(String formUrl) throws AuthException,
			UnavailableTaskException {
		// TODO Auto-generated method stub
		return null;
	}

	public Task getTask(String taskID) throws AuthException,
			UnavailableTaskException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.intalio.tempo.workflow.tms.ITaskManagementService#getTaskList()
	 */
	public Task[] getTaskList() throws AuthException {
		Task[] tasks;
		ITaskDAOConnection connection = taskDAOFactory.openConnection();
		try {
			tasks = server.getTaskList(connection,
					participantToken);
		} finally {
			connection.close();
		}
		return tasks;
	}

	public Document init(String taskID, Document output) throws AuthException,
			UnavailableTaskException {
		// TODO Auto-generated method stub
		return null;
	}

	public void reassign(String taskID, AuthIdentifierSet users,
			AuthIdentifierSet roles, TaskState state) throws AuthException,
			UnavailableTaskException {
		// TODO Auto-generated method stub
		
	}

	public void removeAttachment(String taskID, URL attachmentURL)
			throws AuthException, UnavailableTaskException,
			UnavailableAttachmentException {
		// TODO Auto-generated method stub
		
	}

	public void setOutput(String taskID, Document output) throws AuthException,
			UnavailableTaskException, InvalidTaskStateException {
		// TODO Auto-generated method stub
		
	}

	public void setOutputAndComplete(String taskID, Document output)
			throws AuthException, UnavailableTaskException,
			InvalidTaskStateException {
		// TODO Auto-generated method stub
		
	}

	public void storePipa(PIPATask task) throws AuthException,
			InvalidTaskException {
		// TODO Auto-generated method stub
		
	}

	public void update(Task task) throws AuthException,
			UnavailableTaskException {
		// TODO Auto-generated method stub
		
	}

}
