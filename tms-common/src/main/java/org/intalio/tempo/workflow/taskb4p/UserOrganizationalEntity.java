package org.intalio.tempo.workflow.taskb4p;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("UserEntity")
public class UserOrganizationalEntity extends OrganizationalEntity {
	
	public UserOrganizationalEntity() {
		this.setEntityType(OrganizationalEntity.USER_ENTITY);
	}
}
