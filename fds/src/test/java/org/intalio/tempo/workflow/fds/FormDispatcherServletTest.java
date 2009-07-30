package org.intalio.tempo.workflow.fds;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.intalio.tempo.workflow.fds.core.MessageSender;
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
public class FormDispatcherServletTest {
    private static final String _IB4P_URI = "/ib4p";
    private static final String _URI_PREFIX = "/fds/workflow";
    final InputStream requestInputStream = FormDispatcherServletTest.class.getResourceAsStream("/createMessageToOde.xml");
    final InputStream resultInputStream = FormDispatcherServletTest.class.getResourceAsStream("/workflowProcessesMessage.xml");

    final static ExpectThat expect = new ExpectThatImpl();

    class MockMessageSender extends MessageSender {
        public Document requestAndGetReply(Document requestMessage, String endpoint, String soapAction) throws HttpException, IOException, DocumentException {
            SAXReader reader = new SAXReader();
            Document result = reader.read(resultInputStream);
            return result;
        }
    }

    @Subject
    FormDispatcherServlet fdServlet;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @BeforeSpecification
    void before() throws Exception {
        fdServlet = new FormDispatcherServlet() {
            private static final long serialVersionUID = -4464735137185721223L;

            protected MessageSender getMessageSender() {
                return new MockMessageSender();
            }
        };
    }
    
    @Specification
    public void testGetDescription() throws Exception{
        String info = fdServlet.getServletInfo();
        Assert.assertEquals(info, "Intalio|BPMS Form Dispatcher Services");
    }

    @Specification
    void testIB4P() throws Exception {
        class MockServletInputStream extends ServletInputStream {
            private InputStream _is;

            public MockServletInputStream(InputStream is) throws IOException {
                _is = is;
            }

            public int read() throws IOException {
                return _is.read();
            }

        }
        final MockServletInputStream msis = new MockServletInputStream(requestInputStream);
        CharArrayWriter caw = new CharArrayWriter();
        final PrintWriter pw = new PrintWriter(caw);

        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI();
                will(returnValue(_URI_PREFIX + _IB4P_URI));

                one(request).getHeader("SOAPAction");
                will(returnValue(null));

                one(request).getInputStream();
                will(returnValue(msis));
                
                one(request).getCharacterEncoding();
                will(returnValue("UTF-8"));
                
                one(response).setCharacterEncoding("UTF-8");
                
                one(response).setContentType("text/xml; charset=UTF-8");
                
                one(response).getWriter();
                will(returnValue(pw));
            }
        });

        fdServlet.doPost(request, response);
        Assert.assertTrue(caw.toString().contains("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"));
    }
    
    @Specification
    void testElse() throws Exception {
        class MockServletInputStream extends ServletInputStream {
            private InputStream _is;

            public MockServletInputStream(InputStream is) throws IOException {
                _is = is;
            }

            public int read() throws IOException {
                return _is.read();
            }

        }
        final MockServletInputStream msis = new MockServletInputStream(requestInputStream);
        CharArrayWriter caw = new CharArrayWriter();
        final PrintWriter pw = new PrintWriter(caw);

        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI();
                will(returnValue(_URI_PREFIX + "Else"));

                one(request).getHeader("SOAPAction");
                will(returnValue(null));

                one(request).getInputStream();
                will(returnValue(msis));
                
                one(request).getCharacterEncoding();
                will(returnValue("UTF-8"));
                
                one(response).setCharacterEncoding("UTF-8");
                
                one(response).setContentType("text/xml; charset=UTF-8");
                
                one(response).getWriter();
                will(returnValue(pw));
            }
        });

        fdServlet.doPost(request, response);
        Assert.assertTrue(caw.toString().contains("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"));
    }

}
