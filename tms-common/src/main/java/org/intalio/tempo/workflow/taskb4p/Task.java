package org.intalio.tempo.workflow.taskb4p;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;

@Entity
@DiscriminatorValue("Task")
@NamedQueries( { @NamedQuery(name = Task.FIND_BY_ID, query = "select m from Task m where m.id=?1", hints = { @QueryHint(name = "openjpa.hint.OptimizeResultCount", value = "1") }),
	@NamedQuery(name = Task.FIND_BY_NAME, query = "select m from Task m where m.name=?1")})
public class Task extends TaskAbstract {
    public static final String FIND_BY_ID = "find_by_id"; 
    public static final String FIND_BY_NAME = "fine_by_name";
	
	@Basic
	private String taskInitiator;
	@Persistent(cascade = { CascadeType.ALL })
	private OrganizationalEntity taskStakeholders;
	@Persistent(cascade = { CascadeType.ALL })
	private OrganizationalEntity potentialOwners;
	@Persistent(cascade = { CascadeType.ALL })
	private OrganizationalEntity businessAdministrators;
	@Basic
	private String actualOwner;
	@Persistent(cascade = { CascadeType.ALL })
	private OrganizationalEntity notificationRecipients;
	@Basic
	private String createdBy;
	@Basic
	private String primarySearchBy;
    @Persistent(cascade = { CascadeType.ALL })
    private OrganizationalEntity excludedOwners;
	

	@OneToMany(cascade=CascadeType.REMOVE, mappedBy="task", fetch=FetchType.LAZY)
	private Set<Comment> comments;
	
	@OneToMany(cascade=CascadeType.REMOVE, mappedBy="task", fetch=FetchType.LAZY)
	private Set<Attachment> attachments;
	
	public String getTaskInitiator() {
		return taskInitiator;
	}

	public void setTaskInitiator(String taskInitiator) {
		this.taskInitiator = taskInitiator;
	}

	public OrganizationalEntity getTaskStakeholders() {
		return taskStakeholders;
	}

	public void setTaskStakeholders(OrganizationalEntity taskStakeholders) {
		this.taskStakeholders = taskStakeholders;
	}

	public OrganizationalEntity getPotentialOwners() {
		return potentialOwners;
	}

	public void setPotentialOwners(OrganizationalEntity potentialOwners) {
		this.potentialOwners = potentialOwners;
	}

	public OrganizationalEntity getBusinessAdministrators() {
		return businessAdministrators;
	}

	public void setBusinessAdministrators(
			OrganizationalEntity businessAdministrators) {
		this.businessAdministrators = businessAdministrators;
	}

	public String getActualOwner() {
		return actualOwner;
	}

	public void setActualOwner(String actualOwner) {
		this.actualOwner = actualOwner;
	}

	public OrganizationalEntity getNotificationRecipients() {
		return notificationRecipients;
	}

	public void setNotificationRecipients(
			OrganizationalEntity notificationRecipients) {
		this.notificationRecipients = notificationRecipients;
	}

	public OrganizationalEntity getExcludedOwners() {
        return excludedOwners;
    }

    public void setExcludedOwners(OrganizationalEntity excludedOwners) {
        this.excludedOwners = excludedOwners;
    }

    public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getPrimarySearchBy() {
		return primarySearchBy;
	}

	public void setPrimarySearchBy(String primarySearchBy) {
		this.primarySearchBy = primarySearchBy;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public Set<Attachment> getAttachments() {
		return attachments;
	}
}
