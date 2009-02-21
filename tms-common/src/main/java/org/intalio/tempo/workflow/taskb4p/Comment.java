package org.intalio.tempo.workflow.taskb4p;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="tempob4p_comment")
@NamedQueries( { 
	@NamedQuery(name = Comment.QUERY_ALL_COMMENTS, query = "select m from Comment m where m.task.id=?1 order by m.addedAt") })
public class Comment {
	public static final String QUERY_ALL_COMMENTS = "query_all_comments";
	
	@ManyToOne(optional=false)
	@JoinColumn(name="task_ID", nullable=false, updatable=false)
	private Task task;
	@Basic
	private Date addedAt = null;
	@Basic
	private String addedBy = null;
	@Basic
	private String text = null;
	
	@Id
	@GeneratedValue
	private long internalId;
	
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public Date getAddedAt() {
		return addedAt;
	}
	public void setAddedAt(Date addedAt) {
		this.addedAt = addedAt;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public long getInternalId() {
		return internalId;
	}
}
