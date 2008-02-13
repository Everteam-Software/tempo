package org.intalio.tempo.workflow.wds.core.tms;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;

public class TMSJPAConnection extends AbstractJPAConnection implements
		TMSConnectionInterface {

	public TMSJPAConnection(EntityManager em) {
		super(em);
	}

	public void deletePipaTask(String formUrl) {
		_logger.info("delete task:" + formUrl);
		
		try {
			Query q = entityManager.createNamedQuery(PIPATask.FIND_BY_URL).setParameter(1, formUrl);
			PIPATask toDelete = (PIPATask) q.getSingleResult();
			checkTransactionIsActive();
			entityManager.remove(toDelete);
		} catch (NoResultException nre) {
			// this is okay, it means we did not find anything to delete
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storePipaTask(PIPATask task) {
		_logger.info("store pipa task:"+task.getFormURL());
		checkTransactionIsActive();
		entityManager.persist(task);
	}
	
	 private String getJPQLQueryFromIds(List<String> ids) {
	        StringBuffer buffer = new StringBuffer(Task.FIND_BY_IDS + "(");
	        for(String id : ids)  buffer.append("'"+id+"',");
	        buffer.deleteCharAt(buffer.length()-1);
	        buffer.append(")");
	        if(_logger.isDebugEnabled()) {
	            _logger.info("Query:"+buffer.toString());
	        }
	        return buffer.toString();
	    }
	    
	    @SuppressWarnings("unchecked")
	    public Task[] fetchAllAvailableTasks(UserRoles user) {
	        AuthIdentifierSet roles = user.getAssignedRoles();
	        String userid = user.getUserID();
	        String s = MessageFormat.format(Task.FIND_BY_USER_AND_ROLES, new Object[] { roles.toString(), "('"+userid+"')" });
	        if(_logger.isDebugEnabled()) _logger.debug("fetchAllAvailableTasks query:"+s);
	        Query q = entityManager.createNativeQuery(s, String.class);
	        List<String> l = q.getResultList();
	        if(l.size()<1) return new Task[0];
	        Query q2 = entityManager.createQuery(getJPQLQueryFromIds(l));
	        List<Task> tasks = (List<Task>) q2.getResultList();
	        if(_logger.isDebugEnabled()) {
	            for(Task t : tasks)
	            _logger.info("Found task:"+t.getID());
	        }
	        return (Task[]) new ArrayList(tasks).toArray(new Task[tasks.size()]);
	    }

}
