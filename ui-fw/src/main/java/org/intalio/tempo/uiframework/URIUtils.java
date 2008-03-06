/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: XFormsManager.java 2764 2006-03-16 18:34:41Z ozenzin $
 * $Log:$
 */
package org.intalio.tempo.uiframework;

import java.net.URI;
import java.net.URISyntaxException;

import javax.portlet.PortletRequest;

public class URIUtils {
    
    public static String resolveURI(PortletRequest request, String endpoint) 
        throws URISyntaxException
    {
        URI uri = new URI(endpoint);
        if (!uri.isAbsolute()) {
            uri = new URI(request.getScheme(), null, request.getServerName(), request.getServerPort(), 
                          null, null, null).resolve(uri);
        }
        return uri.toString();
    }
    
}
