package org.intalio.tempo.workflow.taskb4p;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class TaskAbstract {
	@Id
	private String id;
	@Basic
	private TaskType taskType;
	@Basic
	private String name;
	@Basic
	private TaskStatus status;
	@Basic
	private int priority;
	@Basic
	private Date createdOn;
	@Basic
	private Date activationTime;
	@Basic
	private Date expirationTime;
	@Basic
	private boolean isSkipable;
	@Basic
	private Date startBy;
	@Basic
	private Date complteBy;
	@Column(length = 64)
	private String presentationName;
	@Column(length = 254)
	private String presentationSubject;
	@Basic
	private boolean escalated;
	@Column(length = 2048)
	@Lob
	private String faultMessage;
	@Column(length = 2048)
	@Lob
	private String inputMessage;
	@Column(length = 2048)
	@Lob
	private String outputMessage;
	@Basic
	private String renderingMethName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getActivationTime() {
		return activationTime;
	}

	public void setActivationTime(Date activationTime) {
		this.activationTime = activationTime;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public boolean isSkipable() {
		return isSkipable;
	}

	public void setSkipable(boolean isSkipable) {
		this.isSkipable = isSkipable;
	}

	public String getPresentationName() {
		return presentationName;
	}

	public void setPresentationName(String presentationName) {
		this.presentationName = presentationName;
	}

	public String getPresentationSubject() {
		return presentationSubject;
	}

	public void setPresentationSubject(String presentationSubject) {
		this.presentationSubject = presentationSubject;
	}

	public boolean isEscalated() {
		return escalated;
	}

	public void setEscalated(boolean escalated) {
		this.escalated = escalated;
	}

	public Date getStartBy() {
		return startBy;
	}

	public void setStartBy(Date startBy) {
		this.startBy = startBy;
	}

	public Date getComplteBy() {
		return complteBy;
	}

	public void setComplteBy(Date complteBy) {
		this.complteBy = complteBy;
	}

	public String getFaultMessage() {
		return faultMessage;
	}

	public void setFaultMessage(String faultMessage) {
		this.faultMessage = faultMessage;
	}

	public String getInputMessage() {
		return inputMessage;
	}

	public void setInputMessage(String inputMessage) {
		this.inputMessage = inputMessage;
	}

	public String getOutputMessage() {
		return outputMessage;
	}

	public void setOutputMessage(String outputMessage) {
		this.outputMessage = outputMessage;
	}

	public String getRenderingMethName() {
		return renderingMethName;
	}

	public void setRenderingMethName(String renderingMethName) {
		this.renderingMethName = renderingMethName;
	}
}
