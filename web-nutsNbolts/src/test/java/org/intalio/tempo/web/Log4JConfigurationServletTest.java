package org.intalio.tempo.web;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class Log4JConfigurationServletTest {
    final static ExpectThat expect = new ExpectThatImpl();

    @Subject
    Log4JConfigurationServlet log4jServlet;
    
    @Mock
    HttpServletRequest request;

    @Mock
    ServletConfig servletConfig;
    
    FakeHttpServletResponse response = new FakeHttpServletResponse();
    
    @BeforeSpecification
    void before() throws Exception {
        log4jServlet = new Log4JConfigurationServlet();
    }
    
    @Specification
    void testDoGetisFragment() throws Exception {
        expect.that(new Expectations() {
            {
                one(servletConfig).getInitParameter("fragment"); will(returnValue("true"));
                one(request).getParameter("sortbylevel"); will(returnValue("true"));
            }
        });
        log4jServlet.init(servletConfig);
        log4jServlet.doGet(request, response);
        Assert.assertTrue(response.getCharWriter().toString().contains("org.intalio.tempo"));
    }
    
    @Specification
    void testDoGetNotFragment() throws Exception {
        expect.that(new Expectations() {
            {
                one(servletConfig).getInitParameter("fragment"); will(returnValue("false"));
                one(request).getParameter("sortbylevel"); will(returnValue("false"));
            }
        });
        log4jServlet.init(servletConfig);
        log4jServlet.doGet(request, response);
        Assert.assertTrue(response.getCharWriter().toString().contains("</body></html>"));
    }
    
    @Specification
    void testDoPost() throws Exception {
        expect.that(new Expectations() {
            {
                one(servletConfig).getInitParameter("fragment"); will(returnValue("true"));
                one(request).getParameter("class"); will(returnValue("com.test.class"));
                one(request).getParameter("level"); will(returnValue("DEBUG"));
                one(request).getParameter("sortbylevel"); will(returnValue("true"));
            }
        });
        log4jServlet.init(servletConfig);
        log4jServlet.doPost(request, response);
        Assert.assertTrue(response.getCharWriter().toString().contains("com.test.class"));
    }
}
