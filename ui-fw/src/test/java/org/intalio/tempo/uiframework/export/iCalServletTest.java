package org.intalio.tempo.uiframework.export;

import java.net.URI;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.export.iCalServlet;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Stub;
import com.googlecode.instinct.marker.annotate.Subject;


@RunWith(InstinctRunner.class)
public class iCalServletTest extends TestCase {
	  protected transient Logger _log = LoggerFactory.getLogger(getClass());
	    final static ExpectThat expect = new ExpectThatImpl();
	    
	   @Subject iCalServlet servlet;
	   @Mock HttpServletRequest req;
	   @Mock HttpServletResponse resp;
	   @Mock TokenClient tc;
	   @Stub String token = "token1";
	   @Stub Property[] prop = new Property[1];
	   @Mock ITaskManagementService tms;
	   
	   @Mock ServletOutputStream stream;
	   @Mock FormManager fmanager;
	   @Specification
	   public void test() throws Exception{
		   prop[0] = new Property("user", "user1");
		   final Task[] tasks = new Task[1];
		   tasks[0] = new PATask("taskid1", new URI("http://www.intalio.org/form"));		
//		   tasks[0].setInternalID(1111);		   
//		   tasks[0].setFormURLFromString("http://www.intalio.org/form");
		   expect.that(new Expectations(){{
			  one(req).getParameter("token");will(returnValue(token));
			  one(req).getParameter("page");
			  one(req).getParameter("rp");
			  one(req).getParameter("sortname");
			  one(req).getParameter("sortorder");
			  one(req).getParameter("query");
			  one(req).getParameter("full");
			  one(req).getParameter("qtype");
			  one(req).getParameter("type");
			  one(tc).getTokenProperties("The quick brown fox jumps over the lazy dog.");will(returnValue(prop));
              // one(tms).getAvailableTasks("Task", "ORDER BY T._creationDate");will(returnValue(tasks));
              // one(resp).getOutputStream();will(returnValue(stream));
              // one(resp).setContentType("text/calendar");
              // one(resp).addHeader("Content-disposition", "attachment; filename=\"tasks for user1.ics\"");
              // one(fmanager).getURL(tasks[0]);will(returnValue("Http://www.intalio.com"));
			  one(req).getScheme();will(returnValue("schema1"));
			  one(req).getServerName();will(returnValue("localhost"));
			  one(req).getServerPort();will(returnValue(80));
		   }});
		   servlet = new iCalServlet();
		   FormManagerBroker.getInstance().setFormManager(fmanager);
		   Configuration.getInstance().setTokenClient(tc);
		   Configuration.getInstance().setServiceEndpoint("http://www.intalio.org");
		   try{
			   servlet.doGet(req, resp);
		   }catch(Exception e){
//			   e.printStackTrace();
//			   System.out.println("-------------\r\n");
//			   e.getCause().printStackTrace();
			   
		   }
		   
	   }
}
