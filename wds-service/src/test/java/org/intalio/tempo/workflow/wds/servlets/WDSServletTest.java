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

    
    @Specification
    public void retrieveItemFromServletValidURI() throws Exception {

        final Item item = WDSUtil.getSampleItem();

        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI();
                will(returnValue("http://www.task.com/AbscentRequest"));

                one(request).getContextPath();
                will(returnValue("http://www.task.com"));

                one(wdsFactory.getWDSService()).retrieveItem("AbscentRequest", "");
                will(returnValue(item));
                
                ignoring(wdsFactory.getWDSService()).close();
            }
        });
        
        
        wdsServlet.init();
        wdsServlet.doGet(request, response);
        Assert.assertTrue(response.getContentType().contains("meta"));
        Assert.assertEquals(response.getContentLength(), 3);
    }

    
    @Specification
    public void deployFromZip() throws Exception {
        final InputStream requestInputStream = WDSServletTest.class.getResourceAsStream("/forms-AbsenceRequest.zip");
        final FackServletInputStream msis = new FackServletInputStream(requestInputStream);
        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getContentType(); will(returnValue("application/zip"));
                atLeast(1).of(request).getContentLength(); will(returnValue(requestInputStream.toString().getBytes().length));
                
                atLeast(1).of(request).getRequestURI();
                will(returnValue("http://www.task.com/AbscentRequest"));

                one(request).getContextPath();
                will(returnValue("http://www.task.com"));
                
                one(request).getInputStream(); will(returnValue(msis));
                ignoring(wdsFactory.getWDSService());
            }
        });

        wdsServlet.init();
        
        wdsServlet.doPut(request, response); // There will be runtime exception when errors meet
    }
    
    @Specification
    public void deployFromXForm() throws Exception {
        final InputStream requestInputStream = WDSServletTest.class.getResourceAsStream("/AbsenceRequest.xform");
        final InputStream requestInputStreamEx = WDSServletTest.class.getResourceAsStream("/AbsenceRequest.xform");
        final FackServletInputStream msis = new FackServletInputStream(requestInputStream);
        final FackServletInputStream msisEx = new FackServletInputStream(requestInputStreamEx);
        final Item item = XFormsProcessor.processXForm("AbsenceRequest", msisEx);
        
        expect.that(new Expectations() {
            {
                allowing(request).getContentType();
                atLeast(1).of(request).getContentLength(); will(returnValue(requestInputStream.toString().getBytes().length));
                
                atLeast(1).of(request).getRequestURI();
                will(returnValue("http://www.task.com/AbscentRequest"));

                one(request).getContextPath();
                will(returnValue("http://www.task.com"));
                
                one(request).getInputStream(); will(returnValue(msis));
                one(request).getHeader("Is-XForm"); will(returnValue("True"));
                
                one(wdsFactory.getWDSService()).deleteItem("AbscentRequest", "");
                one(wdsFactory.getWDSService()).storeItem(item,"");
                ignoring(wdsFactory.getWDSService()).close();
            }
        });

        wdsServlet.init();
        
        wdsServlet.doPut(request, response);
        Assert.assertEquals(response.getStatus(), 200);
    }
    
    @Specification
    public void deployFromNoneXForm() throws Exception {
        final InputStream requestInputStream = WDSServletTest.class.getResourceAsStream("/AbsenceRequest.xform");
        final InputStream requestInputStreamEx = WDSServletTest.class.getResourceAsStream("/AbsenceRequest.xform");
        final FackServletInputStream msis = new FackServletInputStream(requestInputStream);
        final FackServletInputStream msisEx = new FackServletInputStream(requestInputStreamEx);
        byte[] payload = IOUtils.toByteArray(msisEx);
        final Item item = new Item("AbsenceRequest", "", payload);
        
        expect.that(new Expectations() {
            {
                allowing(request).getContentType();
                atLeast(1).of(request).getContentLength(); will(returnValue(requestInputStream.toString().getBytes().length));
                
                atLeast(1).of(request).getRequestURI();
                will(returnValue("http://www.task.com/AbscentRequest"));

                one(request).getContextPath();
                will(returnValue("http://www.task.com"));
                
                one(request).getInputStream(); will(returnValue(msis));
                one(request).getHeader("Is-XForm"); will(returnValue("False"));
                
                one(wdsFactory.getWDSService()).deleteItem("AbscentRequest", "");
                one(wdsFactory.getWDSService()).storeItem(item,"");
                ignoring(wdsFactory.getWDSService()).close();
            }
        });

        wdsServlet.init();
        
        wdsServlet.doPut(request, response);
        Assert.assertEquals(response.getStatus(), 200);
    }
    
    @Specification
    public void createPIPA() throws Exception {
        final PIPATask task = new PIPATask("test_pipa_task_id","http://www.testtask.com");

        task.setDescription("Task description");
        task.setInitMessageNamespaceURI(URI.create("http://tempo.com"));
        task.setProcessEndpointFromString("http://www.testtask.com/endpoint");
        task.setInitOperationSOAPAction("retrieve");
        
        expect.that(new Expectations() {
            {
                allowing(request).getContentType();
                allowing(request).getContentLength();
                
                atLeast(1).of(request).getRequestURI();
                will(returnValue("http://www.task.com/AbscentRequest"));

                one(request).getContextPath();
                will(returnValue("http://www.task.com"));
                
                one(request).getInputStream();
                one(request).getHeader("Create-PIPA-Task"); will(returnValue("True"));
                
                one(request).getHeader(PIPALoader.HEADER_TASK_ID); will(returnValue("test_pipa_task_id"));
                one(request).getHeader(PIPALoader.HEADER_FORM_URL); will(returnValue("http://www.testtask.com"));
                one(request).getHeader(PIPALoader.HEADER_TASK_DESCRIPTION); will(returnValue("Task description"));
                one(request).getHeader(PIPALoader.HEADER_FORM_NAMESPACE); will(returnValue("http://tempo.com"));
                one(request).getHeader(PIPALoader.HEADER_PROCESS_ENDPOINT); will(returnValue("http://www.testtask.com/endpoint"));
                one(request).getHeader(PIPALoader.HEADER_PROCESS_INIT_ACTION); will(returnValue("retrieve"));
                one(request).getHeader(PIPALoader.HEADER_TASK_USER_OWNERS);
                one(request).getHeader(PIPALoader.HEADER_TASK_ROLE_OWNERS);
                
                one(wdsFactory.getWDSService()).storePipaTask(task, "");
                one(wdsFactory.getWDSService()).close();
            }
        });

        wdsServlet.init();
        
        wdsServlet.doPut(request, response);
        Assert.assertEquals(response.getStatus(), 200);
    }
    
    @Specification
    public void testDeletePIPA() throws Exception {
        expect.that(new Expectations() {
            {
                allowing(request).getContentType();
                allowing(request).getContentLength();
                
                atLeast(1).of(request).getRequestURI();
                will(returnValue("http://www.task.com/AbscentRequest"));

                one(request).getRequestURL(); will(returnValue(new StringBuffer("http://localhost:8080/wds/AbscentRequest")));
                one(request).getContextPath();
                will(returnValue("http://www.task.com"));
                
                one(request).getHeader("Delete-PIPA-Tasks"); will(returnValue("true"));
                
                one(wdsFactory.getWDSService()).deletePIPA("", "AbscentRequest");
                one(wdsFactory.getWDSService()).close();
            }
        });
        
        wdsServlet.init();
        wdsServlet.doDelete(request, response);
    }
    
    @Specification
    public void testDeleteItem() throws Exception {
        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getRequestURI();
                will(returnValue("http://www.task.com/AbscentRequest"));

                one(request).getContextPath();
                will(returnValue("http://www.task.com"));
                
                one(request).getHeader("Delete-PIPA-Tasks"); will(returnValue("false"));
                
                one(wdsFactory.getWDSService()).deleteItem("AbscentRequest", "");
                one(wdsFactory.getWDSService()).close();
            }
        });
        
        wdsServlet.init();
        wdsServlet.doDelete(request, response);
    }
    
    @Specification
    public void testDestroy() throws Exception {
        wdsServlet.init();
        wdsServlet.destroy();   // should be no exceptions
    }
}
