package org.intalio.tempo.feeds;

import java.net.URI;

import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.servlet.ServletRequestContext;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.uiframework.forms.GenericFormManager;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.xml.TaskTypeMapper.TaskType;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class TasksCollectionAdapterTest extends TestCase {
	private static final Logger logger = LoggerFactory
			.getLogger(TasksCollectionAdapterTest.class);
	final static ExpectThat expect = new ExpectThatImpl();
	TasksProvider tp = new TasksProvider();

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TasksCollectionAdapterTest.class);
	}

	@Subject
	TasksCollectionAdapter tca;
	@Mock
	ITaskManagementService client;
	@Mock
	ServletRequestContext request;
	@Mock
	Target target;
	IRI iri;
	Abdera abdera;

	@BeforeSpecification
	void before() throws Exception {
		FormManagerBroker.getInstance().setFormManager(fm);
		iri = new IRI("/test1/test2/test3");
		abdera = new Abdera();
		tca = new TasksCollectionAdapter() {
			protected ITaskManagementService getClient(String token) {
				return client;
			}
		};
	}

	@Mock
	TokenClient tc;
	@Mock GenericFormManager fm;

	@Specification
	public void test() throws Exception {
		final Property[] prop = new Property[1];
		prop[0] = new Property("user", "user1");
		final Task[] tasks = new Task[1];
		tasks[0] = new PATask("taskid1", new URI("http://www.intalio.org/form"));
		expect.that(new Expectations() {
			{
				atLeast(1).of(request).getTarget();
				will(returnValue(target));
				one(target).getType();
				will(returnValue(TargetType.TYPE_ENTRY));
				one(request).getUri();
				will(returnValue(iri));
				atLeast(1).of(request).getAbdera();
				will(returnValue(abdera));
				atLeast(1).of(target).getParameter("collection");
				will(returnValue("all"));
				atLeast(1).of(target).getParameter("user");
				will(returnValue("user"));
				atLeast(1).of(target).getParameter("password");
				will(returnValue("password"));
				atLeast(1).of(target).getParameter("token");
				will(returnValue("token"));
				one(tc).getTokenProperties("token");
				will(returnValue(prop));
				one(client).getTaskList();
				will(returnValue(tasks));
				one(fm).getURL(tasks[0]);will(returnValue("http://www.intalio.org"));
				one(target).getType();will(returnValue(TaskType.ACTIVITY));
				one(tc).getTokenProperties("token");
				will(returnValue(prop));
				one(client).getTaskList();
				will(returnValue(tasks));
				one(fm).getURL(tasks[0]);will(returnValue("http://www.intalio.org"));
			}
		});
		
		Configuration.getInstance().setTokenClient(tc);
		Configuration.getInstance()
				.setServiceEndpoint("http://www.intalio.org");
		expect.that(tca.getEntryID(request).length() == 3);
		expect.that(tca.getAuthor(request) != null);
		expect.that(tca.getId(request) != null);
		expect.that(tca.getTitle(request) == null);
		expect.that(tca.deleteEntry(request)); // not supported
		expect.that(tca.getCategories(request) != null);
		expect.that(tca.getEntry(request) != null);
		expect.that(tca.getFeed(request) != null);
		expect.that(tca.postEntry(request) != null);// not supported
		expect.that(tca.putEntry(request) != null);// not supported
	}

}
