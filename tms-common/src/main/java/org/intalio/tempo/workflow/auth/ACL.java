package org.intalio.tempo.workflow.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
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
        if (getUserOwners().isEmpty() && getRoleOwners().isEmpty())
            return true;
        else if (getUserOwners().contains(user.getUserID()))
            return true;
        else return (CollectionUtils.containsAny(getRoleOwners(),user.getAssignedRoles()));
    }

}
