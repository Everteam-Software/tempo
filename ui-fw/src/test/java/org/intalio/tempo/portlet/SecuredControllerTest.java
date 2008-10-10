package org.intalio.tempo.portlet;

import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.apache.pluto.wrappers.PortletRequestWrapper;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.User;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

import edu.yale.its.tp.cas.client.CASReceipt;

@RunWith(InstinctRunner.class)
public class SecuredControllerTest extends TestCase {
    protected transient Logger _log = LoggerFactory.getLogger(getClass());
    final static ExpectThat expect = new ExpectThatImpl();

    @Subject SecuredController sc;
    @Mock TokenService ts;
    @Mock PortletRequestWrapper request;
    @Mock HttpSession s;
    @Mock ApplicationState st;
    @Mock User user;
 
    @Specification
    public void testGetCurrentUserName(){
        sc = new SecuredController(ts);
        expect.that(new Expectations(){{
            atLeast(1).of(request).getSession();will(returnValue(s));
            atLeast(1).of(s).getAttribute("APPLICATION_STATE");will(returnValue(st));
            atLeast(1).of(st).getCurrentUser();will(returnValue(user));
            atLeast(1).of(user).getName();will(returnValue("user1"));
        }});
        assertTrue(SecuredController.getCurrentUserName(request).equals("user1"));
    }
    
    
    
    @Mock RenderResponse rr;
   // @Mock TestRenderRequest rrequest;
    @Mock BindException be;
    @Mock CASReceipt receipt;
    @Specification
    public void testShowForm() throws Exception{
        sc = new SecuredController(ts, "dummyServiceURL");
        expect.that(new Expectations(){{
//            atLeast(1).of(request).getSession();will(returnValue(s));
            atLeast(1).of(s).getAttribute("edu.yale.its.tp.cas.client.filter.receipt");will(returnValue(receipt));
            atLeast(1).of(receipt).getPgtIou();will(returnValue("dummy"));
            atLeast(1).of(s).getAttribute("APPLICATION_STATE");will(returnValue(st));
            atLeast(1).of(st).getCurrentUser();will(returnValue(user));
       }});
        MockRenderRequest rrequest = new MockRenderRequest(s);
        assertNotNull(sc.showForm(rrequest, rr, be));        
    }
    
  
}
