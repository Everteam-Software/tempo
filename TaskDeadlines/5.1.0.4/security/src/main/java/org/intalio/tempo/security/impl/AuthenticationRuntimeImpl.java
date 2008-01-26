/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.impl;

import java.rmi.RemoteException;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.authentication.AuthenticationRuntime;

/**
 * Authentication Runtime Server.  This is a thin wrapper around a
 * Security Provider to provide connectivity through the Java Connector
 * and Web Services.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class AuthenticationRuntimeImpl
    implements AuthenticationRuntime
{

    private final Realms _providers;
    
	/** 
	 * Default construct (necessary for Java connector)
	 */
	public AuthenticationRuntimeImpl( Realms providers ) 
	{
        _providers = providers;
	}

    // implement AuthenticationRuntime interface
    public boolean authenticate( String user, Property[] credentials )
        throws AuthenticationException, RemoteException
    {
		return _providers.getAuthenticationRuntime( user ).authenticate( user, credentials );
    }

}
