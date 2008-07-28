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

    public static final String TOKEN_PREFIX = "tokenws";

    public static final QName AUTHENTICATE_USER = new QName(TOKEN_NS.getNamespaceURI(), "authenticateUser", TOKEN_PREFIX);

    public static final QName AUTHENTICATE_USER_WITH_CREDENTIALS = new QName(TOKEN_NS.getNamespaceURI(), "authenticateUserWithCredentials", TOKEN_PREFIX);

    public static final QName AUTHENTICATE_USER_RESPONSE = new QName(TOKEN_NS.getNamespaceURI(), "authenticateUserResponse", TOKEN_PREFIX);

    public static final QName GET_TOKEN_PROPERTIES = new QName(TOKEN_NS.getNamespaceURI(), "getTokenProperties", TOKEN_PREFIX);

    public static final QName GET_TOKEN_PROPERTIES_RESPONSE = new QName(TOKEN_NS.getNamespaceURI(), "getTokenPropertiesResponse", TOKEN_PREFIX);

    public static final QName USER = new QName(TOKEN_NS.getNamespaceURI(), "user", TOKEN_PREFIX);

    public static final QName PASSWORD = new QName(TOKEN_NS.getNamespaceURI(), "password", TOKEN_PREFIX);

    public static final QName CREDENTIALS = new QName(TOKEN_NS.getNamespaceURI(), "credentials", TOKEN_PREFIX);

    public static final QName TOKEN = new QName(TOKEN_NS.getNamespaceURI(), "token", TOKEN_PREFIX);

    public static final QName GETTOKEN_FROMTICKET = new QName(TOKEN_NS.getNamespaceURI(), "getTokenFromTicket", TOKEN_PREFIX);

    public static final QName TICKET = new QName(TOKEN_NS.getNamespaceURI(), "ticket", TOKEN_PREFIX);

    public static final QName PROXY_TICKET = new QName(TOKEN_NS.getNamespaceURI(), "proxyTicket", TOKEN_PREFIX);

    public static final QName SERVICE_URL = new QName(TOKEN_NS.getNamespaceURI(), "serviceURL", TOKEN_PREFIX);
}
