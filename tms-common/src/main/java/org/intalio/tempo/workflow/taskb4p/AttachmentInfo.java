package org.intalio.tempo.workflow.taskb4p;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tempob4p_attachmentInfo")
public class AttachmentInfo {
	@Basic
	private String name;
	@Basic
	private AttachmentAccessType accessType;
	@Basic
	private String contentType;
	@Basic
	private Date attachedAt;
	@Basic
	private String attachedBy;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AttachmentAccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AttachmentAccessType accessType) {
		this.accessType = accessType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Date getAttachedAt() {
		return attachedAt;
	}

	public void setAttachedAt(Date attachedAt) {
		this.attachedAt = attachedAt;
	}

	public String getAttachedBy() {
		return attachedBy;
	}

	public void setAttachedBy(String attachedBy) {
		this.attachedBy = attachedBy;
	}

}
