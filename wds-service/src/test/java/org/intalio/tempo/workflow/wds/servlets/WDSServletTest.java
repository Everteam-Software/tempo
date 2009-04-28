package org.intalio.tempo.workflow.wds.servlets;

//import static com.googlecode.instinct.expect.behaviour.Mocker.mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.intalio.tempo.web.SysPropApplicationContextLoader;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.wds.core.Item;
import org.intalio.tempo.workflow.wds.core.PIPALoader;
import org.intalio.tempo.workflow.wds.core.WDSServiceFactory;
import org.intalio.tempo.workflow.wds.core.WDSUtil;
import org.intalio.tempo.workflow.wds.core.xforms.XFormsProcessor;
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

    
    private FackHttpServletResponse response = new FackHttpServletResponse();

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
        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI();
                will(returnValue("test"));

                one(request).getContextPath();
                will(returnValue("/tst"));
            }
        });

        wdsServlet.doGet(request, response);
        Assert.assertTrue(response.getCharWriter().toString().contains("<link rel=\"shortcut icon\" href=\"/favicon.ico\" type=\"image/vnd.microsoft.icon\" />"));
    }

}
