package org.intalio.tempo.workflow.taskb4p;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "tempob4p_organizationalEntity")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
public abstract class OrganizationalEntity {
	@Id
	@GeneratedValue
	private long internalId;

	public long getInternalId() {
		return internalId;
	}		
}
