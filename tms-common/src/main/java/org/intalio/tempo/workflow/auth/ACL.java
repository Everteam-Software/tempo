package org.intalio.tempo.workflow.auth;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;

@Entity
@Table(name="acls")
public class ACL {
    
    @Persistent
    @Column(name="action")
    public String action; 
    
    @OneToOne(cascade=CascadeType.ALL)
    @Column(name="users")
    public AuthIdentifierSet users = new AuthIdentifierSet();
    
    @OneToOne(cascade=CascadeType.ALL)
    @Column(name="roles")
    public AuthIdentifierSet roles = new AuthIdentifierSet();
    
    public ACL() {
        
    }
    
    public ACL(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public AuthIdentifierSet getUsers() {
        return users;
    }

    public void setUsers(AuthIdentifierSet users) {
        this.users = users;
    }

    public AuthIdentifierSet getRoles() {
        return roles;
    }

    public void setRoles(AuthIdentifierSet roles) {
        this.roles = roles;
    }
    
    
    
}
