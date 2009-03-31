package org.intalio.tempo.uiframework;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.intalio.tempo.uiframework.export.CSVServlet;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
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
public class CSVServletTest extends TestCase {
	  protected transient Logger _log = LoggerFactory.getLogger(getClass());
	    final static ExpectThat expect = new ExpectThatImpl();
	    
	   @Subject CSVServlet servlet;
	   @Mock HttpServletRequest req;
	   @Mock HttpServletResponse resp;
	   @Mock FormManager fmanager;
	   @Mock ServletOutputStream stream;
	   @Specification
	   public void test() throws Exception{
		   expect.that(new Expectations(){{
			  one(resp).getOutputStream();will(returnValue(stream));
			  ignoring(stream);
		   }});
		   ServletOutputStream stream1 = resp.getOutputStream();
		   servlet = new CSVServlet();
		   Task[] tasks = new Task[1];
		   tasks[0] = new PATask();		  
		   servlet.generateFile(req, "token", "user", tasks, fmanager, stream1);
		   assertTrue(servlet.getFileExt().equals(".csv"));
		   assertTrue(servlet.getFileMimeType().equals("application/csv"));
	   }
}
