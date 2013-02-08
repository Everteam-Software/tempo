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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.workflow.tms.ITaskManagementServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

    private static Configuration INSTANCE = new Configuration();

    private static String TASK_TAB = "task";
    private static String NOTIFICATION_TAB = "notification";

    private String _serviceEndpoint;
    private String _tmpEndpoint = "http://localhost:8080/ode/processes/completeTask";
    private int _pagingLength;
    private int _refreshTime = 5;
    private int _sessionTimeout = 10;
    private TokenClient _tokenClient;
    private String _baseUrl;
    private String _feedUrl;
    private Boolean _toolbarIcons = Boolean.TRUE;
    private Boolean _claimTaskOnOpen = Boolean.TRUE;
    private int _ajaxTimeout = 5000;
    private Map<String, Map<String, Set<String>>> _toolbarIconSets;
    private Map<String, Set<String>> _bindIconSetToRole;
    private Map<String, Set<String>> _visibleTabs;
    private Logger _log = LoggerFactory.getLogger(Configuration.class);
    private ITaskManagementServiceFactory _tmsFactory;

    public void setTmsFactory(ITaskManagementServiceFactory tmsFactory) {
        this._tmsFactory = tmsFactory;
    }

    public ITaskManagementServiceFactory getTmsFactory() {
        return _tmsFactory;
    }
    
    public Map<String, Set<String>> getVisibleTabs() {
        return _visibleTabs;
    }

    public void setVisibleTabs(Map<String, Set<String>> _visibleTabs) {
        this._visibleTabs = _visibleTabs;
    }

    public Map<String, Set<String>> getBindIconSetToRole() {
        return _bindIconSetToRole;
    }

    public void setBindIconSetToRole(Map<String, Set<String>> _bindIconSetToRole) {
        this._bindIconSetToRole = _bindIconSetToRole;
    }

    public Map<String, Map<String, Set<String>>> getToolbarIconSets() {
        return _toolbarIconSets;
    }

    public void setToolbarIconSets(Map<String, Map<String, Set<String>>> _toolbarIconSets) {
        this._toolbarIconSets = _toolbarIconSets;
    }

    public String[] getTaskIconSetByRole(String[] roles) {
        if (_log.isDebugEnabled()) {
            _log.debug("Get task iconset by Roles:");
            for (int i = 0; i < roles.length; i++) {
                _log.debug(roles[i]);
            }
        }
        HashSet<String> taskIcons = new HashSet<String>();
        HashSet<String> iconSet = getKeysFromValues(_bindIconSetToRole, roles);
        for (String is : iconSet) {
            for (int i = 0; i < roles.length; i++) {
                Map<String, Set<String>> iconSetByRole = _toolbarIconSets.get(is);
                taskIcons.addAll(iconSetByRole.get(TASK_TAB));
            }
        }
        return (String[]) taskIcons.toArray(new String[taskIcons.size()]);
    }
    
    public String[] getTabSetByRole(String[] roles) {
        if (_log.isDebugEnabled()) {
            _log.debug("Get tab set by Roles:");
            for (int i = 0; i < roles.length; i++) {
                _log.debug(roles[i]);
            }
        }
        LinkedHashSet<String> tabs = new LinkedHashSet<String>();
        HashSet<String> roleSet = getKeysFromValues(_bindIconSetToRole, roles);
        for (String rs : roleSet) {
            for (int i = 0; i < roles.length; i++) {
                Set<String> tabSetByRole = _visibleTabs.get(rs);
                tabs.addAll(tabSetByRole);
            }
        }
        return (String[]) tabs.toArray(new String[tabs.size()]);
    }

    public String[] getNotificationIconSetByRole(String[] roles) {
        if (_log.isDebugEnabled()) {
            _log.debug("Get notification iconset by Roles:");
            for (int i = 0; i < roles.length; i++) {
                _log.debug(roles[i]);
            }
        }
        HashSet<String> taskIcons = new HashSet<String>();
        HashSet<String> iconSet = getKeysFromValues(_bindIconSetToRole, roles);
        for (String is : iconSet) {
            for (int i = 0; i < roles.length; i++) {
                Map<String, Set<String>> iconSetByRole = _toolbarIconSets.get(is);
                taskIcons.addAll(iconSetByRole.get(NOTIFICATION_TAB));
            }
        }
        return (String[]) taskIcons.toArray(new String[taskIcons.size()]);
    }

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

    public Boolean getClaimTaskOnOpen() {
        return _claimTaskOnOpen;
    }

    public void setClaimTaskOnOpen(Boolean claimTaskOnOpen) {
        _claimTaskOnOpen = claimTaskOnOpen;
    }

    public void setUseToolbarIcons(Boolean use) {
        _toolbarIcons = use;
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

    private HashSet<String> getKeysFromValues(Map<String, Set<String>> hm, String[] values) {
        HashSet<String> list = new HashSet<String>();
        for (int i = 0; i < values.length; i++) {
            for (String o : hm.keySet()) {
                if (hm.get(o).contains(values[i])) {
                    list.add(o);
                }
            }
        }
        return list;
    }
}
