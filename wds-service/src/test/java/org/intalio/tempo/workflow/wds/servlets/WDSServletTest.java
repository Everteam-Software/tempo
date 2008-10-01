package org.intalio.tempo.workflow.wds.servlets;

import static com.googlecode.instinct.expect.behaviour.Mocker.mock;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.intalio.tempo.web.SysPropApplicationContextLoader;
import org.intalio.tempo.workflow.wds.core.Item;
import org.intalio.tempo.workflow.wds.core.WDSServiceFactory;
import org.intalio.tempo.workflow.wds.core.tms.WDSUtil;
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
public class WDSServletTest {
    final static ExpectThat expect = new ExpectThatImpl();
    final String test_file = "src/test/resources/tempo-wds.xml";
    @Subject
    WDSServlet wdsServlet;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    private WDSServiceFactory wdsFactory;

    @BeforeSpecification
    void before() throws Exception {
        wdsServlet = new WDSServlet() {
            protected String getConfigFile() {
                return test_file;
            }
        };
        SysPropApplicationContextLoader loader;
        try {
            loader = new SysPropApplicationContextLoader(test_file);
        } catch (IOException except) {
            throw new ServletException(except);
        }
        wdsFactory = loader.getBean("wds.servicefactory");
    }

    @Specification
    public void retrieveItemFromServletInvalidURI() throws Exception {
        CharArrayWriter caw = new CharArrayWriter();
        final PrintWriter pw = new PrintWriter(caw);

        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI();
                will(returnValue("test"));

                one(request).getContextPath();
                will(returnValue("/tst"));

                one(response).getWriter();
                will(returnValue(pw));
            }
        });

        wdsServlet.doGet(request, response);
        Assert.assertTrue(caw.toString().contains("<link rel=\"shortcut icon\" href=\"/favicon.ico\" type=\"image/vnd.microsoft.icon\" />"));
    }

    @Specification
    public void retrieveItemFromServletValidURI() throws Exception {

        Item item = WDSUtil.getSampleItem();
        wdsFactory.getWDSService().storeItem(item, "token1");

        final ServletOutputStream sos = new ServletOutputStream() {
            @Override
            public void write(int c) throws IOException {
                System.out.print((char) c);
            }
        };
        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI();
                will(returnValue("http://www.task.com/AbscentRequest"));

                one(request).getContextPath();
                will(returnValue("http://www.task.com"));

                one(response).getOutputStream();
                will(returnValue(sos));

                ignoring(response);
            }
        });

        wdsServlet.init();
        wdsServlet.doGet(request, response);

    }

    /*
    @Specification
    public void putItemToServlet() throws Exception {
        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI();
                will(returnValue("http://www.task.com/AbscentRequest"));

                one(request).getContextPath();
                will(returnValue("http://www.task.com"));

                ignoring(response);
            }
        });

        wdsServlet.init();
        wdsServlet.doPut(request, response);
    }*/
}
