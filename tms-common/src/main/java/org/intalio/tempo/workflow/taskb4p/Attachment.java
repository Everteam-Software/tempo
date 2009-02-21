package org.intalio.tempo.workflow.taskb4p;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;

@Entity
@Table(name="tempob4p_attachment")
@NamedQueries( { 
	@NamedQuery(name = Attachment.QUERY_ALL_INFOS, query = "select m.attachmentInfo from Attachment m where m.task.id=?1 order by m.attachmentInfo.attachedAt"),
	@NamedQuery(name = Attachment.QUERY_ALL_ATTACHMENTS, query = "select m from Attachment m where m.task.id=?1 order by m.attachmentInfo.attachedAt"),
	@NamedQuery(name = Attachment.DELETE_WITH_NAME, query = "delete from Attachment m where m.task.id=?1 and m.attachmentInfo.name=?2")})

public class Attachment {
	
	public static final String QUERY_ALL_INFOS = "query_all_infos";
	public static final String QUERY_ALL_ATTACHMENTS = "query_all_atts";
	public static final String DELETE_WITH_NAME = "delete_with_name";

	@ManyToOne(optional=false)
	@JoinColumn(name="task_ID", nullable=false, updatable=false)
	private Task task = null;
	
	@Persistent(cascade = { CascadeType.ALL })
	private AttachmentInfo attachmentInfo = null;
	
	@Id
	@GeneratedValue
	private long internalId;
	
	@Basic(fetch=FetchType.LAZY)
	@Lob
	private String value = null;

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public AttachmentInfo getAttachmentInfo() {
		return attachmentInfo;
	}

	public void setAttachmentInfo(AttachmentInfo attachmentInfo) {
		this.attachmentInfo = attachmentInfo;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getInternalId() {
		return internalId;
	}
}
