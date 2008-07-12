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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public class Constants {
    public static final String CONFIG_DIR_PROPERTY = "org.intalio.tempo.configDirectory";

    public static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();

    public static final OMNamespace TOKEN_NS =
            OM_FACTORY.createOMNamespace("http://tempo.intalio.org/security/tokenService/", "token");
    public static final OMNamespace RBACQUERY_NS =
        OM_FACTORY.createOMNamespace("http://tempo.intalio.org/security/RBACQueryService/", "RBACQuery");
    protected static final QName PROPERTIES = new QName(TOKEN_NS.getNamespaceURI(), "properties");

    protected static final QName PROPERTY = new QName(TOKEN_NS.getNamespaceURI(), "property");

    public static final QName NAME = new QName(TOKEN_NS.getNamespaceURI(), "name");

    public static final QName VALUE = new QName(TOKEN_NS.getNamespaceURI(), "value");


}
