/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.simple;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Realm definition.
 *
 * @author <a href="boisvert@intalio.com">Alex Boisvert</a>
 */
public class SimpleRealm
{

    /**
     * Realm identifier.
     */
    private String _identifier;
    
    
    /**
     * User list:  contains SimpleUser objects
     */
    private ArrayList<SimpleUser> _userList;
    

    /**
     * User map.  Tuples are { String, SimpleUser }.
     */
    private HashMap<String,SimpleUser> _userMap;

    
    /**
     * Role list: contains SimpleRole objects
     */
    private ArrayList<SimpleRole> _roleList;
    
    
    /**
     * Role map.  Tuples are { String, SimpleRole }.
     */
    private HashMap<String,SimpleRole> _roleMap;


    /** 
     * Construct SimpleRealm (used for unmarshalling)
     */
    public SimpleRealm()
    {
        _userList = new ArrayList<SimpleUser>();
        _userMap = new HashMap<String,SimpleUser>();
        _roleList = new ArrayList<SimpleRole>();
        _roleMap = new HashMap<String,SimpleRole>();
    }
    
    
    /**
     * Get the realm identifer.
     */
    public String getIdentifier()
    {
        return _identifier;
    }
    
    
    /**
     * Set the realm identifier.
     */
    public void setIdentifier( String identifier )
    {
        _identifier = identifier;
    }

    
    /**
     * Add a user.
     */
    public void addUser( SimpleUser user )
    {
        _userList.add( user );
    }

    
    /**
     * Get a user.
     */
    public SimpleUser getUser( String user )
    {
        return (SimpleUser) _userMap.get( user );
    }
    
    
    /**
     * Get a map of the user ( String, SimpleUser ).
     */
    public HashMap<String,SimpleUser> getUsers()
    {
        return _userMap;
    }

    
    /**
     * Add a role
     */
    public void addRole( SimpleRole role )
    {
        _roleList.add( role );
    }


    /**
     * Get a role.
     */
    public SimpleRole getRole( String role )
    {
        return (SimpleRole) _roleMap.get( role );
    }

    
    /**
     * Get a map of the roles ( String, SimpleRole ).
     */
    public HashMap<String,SimpleRole> getRoles()
    {
        return _roleMap;
    }
    
    /**
     * Prepare data structure for runtime use.
     */
    void prepare( SimpleDatabase database )
    {
        int size;
        
        size = _userList.size();
        for ( int i=0; i<size; i++ ) {
            SimpleUser user = (SimpleUser) _userList.get(i);
            user.prepare( database, _identifier );
            _userMap.put( user.getIdentifier(), user );
        }
        
        size = _roleList.size();
        for ( int i=0; i<size; i++ ) {
            SimpleRole role = (SimpleRole) _roleList.get(i);
            role.prepare( database, _identifier );
            _roleMap.put( role.getIdentifier(), role );
        }
    }
    

    /**
     * Resolve object references
     */
    void resolve( SimpleDatabase database )
    {
        int size;
        
        size = _userList.size();
        for ( int i=0; i<size; i++ ) {
            SimpleUser user = (SimpleUser) _userList.get(i);
            user.resolve( database );
        }

		size = _roleList.size();
		for ( int i=0; i<size; i++ ) {
			SimpleRole role = (SimpleRole) _roleList.get(i);
			role.resolve( database );
		}
    }
    
}

