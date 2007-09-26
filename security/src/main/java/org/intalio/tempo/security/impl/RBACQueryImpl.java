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
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;

/**
 * Authentication Query Service.  This is a thin wrapper around a
 * Security Provider to provide connectivity through the Java Connector
 * and Web Services.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class RBACQueryImpl
	implements RBACQuery
{

    private final Realms _providers;
    
	/** 
	 * Default construct (necessary for Java connector)
	 */
	public RBACQueryImpl( Realms providers ) 
	{
        _providers = providers;
	}

	// implement RBACQuery interface
	public String[] ascendantRoles( String role ) 
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( role ).ascendantRoles( role );
	}

    
	// implement RBACQuery interface
	public String[] descendantRoles( String role )
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( role ).descendantRoles( role );
	}
    

	// implement RBACQuery interface
	public String[] assignedRoles( String user )
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( user ).assignedRoles( user );
	}
    
    
	// implement RBACQuery interface
	public String[] assignedUsers( String role )
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( role ).assignedUsers( role );
	}
    
    
	// implement RBACQuery interface
	public String[] authorizedRoles( String user )
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( user ).authorizedRoles( user );
	}
    

	// implement RBACQuery interface
	public String[] authorizedUsers( String role )
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( role ).authorizedUsers( role );
	}

    
	// implement RBACQuery interface
	public String[] roleOperationsOnObject( String role, String object )
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( role ).roleOperationsOnObject( role, object );
	}

    
	// implement RBACQuery interface
	public String[] topRoles( String realm )
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( realm ).topRoles( realm );
	}

    
	// implement RBACQuery interface
	public String[] userOperationsOnObject( String user, String object )
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( user ).userOperationsOnObject( user, object );
	}
    
    
	// implement RBACQuery interface
	public Property[] userProperties( String user )
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( user ).userProperties( user );
	}
    
    
	// implement RBACQuery interface
	public Property[] roleProperties( String role )
		throws RBACException, RemoteException
	{
		return _providers.getRBACQuery( role ).roleProperties( role );
	}
    
}
