/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.security.ws;

import static org.intalio.tempo.security.ws.Constants.OM_FACTORY;
import static org.intalio.tempo.security.ws.Constants.TOKEN_NS;
import static org.intalio.tempo.security.ws.TokenConstants.AUTHENTICATE_USER_RESPONSE;
import static org.intalio.tempo.security.ws.TokenConstants.CREDENTIALS;
import static org.intalio.tempo.security.ws.TokenConstants.GET_TOKEN_PROPERTIES_RESPONSE;
import static org.intalio.tempo.security.ws.TokenConstants.PASSWORD;
import static org.intalio.tempo.security.ws.TokenConstants.TOKEN;
import static org.intalio.tempo.security.ws.TokenConstants.USER;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenWS extends BaseWS {
    private static final Logger LOG = LoggerFactory.getLogger(TokenConstants.class);

    public OMElement authenticateUser(OMElement requestEl) throws AxisFault {
        OMParser request = new OMParser(requestEl);
        String user = request.getRequiredString(USER);
        String password = request.getRequiredString(PASSWORD);

        String token;
        try {
            token = _tokenService.authenticateUser(user, password);
        } catch (AuthenticationException except) {
            if (LOG.isDebugEnabled())
                LOG.debug("authenticateUser:\n" + requestEl, except);
            throw AxisFault.makeFault(except);
        } catch (Exception except) {
            if (LOG.isDebugEnabled())
                LOG.debug("authenticateUser:\n" + requestEl, except);
            throw new RuntimeException(except);
        }

        return authenticateUserResponse(token);
    }

    public OMElement authenticateUserWithCredentials(OMElement requestEl) throws AxisFault {
        OMParser request = new OMParser(requestEl);
        String user = request.getRequiredString(USER);
        Property[] credentials = request.getProperties(CREDENTIALS);

        String token;
        try {
            token = _tokenService.authenticateUser(user, credentials);
        } catch (AuthenticationException except) {
            if (LOG.isDebugEnabled())
                LOG.debug("authenticateUserWithCredentials:\n" + requestEl, except);
            throw AxisFault.makeFault(except);
        } catch (Exception except) {
            if (LOG.isDebugEnabled())
                LOG.debug("authenticateUserWithCredentials:\n" + requestEl, except);
            throw new RuntimeException(except);
        }

        return authenticateUserResponse(token);
    }

    public OMElement getTokenProperties(OMElement requestEl) throws AxisFault {
        OMParser request = new OMParser(requestEl);
        String token = request.getRequiredString(TOKEN);

        Property[] props;
        try {
            props = _tokenService.getTokenProperties(token);
        } catch (AuthenticationException except) {
            if (LOG.isDebugEnabled())
                LOG.debug("getTokenProperties:\n" + requestEl, except);
            throw AxisFault.makeFault(except);
        } catch (Exception except) {
            if (LOG.isDebugEnabled())
                LOG.debug("getTokenProperties:\n" + requestEl, except);
            throw new RuntimeException(except);
        }
        return tokenPropertiesResponse(props);
    }

    private OMElement authenticateUserResponse(String token) {
        OMElement response = OM_FACTORY.createOMElement(AUTHENTICATE_USER_RESPONSE);
        OMElement responseToken = OM_FACTORY.createOMElement("token", TOKEN_NS);
        responseToken.setText(token);
        response.addChild(responseToken);
        return response;
    }

    private OMElement tokenPropertiesResponse(Property[] props) {
        OMElement response = OM_FACTORY.createOMElement(GET_TOKEN_PROPERTIES_RESPONSE);
        OMElement responseProperties = OM_FACTORY.createOMElement(Constants.PROPERTIES);
        for (int i = 0; i < props.length; i++) {
            OMElement prop = OM_FACTORY.createOMElement(Constants.PROPERTY);

            OMElement name = OM_FACTORY.createOMElement(Constants.NAME);
            name.setText(props[i].getName());
            prop.addChild(name);

            OMElement value = OM_FACTORY.createOMElement(Constants.VALUE);
            value.setText(props[i].getValue().toString());
            prop.addChild(value);

            responseProperties.addChild(prop);
        }
        response.addChild(responseProperties);
        return response;
    }
    
    public OMElement getToken(OMElement requestEl) throws AxisFault {
        OMParser request = new OMParser(requestEl);
        String user = request.getRequiredString(USER);

        String token;
        try {
            token = _tokenService.getToken(user);
        } catch (AuthenticationException except) {
            if (LOG.isDebugEnabled())
                LOG.debug("authenticateUser:\n" + requestEl, except);
            throw AxisFault.makeFault(except);
        } catch (Exception except) {
            if (LOG.isDebugEnabled())
                LOG.debug("authenticateUser:\n" + requestEl, except);
            throw new RuntimeException(except);
        }

        return authenticateUserResponse(token);
    }
}
