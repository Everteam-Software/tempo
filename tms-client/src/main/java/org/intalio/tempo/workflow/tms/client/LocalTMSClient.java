package org.intalio.tempo.workflow.tms.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.InvalidTaskException;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.traits.ITaskWithAttachments;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.InvalidTaskStateException;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.TMSServer;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnectionFactory;
import org.intalio.tempo.workflow.util.jpa.TaskFetcher;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.XsdDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class LocalTMSClient implements ITaskManagementService {
	final static Logger logger = LoggerFactory.getLogger(LocalTMSClient.class);
	private String participantToken;
	private ITaskDAOConnectionFactory taskDAOFactory;
	private TMSServer server;

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
		ITaskDAOConnection dao = null;
		try {
			dao = taskDAOFactory.openConnection();
			server.addAttachment(dao, taskID, attachment, participantToken);
		} catch (AccessDeniedException e) {
			logger.error("Error while adding attachment ", e);
		} finally {
			if (dao != null)
				dao.close();
		}

	}

	public void close() {

		logger.error("Calling LocalTMSClient :: close ");

	}

	public void complete(String taskID) throws AuthException,
			UnavailableTaskException, InvalidTaskStateException {

		logger.error("Calling LocalTMSClient :: complete ");

	}

	public Long countAvailableTasks(String taskType, String subQuery)
			throws AuthException {
		long count = 0;

		HashMap map = new HashMap();
		map.put(TaskFetcher.FETCH_CLASS_NAME, taskType);
		map.put(TaskFetcher.FETCH_SUB_QUERY, subQuery);
		ITaskDAOConnection dao = taskDAOFactory.openConnection();
		try {
			count = server.countAvailableTasks(dao, this.participantToken, map);
		} finally {
			if (dao != null) 
				dao.close();
		}
		return count;
	}

	public void create(Task task) throws AuthException, TaskIDConflictException {

		logger.error("Calling LocalTMSClient :: fail ");

	}

	public void delete(String[] taskIDs) throws AuthException,
			UnavailableTaskException {
		logger.error("Calling LocalTMSClient :: fail ");

	}

	public void deleteAll(String fakeDelete, String subQuery, String taskType)
			throws AuthException, UnavailableTaskException {
		logger.error("Calling LocalTMSClient :: fail ");

	}

	public void deletePipa(String formUrl) throws AuthException,
			UnavailableTaskException {
		logger.error("Calling LocalTMSClient :: fail ");

	}

	public void fail(String taskID, String failureCode, String failureReason)
			throws AuthException, UnavailableTaskException,
			InvalidTaskStateException {

		logger.error("Calling LocalTMSClient :: fail ");

	}

	public Attachment[] getAttachments(String taskID) throws AuthException,
			UnavailableTaskException {
		logger.debug("Working on getting attachments .....");
		ITaskDAOConnection dao = null;
		Attachment[] attachments = null;
		try {
			dao = this.taskDAOFactory.openConnection();
			attachments = this.server.getAttachments(dao, taskID,
					participantToken);
		} catch (AccessDeniedException e) {
			logger.error("Cannot get Attachment for taskID" + taskID);
		} finally {
			if (dao != null)
				dao.close();
		}
		return attachments;
	}

	public Task[] getAvailableTasks(String taskType, String subQuery)
			throws AuthException {
		
		ITaskDAOConnection dao = taskDAOFactory.openConnection();
		Task[] tasks = null;
		try {
			final UserRoles user = server.getUserRoles(participantToken);
			tasks = server.getAvailableTasks(dao, participantToken, taskType,
					subQuery);
			for(int k = 0 ; k < tasks.length ; ++k ) {
				Task _task = tasks[k];
		        if (_task instanceof ITaskWithAttachments) {
		        	ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) _task;
		        	try {
						Attachment[] attachments = this.getAttachments(_task.getID());
						for(int j = 0 ; j < tasks.length ; ++j ) {
							taskWithAttachments.addAttachment(attachments[j]);
						}
					} catch (UnavailableTaskException e) {
						logger.error("Error while adding attachement to the task");
					}
		        }
			}
		} catch (Exception e) {
			logger.error("Cannot get getAvailableTasks" + taskType + " , "
					+ subQuery);
		} finally {
			if (dao != null)
				dao.close();
		}
		return tasks;
	}

	public Task[] getAvailableTasks(String taskType, String subQuery,
			String first, String max) throws AuthException {
		Task[] tasks;
		HashMap map = new HashMap();
		map.put(TaskFetcher.FETCH_CLASS_NAME, taskType);
		map.put(TaskFetcher.FETCH_SUB_QUERY, subQuery);
		map.put(TaskFetcher.FETCH_FIRST, first);
		map.put(TaskFetcher.FETCH_MAX, max);
		// final UserRoles user = server.getUserRoles(participantToken);
		ITaskDAOConnection dao = taskDAOFactory.openConnection();
		try {
			tasks = server.getAvailableTasks(dao, participantToken, map);
			for(int k = 0 ; k < tasks.length ; ++k ) {
				Task _task = tasks[k];
		        if (_task instanceof ITaskWithAttachments) {
		        	ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) _task;
		        	try {
						Attachment[] attachments = this.getAttachments(_task.getID());
						logger.debug("Found attachments ... " + attachments);
						for(int j = 0 ; j < attachments.length ; ++j ) {
							AttachmentMetadata attachmentMetadata = attachments[j].getMetadata();
							 AttachmentMetadata metadata = new AttachmentMetadata();
                             String mimeType = attachmentMetadata.getMimeType();
                             if (mimeType != null) {
                                 metadata.setMimeType(mimeType);
                             }
                             String fileName = attachmentMetadata.getFileName();
                             if (fileName != null)
                                 metadata.setFileName(fileName);
                             String title = attachmentMetadata.getTitle();
                             if (title != null)
                                 metadata.setTitle(title);
                             String description2 = attachmentMetadata.getDescription();
                             if (description2 != null)
                                 metadata.setDescription(description2);

                             try {
                                 Date date = attachmentMetadata.getCreationDate();
                                 if ((date != null)) {
                                     metadata.setCreationDate(date);
                                 }
                             } catch (Exception e) {
                                 logger.warn("Error in unmarshalling creation date in attachment from metadata");
                                 metadata.setCreationDate(new Date());
                             }

                             Attachment attachment = new Attachment(metadata, attachments[j].getPayloadURL());
                             taskWithAttachments.addAttachment(attachment);
						}
 
					} catch (UnavailableTaskException e) {
						logger.error("Error while adding attachement to the task");
					}
		        }
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		return tasks;
	}

	public PIPATask getPipa(String formUrl) throws AuthException,
			UnavailableTaskException {
		// TODO Auto-generated method stub
		PIPATask task = null;
		ITaskDAOConnection dao = taskDAOFactory.openConnection();
		try {
			task = server.getPipa(dao, formUrl, participantToken);
		} catch (Exception e) {
			logger.error("Cannot get pipaTask for formUrl" + formUrl);
		} finally {
			if (dao != null)
				dao.close();
		}
		return task;
	}

	public Task getTask(String taskID) throws AuthException,
			UnavailableTaskException {
		Task task = null;
		ITaskDAOConnection dao = taskDAOFactory.openConnection();
		try {
			task = server.getTask(dao, taskID, participantToken);
		} catch (AccessDeniedException e) {
			logger.error("Cannot get Attachment for taskID" + taskID);
		} finally {
			if (dao != null)
				dao.close();
		}
		return task;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.intalio.tempo.workflow.tms.ITaskManagementService#getTaskList()
	 */
	public Task[] getTaskList() throws AuthException {
		Task[] tasks;
		ITaskDAOConnection dao = taskDAOFactory.openConnection();
		try {
			tasks = server.getTaskList(dao, participantToken);
		} finally {
			if (dao != null)
				dao.close();
		}
		return tasks;
	}

	public Document init(String taskID, Document output) throws AuthException,
			UnavailableTaskException {
		logger.error("Calling LocalTMSClient :: init");
		return null;
	}

	public void reassign(String taskID, AuthIdentifierSet users,
			AuthIdentifierSet roles, TaskState state) throws AuthException,
			UnavailableTaskException {
		logger.error("Calling LocalTMSClient :: reassign");
		// TODO Auto-generated method stub

	}

	public void removeAttachment(String taskID, URL attachmentURL)
			throws AuthException, UnavailableTaskException,
			UnavailableAttachmentException {
		ITaskDAOConnection dao = null;
		try {
			dao = taskDAOFactory.openConnection();
			server.removeAttachment(dao, taskID, attachmentURL,
					participantToken);
		}  finally {
			if (dao != null)
				dao.close();
		}

	}

	public void setOutput(String taskID, Document output) throws AuthException,
			UnavailableTaskException, InvalidTaskStateException {
		 logger.error("Calling LocalTMSClient :: setOutput");

	}

	public void setOutputAndComplete(String taskID, Document output)
			throws AuthException, UnavailableTaskException,
			InvalidTaskStateException {
		 logger.error("Calling LocalTMSClient :: setOutputAndComplete");

	}

	public void storePipa(PIPATask task) throws AuthException,
			InvalidTaskException {
		logger.error("Calling LocalTMSClient :: storePipa");

	}

	public void update(Task task) throws AuthException,
			UnavailableTaskException {
		logger.error("Calling LocalTMSClient :: update");

	}

    @Override
    public List<String> getCustomColumns() throws AuthException {
        ITaskDAOConnection dao = taskDAOFactory.openConnection();
        return server.getCustomColumns(dao, participantToken);
                
    }

    @Override
    public Task[] getAvailableTasks(String taskType, String subQuery,
            String first, String max, String fetchMetaData)
            throws AuthException {
        // TODO Auto-generated method stub
        return null;
    }

}
