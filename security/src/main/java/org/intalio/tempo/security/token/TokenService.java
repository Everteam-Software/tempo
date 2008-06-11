/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.token;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.rbac.RBACException;

/**
 * Security token issuing and introspection service.
 * <p>
 * A security token represents a collection of one or more claims, encoded
 * as properties.  This service is used to issue security tokens for 
 * authenticated users and allows introspection of tokens to access 
 * properties contained therein.
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface TokenService
    extends Remote
{
  public static final String CAS_PROXY_TICKET = "com.intalio.tempo.cas.proxyTicket";

	/**
	 * Authenticate a user and return a security token containing
	 * properties about this user.
	 * 
	 * @param user user identifier
	 * @param password password
	 * @return security token
	 */
	public String authenticateUser( String user, String password )
		throws AuthenticationException, RBACException, RemoteException;
	
	
	/**
	 * Authenticate a user and return a security token containing
	 * properties about this user.
	 * 
	 * @param user user identifier
	 * @param credentials set of credentials
	 * @return security token
	 */
	public String authenticateUser( String user, Property[] credentials )
		throws AuthenticationException, RBACException, RemoteException;


	/**
	 * Return the properties encoded in the cryptographic token.
	 * 
	 * @param token token
	 * @return properties encoded in token
	 */
	public Property[] getTokenProperties( String token )
	    throws AuthenticationException, RemoteException;
	
	/**
	 * Get the security token of an authenticated user
	 * 
	 * @param user user identifier
	 * @return security token
	 */
	public String getTokenFromTicket(String ticket, String serviceURL) 
		throws AuthenticationException, RBACException, RemoteException;
}
