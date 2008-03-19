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
package org.intalio.tempo.web.controller;

import java.rmi.RemoteException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
  private static final Logger LOG = LogManager.getLogger(SecuredController.class);
  protected final TokenService _tokenService;

  public SecuredController(TokenService tokenService) {
    super();
    _tokenService = tokenService;
  }

  @Override
  protected final ModelAndView showForm(RenderRequest request, RenderResponse response, BindException errors) throws Exception {
    ModelAndView mav = null;

    String proxyTicket = (String) request.getAttribute(TokenService.CAS_PROXY_TICKET);
    ApplicationState state = getApplicationState(request);
    if (state.getCurrentUser() == null) {

      String token = _tokenService.getTokenFromTicket(proxyTicket);
      String[] grantedRoles = new String[0];
      User currentUser = authenticate(token, grantedRoles);
      state.setCurrentUser(currentUser);
      ApplicationState.setCurrentInstance(request, state);
    }
    mav = new ModelAndView("view");
    fillAuthorization(request, mav);
    return mav;
  }

  @Override
  protected final void processFormSubmission(ActionRequest request, ActionResponse response, Object command, BindException errors) throws Exception {
    ApplicationState state = getApplicationState(request);
    User currentUser = state.getCurrentUser();
    if (currentUser != null) {
      super.processFormSubmission(request, response, command, errors);
    }

  }

  protected ModelAndView securedShowForm(RenderRequest request, RenderResponse response, BindException errors) throws Exception {
    return null;
  }

  public static String getCurrentUserName(PortletRequest request) {
    ApplicationState state = ApplicationState.getCurrentInstance(request);
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
