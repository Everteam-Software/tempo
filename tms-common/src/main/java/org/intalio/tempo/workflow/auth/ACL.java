package org.intalio.tempo.workflow.auth;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.apache.openjpa.persistence.Persistent;

@Entity
public class ACL {
    
    @Persistent
    @Column(name="action")
    public String action; 
    
    @OneToOne(cascade=CascadeType.ALL)
    public AuthIdentifierSet users = new AuthIdentifierSet();
    
    @OneToOne(cascade=CascadeType.ALL)
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
