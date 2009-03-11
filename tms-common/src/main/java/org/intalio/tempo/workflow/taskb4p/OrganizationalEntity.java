package org.intalio.tempo.workflow.taskb4p;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tempob4p_organizationalEntity")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
public abstract class OrganizationalEntity {
	public static final String USER_ENTITY = "user_entity";
	public static final String GROUP_ENTITY = "group_entity";
	
	@Id
	@GeneratedValue
	private long internalId;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="orgEntity")
	private Set<Principal> principals;
	@Basic
	protected String entityType;

	public long getInternalId() {
		return internalId;
	}

	public Set<Principal> getPrincipals() {
		return principals;
	}

	public void setPrincipals(Set<Principal> principals) {
		this.principals = principals;
	}

	public void addPrincipal(Principal principal) {
		if (this.principals == null) {
			principals = new HashSet<Principal>();
		}
		
		principals.add(principal);
	}

	protected void setEntityType(String type) {
		this.entityType = type;
	}
	public String getEntityType() {
		return this.entityType;
	}
	
}
