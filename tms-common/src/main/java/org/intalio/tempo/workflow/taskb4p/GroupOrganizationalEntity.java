package org.intalio.tempo.workflow.taskb4p;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;

@Entity
@DiscriminatorValue("GroupEntity")
public class GroupOrganizationalEntity extends OrganizationalEntity {
    @PersistentCollection(elementType=String.class, elementCascade=CascadeType.ALL, elementEmbedded = false, fetch=FetchType.EAGER)
    @ContainerTable(name="tempob4p_groups")	
	private Set<String> groups;

	public Set<String> getGroups() {
		return groups;
	}

	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}
	
	public void addGroup(String group) {
		if (this.groups == null) {
			groups = new HashSet<String>();
		}
		
		groups.add(group);
	}
}
