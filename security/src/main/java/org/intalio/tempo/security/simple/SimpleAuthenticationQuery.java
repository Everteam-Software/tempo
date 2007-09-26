/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.simple;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.authentication.AuthenticationQuery;
import org.intalio.tempo.security.authentication.UserNotFoundException;

/**
 * Simple implementation of the authentication query functions.
 *
 * @author <a href="boisvert@intalio.com">Alex Boisvert</a>
 */
class SimpleAuthenticationQuery
    implements AuthenticationQuery
{

    private SimpleSecurityProvider _provider;
    
    /** 
     * Construct simple authentication query functions.
     */
    SimpleAuthenticationQuery( String realm, SimpleSecurityProvider provider ) 
    {
        _provider = provider;
    }

    
    // implement AuthenticationQuery interface
    public Property[] getUserCredentials( String user )
        throws AuthenticationException
    {
        SimpleUser  simpleUser;
        Property    password;
		SimpleDatabase database;
        
		database = _provider.getDatabase();
        
        simpleUser = database.getUser( user );
        if ( simpleUser == null ) {
            throw new UserNotFoundException( "User not found: " + user );
        }
        
        password = new Property( AuthenticationConstants.PROPERTY_PASSWORD, 
                                 simpleUser.getPassword() );
        
        return new Property[] { password };
    }
    
}
