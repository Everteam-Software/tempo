package org.intalio.tempo.workflow.auth;

import java.util.Collection;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Type;

@Entity
public class ACL {
    final static Random r = new Random();
    
    @Id
    public String tid; 
    
    @Externalizer("toCollection")
    @Type(Collection.class)
    public AuthIdentifierSet _users = new AuthIdentifierSet();
    
    @Externalizer("toCollection")
    @Type(Collection.class)
    public AuthIdentifierSet _roles = new AuthIdentifierSet();
    
    public ACL() {
        
    }
    
    public ACL(String tid) {
        this.tid = tid;
    }
    
}
