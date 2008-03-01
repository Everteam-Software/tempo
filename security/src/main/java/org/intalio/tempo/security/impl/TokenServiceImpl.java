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
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.security.util.IdentifierUtils;
import org.intalio.tempo.security.util.StringArrayUtils;

/**
 * Implementation of TokenIssuer that uses local authentication and RBAC
 * services.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class TokenServiceImpl
    implements TokenService, AuthenticationConstants
{
    Realms _realms;
    TokenHandler _tokenHandler;

    public TokenServiceImpl()
    {
        // nothing
    }

    /**
     * Default no-arg constructor for Java Connector.
     */
    public TokenServiceImpl( Realms realms )
    {
        _realms = realms;
        _tokenHandler =  new TokenHandler();
    }


    public void setRealms( Realms realms )
    {
        _realms = realms;
    }

    public void setTokenHandler( TokenHandler handler )
    {
        _tokenHandler = handler;
    }

    /**
     * Internal (non-public) method to create a token without credential
     * verification.
     *
     * @param user user identifier
     * @return cryptographic token
     */
    public String createToken( String user )
        throws RBACException, RemoteException
    {
        String[]      roles;
        Property      userProp;
        Property      issueProp;
        Property      rolesProp;
        Property[]    props, userProps;

        // TODO we should use _realms to normalize
        user = IdentifierUtils.normalize(user, _realms.getDefaultRealm(), false, '\\');

        roles = _realms.authorizedRoles( user );

        // place session information in token
        userProp = new Property( PROPERTY_USER, user );
        issueProp = new Property( PROPERTY_ISSUED,
                                  Long.toString( System.currentTimeMillis() ) );
        rolesProp = new Property( PROPERTY_ROLES, StringArrayUtils.toCommaDelimited( roles ) );

        // add all user properties to token properties
        userProps = _realms.userProperties( user );
        props = new Property[ userProps.length + 3 ];
        props[ 0 ] = userProp;
        props[ 1 ] = issueProp;
        props[ 2 ] = rolesProp;
        System.arraycopy( userProps, 0, props, 3, userProps.length );

        return _tokenHandler.createToken( props );
    }


    /**
     * Authenticate a user and return a cryptographic token containing
     * session information.
     *
     * @param user user identifier
     * @param password password
     * @return cryptographic token
     */
    public String authenticateUser( String user, String password )
        throws AuthenticationException, RBACException, RemoteException
    {
        Property[]    props;
        Property      passwordProp;

        passwordProp = new Property( PROPERTY_PASSWORD,
                                        password );
        props = new Property[] { passwordProp };

        return authenticateUser( user, props );
    }


    /**
     * Authenticate a user and return a cryptographic token containing
     * session information.
     *
     * @param user user identifier
     * @param credentials set of credentials
     * @return cryptographic token
     */
    public String authenticateUser( String user, Property[] credentials )
        throws AuthenticationException, RBACException, RemoteException
    {
        if ( ! _realms.authenticate( user, credentials ) ) {
            throw new AuthenticationException( "Authentication failed: User '" + user + "'" );
        }

        return createToken( user );
    }


    /**
     * Return the properties encoded in the cryptographic token.
     *
     * @param token token
     * @return properties encoded in token
     */
    public Property[] getTokenProperties( String token )
        throws AuthenticationException, RemoteException
    {
        return _tokenHandler.parseToken( token );
    }

	public String getToken(String user) throws AuthenticationException, RBACException,
			RemoteException {
		return createToken( user );
	}

}
