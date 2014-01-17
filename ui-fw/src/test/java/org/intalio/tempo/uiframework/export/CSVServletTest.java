package org.intalio.tempo.uiframework.export;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.intalio.tempo.uiframework.forms.FormManager;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.expect.behaviour.Mocker;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class CSVServletTest extends TestCase {
	  protected transient Logger _log = LoggerFactory.getLogger(getClass());
	    final static ExpectThat expect = new ExpectThatImpl();

	   @Subject CSVServlet servlet;
	   HttpServletRequest req;
	   HttpServletResponse resp;
	   FormManager fmanager;
	   ServletOutputStream stream;

	   @Specification
	   public void test() throws Exception{

	       req = Mocker.mock(HttpServletRequest.class);
	       resp = Mocker.mock(HttpServletResponse.class);
	       fmanager = Mocker.mock(FormManager.class);
	       stream = Mocker.mock(ServletOutputStream.class);

		   expect.that(new Expectations(){{
			  one(resp).getOutputStream();will(returnValue(stream));
			  ignoring(stream);
		   }});
		   ServletOutputStream stream1 = resp.getOutputStream();
		   servlet = new CSVServlet();  
		   servlet.generateFile(req, "token", "user", stream1);
		   assertTrue(servlet.getFileExt().equals(".csv"));
		   assertTrue(servlet.getFileMimeType().equals("application/csv"));

		   Mocker.reset();
	   }
}
