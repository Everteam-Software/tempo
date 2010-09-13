package org.intalio.tempo.portlet;

import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class MockRenderRequest implements RenderRequest{
    public MockRenderRequest(){
        
    }
    HttpServletRequest r;
    public MockRenderRequest(HttpSession s){
        r = new MockHttpServletRequest(s);
    }
    public HttpServletRequest getHttpServletRequest(){
        return r;
    }

    public Object getAttribute(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration getAttributeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAuthType() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getContextPath() {
        // TODO Auto-generated method stub
        return null;
    }

    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration getLocales() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getParameter(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Map getParameterMap() {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration getParameterNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getParameterValues(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public PortalContext getPortalContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public PortletMode getPortletMode() {
        // TODO Auto-generated method stub
        return null;
    }

    public PortletSession getPortletSession() {
        // TODO Auto-generated method stub
        return null;
    }

    public PortletSession getPortletSession(boolean arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public PortletPreferences getPreferences() {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration getProperties(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getProperty(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration getPropertyNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRemoteUser() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRequestedSessionId() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getResponseContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration getResponseContentTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getScheme() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getServerName() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getServerPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Principal getUserPrincipal() {
        // TODO Auto-generated method stub
        return null;
    }

    public WindowState getWindowState() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isPortletModeAllowed(PortletMode arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRequestedSessionIdValid() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSecure() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isUserInRole(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isWindowStateAllowed(WindowState arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public void removeAttribute(String arg0) {
        // TODO Auto-generated method stub
        
    }

    public void setAttribute(String arg0, Object arg1) {
        // TODO Auto-generated method stub
        
    }
}