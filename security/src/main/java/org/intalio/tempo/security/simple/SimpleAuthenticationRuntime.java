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
import org.intalio.tempo.security.authentication.AuthenticationRuntime;
import org.intalio.tempo.security.authentication.UserNotFoundException;
import org.intalio.tempo.security.util.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of the authentication runtime functions.
 *
 * @author <a href="boisvert@intalio.com">Alex Boisvert</a>
 */
class SimpleAuthenticationRuntime
    implements AuthenticationRuntime
{

    private SimpleSecurityProvider _provider;
    
	static final Logger LOGGER = LoggerFactory.getLogger( "tempo.security.simple.authentication" );

    /** 
     * Construct simple authentication runtime functions.
     */
    SimpleAuthenticationRuntime( String realm, SimpleSecurityProvider provider ) 
    {
        _provider = provider;
    }


    // implement AuthenticationRuntime interface
    public boolean authenticate( String user, Property[] credentials )
        throws AuthenticationException
    {
        SimpleUser  simpleUser;
        Property    password;

		if ( LOGGER.isDebugEnabled() ) {
			StringBuffer buf = new StringBuffer( "Authenticate: " );
			buf.append( "user='" );
			buf.append( user );
			buf.append( "' credentials={" );
			for ( int i=0; i<credentials.length; i++ ) {
				buf.append( "[name='" );
				buf.append( credentials[i].getName() );
				buf.append( "' value='" );
				buf.append( credentials[i].getValue() );
				buf.append( "']" );
				if ( i < credentials.length-1 ) buf.append( ", " );
			}
			buf.append( "}" );			
			LOGGER.debug( buf.toString() );		
		}
		
		user = _provider.getDatabase().normalize( user );        
        simpleUser = _provider.getDatabase().getUser( user );
        if ( simpleUser == null ) {
            throw new UserNotFoundException( "User not found: " + user );
        }

        password = PropertyUtils.getProperty( credentials, 
                       AuthenticationConstants.PROPERTY_PASSWORD );
        if ( password == null ) {
            return false;
        }

        return simpleUser.getPassword().equals( password.getValue() );
    }
    
}
