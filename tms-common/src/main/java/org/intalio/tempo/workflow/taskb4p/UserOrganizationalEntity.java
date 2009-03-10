package org.intalio.tempo.workflow.taskb4p;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("UserEntity")
public class UserOrganizationalEntity extends OrganizationalEntity {
    @OneToMany(cascade=CascadeType.ALL, mappedBy="orgEntity")
	private Set<Principal> users = null;

	public Set<Principal> getUsers() {
		return users;
	}

	public void setUsers(Set<Principal> users) {
		this.users = users;
	}

	public void addUser(Principal user) {
		if (this.users == null) {
			users = new HashSet<Principal>();
		}
		
		users.add(user);
	}
}
