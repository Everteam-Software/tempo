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
import org.intalio.tempo.security.rbac.RBACAdmin;
import org.intalio.tempo.security.rbac.RBACException;

/**
 * Authentication Query Service.  This is a thin wrapper around a
 * Security Provider to provide connectivity through the Java Connector
 * and Web Services.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class RBACAdminImpl
	implements RBACAdmin
{

    private final Realms _providers;
    
    /** 
	 * Default construct (necessary for Java connector)
	 */
	public RBACAdminImpl( Realms providers ) 
	{
        _providers = providers;
	}

	// implement RBACAdmin interface
	public void addUser( String user, Property[] properties )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( user).addUser( user, properties );
	}
   
    
	// implement RBACAdmin interface
	public void deleteUser( String user )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( user ).deleteUser( user );
	}


	// implement RBACAdmin interface
	public void addRole( String role, Property[] properties )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( role ).addRole( role, properties );
	}

    
	// implement RBACAdmin interface
	public void deleteRole( String role )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( role ).deleteRole( role );
	}

    
	// implement RBACAdmin interface
	public void assignUser( String user, String role )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( user ).assignUser( user, role );
	}
    

	// implement RBACAdmin interface
	public void deassignUser( String user, String role )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( user ).deassignUser( user, role );
	}


	// implement RBACAdmin interface
	public void grantPermission( String role, String operation, String object )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( role ).grantPermission( role, operation, object );
	}


	// implement RBACAdmin interface
	public void revokePermission( String role, String operation, String object )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( role ).revokePermission( role, operation, object );
	}


	// implement RBACAdmin interface
	public void addInheritance( String ascendant, String descendant )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( ascendant ).addInheritance( ascendant, descendant );
	}
    

	// implement RBACAdmin interface
	public void deleteInheritance( String ascendant, String descendant )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( ascendant ).deleteInheritance( ascendant, descendant );
	}
    

	// implement RBACAdmin interface
	public void addAscendant( String ascendant, Property[] properties, String descendant )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( ascendant ).addAscendant( ascendant, properties, descendant );
	}
    

	// implement RBACAdmin interface
	public void addDescendant( String descendant, Property[] properties, String ascendant )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( descendant ).addDescendant( descendant, properties, ascendant );
	}

    
	// implement RBACAdmin interface
	public void setUserProperties( String user, Property[] properties )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( user ).setUserProperties( user, properties );
	}

    
	// implement RBACAdmin interface
	public void setRoleProperties( String role, Property[] properties )
		throws RBACException, RemoteException
	{
        _providers.getRBACAdmin( role ).setRoleProperties( role, properties );
	}
	
}
