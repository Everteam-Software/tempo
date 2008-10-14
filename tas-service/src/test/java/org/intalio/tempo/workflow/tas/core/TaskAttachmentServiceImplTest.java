package org.intalio.tempo.workflow.tas.core;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.workflow.tas.axis2.DummyAuthStrategy;
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
public class TaskAttachmentServiceImplTest extends TestCase {
	protected transient Logger _log = LoggerFactory.getLogger(getClass());
	final static ExpectThat expect = new ExpectThatImpl();

	@Subject
	TaskAttachmentServiceImpl service;

	@Specification
	public void testConstructor() {
		
		service = new TaskAttachmentServiceImpl();
		
		// error construction
		try {
			service = new TaskAttachmentServiceImpl(null, null);
			assertTrue(false);
		} catch (Exception e) {

		}
		try {
			service = new TaskAttachmentServiceImpl(new N3AuthStrategy(), null);
			assertTrue(false);
		} catch (Exception e) {

		}
		
		// BTW test ctor for WDSStorageStrategy 
		service = new TaskAttachmentServiceImpl(new N3AuthStrategy(), new WDSStorageStrategy("http://www.intalio.org"));
		try {
			service = new TaskAttachmentServiceImpl(new N3AuthStrategy(), new WDSStorageStrategy(null));
			assertTrue(false);
		} catch (Exception e) {

		}
	}

	@Specification
	public void testErrorHandling() {
		service = new TaskAttachmentServiceImpl();
		try {
			service.setAuthStrategy(null);
			assertTrue(false);
		} catch (Exception e) {

		}
		try {
			service.setStorageStrategy(null);
			assertTrue(false);
		} catch (Exception e) {

		}

	}

	@Mock
	AuthCredentials ac;
	@Mock
	AttachmentMetadata am;
	@Mock AuthStrategy as;
	@Stub Property[] prop;
	@Mock HttpClient c;
	@Specification
	public void test() throws Exception {
		prop = new  Property[1];
		prop[0] = new Property("a", "a");
		byte[] payload = new String("hello").getBytes();
		expect.that(new Expectations(){{
			atLeast(1).of(am).getFilename();will(returnValue("abc"));
			atLeast(1).of(am).getMimeType();will(returnValue("text/html"));
			ignoring(c);will(returnValue(200));
			
			
			
		}});
		service = new TaskAttachmentServiceImpl(new DummyAuthStrategy(),
				new WDSStorageStrategy(){
			protected HttpClient getClient(){
				return c;
			}
		});
		
		_log.info(service.add(ac, am, payload));
		_log.info(service.add(ac, am, "http://www.intalio.org/"));
		service.delete(ac, "http://www.intalio.org/");
		

	}
}
