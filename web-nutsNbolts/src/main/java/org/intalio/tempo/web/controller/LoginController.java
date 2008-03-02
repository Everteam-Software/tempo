/**
 * Copyright (C) 2006, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id$
 * $Log$
 *//*
package org.intalio.tempo.web.controller;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.util.StringArrayUtils;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.Constants;
import org.intalio.tempo.web.User;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public class LoginController extends UIController {
    private static final String AUTO_LOGIN_ID = "autoLogin";

    private static final String SINGLE_LOGIN_ID = "singleLogin";

    private static final Logger LOG = LogManager.getLogger(LoginController.class);

    protected final TokenService _tokenService;

    private List<String> _grantedRoles = new ArrayList<String>();

    private ModelAndView _redirectSucessfulLogin;

    public LoginController(TokenService tokenService, String redirectSuccessfulLogin) {
        super();
        _tokenService = tokenService;
        _redirectSucessfulLogin = new ModelAndView(new RedirectView(redirectSuccessfulLogin));
    }

    protected void clearAutoLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        clearCookie(AUTO_LOGIN_ID, request, response);
    }

    protected void clearSingleLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        clearCookie(SINGLE_LOGIN_ID, request, response);
    }

    protected void clearCookie(String cookieName, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Cookie newCookie = new Cookie(cookieName, null);
        newCookie.setMaxAge(0);
        newCookie.setPath("/");
        response.addCookie(newCookie);
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

    protected User checkAutoLogin(HttpServletRequest request) throws Exception {
        return checkToken(request, AUTO_LOGIN_ID);
    }

    protected User checkSingleLogin(HttpServletRequest request) throws Exception {
        return checkToken(request, SINGLE_LOGIN_ID);
    }

    protected User checkToken(HttpServletRequest request, String cookieName) throws Exception {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Got cookie: name=" + cookie.getName() + " value=" + cookie.getValue());
                    }
                    try {
                        String token = cookie.getValue();
                        if (token == null && token.trim().length() == 0) {
                            return null;
                        }
                        Property[] props = _tokenService.getTokenProperties(token);
                        String name = extractUser(props);
                        String[] roles = extractRoles(props);
                        return new User(name, roles, token);
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        return null;
                    }
                }
            }
        }
        return null;
    }

    protected void setAutoLoginCookie(HttpServletResponse response, String token) throws Exception {
        Cookie cookie = new Cookie(AUTO_LOGIN_ID, token);
        cookie.setMaxAge(60*60*24*365); // one year
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    protected void setSingleLoginCookie(HttpServletResponse response, String token) throws Exception {
        Cookie cookie = new Cookie(SINGLE_LOGIN_ID, token);
        cookie.setMaxAge(-1); // not persistent, kept until web browser exits
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public List<String> getGrantedRoles() {
        return _grantedRoles;
    }

    public void setGrantedRoles(List<String> roles) {
        _grantedRoles = roles;
    }

    protected User authenticate(String username, String password, String[] grantedRoles) throws SecurityException {
        try {
            String token = _tokenService.authenticateUser(username, password);
            if (token == null) {
                throw new IllegalStateException("Empty token returned from token service");
            }
            return authenticate(token, grantedRoles);
        } catch (Exception ex) {
            throw new SecurityException(ex);
        }
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
                throw new SecurityException("User does not have one of the following role: "
                        + StringArrayUtils.toCommaDelimited(grantedRoles));
            }
            LOG.debug("User: " + user);
            return user;
        } catch (AuthenticationException ex) {
            throw new SecurityException(ex);
        } catch (RemoteException ex) {
            throw new SecurityException(ex);
        }
    }

    protected User authenticate(String username, String password, Errors errors) {
        try {
            LOG.debug("Login user=" + username);
            User currentUser = authenticate(username, password, convertRoles(_grantedRoles));

            return currentUser;
        } catch (SecurityException e) {
            LOG.error("Error during login", e);
            errors.reject("org_intalio_tempo_web_controller_login_loginError", "The user identifier or password you provided is invalid");
        }
        return null;
    }

    // @note(alex) Called by reflection - see UIController
    @SuppressWarnings("unchecked")
    public ModelAndView logIn(HttpServletRequest request, HttpServletResponse response, LoginCommand login,
            BindException errors) throws Exception {
        // Checking whether logged in
        ApplicationState state = getApplicationState(request);
        if (state.getCurrentUser() != null) {
            return _redirectSucessfulLogin;
        }
        if (!errors.hasErrors()) {
            User user = authenticate(login.getUsername(), login.getPassword(), errors);

            if (user != null) {
                state.setCurrentUser(user);

                if (login.isAutoLogin()) {
                    // set autoLogin
                    setAutoLoginCookie(response, user.getToken());
                }
                setSingleLoginCookie(response, user.getToken());
                String prevAction = state.getPreviousAction();
                if (prevAction == null) {
                    return _redirectSucessfulLogin;
                } else {
                    state.setPreviousAction(null);
                    return new ModelAndView(new RedirectView(prevAction));
                }
            }
        }
        Map model = errors.getModel();
        model.put("login", login);

        return new ModelAndView(Constants.LOGIN_VIEW, model);
    }

    private ApplicationState getApplicationState(HttpServletRequest request) {
        ApplicationState state = ApplicationState.getCurrentInstance(request);
        if (state == null) {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            state = (ApplicationState) context.getBean("applicationState");
            try {
                state = state.getClass().newInstance();
            } catch (Exception e) {
                LOG.error("Unable to clone application state", e);
            }
            ApplicationState.setCurrentInstance(request, state);
        }
        return state;
    }

    // @note(alex) Called by reflection - see UIController
    public ModelAndView logOut(HttpServletRequest request, HttpServletResponse response, LoginCommand login,
            BindException errors) throws Exception {
        ApplicationState state = getApplicationState(request);
        if (state != null) {
            if (state.getCurrentUser() != null) LOG.debug("Logout: user=" + state.getCurrentUser().getName());
            state.setCurrentUser(null);
            state.setPreviousAction(null);
            clearAutoLogin(request, response);
            clearSingleLogin(request, response);
        }
        Map model = new HashMap();
        model.put("login", new LoginCommand());

        return new ModelAndView(Constants.LOGIN_VIEW, model);
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors)
            throws Exception {
        ApplicationState state = getApplicationState(request);
        LOG.debug("showForm() state=" + state);

        // Checking whether logged in
        User user = null;
        if (state.getCurrentUser() != null) {
            user = state.getCurrentUser();
        }

        if (user == null) user = checkSingleLogin(request);
        if (user == null) user = checkAutoLogin(request);
        
        if (user == null) {
            // do login
            // handle login from HTTP request
            String username = request.getParameter(LoginValidator.USERNAME_PARAM);
            String password = request.getParameter(LoginValidator.PASSWORD_PARAM);

            // LOG.debug("username: " + username + " password: " + password);
            if (username != null && !"".equals(username) && password != null) {
                user = authenticate(username, password, errors);
            }
        }

        if (user != null) {
            LOG.debug("User authenticated: " + user.getName());
            state.setCurrentUser(user);
            String prevAction = state.getPreviousAction();
            if (prevAction == null) {
                LOG.debug("Redirect after succesful login");
                return _redirectSucessfulLogin;
            } else {
                LOG.debug("Redirect after succesful login: " + prevAction);
                state.setPreviousAction(null);
                return new ModelAndView(new RedirectView(prevAction));
            }
        }
        LOG.debug("" + errors);

        Map model = errors.getModel();
        model.put("login", new LoginCommand());

        return new ModelAndView(Constants.LOGIN_VIEW, model);
    }

    public static class LoginValidator implements org.springframework.validation.Validator {
        private static final String USERNAME_PARAM = "username";

        private static final String PASSWORD_PARAM = "password";

        *//**
         * Minimal length of the component value
         *//*
        private static final int USERNAME_MIN_LENGTH = 0;

        *//**
         * Maximum length of the component value
         *//*
        private static final int USERNAME_MAX_LENGTH = 30;

        *//**
         * Minimum text length
         *//*
        private static final int PASSWORD_MIN_LENGTH = 0;

        *//**
         * Maximum text length
         *//*
        private static final int PASSWORD_MAX_LENGTH = 12;

        public void validate(Object obj, Errors errors) {
            LOG.debug("Validate: " + obj + " " + errors);
            if (obj instanceof LoginCommand) {
                LoginCommand loginCommand = (LoginCommand) obj;
    
                // validate username
                if (StringUtils.isEmpty(loginCommand.getUsername())) {
                    errors.rejectValue(USERNAME_PARAM, "com_intalio_bpms_console_username_required");
                } else if (loginCommand.getUsername().length() < USERNAME_MIN_LENGTH) {
                    errors.rejectValue(USERNAME_PARAM, "com_intalio_bpms_console_username_less",
                            new Object[] { USERNAME_MIN_LENGTH }, null);
                } else if (loginCommand.getUsername().length() > USERNAME_MAX_LENGTH) {
                    errors.rejectValue(USERNAME_PARAM, "com_intalio_bpms_console_username_more",
                            new Object[] { USERNAME_MAX_LENGTH }, null);
                }
    
                // validate password
                if (StringUtils.isEmpty(loginCommand.getPassword())) {
                    errors.rejectValue(PASSWORD_PARAM, "com_intalio_bpms_console_password_required");
                } else if (loginCommand.getPassword().length() < PASSWORD_MIN_LENGTH) {
                    errors.rejectValue(PASSWORD_PARAM, "com_intalio_bpms_console_password_less",
                            new Object[] { PASSWORD_MIN_LENGTH }, null);
                } else if (loginCommand.getPassword().length() > PASSWORD_MAX_LENGTH) {
                    errors.rejectValue(PASSWORD_PARAM, "com_intalio_bpms_console_password_more",
                            new Object[] { PASSWORD_MAX_LENGTH }, null);
                }
            }
        }

        public boolean supports(Class clazz) {
            return LoginCommand.class.equals(clazz);
        }
    }

}*/