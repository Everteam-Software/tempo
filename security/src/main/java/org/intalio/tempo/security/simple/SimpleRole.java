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

/**
 * Role definition.
 *
 * @author <a href="boisvert@intalio.com">Alex Boisvert</a>
 */
public class SimpleRole
{

    /**
     * Role identifier
     */
    private String _identifier;

    
    /**
     * Role description.
     */
    private String _description;
    

    /**
     * Descendant roles
     */
    private String[] _descendants;
   
    
    /**
     * List of ascendant roles (SimpleRole).
     */
    private ArrayList<SimpleRole> _ascendants;
    
    /**
     * Map of sub-roles.  Tuples are ( String, SimpleRole ).
     */
    private HashMap<String,SimpleRole> _descendantsMap;

    
    /**
     * Permissions assigned to this role.
     */
    private SimplePermission[] _permissions;

    
    /**
     * Map of object permissions.  Tuples are { String (object), HashMap of { permission, SimplePermission } }.
     */
    private HashMap<String, HashMap<String,SimplePermission> > _objectMap;
    

    
    /** 
     * Construct Simplerole (used for unmarshalling)
     */
    public SimpleRole()
    {
        _ascendants = new ArrayList<SimpleRole>();
        _descendants = new String[0];
		_permissions = new SimplePermission[ 0 ];
    }
    
    
    /**
     * Get the role identifier.
     */
    public String getIdentifier()
    {
        return _identifier;
    }
    
    
    /**
     * Set the role identifier.
     */
    public void setIdentifier( String identifier )
    {
        _identifier = identifier;
    }

    
    /**
     * Get the role description.
     */
    public String getDescription()
    {
        return _description;
    }
    
    
    /**
     * Set the role description.
     */
    public void setDescription( String description )
    {
        _description = description;
    }
    

    /**
     * Get the sub-roles.
     */
    public String[] getDescendants()
    {
    	return _descendants;
    }

    
    /**
     * Add a sub-role.
     */
    public void setDescendants( String[] descendants )
    {
        _descendants = descendants;
    }
    

    /**
     * Add ascendant role.
     */
    public void addAscendant( SimpleRole ascendant )
    {
        _ascendants.add( ascendant );
    }

    
    /**
     * Get the list of ascendant roles (valid only after roles are resolved).
     */
    public ArrayList<SimpleRole> getAscendantRoles()
    {
        return _ascendants;
    }
    
    
    /**
     * Return true if a given role is a descendant of this role.
     */
    public boolean hasDescendantRole( String subRole )
    {
        Iterator    iter;
        SimpleRole  simpleRole;

        // check immediate descendants
        if ( _descendantsMap.get( subRole ) != null ) {
            return true;
        }

        // check transitive descendants
        iter = _descendantsMap.values().iterator();
        while ( iter.hasNext() ) {
            simpleRole = (SimpleRole) iter.next();
            if ( simpleRole.hasDescendantRole( subRole ) ) {
                return true;
            }
        }
        // descendent role not found
        return false;
    }
    
    
    /**
     * Get a map of the role's authorized roles ( String, SimpleRole ).
     */
    public void getAuthorizedRoles( HashMap<String,SimpleRole> result )
    {
        SimpleRole  simpleRole;
        Iterator    iter;
        
        iter = _descendantsMap.values().iterator();
        while ( iter.hasNext() ) {
            simpleRole = (SimpleRole) iter.next();
            
            // avoid infinite recursion
            if ( result.get( simpleRole.getIdentifier() ) == null ) {
                result.put( simpleRole.getIdentifier(), simpleRole );
                simpleRole.getAuthorizedRoles( result );
            }
        }
    }

    
    /**
     * Get the permissions
     */
    public SimplePermission[] getPermissions()
    {
        return _permissions;
    }

    
    /**
     * Add a permission
     */
    public void setPermissions( SimplePermission[] permissions )
    {
		_permissions = permissions;
    }
    

    /**
     * Add the operations allowed for this role on a given object.  HashMap is { String - operation, SimplePermission }.
     */
    public void addOperationsOnObject( String object, HashMap<String,SimplePermission> result )
    {
        HashMap<String,SimplePermission> perms;
        
		perms = (HashMap<String,SimplePermission>) _objectMap.get( object );
        if ( perms != null ) {
            result.putAll( perms );
        }
    }


    
    /**
     * Return true if role is authorized to perform operation on object.
     */
    public boolean checkAccess( String operation, String object )
    {
        HashMap perms =  _objectMap.get( object );
        if ( perms == null ) {
            // the star "*" indicates all objects
            perms =  _objectMap.get( "*" );
            if ( perms == null ) {
                return false;
            }
        }
        
        if ( perms.get( operation ) != null ) {
            return true;
        }
        
        // the star "*" indicates all operations
        return perms.get( "*" ) != null;
        
    }
    
    
    /**
     * Prepare data structure for runtime use.
     */
    void prepare( SimpleDatabase database, String realm )
    {
        SimplePermission  perm;
        HashMap<String,SimplePermission> perms;
        
        _identifier = database.normalize( _identifier, realm );

		// normalize descendants relative to current realm
		for ( int i=0; i<_descendants.length; i++ ) {
			_descendants[i] = database.normalize( _descendants[i], realm );
		}
		
        // create object permission map
        _objectMap = new HashMap<String,HashMap<String,SimplePermission>>();
        for ( int i=0; i<_permissions.length; i++ ) {
            perm = _permissions[i];
            perms = (HashMap<String,SimplePermission>) _objectMap.get( perm.getObject() );
            if ( perms == null ) {
                perms = new HashMap<String,SimplePermission>();
                _objectMap.put( perm.getObject(), perms );
            }
            perms.put( perm.getOperation(), perm );
        }
    }
    
    
    /**
     * Resolve object references
     */
    void resolve( SimpleDatabase database )
    {
        String      role;
        SimpleRole  simpleRole;
        
        _descendantsMap = new HashMap<String,SimpleRole>();
        for ( int i=0; i<_descendants.length; i++ ) {
            role = _descendants[ i ];
            simpleRole = database.getRole( role );
            if ( simpleRole == null ) {
                throw new IllegalStateException( "Invalid role reference: " + role );
            }
            simpleRole.addAscendant( this );
            _descendantsMap.put( simpleRole.getIdentifier(), simpleRole );
        }
    }
}
