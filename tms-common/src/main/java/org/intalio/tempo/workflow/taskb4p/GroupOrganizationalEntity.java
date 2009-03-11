package org.intalio.tempo.workflow.taskb4p;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("GroupEntity")
public class GroupOrganizationalEntity extends OrganizationalEntity {

	public GroupOrganizationalEntity() {
		this.setEntityType(OrganizationalEntity.GROUP_ENTITY);
	}
}
