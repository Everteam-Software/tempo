package org.intalio.tempo.portlet;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.apache.pluto.wrappers.PortletRequestWrapper;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.UIFWApplicationState;
import org.intalio.tempo.uiframework.actions.TasksCollector;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
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
import com.googlecode.instinct.marker.annotate.Stub;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class TasksActionTest extends TestCase {
	   protected transient Logger _log = LoggerFactory.getLogger(getClass());
	    final static ExpectThat expect = new ExpectThatImpl();
	    
	    @Subject TasksAction ta;
	    @Subject TasksCollector tc;
	    @Mock PortletRequestWrapper req; 
	    @Mock PortletResponse res;
	    @Mock HttpSession s;
//	    @Mock ApplicationState st;
	    @Mock UIFWApplicationState st;
	    @Mock BindException be;
	    @Mock Map<String, Object> model;
	    @Mock User user;
	    @Stub HashMap<String, Object> m;
	    @Mock FormManager formManager;
	    
//	    @Specification
//	    public void testExecute() throws Exception{
//	    	
//	    	FormManagerBroker.getInstance().setFormManager(formManager);
//	    	
//	    	//model = new HashMap<String, Object>();
//	    	ta = new TasksAction();
//	    	
//	    	
//	    	
//	    	final String token = "token1";
//	    	final String user1 = "user1";
//	    	tc = new TasksCollector(req, user1, token);
//	    	
//	    	m = new HashMap<String, Object>();
//	    	m.put("errors", ta.getErrors());
//	    	m.put("participantToken", token);
//	    	m.put("currentUser", user1);
//	    	m.put("refreshTime", 5);
//	    	expect.that(new Expectations(){{
//	    		one(req).getParameter("update");will(returnValue("true"));
//	    		atLeast(1).of(be).getModel();will(returnValue(model));	    		
//	    		atLeast(1).of(model).put("errors", ta.getErrors());
//	    		atLeast(1).of(req).getSession();will(returnValue(s));
//	    		atLeast(1).of(s).getAttribute("APPLICATION_STATE");will(returnValue(st));
//	            atLeast(1).of(st).getCurrentUser();will(returnValue(user));
//	            atLeast(1).of(user).getToken();will(returnValue(token));
//	            atLeast(1).of(user).getName();will(returnValue(user1));
//	            atLeast(1).of(model).put("tasks", tc.retrieveTasks());
//	            atLeast(1).of(model).put("participantToken", token);
//	            atLeast(1).of(model).put("currentUser", user1);
//	            atLeast(1).of(model).put("refreshTime", 5);
//	            atLeast(1).of(model).size();will(returnValue(5));
//	            atLeast(1).of(model).entrySet();will(returnValue(m.entrySet()));
//	            one(req).getParameter("update");will(returnValue("false"));
//	    	}});
//	    	Configuration.getInstance().setServiceEndpoint("http://www.intalio.org");
//	    	ta.setRequest(req);
//	    	ta.setResponse(res);
//	    	ta.setBindErrors(be);
//	    	ta.setCommand("command");
//	    	ta.setRequiredRoles(new String[]{"role1"});
//	    	assertTrue(ta.getBindErrors().equals(be));
//	    	assertTrue(ta.getRequest().equals(req));
//	    	assertTrue(ta.getResponse().equals(res));
//	    	assertTrue(ta.getCommand().equals("command"));
//	    	assertTrue(ta.getRequiredRoles()[0].equals("role1"));
//	    	assertNotNull(ta.doExecution());
//	    	assertNotNull(ta.doExecution());
//	    	assertNotNull(ta.getErrorView());
//	    	
//	    }

}
