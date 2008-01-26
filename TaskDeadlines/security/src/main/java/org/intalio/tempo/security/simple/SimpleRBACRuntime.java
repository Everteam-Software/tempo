/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.simple;

import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACRuntime;
import org.intalio.tempo.security.rbac.RoleNotFoundException;
import org.intalio.tempo.security.rbac.UserNotFoundException;

/**
 * Simple implementation of the RBAC runtime functions.
 *
 * @author <a href="boisvert@intalio.com">Alex Boisvert</a>
 */
class SimpleRBACRuntime
    implements RBACRuntime
{

    private SimpleSecurityProvider _provider;

    /** 
     * Construct simple RBAC runtime functions.
     */
    SimpleRBACRuntime( String realm, SimpleSecurityProvider provider ) 
    {
        _provider = provider;
    }


    // implement RBACRuntime interface
    public boolean checkAccess( String user, String[] roles, String operation, String object )
        throws RBACException
    {
        String          role;
        SimpleUser      simpleUser;
        SimpleRole      simpleRole;
		SimpleDatabase  database;
        
		database = _provider.getDatabase();
        user = database.normalize( user );
        database.normalize( roles );
        
        simpleUser = database.getUser( user );
        if ( simpleUser == null ) {
            throw new UserNotFoundException( "User not found: " + user );
        }
        
        // check role assignments and permissions
        for ( int i=0; i<roles.length; i++ ) {
            role = roles[i];
            
            simpleRole = database.getRole( role );
            if ( simpleRole == null ) {
                throw new RoleNotFoundException( "Role not found: " + role );
            }

            if ( ! simpleUser.isAuthorizedRole( role ) ) {
                throw new RBACException( "User '" + user + "' is not authorized for role '" 
                                         + role + "'" );
            }
            
            if ( simpleRole.checkAccess( operation, object ) ) {
                return true;
            }
        }
        
        // no permission found
        return false;
    }
    
}
