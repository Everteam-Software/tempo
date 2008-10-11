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

import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.token.TokenService;

/**
 * Client web services API for the Token Service.
 */
public class TokenClient implements TokenService {

    String _endpoint;

    /**
     * Create a token service client
     * 
     * @param endpointUrl
     *            endpoint of the token service
     */
    public TokenClient(String endpointUrl) {
        _endpoint = endpointUrl;
    }

    public String authenticateUser(String user, String password) throws AuthenticationException, RBACException, RemoteException {
        OMElement request = element(TokenConstants.AUTHENTICATE_USER);
        request.addChild(elementText(TokenConstants.USER, user));
        request.addChild(elementText(TokenConstants.PASSWORD, password));
        OMParser response = invoke(TokenConstants.AUTHENTICATE_USER.getLocalPart(), request);
        return response.getRequiredString(TokenConstants.TOKEN);
    }

    public String authenticateUser(String user, Property[] credentials) throws AuthenticationException, RBACException, RemoteException {
        OMElement request = element(TokenConstants.AUTHENTICATE_USER_WITH_CREDENTIALS);
        request.addChild(elementText(TokenConstants.USER, user));
        OMElement requestCred = element(TokenConstants.CREDENTIALS);
        for (int i = 0; i < credentials.length; i++) {
            OMElement prop = element(Constants.PROPERTY);
            prop.addChild(elementText(Constants.NAME, credentials[i].getName()));
            prop.addChild(elementText(Constants.VALUE, credentials[i].getValue().toString()));
            requestCred.addChild(prop);
        }
        request.addChild(requestCred);
        OMParser response = invoke(TokenConstants.AUTHENTICATE_USER_WITH_CREDENTIALS.getLocalPart(), request);
        return response.getRequiredString(TokenConstants.TOKEN);
    }

    public Property[] getTokenProperties(String token) throws AuthenticationException, RemoteException {
        OMElement request = element(TokenConstants.GET_TOKEN_PROPERTIES);
        request.addChild(elementText(TokenConstants.TOKEN, token));
        OMParser response = invoke(TokenConstants.GET_TOKEN_PROPERTIES.getLocalPart(), request);
        return response.getProperties(Constants.PROPERTIES);
    }

    protected OMParser invoke(String action, OMElement request) throws AxisFault {
        ServiceClient serviceClient = getServiceClient();
        Options options = serviceClient.getOptions();
        EndpointReference targetEPR = new EndpointReference(_endpoint);
        options.setTo(targetEPR);
        options.setAction(action);
        OMElement response = serviceClient.sendReceive(request);
        return new OMParser(response);
    }

    private static OMElement element(QName name) {
        return OM_FACTORY.createOMElement(name);
    }

    private static OMElement elementText(QName name, String text) {
        OMElement element = OM_FACTORY.createOMElement(name);
        element.setText(text);
        return element;
    }

    public String getTokenFromTicket(String ticket, String serviceURL) throws AuthenticationException, RBACException, RemoteException {
        OMElement request = element(TokenConstants.PROXY_TICKET);
        request.addChild(elementText(TokenConstants.TICKET, ticket));
        request.addChild(elementText(TokenConstants.SERVICE_URL, serviceURL));
        OMParser response = invoke(TokenConstants.GETTOKEN_FROMTICKET.getLocalPart(), request);
        return response.getRequiredString(TokenConstants.TOKEN);
    }

    protected ServiceClient getServiceClient() throws AxisFault {
        return new ServiceClient();
    }
}
