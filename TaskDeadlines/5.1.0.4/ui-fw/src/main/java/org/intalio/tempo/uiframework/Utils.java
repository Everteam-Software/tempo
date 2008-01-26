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
 * $Id: Utils.java 2764 2006-03-16 18:34:41Z ozenzin $
 * $Log:$
 */
package org.intalio.tempo.uiframework;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Contains useful/convenient methods used through-out the code.
 */
public class Utils {

    /**
     * If host address is not specified in <code>endpoint</code> - will assume the same host on which this
     * web-app resides.
     *
     * @param endpoint - any  string confirming the {@link URI} rules.
     * @param request 
     * @return  always return string representing absolute {@link URI}. 
     * @throws URISyntaxException
     */
    public static String resolveEndpoint(String endpoint, HttpServletRequest request) throws URISyntaxException {
        URI uri = new URI(endpoint);
        if (!uri.isAbsolute()) {
            uri = new URI(request.getScheme(), null,
                    request.getServerName(), request.getServerPort(), null, null, null).resolve(uri);
        }

        return uri.toString();
    }
}
