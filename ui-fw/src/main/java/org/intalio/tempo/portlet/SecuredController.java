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
package org.intalio.tempo.portlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.pluto.wrappers.PortletRequestWrapper;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.util.StringArrayUtils;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.User;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.ModelAndView;

public class SecuredController extends UIController {
	private static final Logger LOG = LoggerFactory.getLogger(SecuredController.class);
    protected final TokenService _tokenService;
    protected String _serviceURL;
    private TokenHandler tokenHandler;

    private static final String PROXY_TICKET = "TEMPO_CAS_TICKET";

    public SecuredController(TokenService tokenService) {
        super();
        _tokenService = tokenService;
    }

    public SecuredController(TokenService tokenService, String serviceURL) {
        super();
        _tokenService = tokenService;
        _serviceURL = serviceURL;
    }


	public void setTokenHandler(TokenHandler tokenHandler) {
		this.tokenHandler = tokenHandler;
	}

	@Override
    protected final ModelAndView showForm(RenderRequest request, RenderResponse response, BindException errors) throws Exception {
        ModelAndView mav = null;
        Method getHttpServletRequest = request.getClass().getMethod("getHttpServletRequest");
        HttpServletRequest hsr = (HttpServletRequest) getHttpServletRequest.invoke(request);
        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(hsr);
        
        ApplicationState state = getApplicationState(wrapper);
        User currentUser = state.getCurrentUser();

        if (currentUser == null) {
            String ticket = tokenHandler.getTiket(request);
            String token = tokenHandler.getToken(_tokenService, ticket);
            String[] grantedRoles = new String[0];
            currentUser = authenticate(token, grantedRoles);
            state.setCurrentUser(currentUser);
            ApplicationState.setCurrentInstance(wrapper, state);
        }
        
        mav = new ModelAndView("portlet");
        fillAuthorization(wrapper, mav);

        return mav;
    }

    @Override
    protected final void processFormSubmission(ActionRequest request, ActionResponse response, Object command, BindException errors) throws Exception {

    }

    @Override
    protected void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
        PortletRequestWrapper requestWrapper = new PortletRequestWrapper(request);
        ApplicationState state = getApplicationState(requestWrapper);
        User currentUser = state.getCurrentUser();
        if (currentUser != null) {
            super.handleActionRequestInternal(request, response);
        }
    }

    protected ModelAndView securedShowForm(RenderRequest request, RenderResponse response, BindException errors) throws Exception {
        return null;
    }

    public static String getCurrentUserName(PortletRequest request) {
        PortletRequestWrapper requestWrapper = new PortletRequestWrapper(request);
        ApplicationState state = ApplicationState.getCurrentInstance(requestWrapper);
        if (state == null || state.getCurrentUser() == null) {
            return "UnknownUser";
        }
        return state.getCurrentUser().getName();
    }

    @Override
    protected ModelAndView renderFormSubmission(RenderRequest request, RenderResponse response, Object arg2, BindException errors) throws Exception {
        return null;
    }

    protected User authenticate(String token, String[] grantedRoles) throws SecurityException {
        try {
            Property[] props = _tokenService.getTokenProperties(token);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Token properties: " + PropertyUtils.toMap(props));
            }

            String name = extractUser(props);
            String[] roles = extractRoles(props);
            User user = new User(name, roles, token);
            if (grantedRoles.length > 0 && !user.hasOneRoleOf(grantedRoles)) {
                throw new SecurityException("User does not have one of the following role: " + StringArrayUtils.toCommaDelimited(grantedRoles));
            }
            LOG.debug("User: " + user);
            return user;
        } catch (AuthenticationException ex) {
            throw new SecurityException(ex);
        } catch (RemoteException ex) {
            throw new SecurityException(ex);
        }
    }

    private static String extractProperty(String propName, Property[] props) {
        for (Property prop : props) {
            if (propName.equals(prop.getName())) {
                return (String) prop.getValue();
            }
        }
        return null;
    }

    private static String extractUser(Property[] props) {
        return extractProperty(AuthenticationConstants.PROPERTY_USER, props);
    }

    private static String[] extractRoles(Property[] props) {
        String rolesCommaList = extractProperty(AuthenticationConstants.PROPERTY_ROLES, props);
        if (rolesCommaList != null) {
            String[] roleStrings = StringArrayUtils.parseCommaDelimited(rolesCommaList);
            return roleStrings;
        }
        return null;
    }
}
