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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationAdmin;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.authentication.AuthenticationQuery;
import org.intalio.tempo.security.authentication.AuthenticationRuntime;
import org.intalio.tempo.security.authentication.provider.AuthenticationProvider;
import org.intalio.tempo.security.provider.SecurityProvider;
import org.intalio.tempo.security.rbac.RBACAdmin;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.RBACRuntime;
import org.intalio.tempo.security.rbac.provider.RBACProvider;
import org.intalio.tempo.security.util.IdentifierUtils;

/**
 * Realms - registry for security providers
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class Realms
    implements AuthenticationAdmin, AuthenticationQuery, AuthenticationRuntime,
                RBACAdmin, RBACQuery, RBACRuntime
{

    /**
     * Map of { Realm as String, Provider }
     */
	private HashMap<String,SecurityProvider> _realms = new HashMap<String,SecurityProvider>();

    
    /**
     * Name of default realm
     */
    private String _defaultRealm = "default";
    
    
    public Realms()
    {
        // nothing
    }
    
    /**
     * Register security providers
     * 
     */
    public void setSecurityProviders( List<SecurityProvider> providers )
        throws AuthenticationException, RBACException
    {
        for ( SecurityProvider provider : providers ) {
            String[] realms = provider.getRealms();
            for ( int i=0; i<realms.length; i++ ) {
                _realms.put( realms[i].toLowerCase(), provider );
            }
        }
    }
    
    
    public List<SecurityProvider> getSecurityProviders()
    {
        return new ArrayList<SecurityProvider>( _realms.values() );
    }
    
    public String[] getRealmIdentifiers()
    {
        return _realms.keySet().toArray( new String[ _realms.size() ] );
    }
    
    /**
     * Get name of default realm
     */
    public String getDefaultRealm()
    {
        return _defaultRealm;
    }
    
    
    /**
     * Set name of default realm
     */
    public void setDefaultRealm( String defaultRealm )
    {
        _defaultRealm = defaultRealm;
    }
    
    
    /**
	 * Get _context SecurityProvider for a given realm. 
	 */
    public SecurityProvider getSecurityProvider( String realm )
    {
		if ( realm == null || realm.length() == 0 ) {
			realm = _defaultRealm;
		} else {
			realm = realm.toLowerCase();
		}

		return _realms.get( realm );
	}

	/**
	 * Get AuthenticationProvider for a given realm. 
	 */
	AuthenticationProvider getAuthenticationProvider( String realm )
		throws AuthenticationException
	{
		SecurityProvider        security;
		AuthenticationProvider  auth;

		security = getSecurityProvider( realm );
        if ( security == null ) {
            throw new AuthenticationException("There is no such realm - '" + realm + "'!" );
        }

		auth = security.getAuthenticationProvider( realm );
		if ( auth == null ) {
			throw new AuthenticationException( 
                "SecurityProvider doesn't provide AuthenticationProvider "
				+ "for realm '" + realm + "'" );
		}
		return auth;
	}	


	/**
	 * Get RBACProvider for a given realm. 
	 */
	RBACProvider getRBACProvider( String realm )
		throws RBACException
	{
		SecurityProvider  security;
		RBACProvider      rbac;

		security = getSecurityProvider( realm );
		rbac = security.getRBACProvider( realm );
		if ( rbac == null ) {
			throw new RBACException( 
				"SecurityProvider '" + security.getName()
				+ "' doesn't provide RBACProvider "
				+ "for realm '" + realm + "'" );
		}
		return rbac;
	}	
	
	
	/**
	 * Get AuthenticationAdmin for a given role/user identifier. 
	 */
	AuthenticationAdmin getAuthenticationAdmin( String identifier )
		throws AuthenticationException
	{
		AuthenticationProvider  auth;
		AuthenticationAdmin     admin;
		String                  realm;
						
		realm = IdentifierUtils.getRealm( identifier );
		auth = getAuthenticationProvider( realm );						
		admin = auth.getAdmin();
		if ( admin == null ) {
			throw new AuthenticationException( 
				"AuthenticationProvider doesn't provide AuthenticationAdmin" );
		}
		return admin;		
	}


	/**
	 * Get AuthenticationQuery for a given role/user identifier. 
	 */
	AuthenticationQuery getAuthenticationQuery( String identifier )
		throws AuthenticationException
	{
		AuthenticationProvider  auth;
		AuthenticationQuery     query;
		String                  realm;
						
		realm = IdentifierUtils.getRealm( identifier );
		auth = getAuthenticationProvider( realm );						
		query = auth.getQuery();
		if ( query == null ) {
			throw new AuthenticationException( 
				"AuthenticationProvider doesn't provide AuthenticationQuery" );
		}
		return query;		
	}


	/**
	 * Get AuthenticationRuntime for a given role/user identifier. 
	 */
	AuthenticationRuntime getAuthenticationRuntime( String identifier )
		throws AuthenticationException
	{
		AuthenticationProvider auth;
		AuthenticationRuntime  runtime;
		String                 realm;
						
		realm = IdentifierUtils.getRealm( identifier );
		auth = getAuthenticationProvider( realm );						
		runtime = auth.getRuntime();
		if ( runtime == null ) {
			throw new AuthenticationException(
				"AuthenticationProvider doesn't provide AuthenticationRuntime" );
		}
		return runtime;		
	}


	/**
	 * Get RBACAdmin for a given role/user identifier. 
	 */
	RBACAdmin getRBACAdmin( String identifier )
		throws RBACException
	{
		RBACProvider  rbac;
		RBACAdmin     admin;
		String        realm;
						
		realm = IdentifierUtils.getRealm( identifier );
		rbac = getRBACProvider( realm );						
		admin = rbac.getAdmin();
		if ( admin == null ) {
			throw new RBACException( 
				"RBACProvider doesn't provide RBACAdmin" );
		}
		return admin;		
	}


	/**
	 * Get RBACQuery for a given role/user identifier. 
	 */
	RBACQuery getRBACQuery( String identifier )
		throws RBACException
	{
		RBACProvider rbac;
		RBACQuery    query;
		String       realm;
						
		realm = IdentifierUtils.getRealm( identifier );
		rbac = getRBACProvider( realm );						
		query = rbac.getQuery();
		if ( query == null ) {
			throw new RBACException( "RBACProvider doesn't provide RBACQuery" );
		}
		return query;		
	}


	/**
	 * Get RBACRuntime for a given role/user identifier. 
	 */
	RBACRuntime getRBACRuntime( String identifier )
		throws RBACException
	{
		RBACProvider rbac;
		RBACRuntime  runtime;
		String       realm;
						
        realm = IdentifierUtils.getRealm( identifier );
        rbac = getRBACProvider( realm );						
		runtime = rbac.getRuntime();
		if ( runtime == null ) {
			throw new RBACException( "RBACProvider doesn't provide RBACRuntime" );
		}
		return runtime;		
	}

    // implement AuthenticationAdmin interface
    public void setUserCredentials( String user, Property[] credentials )
         throws AuthenticationException, RemoteException
     {
        getAuthenticationAdmin( user ).setUserCredentials( user, credentials );
    }
    
    // implement AuthenticationQuery interface
    public Property[] getUserCredentials( String user )
        throws AuthenticationException, RemoteException
    {
        return getAuthenticationQuery( user ).getUserCredentials( user );
    }

    // implement AuthenticationRuntime interface
    public boolean authenticate( String user, Property[] credentials )
        throws AuthenticationException, RemoteException
    {
        return getAuthenticationRuntime( user ).authenticate( user, credentials );
    }
    
    // implement RBACAdmin interface
    public void addUser( String user, Property[] properties )
        throws RBACException, RemoteException
    {
        getRBACAdmin( user).addUser( user, properties );
    }
   
    
    // implement RBACAdmin interface
    public void deleteUser( String user )
        throws RBACException, RemoteException
    {
        getRBACAdmin( user ).deleteUser( user );
    }


    // implement RBACAdmin interface
    public void addRole( String role, Property[] properties )
        throws RBACException, RemoteException
    {
        getRBACAdmin( role ).addRole( role, properties );
    }

    
    // implement RBACAdmin interface
    public void deleteRole( String role )
        throws RBACException, RemoteException
    {
        getRBACAdmin( role ).deleteRole( role );
    }

    
    // implement RBACAdmin interface
    public void assignUser( String user, String role )
        throws RBACException, RemoteException
    {
        getRBACAdmin( user ).assignUser( user, role );
    }
    

    // implement RBACAdmin interface
    public void deassignUser( String user, String role )
        throws RBACException, RemoteException
    {
        getRBACAdmin( user ).deassignUser( user, role );
    }


    // implement RBACAdmin interface
    public void grantPermission( String role, String operation, String object )
        throws RBACException, RemoteException
    {
        getRBACAdmin( role ).grantPermission( role, operation, object );
    }


    // implement RBACAdmin interface
    public void revokePermission( String role, String operation, String object )
        throws RBACException, RemoteException
    {
        getRBACAdmin( role ).revokePermission( role, operation, object );
    }


    // implement RBACAdmin interface
    public void addInheritance( String ascendant, String descendant )
        throws RBACException, RemoteException
    {
        getRBACAdmin( ascendant ).addInheritance( ascendant, descendant );
    }
    

    // implement RBACAdmin interface
    public void deleteInheritance( String ascendant, String descendant )
        throws RBACException, RemoteException
    {
        getRBACAdmin( ascendant ).deleteInheritance( ascendant, descendant );
    }
    

    // implement RBACAdmin interface
    public void addAscendant( String ascendant, Property[] properties, String descendant )
        throws RBACException, RemoteException
    {
        getRBACAdmin( ascendant ).addAscendant( ascendant, properties, descendant );
    }
    

    // implement RBACAdmin interface
    public void addDescendant( String descendant, Property[] properties, String ascendant )
        throws RBACException, RemoteException
    {
        getRBACAdmin( descendant ).addDescendant( descendant, properties, ascendant );
    }

    
    // implement RBACAdmin interface
    public void setUserProperties( String user, Property[] properties )
        throws RBACException, RemoteException
    {
        getRBACAdmin( user ).setUserProperties( user, properties );
    }

    
    // implement RBACAdmin interface
    public void setRoleProperties( String role, Property[] properties )
        throws RBACException, RemoteException
    {
        getRBACAdmin( role ).setRoleProperties( role, properties );
    }

    // implement RBACQuery interface
    public String[] ascendantRoles( String role ) 
        throws RBACException, RemoteException
    {
        return getRBACQuery( role ).ascendantRoles( role );
    }

    
    // implement RBACQuery interface
    public String[] descendantRoles( String role )
        throws RBACException, RemoteException
    {
        return getRBACQuery( role ).descendantRoles( role );
    }
    

    // implement RBACQuery interface
    public String[] assignedRoles( String user )
        throws RBACException, RemoteException
    {
        return getRBACQuery( user ).assignedRoles( user );
    }
    
    
    // implement RBACQuery interface
    public String[] assignedUsers( String role )
        throws RBACException, RemoteException
    {
        return getRBACQuery( role ).assignedUsers( role );
    }
    
    
    // implement RBACQuery interface
    public String[] authorizedRoles( String user )
        throws RBACException, RemoteException
    {
        return getRBACQuery( user ).authorizedRoles( user );
    }
    

    // implement RBACQuery interface
    public String[] authorizedUsers( String role )
        throws RBACException, RemoteException
    {
        return getRBACQuery( role ).authorizedUsers( role );
    }

    
    // implement RBACQuery interface
    public String[] roleOperationsOnObject( String role, String object )
        throws RBACException, RemoteException
    {
        return getRBACQuery( role ).roleOperationsOnObject( role, object );
    }

    
    // implement RBACQuery interface
    public String[] topRoles( String realm )
        throws RBACException, RemoteException
    {
    	
        return getRBACProvider( realm ).getQuery().topRoles( realm );
    }

    
    // implement RBACQuery interface
    public String[] userOperationsOnObject( String user, String object )
        throws RBACException, RemoteException
    {
        return getRBACQuery( user ).userOperationsOnObject( user, object );
    }
    
    
    // implement RBACQuery interface
    public Property[] userProperties( String user )
        throws RBACException, RemoteException
    {
        return getRBACQuery( user ).userProperties( user );
    }
    
    
    // implement RBACQuery interface
    public Property[] roleProperties( String role )
        throws RBACException, RemoteException
    {
        return getRBACQuery( role ).roleProperties( role );
    }

    // implement RBACRuntime interface
    public boolean checkAccess( String user, String[] roles, 
                                String operation, String object )
        throws RBACException, RemoteException
    {
        return getRBACRuntime( user ).checkAccess( user, roles, operation, object );
    }
    
}
