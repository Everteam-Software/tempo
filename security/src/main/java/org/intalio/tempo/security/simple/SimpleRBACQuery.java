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
import java.util.Iterator;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.rbac.RBACConstants;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.RoleNotFoundException;
import org.intalio.tempo.security.rbac.UserNotFoundException;
import org.intalio.tempo.security.util.IdentifierUtils;

/**
 * Simple implementation of the RBAC query functions.
 *
 * @author <a href="boisvert@intalio.com">Alex Boisvert</a>
 */
class SimpleRBACQuery
    implements RBACQuery
{
	
	private String _realm;	

    private SimpleSecurityProvider _provider;
    
    /** 
     * Construct simple RBAC query functions.
     */
    SimpleRBACQuery( String realm, SimpleSecurityProvider provider ) 
    {
    	_realm = realm;
        _provider = provider;
    }

    
    // implement RBACQuery interface
    public String[] ascendantRoles( String role ) 
        throws RBACException
    {
        SimpleRole  simpleRole;
        ArrayList<SimpleRole>   list;
        SimpleDatabase database;
        
        database = _provider.getDatabase();
        role = database.normalize( role );
        simpleRole = database.getRole( role );
        if ( simpleRole == null ) {
            throw new RoleNotFoundException( "Role not found: " + role );
        }

        list = simpleRole.getAscendantRoles();
        
		ArrayList response=new ArrayList<String>();

		for (SimpleRole r : list) {
			response.add(r.getIdentifier()); 
		}
		return (String[]) response.toArray( new String[ list.size() ] );
    }

    
    // implement RBACQuery interface
    public String[] descendantRoles( String role )
        throws RBACException
    {
        SimpleRole      simpleRole;
		SimpleDatabase  database;
        
		database = _provider.getDatabase();
        role = database.normalize( role );
        
        simpleRole = database.getRole( role );
        if ( simpleRole == null ) {
            throw new RoleNotFoundException( "Role not found: " + role );
        }

        return simpleRole.getDescendants();
    }
    

    // implement RBACQuery interface
    public String[] assignedRoles( String user )
        throws RBACException
    {
        SimpleUser      simpleUser;
        HashMap<String,SimpleRole> roles;
		SimpleDatabase  database;
        
		database = _provider.getDatabase();
        user = database.normalize( user );
        simpleUser = database.getUser( user );
        if ( simpleUser == null ) {
            throw new UserNotFoundException( "User not found: " + user );
        }
        
        roles = simpleUser.getAssignedRolesMap();
        
        return (String[]) roles.keySet().toArray( new String[ roles.size() ] );
    }
    
    
    // implement RBACQuery interface
    public String[] assignedUsers( String role )
        throws RBACException
    {
        SimpleRole      simpleRole;
        SimpleUser      simpleUser;
        Iterator        iter;
        ArrayList<String> list;
		SimpleDatabase  database;
        
		database = _provider.getDatabase();
        role = database.normalize( role );

        simpleRole = database.getRole( role );
        if ( simpleRole == null ) {
            throw new RoleNotFoundException( "Role not found: " + role );
        }
        
        list = new ArrayList<String>();
        iter = database.getUsers();
        while ( iter.hasNext() ) {
            simpleUser = (SimpleUser) iter.next();
            if ( simpleUser.getAssignedRolesMap().get( role ) != null ) {
                list.add( simpleUser.getIdentifier() );
            }
        }
        return (String[]) list.toArray( new String[ list.size() ] );
    }
    
    
    // implement RBACQuery interface
    public String[] authorizedRoles( String user )
        throws RBACException
    {
        SimpleUser      simpleUser;
        HashMap<String,SimpleRole> roles;
		SimpleDatabase  database;
        
		database = _provider.getDatabase();
        
        user = database.normalize( user );

        simpleUser = database.getUser( user );
        if ( simpleUser == null ) {
            throw new UserNotFoundException( "User not found: " + user );
        }

        roles = new HashMap<String,SimpleRole>();
        simpleUser.getAuthorizedRoles( roles );
        
        return (String[]) roles.keySet().toArray( new String[ roles.size() ] );
    }
    

    // implement RBACQuery interface
    public String[] authorizedUsers( String role )
        throws RBACException
    {
        SimpleRole      simpleRole;
        SimpleUser      simpleUser;
        Iterator        iter;
        ArrayList<String>  list;
		SimpleDatabase  database;
        
		database = _provider.getDatabase();
        role = database.normalize( role );

        simpleRole = database.getRole( role );
        if ( simpleRole == null ) {
            throw new RoleNotFoundException( "Role not found: " + role );
        }
        
        list = new ArrayList<String>();
        iter = database.getUsers();
        while ( iter.hasNext() ) {
            simpleUser = (SimpleUser) iter.next();
            if ( simpleUser.isAuthorizedRole( role ) ) {
                list.add( simpleUser.getIdentifier() );
            }
        }
        return (String[]) list.toArray( new String[ list.size() ] );
    }

    
    // implement RBACQuery interface
    public String[] roleOperationsOnObject( String role, String object )
        throws RBACException
    {
        SimpleRole      simpleRole;
        HashMap<String,SimplePermission> result;
        HashMap <String,SimpleRole>      roles;
        Iterator        iter;
		SimpleDatabase  database;
        
		database = _provider.getDatabase();
        
        role = database.normalize( role );

        simpleRole = database.getRole( role );
        if ( simpleRole == null ) {
            throw new RoleNotFoundException( "Role not found: " + role );
        }

        result = new HashMap<String,SimplePermission>();

        // add direct role operations
        simpleRole.addOperationsOnObject( object, result );
        
        // add inherited role operations
        roles = new HashMap<String,SimpleRole>();
        simpleRole.getAuthorizedRoles( roles );
        iter = roles.values().iterator();
        while ( iter.hasNext() ) {
            simpleRole = (SimpleRole) iter.next();
            simpleRole.addOperationsOnObject( object, result );
        }
        
        return (String[]) result.keySet().toArray( new String[ result.size() ] );
    }

    
    // implement RBACQuery interface
    public String[] topRoles( String realm )
        throws RBACException
    {
        Iterator       iter;
		SimpleRealm    simpleRealm;
		SimpleRole     simpleRole;
        ArrayList<String>      result;
		SimpleDatabase database;
        
		database = _provider.getDatabase();
        
		simpleRealm = database.getRealm( _realm );
		if ( simpleRealm == null ) {
			throw new RBACException( "Unknown realm '" + _realm + "'" );        		
		}
		        
        result = new ArrayList<String>();
        iter = database.getRoles();
        while ( iter.hasNext() ) {
            simpleRole = (SimpleRole) iter.next();
            if ( ( simpleRole.getAscendantRoles().size() == 0 ) 
            && ( _realm.equals( IdentifierUtils.getRealm( simpleRole.getIdentifier() ) ) ) )
            {
                result.add( simpleRole.getIdentifier() );
            }
        }
        
        return (String[]) result.toArray( new String[ result.size() ] );
    }

    
    // implement RBACQuery interface
    public String[] userOperationsOnObject( String user, String object )
        throws RBACException
    {
        SimpleUser      simpleUser;
        SimpleRole      simpleRole;
        HashMap<String,SimplePermission> result;
        HashMap<String,SimpleRole> roles;
        Iterator        iter;
		SimpleDatabase  database;
        
		database = _provider.getDatabase();
        
        user = database.normalize( user );

        simpleUser = database.getUser( user );
        if ( simpleUser == null ) {
            throw new UserNotFoundException( "User not found: " + user );
        }

        roles = new HashMap<String,SimpleRole>();
        simpleUser.getAuthorizedRoles( roles );
        
        result = new HashMap<String,SimplePermission>();

        // add inherited role operations
        roles = new HashMap<String,SimpleRole>();
        simpleUser.getAuthorizedRoles( roles );
        iter = roles.values().iterator();
        while ( iter.hasNext() ) {
            simpleRole = (SimpleRole) iter.next();
            simpleRole.addOperationsOnObject( object, result );
        }
        
        return (String[]) result.keySet().toArray( new String[ result.size() ] );
        
    }
    
    
    // implement RBACQuery interface
    public Property[] userProperties( String user )
        throws RBACException
    {
        SimpleUser      simpleUser;
		SimpleDatabase  database;
        
		database = _provider.getDatabase();
        
        user = database.normalize( user );

        simpleUser = database.getUser( user );
        if ( simpleUser == null ) {
            throw new UserNotFoundException( "User not found: " + user );
        }
        
        Property name = new Property( RBACConstants.PROPERTY_FULL_NAME,
                                      simpleUser.getName() );
        
        Property email = new Property( RBACConstants.PROPERTY_EMAIL,
                                       simpleUser.getEmail() );

        return new Property[] { name, email };
    }
    
    
    // implement RBACQuery interface
    public Property[] roleProperties( String role )
        throws RBACException
    {
        SimpleRole      simpleRole;
		SimpleDatabase  database;
        
		database = _provider.getDatabase();
        
        role = database.normalize( role );

        simpleRole = database.getRole( role );
        if ( simpleRole == null ) {
            throw new RoleNotFoundException( "Role not found: " + role );
        }
        
        Property description = new Property( RBACConstants.PROPERTY_DESCRIPTION, 
                                             simpleRole.getDescription() );
        
        return new Property[] { description };
    }
    
}
