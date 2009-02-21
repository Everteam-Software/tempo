package org.intalio.tempo.workflow.taskb4p;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
	private boolean hasPotentialOwners;
	@Basic
	private boolean startByExists;
	@Basic
	private boolean completeByExists;
	@Column(length = 64)
	private String presentationName;
	@Column(length = 254)
	private String presentationSubject;
	@Basic
	private boolean renderingMethodExists;
	@Basic
	private boolean hasOutput;
	@Basic
	private boolean hasFault;
	@Basic
	private boolean escalated;

	// below two attributes should be calculated dynamically
	private boolean hasAttachments;
	private boolean hasComments;

	
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

	public boolean isHasPotentialOwners() {
		return hasPotentialOwners;
	}

	public void setHasPotentialOwners(boolean hasPotentialOwners) {
		this.hasPotentialOwners = hasPotentialOwners;
	}

	public boolean isStartByExists() {
		return startByExists;
	}

	public void setStartByExists(boolean startByExists) {
		this.startByExists = startByExists;
	}

	public boolean isCompleteByExists() {
		return completeByExists;
	}

	public void setCompleteByExists(boolean completeByExists) {
		this.completeByExists = completeByExists;
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

	public boolean isRenderingMethodExists() {
		return renderingMethodExists;
	}

	public void setRenderingMethodExists(boolean renderingMethodExists) {
		this.renderingMethodExists = renderingMethodExists;
	}

	public boolean isHasOutput() {
		return hasOutput;
	}

	public void setHasOutput(boolean hasOutput) {
		this.hasOutput = hasOutput;
	}

	public boolean isHasFault() {
		return hasFault;
	}

	public void setHasFault(boolean hasFault) {
		this.hasFault = hasFault;
	}

	public boolean isEscalated() {
		return escalated;
	}

	public void setEscalated(boolean escalated) {
		this.escalated = escalated;
	}

	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}

	public void setHasComments(boolean hasComments) {
		this.hasComments = hasComments;
	}

	public boolean isHasAttachments() {
		return hasAttachments;
	}

	public boolean isHasComments() {
		return hasComments;
	}
}
