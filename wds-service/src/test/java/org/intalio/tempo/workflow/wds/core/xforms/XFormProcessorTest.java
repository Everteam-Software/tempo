package org.intalio.tempo.workflow.wds.core.xforms;

import java.io.FileInputStream;
import java.net.URL;

import org.intalio.tempo.workflow.wds.core.Item;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Specification;

@RunWith(InstinctRunner.class)
public class XFormProcessorTest {

    String[] RESOURCES = new String[] { "/personnel.xform" };
    final static ExpectThat expect = new ExpectThatImpl();
    
    @Specification
    public void runTheXFormProcessor() throws Exception {
        for (String r : RESOURCES) {
            URL url = XFormConversionTest.class.getResource(r);
            Item item = XFormsProcessor.processXForm("http://www.task.com/tasks.xform",new FileInputStream(url.getFile()));
            String payload = new String(item.getPayload());
            expect.that(payload).containsString("oxf:/http://www.task.com/PersonnelRequisition.xform.xsd");
        }
    }
}
