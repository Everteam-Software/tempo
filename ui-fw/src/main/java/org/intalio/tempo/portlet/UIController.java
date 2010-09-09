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
 */
package org.intalio.tempo.portlet;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.wrappers.PortletRequestWrapper;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.User;
import org.intalio.tempo.web.controller.ActionDef;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractFormController;

/**
 * Controller that dispatches form submission based on "actionName" request
 * parameter.
 * <p>
 * Uses reflection to invoke associated methods on the controller itself, or
 * instantiates an Action class to handle the request.
 */
public abstract class UIController extends AbstractFormController {
    protected static final String ACTION_ID_PARAM = "actionName";

    private static final Log LOG = LogFactory.getLog(UIController.class);

    protected final Map<String, Method> _actionMethods = new HashMap<String, Method>();

    protected final Map<String, Class<Action>> _actionConstructors = new HashMap<String, Class<Action>>();

    protected final Map<String, Collection<String>> _actionGrantedRoles = new HashMap<String, Collection<String>>();

    protected ActionDef _defaultAction;

    protected Collection<ActionDef> _actionDefs;

    public UIController() {
        // Set default command class
        setCommandClass(Object.class);

        // Read actionMethods
        // Action method == Request,
        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (method.getReturnType().equals(ModelAndView.class)) {
                int modifiers = method.getModifiers();
                if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers)) {
                    Class[] params = method.getParameterTypes();
                    if (params.length == 4 && params[0].equals(HttpServletRequest.class) && params[1].equals(HttpServletResponse.class)
                                    && Errors.class.isAssignableFrom(params[3])) {
                        _actionMethods.put(method.getName(), method);
                    }
                }
            }
        }
    }

    @Override
    protected void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
        ModelAndView mav = null;

        String actionName = request.getParameter(ACTION_ID_PARAM);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Form submission:  action=" + actionName);
        }

        if (mav == null) {
            Class<Action> actionClass = _actionConstructors.get(actionName);
            if (actionClass == null) {
                throw new Exception("No method or action class found for action " + actionName + "on " + getClass());
            }
            LOG.debug("Form submission:  action class=" + actionClass);
            Action action = actionClass.newInstance();
            action.setRequest(request);
            action.setResponse(response);
            String[] roles = convertRoles(_actionGrantedRoles.get(actionName));
            action.setRequiredRoles(roles);
            action.setBindErrors(new BindException(new Object(), ""));

            mav = action.doExecution();
        }

        fillAuthorization(new PortletRequestWrapper(request), mav);

        LOG.debug("Form submission:  ViewModel=" + mav);
    }

    protected void fillAuthorization(HttpServletRequest request, ModelAndView mav) {
        ApplicationState state = ApplicationState.getCurrentInstance(request);
        if (state != null && state.getCurrentUser() != null && _actionDefs != null) {
            User user = state.getCurrentUser();
            Map model = mav.getModel();
            for (ActionDef def : _actionDefs) {
                model.put(def.getActionName() + "Authorized", def.isAuthorized(user));
                LOG.debug(def.getActionName() + "Authorized=" + def.isAuthorized(user));
            }
        } else {
            LOG.debug("Form submission:  no available user, state or actionDefs");
        }
    }

    protected void bind(HttpServletRequest request, Object command) {
        ServletRequestDataBinder binder = new ServletRequestDataBinder(command);
        binder.bind(request);
    }

    public Collection<ActionDef> getActionDefs() {
        return _actionDefs;
    }

    public void setActionDefs(Collection<ActionDef> actionDefs) {
        LOG.debug("Set action defs on " + getClass() + " with " + actionDefs);
        _actionDefs = actionDefs;
        initActionDefs();
    }

    private void initActionDefs() {
        if (_actionDefs != null) {
            for (ActionDef actionDef : _actionDefs) {
                try {
                    Class actionClass = lookupClass(actionDef.getActionClass());
                    _actionConstructors.put(actionDef.getActionName(), actionClass);
                    _actionGrantedRoles.put(actionDef.getActionName(), actionDef.getGrantedRoles());
                } catch (ClassNotFoundException ex) {
                    LOG.fatal(ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    protected Class lookupClass(String className) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            return (Class) Class.forName(className, true, classLoader);
        } catch (Exception ex) {
            return (Class) Class.forName(className);
        }
    }

    public ActionDef getDefaultAction() {
        return _defaultAction;
    }

    public void setDefaultAction(ActionDef defaultAction) {
        _defaultAction = defaultAction;
    }

    protected Action instantiateAction(String actionName) {
        Class<Action> actionClass = _actionConstructors.get(actionName);
        if (actionClass == null) {
            throw new RuntimeException("Could not find class for action " + actionName + '.');
        }
        try {
            return actionClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected Action<Object> instantiateDefaultAction() {
        Action<Object> action = instantiateAction(_defaultAction.getActionName());
        String[] roles = convertRoles(_actionGrantedRoles.get(_defaultAction.getActionName()));
        action.setRequiredRoles(roles);
        return action;
    }

    public static String[] convertRoles(Collection<String> roles) {
        return roles.toArray(new String[roles.size()]);
    }

    public ApplicationState getApplicationState(HttpServletRequestWrapper request) {
        ApplicationState state = ApplicationState.getCurrentInstance(request);
        if (state == null) {
            // WebApplicationContext context =
            // WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            ApplicationContext context = this.getApplicationContext();
            state = (ApplicationState) context.getBean("applicationState");
            if (state == null) {
                throw new IllegalStateException("Missing 'applicationState' object in Spring context");
            }
            try {
                state = state.getClass().newInstance();
            } catch (Exception e) {
                LOG.error("Unable to clone application state", e);
            }
            ApplicationState.setCurrentInstance(request, state);
        }
        return state;
    }
}
