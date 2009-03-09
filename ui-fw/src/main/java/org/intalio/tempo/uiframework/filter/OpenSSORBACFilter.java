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
import javax.servlet.http.HttpServletRequestWrapper;
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

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;

public class OpenSSORBACFilter implements Filter {
	private static final Logger LOG = LogManager.getLogger("tempo.security");

	private static final String APPLICATION_STATE = "applicationState";
	private static final String SESSION_APPLICATION_STATE = "APPLICATION_STATE";

	private static final String LOGOUT_PATH = "/login.htm";
	private static final String LOGOUT_METHOD = "post";
	private static final String LOGOUT_ACTION_NAME = "actionName";
	private static final String LOGOUT_ACTION_VALUE = "logOut";

	private static final String LOGOUT_URL_FILTER_CFG = "com.instalo.ui-fw.logout.url";

	private FilterConfig _filterConfig = null;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpSession session = httpServletRequest.getSession();

		if (LOGOUT_PATH.equalsIgnoreCase(httpServletRequest.getServletPath())
				&& LOGOUT_METHOD.equalsIgnoreCase(httpServletRequest
						.getMethod())
				&& LOGOUT_ACTION_VALUE.equalsIgnoreCase(httpServletRequest
						.getParameter(LOGOUT_ACTION_NAME))) {
			doSignOut(session, (HttpServletResponse) response);
		} else {
			try {
				SSOTokenManager manager = SSOTokenManager.getInstance();
				SSOToken ssoToken = manager.createSSOToken(httpServletRequest);

				if (manager.isValidToken(ssoToken)) {
					// check the application state used by Intalio
					if (null == session.getAttribute(SESSION_APPLICATION_STATE)) {
						doSignIn(httpServletRequest, session, ssoToken
								.getTokenID().toString());
					}
					chain.doFilter(request, response);
				}
			} catch (SSOException e) {
				throw new RemoteException("SSOException occured: "
						+ e.getMessage());
			}

		}

	}

	private void doSignOut(HttpSession session, HttpServletResponse response)
			throws IOException {
		LOG.info("signing out from Opensso..");
		session.invalidate();
		// manager.destroyToken(ssoToken);

		// redirect to logout url
		String logoutUrl = this._filterConfig
				.getInitParameter(LOGOUT_URL_FILTER_CFG);
		response.sendRedirect(logoutUrl);
		LOG.info("user has been signed out successfully from Intalio");
	}

	private void doSignIn(HttpServletRequest httpServletRequest,
			HttpSession session, String ssoTokenId) throws RemoteException {
		LOG.info("signing in with OpenSSO....");

		TokenService tokenService = Configuration.getInstance()
				.getTokenClient();
		HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(
				httpServletRequest);
		ApplicationState state = ApplicationState
				.getCurrentInstance(requestWrapper);

		if (state == null) {
			WebApplicationContext context = getWebApplicationState(session);
			state = (ApplicationState) context.getBean(APPLICATION_STATE);
			try {
				state = state.getClass().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (state.getCurrentUser() == null) {
			String tempoToken = null;
			try {
				tempoToken = tokenService.getTokenFromOpenSSOToken(ssoTokenId);
			} catch (AuthenticationException e) {
				throw new RuntimeException("Could not get token!", e);
			} catch (RBACException e) {
				throw new RuntimeException("Could not get token!", e);
			}

			User currentUser = authenticate(tokenService, tempoToken);
			state.setCurrentUser(currentUser);
		}
		ApplicationState.setCurrentInstance(requestWrapper, state);
	}

	protected User authenticate(TokenService tokenService, String token)
			throws SecurityException {
		try {
			Property[] props = tokenService.getTokenProperties(token);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Token properties: " + PropertyUtils.toMap(props));
			}

			String name = extractUser(props);
			String[] roles = extractRoles(props);
			User user = new User(name, roles, token);

			if (LOG.isDebugEnabled()) {
				LOG.debug("User: " + user);
			}
			return user;
		} catch (AuthenticationException ex) {
			throw new SecurityException(ex);
		} catch (RemoteException ex) {
			throw new SecurityException(ex);
		}
	}

	protected WebApplicationContext getWebApplicationState(HttpSession session) {
		return WebApplicationContextUtils
				.getRequiredWebApplicationContext(session.getServletContext());
	}

	private static String extractUser(Property[] props) {
		return extractProperty(AuthenticationConstants.PROPERTY_USER, props);
	}

	private static String[] extractRoles(Property[] props) {
		String[] result = null;

		String rolesCommaList = extractProperty(
				AuthenticationConstants.PROPERTY_ROLES, props);
		if (rolesCommaList != null) {
			result = StringArrayUtils.parseCommaDelimited(rolesCommaList);
		}

		return result;
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

	public void init(FilterConfig filterConfig) throws ServletException {
		this._filterConfig = filterConfig;
	}
}
