package org.intalio.tempo.uiframework.filter;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.util.StringArrayUtils;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.User;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import edu.yale.its.tp.cas.client.CASReceipt;
import edu.yale.its.tp.cas.proxy.ProxyTicketReceptor;

/**
 * @author Arthur
 */
public class CASRBACFilter implements Filter {
    private static final Logger LOG = LogManager.getLogger("tempo.security");

    private static final String TEMPO_CONFIG_DIRECTORY = "org.intalio.tempo.configDirectory";
    private static final String SECURITY_CONFIG_XML = "securityConfig.xml";

    private static final String TOKEN_SERVICE = "tokenService";

    private static final String CAS_RECEIPT = "edu.yale.its.tp.cas.client.filter.receipt";
    private static final String SERVICE_URL = "edu.yale.its.tp.cas.client.filter.serviceUrl";
    private static final String LOGOUT_URL = "edu.yale.its.tp.cas.client.filter.logoutUrl";

    private static final String APPLICATION_STATE = "applicationState";

    private static final String LOGOUT_PATH = "/login.htm";
    private static final String LOGOUT_METHOD = "post";
    private static final String LOGOUT_ACTION_NAME = "actionName";
    private static final String LOGOUT_ACTION_VALUE = "logOut";

    private static final String SESSION_APPLICATION_STATE = "APPLICATION_STATE";

    private FilterConfig _filterConfig;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.debug(">> CAS Filter");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpSession session = httpServletRequest.getSession();

        if (LOGOUT_PATH.equalsIgnoreCase(httpServletRequest.getServletPath()) && LOGOUT_METHOD.equalsIgnoreCase(httpServletRequest.getMethod())
                        && LOGOUT_ACTION_VALUE.equalsIgnoreCase(httpServletRequest.getParameter(LOGOUT_ACTION_NAME))) {

            doSignOut(session, (HttpServletResponse) response);

        } else {

            if (null == httpServletRequest.getSession().getAttribute(SESSION_APPLICATION_STATE)) {
                doSignIn(httpServletRequest, session);
            }
            chain.doFilter(request, response);
        }

    }

    private void doSignIn(HttpServletRequest httpServletRequest, HttpSession session) throws RemoteException {
        LOG.info("signing in with CAS....");
        String serviceURL = _filterConfig.getInitParameter(SERVICE_URL);

        TokenService tokenService = Configuration.getInstance().getTokenClient();
        String pgtIou = null;
        CASReceipt CASreceipt = (CASReceipt) session.getAttribute(CAS_RECEIPT);
        if (CASreceipt != null)
            pgtIou = CASreceipt.getPgtIou();
        if (pgtIou != null) {
            try {
                String proxyTicket = ProxyTicketReceptor.getProxyTicket(pgtIou, serviceURL);
                ApplicationState state = ApplicationState.getCurrentInstance(httpServletRequest);
                if (state == null) {
                    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());
                    state = (ApplicationState) context.getBean(APPLICATION_STATE);
                    try {
                        state = state.getClass().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (state.getCurrentUser() == null) {
                    String token;
                    try {
                        token = tokenService.getTokenFromTicket(proxyTicket, serviceURL);
                    } catch (AuthenticationException e) {
                        throw new RuntimeException("Could not get token!", e);
                    } catch (RBACException e) {
                        throw new RuntimeException("Could not get token!", e);
                    }
                    String[] grantedRoles = new String[0];
                    User currentUser = authenticate(tokenService, token, grantedRoles);
                    state.setCurrentUser(currentUser);
                }
                ApplicationState.setCurrentInstance(httpServletRequest, state);
            } catch (IOException e) {
                throw new RuntimeException("Could not get the proxy ticket!", e);
            }
        }
    }

    private void doSignOut(HttpSession session, HttpServletResponse httpServletResponse) throws IOException {
        LOG.info("signing out from CAS..");
        session.invalidate();
        String logoutURL = _filterConfig.getInitParameter(LOGOUT_URL);
        httpServletResponse.sendRedirect(logoutURL);
        LOG.info("user has been signed out successfully from Intalio");
    }

    public void destroy() {

    }

    public void init(FilterConfig filterConfig) {

        _filterConfig = filterConfig;
    }

    private static String extractProperty(String propName, Property[] props) {
        String result = null;

        for (Property prop : props) {
            if (propName.equals(prop.getName())) {
                result = (String) prop.getValue();
            }
        }

        return result;
    }

    private static String extractUser(Property[] props) {
        return extractProperty(AuthenticationConstants.PROPERTY_USER, props);
    }

    private static String[] extractRoles(Property[] props) {
        String[] result = null;

        String rolesCommaList = extractProperty(AuthenticationConstants.PROPERTY_ROLES, props);
        if (rolesCommaList != null) {
            result = StringArrayUtils.parseCommaDelimited(rolesCommaList);
        }

        return result;
    }

    protected User authenticate(TokenService tokenService, String token, String[] grantedRoles) throws SecurityException {
        try {
            Property[] props = tokenService.getTokenProperties(token);
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
}
