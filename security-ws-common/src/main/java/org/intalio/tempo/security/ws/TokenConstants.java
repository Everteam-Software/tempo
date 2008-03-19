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

import static org.intalio.tempo.security.ws.Constants.TOKEN_NS;

import javax.xml.namespace.QName;

public class TokenConstants {

    public static final QName AUTHENTICATE_USER = new QName(TOKEN_NS.getNamespaceURI(), "authenticateUser");

    public static final QName AUTHENTICATE_USER_WITH_CREDENTIALS =
            new QName(TOKEN_NS.getNamespaceURI(), "authenticateUserWithCredentials");

    public static final QName AUTHENTICATE_USER_RESPONSE =
            new QName(TOKEN_NS.getNamespaceURI(), "authenticateUserResponse");

    public static final QName GET_TOKEN_PROPERTIES = new QName(TOKEN_NS.getNamespaceURI(), "getTokenProperties");

    public static final QName GET_TOKEN_PROPERTIES_RESPONSE =
            new QName(TOKEN_NS.getNamespaceURI(), "getTokenPropertiesResponse");

    public static final QName USER = new QName(TOKEN_NS.getNamespaceURI(), "user");

    public static final QName PASSWORD = new QName(TOKEN_NS.getNamespaceURI(), "password");

    public static final QName CREDENTIALS = new QName(TOKEN_NS.getNamespaceURI(), "credentials");

    public static final QName TOKEN = new QName(TOKEN_NS.getNamespaceURI(), "token");

    public static final QName GETTOKEN_FROMTICKET = new QName(TOKEN_NS.getNamespaceURI(), "getTokenFromTicket");
    
    public static final QName TICKET = new QName(TOKEN_NS.getNamespaceURI(), "ticket");
    
    public static final QName PROXY_TICKET = new QName(TOKEN_NS.getNamespaceURI(), "proxyTicket");
}
