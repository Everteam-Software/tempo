package org.intalio.tempo.workflow.tmsb4p.server;

import java.net.URL;

import org.apache.axis2.AxisFault;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.tms.TMSException;

import org.w3c.dom.Document;

public class TMSServer implements ITMSServer{
 
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public void addAttachment(String taskID, Attachment attachment, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public void complete(String taskID, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public void create(Task task, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public void delete(String[] taskIDs, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public void deleteAll(boolean fakeDelete, String subquery, String subqueryClass, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public void deletePipa(String formUrl, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public void fail(String taskID, String failureCode, String failureReason, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public Attachment[] getAttachments(String taskID, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        return null;
    }

    public Task[] getAvailableTasks(String participantToken, String taskType, String subQuery) throws TMSException {
        // TODO Auto-generated method stub
        return null;
    }

    public PIPATask getPipa(String formUrl, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        return null;
    }

    public Task getTask(String taskID, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        return null;
    }

    public Task[] getTaskList(String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        return null;
    }

    public UserRoles getUserRoles(String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        return null;
    }

    public Document initProcess(String taskID, Document input, String participantToken) throws TMSException, AxisFault {
        // TODO Auto-generated method stub
        return null;
    }

    public void reassign(String taskID, AuthIdentifierSet users, AuthIdentifierSet roles, TaskState state, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public void removeAttachment(String taskID, URL attachmentURL, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public void setOutput(String taskID, Document output, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public void setOutputAndComplete(String taskID, Document output, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

    public void storePipa(PIPATask task, String participantToken) throws TMSException {
        // TODO Auto-generated method stub
        
    }

}
