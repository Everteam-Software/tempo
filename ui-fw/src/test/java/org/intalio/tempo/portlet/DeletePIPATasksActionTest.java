package org.intalio.tempo.portlet;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.apache.pluto.wrappers.PortletRequestWrapper;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.User;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class DeletePIPATasksActionTest extends TestCase {
    protected transient Logger _log = LoggerFactory.getLogger(getClass());
    final static ExpectThat expect = new ExpectThatImpl();

    @Subject
    DeletePIPATasksAction act;
    @Mock
    PortletRequestWrapper request;
    @Mock
    HttpSession s;
    @Mock
    ApplicationState st;
    @Mock
    User user;

    @Specification
    public void testExecute() {
        act = new DeletePIPATasksAction();
        expect.that(new Expectations() {
            {
                atLeast(1).of(request).getSession();
                will(returnValue(s));
                atLeast(1).of(s).getAttribute("APPLICATION_STATE");
                will(returnValue(st));
                atLeast(1).of(st).getCurrentUser();
                will(returnValue(user));
                atLeast(1).of(user).getToken();
                will(returnValue("token"));
                atLeast(1).of(request).getScheme();
                will(returnValue("schema"));
                atLeast(1).of(request).getServerName();
                will(returnValue("MockServer"));
                atLeast(1).of(request).getServerPort();
                will(returnValue(90));
            }
        });
        act.setRequest(request);
        assertNotNull(act.execute());
        assertNull(act.getErrorView());

    }
}
