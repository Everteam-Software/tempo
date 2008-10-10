package org.intalio.tempo.feeds;

import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.servlet.ServletRequestContext;
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
    private static final Logger logger = LoggerFactory.getLogger(TasksCollectionAdapterTest.class);
    final static ExpectThat expect = new ExpectThatImpl();
    TasksProvider tp = new TasksProvider();
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TasksCollectionAdapterTest.class);
    }
    
    @Subject TasksCollectionAdapter tca = new TasksCollectionAdapter();
    @Mock   ServletRequestContext request;
    @Mock   Target target;
     IRI iri;
     Abdera abdera;

    
    @BeforeSpecification
    void before() throws Exception {
         iri = new IRI("/test1/test2/test3");
         abdera = new Abdera();
    }
    
    @Specification
    public void test() throws Exception {
        expect.that(new Expectations() {{
            atLeast(1).of(request).getTarget(); will(returnValue(target));
            one(target).getType();will(returnValue(TargetType.TYPE_ENTRY));
            one(request).getUri();will(returnValue(iri));
            atLeast(1).of(request).getAbdera();will(returnValue(abdera));           
            atLeast(1).of(target).getParameter("collection");will(returnValue("test"));
            atLeast(1).of(target).getParameter("user");will(returnValue("user"));
            atLeast(1).of(target).getParameter("password");will(returnValue("password"));
            atLeast(1).of(target).getParameter("token");will(returnValue("token"));
        }});
        expect.that(tca.getEntryID(request).length()==3);
        expect.that(tca.getAuthor(request) != null);
        expect.that(tca.getId(request) != null);
        expect.that(tca.getTitle(request)==null);
        expect.that(tca.deleteEntry(request));    // not supported
        expect.that(tca.getCategories(request) != null);        
        expect.that(tca.getEntry(request) != null);
        expect.that(tca.getFeed(request) != null);
        expect.that(tca.postEntry(request) != null);// not supported
        expect.that(tca.putEntry(request) != null);// not supported
    }

}
