/**
 * Copyright (C) 2003-2008, Intalio Inc.
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

import edu.yale.its.tp.cas.client.ProxyTicketValidator;

/**
 * Implementation of TokenIssuer that uses local authentication and RBAC
 * services.
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class TokenServiceImpl implements TokenService {
    Realms _realms;
    TokenHandler _tokenHandler;

    String _validateURL;
    boolean _passwordAsAProperty;

    public TokenServiceImpl() {
        // nothing
    }

    /**
     * Default no-arg constructor for Java Connector.
     */
    public TokenServiceImpl(Realms realms) {
        _realms = realms;
        _tokenHandler = new TokenHandler();
    }

    public TokenServiceImpl(Realms realms, String validateURL) {
        _realms = realms;
        _tokenHandler = new TokenHandler();
        _validateURL = validateURL;
    }

    public void setPasswordAsAProperty(Boolean asAProperty) {
        _passwordAsAProperty = asAProperty;
    }

    public void setRealms(Realms realms) {
        _realms = realms;
    }

    public void setTokenHandler(TokenHandler handler) {
        _tokenHandler = handler;
    }

    /**
     * Internal (non-public) method to create a token without credential
     * verification.
     * 
     * @param user
     *            user identifier
     * @return cryptographic token
     */
    public String createToken(String user) throws RBACException, RemoteException {
        return createToken(user, null);
    }

    public String createToken(String user, String password) throws RBACException, RemoteException {
        // TODO we should use _realms to normalize
        user = IdentifierUtils.normalize(user, _realms.getDefaultRealm(), false, '\\');

        String[] roles = _realms.authorizedRoles(user);

        // place session information in token
        Property userProp = new Property(AuthenticationConstants.PROPERTY_USER, user);
        Property issueProp = new Property(AuthenticationConstants.PROPERTY_ISSUED, Long.toString(System.currentTimeMillis()));
        Property rolesProp = new Property(AuthenticationConstants.PROPERTY_ROLES, StringArrayUtils.toCommaDelimited(roles));

        // add all user properties to token properties
        Property[] userProps = _realms.userProperties(user);
        boolean pap = (password != null && _passwordAsAProperty);
        int length = pap? 4 : 3;
        Property[] props = new Property[userProps.length + length];
        props[0] = userProp;
        props[1] = issueProp;
        props[2] = rolesProp;
        if (pap)
            props[3] = new Property(AuthenticationConstants.PROPERTY_PASSWORD, password);
        System.arraycopy(userProps, 0, props, length, userProps.length);
        return _tokenHandler.createToken(props);
    }

    /**
     * Authenticate a user and return a cryptographic token containing session
     * information.
     * 
     * @param user
     *            user identifier
     * @param password
     *            password
     * @return cryptographic token
     */
    public String authenticateUser(String user, String password) throws AuthenticationException, RBACException, RemoteException {
        Property[] props;
        Property passwordProp;

        passwordProp = new Property(AuthenticationConstants.PROPERTY_PASSWORD, password);
        props = new Property[] { passwordProp };

        if (!_realms.authenticate(user, props)) {
            throw new AuthenticationException("Authentication failed: User '" + user + "'");
        }

        return createToken(user, password);
    }

    /**
     * Authenticate a user and return a cryptographic token containing session
     * information.
     * 
     * @param user
     *            user identifier
     * @param credentials
     *            set of credentials
     * @return cryptographic token
     */
    public String authenticateUser(String user, Property[] credentials) throws AuthenticationException, RBACException, RemoteException {
        if (!_realms.authenticate(user, credentials)) {
            throw new AuthenticationException("Authentication failed: User '" + user + "'");
        }

        return createToken(user);
    }

    /**
     * Return the properties encoded in the cryptographic token.
     * 
     * @param token
     *            token
     * @return properties encoded in token
     */
    public Property[] getTokenProperties(String token) throws AuthenticationException, RemoteException {
        return _tokenHandler.parseToken(token);
    }

    public ProxyTicketValidator getProxyTicketValidator(){
        return new ProxyTicketValidator();
    }
    
    public String getTokenFromTicket(String proxyTicket, String serviceURL) throws AuthenticationException, RBACException, RemoteException {
        ProxyTicketValidator pv = getProxyTicketValidator();
        pv.setCasValidateUrl(_validateURL);
        pv.setService(serviceURL);
        pv.setServiceTicket(proxyTicket);

        try {
            pv.validate();
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed! Proxy ticket invalid!");
        }

        if (pv.isAuthenticationSuccesful()) {
            String user = pv.getUser();

            if (user == null) {
                throw new AuthenticationException("Authentication failed: User" + user + "'");
            }
            return createToken(user);
        } else {
            throw new AuthenticationException("Authentication failed! Proxy ticket authentication faild!");
        }

    }

}
