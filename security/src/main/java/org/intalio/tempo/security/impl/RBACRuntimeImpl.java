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
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACRuntime;

/**
 * Authentication Runtime Service.  This is a thin wrapper around a
 * Security Provider to provide connectivity through the Java Connector
 * and Web Services.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class RBACRuntimeImpl
    implements RBACRuntime
{

    private final Realms _providers;
    
	/** 
	 * Default construct (necessary for Java connector)
	 */
	public RBACRuntimeImpl( Realms providers ) 
	{
        _providers = providers;
	}

	// implement RBACRuntime interface
	public boolean checkAccess( String user, String[] roles, 
								String operation, String object )
		throws RBACException, RemoteException
	{
		return _providers.getRBACRuntime( user ).checkAccess( user, roles, operation, object );
	}

}
