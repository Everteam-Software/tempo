package org.intalio.tempo.workflow.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;

@Entity
@Table(name="tempo_acl")
public class ACL extends BaseRestrictedEntity {
    
    @Persistent
    @Column(name="action")
    public String action; 
    
    public ACL() {
        super();
    }
    
    public ACL(String action) {
        this();
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isAuthorizedAction(UserRoles user, String action) {
        // Note: Action is authorized if there's no ACL provided (default)
        if (_userOwners.isEmpty() && _roleOwners.isEmpty())
            return true;
        if (_userOwners.contains(user.getUserID()))
            return true;
        if (((AuthIdentifierSet)_roleOwners).intersects(user.getAssignedRoles()))
            return true;
        return false;
    }

}
