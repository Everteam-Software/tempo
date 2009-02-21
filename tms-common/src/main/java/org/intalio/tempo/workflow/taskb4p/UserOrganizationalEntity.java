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
@DiscriminatorValue("UserEntity")
public class UserOrganizationalEntity extends OrganizationalEntity {
    @PersistentCollection(elementType=String.class, elementCascade=CascadeType.ALL, elementEmbedded = false, fetch=FetchType.EAGER)
    @ContainerTable(name="tempob4p_users")	
	private Set<String> users = null;

	public Set<String> getUsers() {
		return users;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}

	public void addUser(String user) {
		if (this.users == null) {
			users = new HashSet<String>();
		}
		
		users.add(user);
	}
}
