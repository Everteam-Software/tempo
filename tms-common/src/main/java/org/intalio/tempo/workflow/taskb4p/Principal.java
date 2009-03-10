package org.intalio.tempo.workflow.taskb4p;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="tempob4p_principal")
public class Principal {
	@Id
	@GeneratedValue
	private long internalId;
	@Basic
	private String value;
	@ManyToOne(optional=false)
	@JoinColumn(name="org_entity_id", nullable=false, updatable=false)
	private OrganizationalEntity orgEntity;
	
	public long getInternalId() {
		return internalId;
	}
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public OrganizationalEntity getOrgEntity() {
		return orgEntity;
	}
	public void setOrgEntity(OrganizationalEntity orgEntity) {
		this.orgEntity = orgEntity;
	}
}
