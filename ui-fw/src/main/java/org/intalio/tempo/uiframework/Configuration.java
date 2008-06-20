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

import org.intalio.tempo.security.ws.TokenClient;

public class Configuration {

    private static Configuration INSTANCE = new Configuration();

    private String _serviceEndpoint;

    private int _pagingLength;
    
    private int _refreshTime = 5;
    
    private TokenClient _tokenClient;
 
    private Configuration() {
    }

    public String getServiceEndpoint() {
        return _serviceEndpoint;
    }

    public void setServiceEndpoint(String serviceEndpoint) {
        _serviceEndpoint = serviceEndpoint;
    }

    public int getPagingLength() {
        return _pagingLength;
    }

    public void setPagingLength(int pagingLength) {
        _pagingLength = pagingLength;
    }

    public static Configuration getInstance() {
        return INSTANCE;
    }

    public int getRefreshTime() {
        return _refreshTime;
    }

    public void setRefreshTime(int time) {
        _refreshTime = time;
    }
    
    public void setTokenService(TokenClient tc) {
        _tokenClient = tc;
    }
    
    public TokenClient getTokenService() {
        return _tokenClient;
    }
}
