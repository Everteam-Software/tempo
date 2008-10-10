/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.intalio.tempo.web;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.UserTransaction;

import org.alfresco.config.ConfigService;
import org.alfresco.i18n.I18NUtil;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.TempFileProvider;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.portlet.AlfrescoDefaultViewSelector;
import org.alfresco.web.app.servlet.AuthenticationHelper;
import org.alfresco.web.app.servlet.AuthenticationStatus;
import org.alfresco.web.bean.ErrorBean;
import org.alfresco.web.bean.FileUploadBean;
import org.alfresco.web.bean.LoginBean;
import org.alfresco.web.bean.NavigationBean;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.User;
import org.alfresco.web.config.ClientConfigElement;
import org.alfresco.web.config.LanguagesConfigElement;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.portlet.MyFacesGenericPortlet;
import org.apache.myfaces.portlet.PortletUtil;
import org.springframework.web.context.WebApplicationContext;

import com.liferay.portal.service.UserServiceUtil;

/**
 * Class to extend the MyFacesGenericPortlet to provide behaviour specific to
 * Alfresco web client. Handles upload of multi-part forms through a JSR-168
 * Portlet, generic error handling and session login authentication.
 * 
 * @author Gavin Cornwell, Kevin Roast
 */
public class AlfrescoFacesPortlet extends MyFacesGenericPortlet {
    private static final String PREF_ALF_USERNAME = "_alfUserName";
    private static final String SESSION_LAST_VIEW_ID = "_alfLastViewId";

    private static final String ERROR_PAGE_PARAM = "error-page";
    private static final String ERROR_OCCURRED = "error-occurred";
    private List<String> m_languages;
    private static Log logger = LogFactory.getLog(AlfrescoFacesPortlet.class);

    private String loginPage = null;
    private String errorPage = null;

    /**
     * Called by the portlet container to allow the portlet to process an action
     * request.
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        Application.setInPortalServer(true);

        // Set the current locale
        I18NUtil.setLocale(Application.getLanguage(request.getPortletSession()));

        boolean isMultipart = PortletFileUpload.isMultipartContent(request);

        try {
            // NOTE: Due to filters not being called within portlets we can not
            // make use
            // of the MyFaces file upload support, therefore we are using a pure
            // portlet request/action to handle file uploads until there is a
            // solution.

            if (isMultipart) {
                if (logger.isDebugEnabled())
                    logger.debug("Handling multipart request...");

                PortletSession session = request.getPortletSession();

                // get the file from the request and put it in the session
                DiskFileItemFactory factory = new DiskFileItemFactory();
                PortletFileUpload upload = new PortletFileUpload(factory);
                List<FileItem> fileItems = upload.parseRequest(request);
                Iterator<FileItem> iter = fileItems.iterator();
                FileUploadBean bean = new FileUploadBean();
                while (iter.hasNext()) {
                    FileItem item = iter.next();
                    String filename = item.getName();
                    if (item.isFormField() == false) {
                        if (logger.isDebugEnabled())
                            logger.debug("Processing uploaded file: " + filename);

                        // workaround a bug in IE where the full path is
                        // returned
                        // IE is only available for Windows so only check for
                        // the Windows path separator
                        int idx = filename.lastIndexOf('\\');

                        if (idx == -1) {
                            // if there is no windows path separator check for
                            // *nix
                            idx = filename.lastIndexOf('/');
                        }

                        if (idx != -1) {
                            filename = filename.substring(idx + File.separator.length());
                        }

                        File tempFile = TempFileProvider.createTempFile("alfresco", ".upload");
                        item.write(tempFile);
                        bean.setFile(tempFile);
                        bean.setFileName(filename);
                        bean.setFilePath(tempFile.getAbsolutePath());
                        session.setAttribute(FileUploadBean.FILE_UPLOAD_BEAN_NAME, bean, PortletSession.PORTLET_SCOPE);
                    }
                }

                // Set the VIEW_ID parameter to tell the faces portlet bridge to
                // treat the request
                // as a JSF request, this will send us back to the previous page
                // we came from.
                String lastViewId = (String) request.getPortletSession().getAttribute(SESSION_LAST_VIEW_ID);
                if (lastViewId != null) {
                    response.setRenderParameter(VIEW_ID, lastViewId);
                }
            } else {
                User user = (User) request.getPortletSession().getAttribute(AuthenticationHelper.AUTHENTICATION_USER);
                if (user != null) {
                    // setup the authentication context
                    try {
                        WebApplicationContext ctx = (WebApplicationContext) getPortletContext().getAttribute(
                                        WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
                        AuthenticationService auth = (AuthenticationService) ctx.getBean("AuthenticationService");
                        auth.validate(user.getTicket());

                        // save last username into portlet preferences, get from
                        // LoginBean state
                        LoginBean loginBean = (LoginBean) request.getPortletSession().getAttribute(AuthenticationHelper.LOGIN_BEAN);
                        if (loginBean != null) {
                            // TODO: Need to login to the Portal to get a user
                            // here to store prefs against
                            // so not really a suitable solution as they get
                            // thrown away at present!
                            // Also would need to store prefs PER user - so auto
                            // login for each...?
                            String oldValue = request.getPreferences().getValue(PREF_ALF_USERNAME, null);
                            if (oldValue == null || oldValue.equals(loginBean.getUsernameInternal()) == false) {
                                if (request.getPreferences().isReadOnly(PREF_ALF_USERNAME) == false) {
                                    request.getPreferences().setValue(PREF_ALF_USERNAME, loginBean.getUsernameInternal());
                                    request.getPreferences().store();
                                }
                            }
                        }

                        // do the normal JSF processing
                        super.processAction(request, response);
                    } catch (AuthenticationException authErr) {
                        // remove User object as it's now useless
                        request.getPortletSession().removeAttribute(AuthenticationHelper.AUTHENTICATION_USER);
                    }
                } else {
                    // do the normal JSF processing as we may be on the login
                    // page
                    super.processAction(request, response);
                }
            }
        } catch (Throwable e) {
            if (getErrorPage() != null) {
                handleError(request, response, e);
            } else {
                logger.warn("No error page configured, re-throwing exception");

                if (e instanceof PortletException) {
                    throw (PortletException) e;
                } else if (e instanceof IOException) {
                    throw (IOException) e;
                } else {
                    throw new PortletException(e);
                }
            }
        }
    }

    /**
     * @see org.apache.myfaces.portlet.MyFacesGenericPortlet#facesRender(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    protected void facesRender(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        try {
            User user = (User) request.getPortletSession().getAttribute(AuthenticationHelper.AUTHENTICATION_USER);

            if (user == null) {
                Method getHttpServletRequest = request.getClass().getMethod("getHttpServletRequest");
                HttpServletRequest hsr = (HttpServletRequest) getHttpServletRequest.invoke(request);
                Long userID = (Long) hsr.getSession().getAttribute("USER_ID");
                com.liferay.portal.model.User liferayUser = UserServiceUtil.getUserById(userID);
                String userName = liferayUser.getScreenName();
                logger.debug("user name for alfresco is:" + userName);
                getPortletContext().removeAttribute("loggedin");
                setAuthenticatedUser(request, userName);
            }else{
                logger.debug("User got from portlet session is: " + user.getUserName());
            }
        } catch (Throwable e) {
            // do nothing
        }

        Application.setInPortalServer(true);

        // Set the current locale
        I18NUtil.setLocale(Application.getLanguage(request.getPortletSession()));

        if (request.getParameter(ERROR_OCCURRED) != null) {
            String errorPage = Application.getErrorPage(getPortletContext());

            if (logger.isDebugEnabled())
                logger.debug("An error has occurred, redirecting to error page: " + errorPage);

            response.setContentType("text/html");
            PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(errorPage);
            dispatcher.include(request, response);
        } else {
            WebApplicationContext ctx = (WebApplicationContext) getPortletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            AuthenticationService auth = (AuthenticationService) ctx.getBean("AuthenticationService");
            
            
            // if we have no User object in the session then an HTTP Session
            // timeout must have occured
            // use the viewId to check that we are not already on the login page
            PortletSession session = request.getPortletSession();
            String viewId = request.getParameter(VIEW_ID);
            logger.debug("View ID is:" + viewId);
            // keep track of last view id so we can use it as return page from
            // multi-part requests
            request.getPortletSession().setAttribute(SESSION_LAST_VIEW_ID, viewId);
            User user = (User) request.getPortletSession().getAttribute(AuthenticationHelper.AUTHENTICATION_USER);
            if (user == null && (viewId == null || viewId.equals(getLoginPage()) == false)) {
                if (AuthenticationHelper.portalGuestAuthenticate(ctx, session, auth) == AuthenticationStatus.Guest) {
                    if (logger.isDebugEnabled())
                        logger.debug("Guest access successful.");

                    // perform the forward to the page processed by the Faces
                    // servlet
                    response.setContentType("text/html");
                    request.getPortletSession().setAttribute(PortletUtil.PORTLET_REQUEST_FLAG, "true");

                    // get the start location as configured by the web-client
                    // config
                    ConfigService configService = (ConfigService) ctx.getBean("webClientConfigService");
                    ClientConfigElement configElement = (ClientConfigElement) configService.getGlobalConfig().getConfigElement("client");
                    if (NavigationBean.LOCATION_MYALFRESCO.equals(configElement.getInitialLocation())) {
                        nonFacesRequest(request, response, "/jsp/dashboards/container.jsp");
                    } else {
                        nonFacesRequest(request, response, "/jsp/browse/browse.jsp");
                    }
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug("No valid User login, requesting login page. ViewId: " + viewId);

                    // set last used username as special session value used by
                    // the LoginBean
                    session.setAttribute(AuthenticationHelper.SESSION_USERNAME, request.getPreferences().getValue(PREF_ALF_USERNAME, null));

                    // login page is the default portal page
                    response.setContentType("text/html");
                    request.getPortletSession().setAttribute(PortletUtil.PORTLET_REQUEST_FLAG, "true");
                    nonFacesRequest(request, response);
                }
            } else {
                if (session.getAttribute(AuthenticationHelper.SESSION_INVALIDATED) != null) {
                    // remove the username preference value as explicit logout
                    // was requested by the user
                    if (request.getPreferences().isReadOnly(PREF_ALF_USERNAME) == false) {
                        request.getPreferences().reset(PREF_ALF_USERNAME);
                    }
                    session.removeAttribute(AuthenticationHelper.SESSION_INVALIDATED);
                }

                try {
                    if (user != null) {
                        if (logger.isDebugEnabled())
                            logger.debug("Validating ticket: " + user.getTicket());

                        // setup the authentication context
                        auth.validate(user.getTicket());
                    }

                    // do the normal JSF processing
                    String loggedin = (String) getPortletContext().getAttribute("loggedin");
                    logger.debug("logged in?:" + loggedin);
                    if (loggedin != null && loggedin.equalsIgnoreCase("true") && viewId != null) {
                        super.facesRender(request, response);
                    } else {
                        getPortletContext().setAttribute("loggedin", "true");
                        response.setContentType("text/html");
                        request.getPortletSession().setAttribute(PortletUtil.PORTLET_REQUEST_FLAG, "true");
                        nonFacesRequest(request, response, "/jsp/browse/browse.jsp");
                    }
                } catch (AuthenticationException authErr) {
                    // ticket is no longer valid!
                    if (logger.isDebugEnabled())
                        logger.debug("Invalid ticket, requesting login page.");

                    // remove User object as it's now useless
                    request.getPortletSession().removeAttribute(AuthenticationHelper.AUTHENTICATION_USER);

                    // login page is the default portal page
                    response.setContentType("text/html");
                    request.getPortletSession().setAttribute(PortletUtil.PORTLET_REQUEST_FLAG, "true");
                    nonFacesRequest(request, response);
                } catch (Throwable e) {
                    if (getErrorPage() != null) {
                        handleError(request, response, e);
                    } else {
                        logger.warn("No error page configured, re-throwing exception");

                        if (e instanceof PortletException) {
                            throw (PortletException) e;
                        } else if (e instanceof IOException) {
                            throw (IOException) e;
                        } else {
                            throw new PortletException(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles errors that occur during a process action request
     */
    private void handleError(ActionRequest request, ActionResponse response, Throwable error) throws PortletException, IOException {
        // get the error bean from the session and set the error that occurred.
        PortletSession session = request.getPortletSession();
        ErrorBean errorBean = (ErrorBean) session.getAttribute(ErrorBean.ERROR_BEAN_NAME, PortletSession.PORTLET_SCOPE);
        if (errorBean == null) {
            errorBean = new ErrorBean();
            session.setAttribute(ErrorBean.ERROR_BEAN_NAME, errorBean, PortletSession.PORTLET_SCOPE);
        }
        errorBean.setLastError(error);

        response.setRenderParameter(ERROR_OCCURRED, "true");
    }

    /**
     * Handles errors that occur during a render request
     */
    private void handleError(RenderRequest request, RenderResponse response, Throwable error) throws PortletException, IOException {
        // get the error bean from the session and set the error that occurred.
        PortletSession session = request.getPortletSession();
        ErrorBean errorBean = (ErrorBean) session.getAttribute(ErrorBean.ERROR_BEAN_NAME, PortletSession.PORTLET_SCOPE);
        if (errorBean == null) {
            errorBean = new ErrorBean();
            session.setAttribute(ErrorBean.ERROR_BEAN_NAME, errorBean, PortletSession.PORTLET_SCOPE);
        }
        errorBean.setLastError(error);

        // if the faces context is available set the current view to the browse
        // page
        // so that the error page goes back to the application (rather than
        // going back
        // to the same page which just throws the error again meaning we can
        // never leave
        // the error page)
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            ViewHandler viewHandler = context.getApplication().getViewHandler();
            // TODO: configure the portlet error return page
            UIViewRoot view = viewHandler.createView(context, "/jsp/browse/browse.jsp");
            context.setViewRoot(view);
        }

        // get the error page and include that instead
        String errorPage = getErrorPage();

        if (logger.isDebugEnabled())
            logger.debug("An error has occurred, redirecting to error page: " + errorPage);

        response.setContentType("text/html");
        PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(errorPage);
        dispatcher.include(request, response);
    }

    /**
     * @see org.apache.myfaces.portlet.MyFacesGenericPortlet#setDefaultViewSelector()
     */
    protected void setDefaultViewSelector() throws UnavailableException {
        super.setDefaultViewSelector();
        if (this.defaultViewSelector == null) {
            this.defaultViewSelector = new AlfrescoDefaultViewSelector();
        }
    }

    /**
     * @return Retrieves the configured login page
     */
    private String getLoginPage() {
        if (this.loginPage == null) {
            this.loginPage = Application.getLoginPage(getPortletContext());
        }

        return this.loginPage;
    }

    /**
     * @return Retrieves the configured error page
     */
    private String getErrorPage() {
        if (this.errorPage == null) {
            this.errorPage = Application.getErrorPage(getPortletContext());
        }

        return this.errorPage;
    }

    private void setAuthenticatedUser(PortletRequest req, String userName) {

        WebApplicationContext ctx = (WebApplicationContext) getPortletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        ServiceRegistry serviceRegistry = (ServiceRegistry) ctx.getBean(ServiceRegistry.SERVICE_REGISTRY);
        TransactionService transactionService = serviceRegistry.getTransactionService();
        NodeService nodeService = serviceRegistry.getNodeService();

        AuthenticationComponent authComponent = (AuthenticationComponent) ctx.getBean("authenticationComponent");
        AuthenticationService authService = (AuthenticationService) ctx.getBean("authenticationService");
        PersonService personService = (PersonService) ctx.getBean("personService");

        // Get a list of the available locales
        ConfigService configServiceService = (ConfigService) ctx.getBean("webClientConfigService");
        LanguagesConfigElement configElement = (LanguagesConfigElement) configServiceService.getConfig("Languages").getConfigElement(
                        LanguagesConfigElement.CONFIG_ELEMENT_ID);

        m_languages = configElement.getLanguages();

        // Set up the user information
        UserTransaction tx = transactionService.getUserTransaction();
        NodeRef homeSpaceRef = null;
        User user;
        try {
            tx.begin();
            // Set the authentication
            authComponent.setCurrentUser(userName);
            user = new User(userName, authService.getCurrentTicket(), personService.getPerson(userName));
            homeSpaceRef = (NodeRef) nodeService.getProperty(personService.getPerson(userName), ContentModel.PROP_HOMEFOLDER);
            if (homeSpaceRef == null) {
                logger.warn("Home Folder is null for user '" + userName + "', using company_home.");
                homeSpaceRef = (NodeRef) nodeService.getRootNode(Repository.getStoreRef());
            }
            user.setHomeSpaceId(homeSpaceRef.getId());
            tx.commit();
        } catch (Throwable ex) {
            logger.error(ex);

            try {
                tx.rollback();
            } catch (Exception ex2) {
                logger.error("Failed to rollback transaction", ex2);
            }

            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException("Failed to set authenticated user", ex);
            }
        }

        // Store the user
        req.getPortletSession().setAttribute(AuthenticationHelper.AUTHENTICATION_USER, user);
        req.getPortletSession().setAttribute(LoginBean.LOGIN_EXTERNAL_AUTH, Boolean.TRUE);

        logger.debug("...authenticated user is: " + user.getUserName() + user.getTicket());
    }

}
