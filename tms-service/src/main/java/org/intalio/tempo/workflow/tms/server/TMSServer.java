/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.tms.server;

import java.net.URL;
import java.util.ArrayList;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.InvalidTaskException;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.traits.ITaskWithAttachments;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.tms.InvalidTaskStateException;
import org.intalio.tempo.workflow.tms.TaskIDConflictException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class TMSServer implements ITMSServer {

	private static final Logger _logger = LoggerFactory.getLogger(TMSServer.class);

    private IAuthProvider _authProvider;

	private ITaskDAOConnectionFactory _taskDAOFactory;

    public TMSServer() {
    }

    public TMSServer(IAuthProvider authProvider, ITaskDAOConnectionFactory taskDAOFactory) {
        _logger.info("New TMS Instance");
        assert authProvider != null : "IAuthProvider implementation is absent!";
        assert taskDAOFactory != null : "ITaskDAOConnectionFactory implementation is absent!";

        setAuthProvider(authProvider);
		setTaskDAOFactory(taskDAOFactory);
		_logger.info("Finished loading");
	}


    public void setAuthProvider(IAuthProvider authProvider) {
        this._authProvider = authProvider;
        _logger.info("IAuthProvider implementation : " + _authProvider.getClass());
    }

    public void setTaskDAOFactory(ITaskDAOConnectionFactory taskDAOFactory) {
        this._taskDAOFactory = taskDAOFactory;
        _logger.info("ITaskDAOConnectionFactory implementation : " + _taskDAOFactory.getClass());
    }

    public Task[] getTaskList(String participantToken) throws AuthException {
        UserRoles credentials = _authProvider.authenticate(participantToken);
        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
		    Task[] result = dao.fetchAllAvailableTasks(credentials);
            _logger.info("Number of workflow Tasks for user " + credentials.getUserID() + ":" + result.length);
            return result;
        } catch(Exception e) {
            _logger.error("Error while tasks list retrieval for user " + credentials.getUserID(), e);
            throw new RuntimeException(e);
        } 
    }

    public UserRoles getUserRoles(String participantToken) throws AuthException {
        return _authProvider.authenticate(participantToken);
    }
    
	public Task getTask(String taskID, String participantToken) throws AuthException, UnavailableTaskException {
        Task task = null;
        UserRoles credentials = _authProvider.authenticate(participantToken);
		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
            task = dao.fetchTaskIfExists(taskID);
        } catch (Exception e) {
            _logger.error("Cannot retrieve Workflow Task " + taskID);
        } 
        if ((task != null) && task.isAvailableTo(credentials)) {
            if (_logger.isDebugEnabled())
                _logger.debug("Workflow Task " + task + " for user " + credentials.getUserID());
        } else {
            throw new UnavailableTaskException("Workflow Task " + task + " is not for " + credentials.getUserID());
        }
        return task;
    }

	public void setOutput(String taskID,
                          Document output,
                          String participantToken) throws AuthException, UnavailableTaskException {

        Task task = null;
        boolean available = false;
        UserRoles credentials = _authProvider.authenticate(participantToken);
		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
			task = dao.fetchTaskIfExists(taskID);
            available = task instanceof ITaskWithOutput && task.isAvailableTo(credentials);
            if (available) {
                ITaskWithOutput taskWithOutput = (ITaskWithOutput) task;
                taskWithOutput.setOutput(output);
                dao.updateTask(task);
                dao.commit();
                if (_logger.isDebugEnabled())
                    _logger.debug(credentials.getUserID() + " has set output for Workflow Task " + task);
            }
        } catch (Exception e) {
            _logger.error("Cannot retrieve Workflow Task " + taskID, e);
        }
        if (!available) {
            throw new UnavailableTaskException(credentials.getUserID() + " cannot set output for Workflow Task " + task);
        }
	}

	public void complete(String taskID, String participantToken)
			throws AuthException, UnavailableTaskException, InvalidTaskStateException {

        Task task = null;
        boolean available = false;
		UserRoles credentials = _authProvider.authenticate(participantToken);
		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
			task = dao.fetchTaskIfExists(taskID);
			available = task instanceof ITaskWithState && task.isAvailableTo(credentials);
			if (available) {
				ITaskWithState taskWithState = (ITaskWithState) task;
				taskWithState.setState(TaskState.COMPLETED);
				dao.updateTask(task);
				dao.commit();
				if (_logger.isDebugEnabled())
                    _logger.debug(credentials.getUserID() + " has completed the Workflow Task " + task);
			}
        } catch (Exception e) {
            _logger.error("Error to complete Workflow Task " + taskID);
		} 
        if (!available) {
            throw new UnavailableTaskException(credentials.getUserID() + " cannot complete Workflow Task " + task);
        }
	}

	public void setOutputAndComplete(String taskID, Document output, String participantToken)
            throws AuthException, UnavailableTaskException, InvalidTaskStateException {

		Task task = null;
        boolean available = false;
        UserRoles credentials = _authProvider.authenticate(participantToken);
		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
			task = dao.fetchTaskIfExists(taskID);
			available = task instanceof ITaskWithOutput
					&& task instanceof ITaskWithState
					&& task.isAvailableTo(credentials);
			if (available) {
				((ITaskWithOutput) task).setOutput(output);
				((ITaskWithState) task).setState(TaskState.COMPLETED);
				dao.updateTask(task);
				dao.commit();
				if (_logger.isDebugEnabled())
                    _logger.debug(credentials.getUserID() + " has set output and completed Workflow Task " + task);
			}
        } catch (Exception e) {
            _logger.error("Error to set output abd complete Workflow Task " + taskID, e);
		} 
        if (!available) {
            throw new UnavailableTaskException(credentials.getUserID() +
            		" cannot set output and complete Workflow Task " + task);
        }
	}

	public void fail(String taskID, String failureCode, String failureReason, String participantToken)
			throws AuthException, UnavailableTaskException {

		Task task = null;
        boolean available = false;

		//UserRoles credentials = _authProvider.authenticate(participantToken);
		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
			task = dao.fetchTaskIfExists(taskID);
			available = task instanceof ITaskWithState 
			//&& task.isAvailableTo(credentials)
			;
			if (available) {
				ITaskWithState taskWithState = (ITaskWithState) task;
				taskWithState.setState(TaskState.FAILED);
				taskWithState.setFailureCode(failureCode);
				taskWithState.setFailureReason(failureReason);
				dao.updateTask(task);
				dao.commit();
                _logger.debug(
                		//credentials.getUserID() + 
                		" fails Workflow Task " + task +
                		" with code: " + failureCode + " and reason: " + failureReason);
            }
        } catch (Exception e) {
            _logger.error("Error to set as failed the Workflow Task " + taskID, e);
		}
        if (!available) {
            throw new UnavailableTaskException(
            		//credentials.getUserID() +
            		" cannot set state as FAILED for Workflow Task " + task);
        }
	}

	public void delete(String[] taskIDs, String participantToken)
			throws AuthException, UnavailableTaskException {

		Task task;
        boolean available;
        ArrayList<Task> problemTasks = new ArrayList<Task>();

        UserRoles credentials = _authProvider.authenticate(participantToken);
		if (!credentials.hasSystemRole()) {
			throw new AuthException("Only User with System Role may delete tasks!");
		}

		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {
    		for (String taskID : taskIDs) {
    			try {
    				task = dao.fetchTaskIfExists(taskID);
    				available = (task != null);
    				if (available) {
    					dao.deleteTask(task.getInternalId(), taskID);
    					dao.commit();
    					if (_logger.isDebugEnabled())
                            _logger.debug(credentials.getUserID() + " has deleted Workflow Task " + task);
    				} else {
    					problemTasks.add(task);
    				}
    			} catch (Exception e) {
    				_logger.error("Cannot retrieve Workflow Tasks", e);
    			}
    		}
        } finally {
            //dao.close();
        }
        if (problemTasks.size() > 0) {
            throw new UnavailableTaskException(credentials.getUserID() +
            		" cannot delete Workflow Tasks: " + problemTasks);
        }
	}

	public void create(Task task, String participantToken)
			throws AuthException, TaskIDConflictException {
		// UserRoles credentials =
		// _authProvider.authenticate(participantToken);// FIXME: decide on this
		// issue

		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
			dao.createTask(task);
			dao.commit();
			if (_logger.isDebugEnabled())
                _logger.debug("Workflow Task " + task + " was created");	
			// TODO : Use credentials.getUserID() 	:vb
        } catch (Exception e) {
            _logger.error("Cannot create Workflow Tasks", e);		// TODO : TaskIDConflictException must be rethrowed	:vb
		} finally {
			dao.close();
		}
	}

	private Document sendInitMessage(PIPATask task,
                                     String participantToken,
                                     Document input) {

        OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNamespace = omFactory.createOMNamespace(task
				.getInitMessageNamespaceURI().toString(), "user");

		OMElement omInitProcessRequest = omFactory.createOMElement(
				"initProcessRequest", omNamespace);
		OMElement omTaskId = omFactory.createOMElement("taskId", omNamespace,
				omInitProcessRequest);
		omTaskId.setText(task.getID());
		OMElement omParticipantToken = omFactory.createOMElement(
				"participantToken", omNamespace, omInitProcessRequest);
		omParticipantToken.setText(participantToken);
		
		OMElement omTaskOutput = omFactory.createOMElement("taskOutput",
				omNamespace, omInitProcessRequest);
		// OMElement omOutput = omFactory.createOMElement("output", omNamespace, omTaskOutput);
		 
		// omOutput.addChild(OMDOMConvertor.convertDOMToOM(input, omFactory));
		 XmlTooling xmlTooling = new XmlTooling();
		 omTaskOutput.addChild(xmlTooling.convertDOMToOM(input, omFactory));

		Options options = new Options();
		options.setTo(new EndpointReference(task.getProcessEndpoint().toString()));
		options.setAction(task.getInitOperationSOAPAction());

		try {
	          if (_logger.isDebugEnabled()) {
	              _logger.debug(task + " was used to start the process " + task.getProcessEndpoint());
	              _logger.debug("Request to Ode:\n"+omInitProcessRequest.toString());
	          }

			ServiceClient client = new ServiceClient();
			client.setOptions(options);

			OMElement response = client.sendReceive(omInitProcessRequest);

			return xmlTooling.convertOMToDOM(response);
		} catch (AxisFault f) {
            throw new RuntimeException(f);
		}
	}

	public Document initProcess(String taskID, Document input,
			String participantToken) throws AuthException,
			UnavailableTaskException {
		Task task = null;
        boolean available = false;
        Document document = null;

		UserRoles credentials = _authProvider.authenticate(participantToken);
		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
			task = dao.fetchTaskIfExists(taskID);
			available = (task instanceof PIPATask) && (task.isAvailableTo(credentials));
			if (available) {
				PIPATask pipaTask = (PIPATask) task;
				document = sendInitMessage(pipaTask, participantToken, input);
				if (_logger.isDebugEnabled())
                    _logger.debug(credentials.getUserID() + " has initialized process " + pipaTask.getProcessEndpoint()
                            + " with Workflow PIPA Task " + task);
			} 
        } catch (Exception e) {
            _logger.error("Error to start the process with PIPA task " + taskID, e);
		} finally {
			dao.close();
		}
        if (!available) {
            String msg = credentials.getUserID() + " cannot start process with Workflow PIPA Task " + task;
            _logger.error(msg);
            throw new UnavailableTaskException(msg);
        }
        return document;
	}

	public Attachment[] getAttachments(String taskID, String participantToken)
			throws AuthException, UnavailableTaskException {
		Task task = null;
        boolean available = false;

        UserRoles credentials = _authProvider.authenticate(participantToken);
		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
			task = dao.fetchTaskIfExists(taskID);
			available = task instanceof ITaskWithAttachments && task.isAvailableTo(credentials);
			if (available) {
				ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
				return taskWithAttachments.getAttachments().toArray(new Attachment[] {});
			}
        } catch (Exception e) {
            _logger.error("Error to get attachments for Workflow Task " + taskID, e);
		} finally {
			dao.close();
		}
        if (!available) {
            String msg = credentials.getUserID() + " cannot get attachments for Workflow Task " + task;
            _logger.error(msg);
            throw new UnavailableTaskException(msg);
        }
        return null;
	}

	public void addAttachment(String taskID, Attachment attachment,
			String participantToken) throws AuthException,
			UnavailableTaskException {
		Task task = null;
        UserRoles credentials = _authProvider.authenticate(participantToken);
		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
        try {
			task = dao.fetchTaskIfExists(taskID);
            if (task instanceof ITaskWithAttachments == false) {
                throw new UnavailableTaskException("Task does not support attachments");
            }
			if (!task.isAvailableTo(credentials)) {
                throw new UnavailableTaskException(credentials.getUserID() +
                        " cannot add attachment " + attachment + " for Workflow Task " + task);
            }
			ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
			taskWithAttachments.addAttachment(attachment);
			dao.updateTask(task);
			dao.commit();
			if (_logger.isDebugEnabled())
                _logger.debug(credentials.getUserID() + " has added attachment " + attachment + "to Workflow Task "
                        + task);
        } catch (RuntimeException e) {
            _logger.error("Cannot retrieve workflow tasks", e);
            throw e;
		} finally {
			dao.close();
		}
	}

	public void removeAttachment(String taskID, URL attachmentURL,
			String participantToken) throws AuthException,
			UnavailableAttachmentException, UnavailableTaskException {

		Task task = null;
        boolean availableTask = false;
        boolean availableAttachment = false;

		UserRoles credentials = _authProvider.authenticate(participantToken);
		ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
			task = dao.fetchTaskIfExists(taskID);
			availableTask = task instanceof ITaskWithAttachments && task.isAvailableTo(credentials);
			if (availableTask) {
				ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) task;
				Attachment removedAttachment = taskWithAttachments.removeAttachment(attachmentURL);
				availableAttachment = (removedAttachment != null);
				if (availableAttachment) {
					dao.updateTask(task);
					dao.commit();
					if (_logger.isDebugEnabled())
                        _logger.debug(credentials.getUserID() + " has removed attachment " + attachmentURL
                                + " for Workflow Task " + task);
				}
			}
        } catch (Exception e) {
            _logger.error("Error while delete attachment " + attachmentURL +
                    " for Workflow Task " + taskID, e);
		} finally {
			dao.close();
		}
        if (!availableTask || !availableAttachment) {
            throw new UnavailableTaskException(credentials.getUserID() +
            		" cannot remove attachment for Workflow Task " + task +
                    ", is problem with task? - " + availableTask +
                    ", is problem with attachment? - " + availableAttachment);
        }
	}

	public void reassign(String taskID, AuthIdentifierSet users,
			AuthIdentifierSet roles, TaskState state, String participantToken)
			throws AuthException, UnavailableTaskException {
		//UserRoles credentials = _authProvider  // TODO: this requires SYSTEM role to be present
		// .authenticate(participantToken);            // for the Escalations to work. This is a security hole for now!

		Task task = null;
        boolean available = false;

        ITaskDAOConnection dao = _taskDAOFactory.openConnection();
		try {
			task = dao.fetchTaskIfExists(taskID);
			//if (task.isAvailableTo(credentials) && (task instanceof ITaskWithState)) { // TODO: see above
			available = task instanceof ITaskWithState;
			if (available) {
				((ITaskWithState) task).setState(state);
                AuthIdentifierSet uOwners = task.getUserOwners();
                AuthIdentifierSet rOwners = task.getRoleOwners();
                if(_logger.isDebugEnabled())
                _logger.debug("For Workflow Task " + taskID + " user owners " + uOwners + " and role owners " + rOwners);
                uOwners.clear();
				task.getUserOwners().addAll(users);
				rOwners.clear();
				task.getRoleOwners().addAll(roles);

				dao.updateTask(task);
				dao.commit();
				if(_logger.isDebugEnabled())
				_logger.debug(" changed to user owners " + users + " and role owners " + roles);
			}
        } catch (Exception e) {
            _logger.error("Cannot retrieve Workflow Tasks", e);
		} finally {
			dao.close();
		}
        if (!available) {
            throw new UnavailableTaskException("Error to ressign Workflow Task " + task);
        }
	}

    public void deletePipa(String formUrl, String participantToken) throws AuthException, UnavailableTaskException {
        try {
            // UserRoles credentials = _authProvider.authenticate(participantToken);
            ITaskDAOConnection dao = _taskDAOFactory.openConnection();
            dao.deletePipaTask(formUrl);
            dao.commit();    
        } catch (Exception e) {
            //throw new UnavailableTaskException(e);
        }
    }

    public PIPATask getPipa(String formUrl, String participantToken) throws AuthException, UnavailableTaskException {
        try {
            ITaskDAOConnection dao = _taskDAOFactory.openConnection();
            return dao.fetchPipa(formUrl);
        } catch (Exception e) {
            throw new UnavailableTaskException(e);
        }
    }

    public void storePipa(PIPATask task, String participantToken) throws AuthException, InvalidTaskException {
        try {
            ITaskDAOConnection dao = _taskDAOFactory.openConnection();
            dao.storePipaTask(task);
            dao.commit();
        } catch (Exception e) {
            throw new InvalidTaskException(e);
        }
    }
}
