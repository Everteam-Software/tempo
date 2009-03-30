/**
 * Copyright (c) 2005-2008 Intalio inc.
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
    private String _tmpEndpoint = "http://localhost:8080/ode/processes/completeTask";
    private int _pagingLength;
    private int _refreshTime = 5;
    private int _sessionTimeout = 10;
    private TokenClient _tokenClient;
    private String _baseUrl;
    private String _feedUrl;
    private Boolean _toolbarIcons = Boolean.TRUE;
    private int _ajaxTimeout = 5000;
 
    public String getFeedUrl() {
        return _feedUrl;
    }

    public void setFeedUrl(String url) {
        _feedUrl = url;
    }
    
    public void setAjaxTimeout(int timeout) {
        _ajaxTimeout = timeout;
    }
    
    public int getAjaxTimeout() {
        return _ajaxTimeout;
    }
    
    public void setUseToolbarIcons(Boolean use) {
        _toolbarIcons=use;
    }
    
    public Boolean isUseToolbarIcons() {
        return _toolbarIcons;
    }

    private Configuration() {
    }

    public String getServiceEndpoint() {
        return _serviceEndpoint;
    }
    
    public String getFeedItemBaseUrl() {
        return _baseUrl;
    }
    
    public void setFeedItemBaseUrl(String baseUrl) {
        this._baseUrl = baseUrl;
    }

    public void setServiceEndpoint(String serviceEndpoint) {
        _serviceEndpoint = serviceEndpoint;
    }

    public void setTMPEndpoint(String serviceEndpoint) {
        _tmpEndpoint = serviceEndpoint;
    }

    public String getTMPEndpoint() {
	    return _tmpEndpoint;
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

	public int getSessionTimeout() {
        return _sessionTimeout;
    }

    public void setSessionTimeout(int time) {
        _sessionTimeout = time;
    }
    
    public void setTokenClient(TokenClient tc) {
        _tokenClient = tc;
    }
    
    public TokenClient getTokenClient() {
        return _tokenClient;
    }
}
