/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.simple;

import java.util.HashMap;
import java.util.Iterator;

/**
 * User definition.
 *
 * @author <a href="boisvert@intalio.com">Alex Boisvert</a>
 */
public class SimpleUser
{

    /**
     * User identifier
     */
    private String _identifier;

    
    /**
     * User name.
     */
    private String _name;
    
    
    /**
     * User's email address.
     */
    private String _email;

    
    /**
     * User's password.
     */
    private String _password;


    /**
     * Role assignments.
     */
    private String[] _roles;

    
    /**
     * Resolved role assignments.  Tuples are { String, SimpleRole }.
     */
    private HashMap<String,SimpleRole> _resolvedRoles;
    
    
    /** 
     * Construct SimpleUser (used for unmarshalling)
     */
    public SimpleUser()
    {
        _roles = new String[0];
    }
    
    
    /**
     * Get the user identifier.
     */
    public String getIdentifier()
    {
        return _identifier;
    }
    
    
    /**
     * Set the user identifier.
     */
    public void setIdentifier( String identifier )
    {
		_identifier = identifier;
    }

    
    /**
     * Get the user name.
     */
    public String getName()
    {
        return _name;
    }
    
    
    /**
     * Set the user name.
     */
    public void setName( String name )
    {
        _name = name;
    }
    

    /**
     * Get the user's email address.
     */
    public String getEmail()
    {
        return _email;
    }
    
    
    /**
     * Set the user's email address.
     */
    public void setEmail( String email )
    {
        _email = email;
    }
    

    /**
     * Get the user's password.
     */
    public String getPassword()
    {
        return _password;
    }
    
    
    /**
     * Set the user's password.
     */
    public void setPassword( String password )
    {
        _password = password;
    }

    

	/** 
	 * Get assigned roles.
	 */
	public String[] getAssignedRoles()
	{
		return _roles;
	}


    /** 
     * Set assigned roles.
     */
    public void setAssignedRoles( String[] roles )
    {
    	_roles = roles;
    }
    
    
    /**
     * Return true if user has direct role assignment.
     */
    public boolean isAssignedRole( String role )
    {
        return _resolvedRoles.get( role ) != null;
    }
    
    
    /**
     * Get a map of the user's assigned roles ( String, SimpleRole ).
     */
    public HashMap<String,SimpleRole> getAssignedRolesMap()
    {
        return _resolvedRoles;
    }
    
    
    /**
     * Get a map of the user's authorized roles ( String, SimpleRole ).
     */
    public void getAuthorizedRoles( HashMap<String,SimpleRole> result )
    {
        SimpleRole  simpleRole;
        Iterator    iter;
        
        iter = _resolvedRoles.values().iterator();
        while ( iter.hasNext() ) {
            simpleRole = (SimpleRole) iter.next();
            result.put( simpleRole.getIdentifier(), simpleRole );
            simpleRole.getAuthorizedRoles( result );
        }
    }
    
    
    /**
     * Return true if user is authorized to a given role.
     */
    public boolean isAuthorizedRole( String role )
    {
        SimpleRole  simpleRole;
        Iterator    iter;
        
        if ( isAssignedRole( role ) ) {
            return true;
        }
        
        iter = _resolvedRoles.values().iterator();
        while ( iter.hasNext() ) {
            simpleRole = (SimpleRole) iter.next();
            if ( simpleRole.hasDescendantRole( role ) ) {
                return true;
            }
        }
        // not authorized
        return false;
    }
    

    /**
     * Prepare this object before it is used.
     */
    void prepare( SimpleDatabase database, String realm )
    {
        String role;
        
        if ( _identifier == null ) {
        	throw new IllegalStateException( "User doesn't have identifier in realm '"
        		+ realm + "'\n name '" + _name + "', email '" + _email + "'" );
        }
        _identifier = database.normalize( _identifier, realm );
        for ( int i=0; i<_roles.length; i++ ) {
            role = _roles[i];
            role = database.normalize( role, realm );
            _roles[i] = role;
        }
    }

    
    /**
     * Resolve object references
     */
    void resolve( SimpleDatabase database )
    {
        String      role;
        SimpleRole  simpleRole;
        
        _resolvedRoles = new HashMap<String,SimpleRole>();
        for ( int i=0; i<_roles.length; i++ ) {
			role = _roles[i];
            simpleRole = database.getRole( role );
            if ( simpleRole == null ) {
                throw new IllegalStateException( "Role not found: " + role );
            }
            _resolvedRoles.put( role, simpleRole );
        }
    }
    
}
