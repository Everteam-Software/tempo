package org.intalio.tempo.workflow.auth;

import java.util.Collection;

import javax.persistence.Entity;

import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.Type;

@Entity
public class ACL {
    
    @Persistent
    public String action; 
    
    @Externalizer("toCollection")
    @Type(Collection.class)
    public AuthIdentifierSet _users = new AuthIdentifierSet();
    
    @Externalizer("toCollection")
    @Type(Collection.class)
    public AuthIdentifierSet _roles = new AuthIdentifierSet();
    
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
    
}
