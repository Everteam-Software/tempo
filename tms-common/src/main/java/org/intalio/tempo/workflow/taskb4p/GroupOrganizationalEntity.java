package org.intalio.tempo.workflow.taskb4p;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("GroupEntity")
public class GroupOrganizationalEntity extends OrganizationalEntity {
    @OneToMany(cascade=CascadeType.ALL, mappedBy="orgEntity")	
	private Set<Principal> groups;

	public Set<Principal> getGroups() {
		return groups;
	}

	public void setGroups(Set<Principal> groups) {
		this.groups = groups;
	}
	
	public void addGroup(Principal group) {
		if (this.groups == null) {
			groups = new HashSet<Principal>();
		}
		
		groups.add(group);
	}
}
