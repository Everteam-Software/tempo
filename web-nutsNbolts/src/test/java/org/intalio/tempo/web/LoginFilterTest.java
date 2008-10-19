package org.intalio.tempo.web;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.intalio.tempo.web.controller.FakeUIFWApplicationState;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class LoginFilterTest {
    final static ExpectThat expect = new ExpectThatImpl();
    private XmlWebApplicationContext context;
    private MockServletContext msc;

    @Subject
    LoginFilter loginFilter;
    
    @Mock
    FilterChain filterChain;
    
    @Mock
    HttpServletRequest request;
    
    @Mock
    HttpSession session;
    
    FakeHttpServletResponse response = new FakeHttpServletResponse();
    
    @BeforeSpecification
    void before() throws Exception {
        loginFilter = new LoginFilter();
        String[] contexts = new String[] { "tempo-ui-fw-servlet.xml", "tempo-ui-fw.xml" };
        context = new SysPropWebApplicationContext();
        context.setConfigLocations(contexts);
        msc = new MockServletContext();
        context.setServletContext(msc);
        context.refresh();
        msc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
    }
    
    @Specification
    void testDoFilterLogin() throws Exception {
        expect.that(new Expectations() {
            {
                one(request).getRequestURI(); will(returnValue("http://localhost/login.htm"));
                allowing(filterChain);
            }
        });
        loginFilter.doFilter(request, response, filterChain);
    }
    
    @Specification
    void testDoFilterWithSecure() throws Exception {
        Cookie cookie = new Cookie("SECURE_RANDOM", "secure");
        final Cookie[] cookies = new Cookie[1];
        cookies[0] = cookie;

        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI(); will(returnValue("http://localhost/tasks.htm"));
                one(request).getSession(); will(returnValue(session));
                one(session).getAttribute("SECURE_RANDOM"); will(returnValue("secure"));
                atLeast(1).of(request).getCookies(); will(returnValue(cookies));
                allowing(filterChain);
            }
        });
        loginFilter.doFilter(request, response, filterChain);
    }

    @Specification
    void testDoFilterWithContextButNoUser() throws Exception {
        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI(); will(returnValue("http://localhost/tasks.htm"));
                allowing(request).getSession(); will(returnValue(session));
                one(session).getAttribute("SECURE_RANDOM"); will(returnValue("secure"));
                allowing(request).getCookies();
                one(session).getServletContext(); will(returnValue(msc));
                allowing(filterChain);
            }
        });
        loginFilter.doFilter(request, response, filterChain);
        Assert.assertEquals(response.getRedirectURL(), "/login.htm");
    }
    
    @Specification
    void testDoFilterWithContextSingleLogin() throws Exception {
        Cookie cookie = new Cookie("singleLogin", "token1");
        final Cookie[] cookies = new Cookie[1];
        cookies[0] = cookie;
        final FakeUIFWApplicationState state = new FakeUIFWApplicationState();
        User currentUser = new User("test1", new String[] { "test/test1" }, "token1");
        state.setCurrentUser(currentUser);

        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI(); will(returnValue("http://localhost/tasks.htm"));
                allowing(request).getSession(); will(returnValue(session));
                one(session).getAttribute("SECURE_RANDOM"); will(returnValue("secure"));
                allowing(request).getCookies(); will(returnValue(cookies));
                one(session).getServletContext(); will(returnValue(msc));
                one(session).getAttribute("APPLICATION_STATE"); will(returnValue(state));
                allowing(session);
                allowing(request);
                allowing(filterChain);
            }
        });
        loginFilter.doFilter(request, response, filterChain);
    }
}
