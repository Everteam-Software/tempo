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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.security.util.IdentifierUtils;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.util.StringArrayUtils;
import org.intalio.tempo.security.util.TimeExpirationMap;

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
    // should we try to put the password in the token itself
    boolean _passwordAsAProperty;
    // should we NOT put the roles in the token, and cache them in memory instead
    boolean cacheRoles = false;

    // check every minute, expire after one hour
    TimeExpirationMap userAndRoles = new TimeExpirationMap(1000 * 60 * 30, 1000 * 60);

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

    public final boolean isCacheRoles() {
        return cacheRoles;
    }

    public final void setCacheRoles(boolean cacheRoles) {
        this.cacheRoles = cacheRoles;
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

        // place session information in token
        Property userProp = new Property(AuthenticationConstants.PROPERTY_USER, user);
        Property issueProp = new Property(AuthenticationConstants.PROPERTY_ISSUED, Long.toString(System.currentTimeMillis()));

        // add all user properties to token properties
        Property[] userProps = _realms.userProperties(user);
        List<Property> props = new ArrayList<Property>();
        
        props.add(userProp);
        props.add(issueProp);
        if(!cacheRoles) {
            String[] roles = _realms.authorizedRoles(user);
            props.add(new Property(AuthenticationConstants.PROPERTY_ROLES, StringArrayUtils.toCommaDelimited(roles)));
        }
        if((password != null && _passwordAsAProperty)) 
            props.add(new Property(AuthenticationConstants.PROPERTY_PASSWORD, password));
        for(Property p : userProps) props.add(p);
        return _tokenHandler.createToken(props.toArray(new Property[props.size()]));
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
        Property[] props = _tokenHandler.parseToken(token);
        Map<String, Object> map = PropertyUtils.toMap(props);
        String user = ((Property) map.get(AuthenticationConstants.PROPERTY_USER)).getValue().toString();
        Property rolesForUser = null;
        if(this.cacheRoles) rolesForUser = (Property) userAndRoles.get(user);
        if(rolesForUser!=null) {
            // if we have the roles in cache
            map.put(AuthenticationConstants.PROPERTY_ROLES, rolesForUser);
        } else {
            try {
                String[] roles = _realms.authorizedRoles(user);
                rolesForUser = new Property(AuthenticationConstants.PROPERTY_ROLES, StringArrayUtils.toCommaDelimited(roles));
            } catch (RBACException e) {
                throw new AuthenticationException("Could not get roles for user:"+user);
            }    
        }

        // cache only if needed
        if(this.cacheRoles) userAndRoles.put(user, rolesForUser);
        
        map.put(AuthenticationConstants.PROPERTY_ROLES, rolesForUser);
        return map.values().toArray(new Property[map.size()]);
    }

    public ProxyTicketValidator getProxyTicketValidator() {
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
