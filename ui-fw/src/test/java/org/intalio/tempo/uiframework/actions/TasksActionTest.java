package org.intalio.tempo.uiframework.actions;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.intalio.tempo.portlet.MockHttpServletRequest;
import org.intalio.tempo.portlet.MockHttpServletResponse;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.UIFWApplicationState;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.web.User;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
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
	    @Mock MockHttpServletRequest req; 
	    @Mock MockHttpServletResponse res;
	    @Mock HttpSession s;
//	    @Mock ApplicationState st;
	    @Mock UIFWApplicationState st;
	    @Mock BindException be;
	    @Mock Map<String, Object> model;
	    @Mock User user;
	    @Stub HashMap<String, Object> m;
	    @Mock FormManager fm;
	    @Mock ITaskManagementService tm;
	    @Specification
	    public void testExecute() throws Exception{
	    	FormManagerBroker.getInstance().setFormManager(fm);
	    	Configuration.getInstance().setServiceEndpoint("http://www.intaili.org");
	    	
	    	//model = new HashMap<String, Object>();
	    	ta = new TasksAction(){
	    		protected TasksCollector getTaskCollector(String user, String token){
	    		    	return new TasksCollector(new HttpServletRequestWrapper(_request), user, token){
	    		    		protected ITaskManagementService getTaskManager(String endpoint, String token){
	    		    	    	return tm;
	    		    	    }	
	    		    	};
	    		 }
	    		
	    	};
	    	final String token = "token1";
	    	final String user1 = "user1";
	    	final Task[] tasks= new Task[1];
	    	tasks[0]= new PATask("taskid1", new URI("http://www.intalio.org/task"));
	    	m = new HashMap<String, Object>();
	    	m.put("errors", ta.getErrors());
	    	m.put("participantToken", token);
	    	m.put("currentUser", user1);
	    	m.put("refreshTime", 5);
	    	expect.that(new Expectations(){{
	    		one(req).getParameter("update");will(returnValue("true"));
	    		atLeast(1).of(be).getModel();will(returnValue(model));	    		
	    		atLeast(1).of(model).put("errors", ta.getErrors());
	    		atLeast(1).of(req).getSession();will(returnValue(s));
	    		atLeast(1).of(s).getAttribute("APPLICATION_STATE");will(returnValue(st));
	            atLeast(1).of(st).getCurrentUser();will(returnValue(user));
	            atLeast(1).of(user).getToken();will(returnValue(token));
	            atLeast(1).of(user).getName();will(returnValue(user1));
	            atLeast(1).of(req).getScheme();will(returnValue("schema"));
	            atLeast(1).of(req).getServerName();will(returnValue("www.intalio.com"));
	            atLeast(1).of(req).getServerPort();will(returnValue(80));
	            atLeast(1).of(tm).getAvailableTasks("Notification", "NOT T._state = TaskState.COMPLETED ORDER BY T._creationDate");
	            will(returnValue(tasks));
	            atLeast(1).of(fm).getURL(tasks[0]);will(returnValue("http://www.intalio.org"));
//	            atLeast(1).of(model).put("participantToken", token);
//	            atLeast(1).of(model).put("currentUser", user1);
//	            atLeast(1).of(model).put("refreshTime", 5);
//	            atLeast(1).of(model).size();will(returnValue(4));
//	            atLeast(1).of(model).entrySet();will(returnValue(m.entrySet()));
	            
	            atLeast(1).of(tm).getAvailableTasks("PIPATask", "ORDER BY T._creationDate");will(returnValue(tasks));
//	            tm.getAvailableTasks("PIPATask", "ORDER BY T._creationDate");will(returnValue(tasks));
	            atLeast(1).of(tm).getAvailableTasks("PATask", "NOT T._state = TaskState.COMPLETED ORDER BY T._creationDate");
	            ignoring(model);
	            one(req).getParameter("update");will(returnValue("false"));
	    	}});
	    	
	    	
	    	ta.setRequest(req);
	    	ta.setResponse(res);
	    	ta.setBindErrors(be);
	    	assertNotNull(ta.execute());
	    	assertNotNull(ta.execute());
	    	assertNotNull(ta.getErrorView());
	    	
	    }

}
