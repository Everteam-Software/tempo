package org.intalio.tempo.uiframework.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import junit.framework.Assert;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.UIFWApplicationState;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.springframework.web.context.WebApplicationContext;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

import edu.yale.its.tp.cas.client.CASReceipt;

@RunWith(InstinctRunner.class)
public class CASRBACFilterTest {
    private class MockTokenService extends TokenClient {
        public MockTokenService(String endpointUrl) {
            super(endpointUrl);
        }
        
        @Override
        public String authenticateUser(String user, String password) throws AuthenticationException, RBACException, RemoteException {
            return null;
        }
        
        @Override
        public String authenticateUser(String user, Property[] credentials) throws AuthenticationException, RBACException, RemoteException {
            return null;
        }
        
        @Override
        public Property[] getTokenProperties(String token) throws AuthenticationException, RemoteException {
            return new Property[]{new Property("name", "value")};
        }
        
        @Override
        public String getTokenFromTicket(String ticket, String serviceURL) throws AuthenticationException, RBACException, RemoteException {
            return null;
        }
    }

    private class MockServletContext implements ServletContext {

        public Object getAttribute(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public Enumeration getAttributeNames() {
            // TODO Auto-generated method stub
            return null;
        }

        public ServletContext getContext(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public String getInitParameter(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public Enumeration getInitParameterNames() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getMajorVersion() {
            // TODO Auto-generated method stub
            return 0;
        }

        public String getMimeType(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public int getMinorVersion() {
            // TODO Auto-generated method stub
            return 0;
        }

        public RequestDispatcher getNamedDispatcher(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public String getRealPath(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public RequestDispatcher getRequestDispatcher(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public URL getResource(String arg0) throws MalformedURLException {
            // TODO Auto-generated method stub
            return null;
        }

        public InputStream getResourceAsStream(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public Set getResourcePaths(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public String getServerInfo() {
            // TODO Auto-generated method stub
            return null;
        }

        public Servlet getServlet(String arg0) throws ServletException {
            // TODO Auto-generated method stub
            return null;
        }

        public String getServletContextName() {
            // TODO Auto-generated method stub
            return null;
        }

        public Enumeration getServletNames() {
            // TODO Auto-generated method stub
            return null;
        }

        public Enumeration getServlets() {
            // TODO Auto-generated method stub
            return null;
        }

        public void log(String arg0) {
            // TODO Auto-generated method stub

        }

        public void log(Exception arg0, String arg1) {
            // TODO Auto-generated method stub

        }

        public void log(String arg0, Throwable arg1) {
            // TODO Auto-generated method stub

        }

        public void removeAttribute(String arg0) {
            // TODO Auto-generated method stub

        }

        public void setAttribute(String arg0, Object arg1) {
            // TODO Auto-generated method stub

        }
    }

    private class MockSession implements HttpSession {

        public Object getAttribute(String arg0) {
            // TODO Auto-generated method stub
            if (arg0.equals("edu.yale.its.tp.cas.client.filter.receipt")) {
                CASReceipt receipt = new CASReceipt();
                receipt.setPgtIou("");
                return receipt;
            }
            return null;
        }

        public Enumeration getAttributeNames() {
            // TODO Auto-generated method stub
            return null;
        }

        public long getCreationTime() {
            // TODO Auto-generated method stub
            return 0;
        }

        public String getId() {
            // TODO Auto-generated method stub
            return null;
        }

        public long getLastAccessedTime() {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getMaxInactiveInterval() {
            // TODO Auto-generated method stub
            return 0;
        }

        public ServletContext getServletContext() {
            // TODO Auto-generated method stub
            return new MockServletContext();
        }

        public HttpSessionContext getSessionContext() {
            // TODO Auto-generated method stub
            return null;
        }

        public Object getValue(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public String[] getValueNames() {
            // TODO Auto-generated method stub
            return null;
        }

        public void invalidate() {
            // TODO Auto-generated method stub

        }

        public boolean isNew() {
            // TODO Auto-generated method stub
            return false;
        }

        public void putValue(String arg0, Object arg1) {
            // TODO Auto-generated method stub

        }

        public void removeAttribute(String arg0) {
            // TODO Auto-generated method stub

        }

        public void removeValue(String arg0) {
            // TODO Auto-generated method stub

        }

        public void setAttribute(String arg0, Object arg1) {
            // TODO Auto-generated method stub

        }

        public void setMaxInactiveInterval(int arg0) {
            // TODO Auto-generated method stub

        }

    }

    private class MockFilterConfig implements FilterConfig {

        public String getFilterName() {
            // TODO Auto-generated method stub
            return "CASRBACFilter";
        }

        public String getInitParameter(String arg0) {
            // TODO Auto-generated method stub
            return "/login.htm";
        }

        public Enumeration getInitParameterNames() {
            // TODO Auto-generated method stub
            return null;
        }

        public ServletContext getServletContext() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private class MockFilterChain implements FilterChain {

        public void doFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException {
            // do nothing
        }
    }

    final HttpSession session = new MockSession();
    final FilterChain chain = new MockFilterChain();
    final static ExpectThat expect = new ExpectThatImpl();

    @Subject
    CASRBACFilter filter;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    WebApplicationContext context;

    @BeforeSpecification
    public void before() {
        filter = new CASRBACFilter() {
            protected WebApplicationContext getWebApplicationState(HttpSession session) {
                return context;
            }
        };
        FilterConfig filterConfig = new MockFilterConfig();
        filter.init(filterConfig);
        TokenClient tokenClient = new MockTokenService("http://localhost:8080/token");
        Configuration.getInstance().setTokenClient(tokenClient);
    }

    @Specification
    public void testDoFilterSignOut() throws Exception {
        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getSession();
                will(returnValue(session));

                atLeast(1).of(request).getServletPath();
                will(returnValue("/login.htm"));

                atLeast(1).of(request).getMethod();
                will(returnValue("post"));

                atLeast(1).of(request).getParameter("actionName");
                will(returnValue("logOut"));

                atLeast(1).of(response).sendRedirect("/login.htm");
            }
        });
        filter.doFilter(request, response, chain);
        Assert.assertTrue(true);
    }

    @Specification
    public void testDoFilterSignIn() throws Exception {
        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getSession();
                will(returnValue(session));

                atLeast(1).of(request).getServletPath();
                will(returnValue("/logout.htm"));

                one(context).getBean("applicationState");
                will(returnValue(new UIFWApplicationState()));
            }
        });
        filter.doFilter(request, response, chain);
        Assert.assertTrue(true);
    }

}
