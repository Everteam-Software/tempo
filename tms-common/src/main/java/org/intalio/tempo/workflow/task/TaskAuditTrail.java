package org.intalio.tempo.workflow.task;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;

@Entity
@Table(name="taskaudittrail")
public class TaskAuditTrail{
    
	
	@Persistent
	@Column(name = "task_description")   
	private String taskDescription;

	@Persistent
	@Column(name = "current_owner")
	private String currentOwner;
	
	@Persistent
	@Column(name="previous_owner")
	private String previousOwner;
	
	@Persistent
	@Column(name = "user_id")
    private String userid;
	
	@Persistent
	@Column(name = "action_performed")
    private String actionPerformed;
    
    
	@Persistent
    @Column(name = "audit_date")
    private Date auditDate;


	/**
	 * @return the taskDescription
	 */
	public String getTaskDescription() {
		return taskDescription;
	}


	/**
	 * @param taskDescription the taskDescription to set
	 */
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}


	/**
	 * @return the currentOwner
	 */
	public String getCurrentOwner() {
		return currentOwner;
	}


	/**
	 * @param currentOwner the currentOwner to set
	 */
	public void setCurrentOwner(String currentOwner) {
		this.currentOwner = currentOwner;
	}


	/**
	 * @return the previousOwner
	 */
	public String getPreviousOwner() {
		return previousOwner;
	}


	/**
	 * @param previousOwner the previousOwner to set
	 */
	public void setPreviousOwner(String previousOwner) {
		this.previousOwner = previousOwner;
	}


	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}


	/**
	 * @param userid the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}


	/**
	 * @return the actionPerformed
	 */
	public String getActionPerformed() {
		return actionPerformed;
	}


	/**
	 * @param actionPerformed the actionPerformed to set
	 */
	public void setActionPerformed(String actionPerformed) {
		this.actionPerformed = actionPerformed;
	}


	/**
	 * @return the auditDate
	 */
	public Date getAuditDate() {
		return auditDate;
	}


	/**
	 * @param auditDate the auditDate to set
	 */
	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}    
    
	

}
