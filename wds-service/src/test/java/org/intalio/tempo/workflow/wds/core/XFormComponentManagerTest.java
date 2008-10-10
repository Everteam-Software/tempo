package org.intalio.tempo.workflow.wds.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.workflow.wds.core.xforms.XFormsProcessor;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class XFormComponentManagerTest {
    final static ExpectThat expect = new ExpectThatImpl();
    
    @Subject(auto = false)
    XFormComponentManager xFormCM;
    
    @Mock
    WDSServiceFactory wdsFactory;
    
    @Mock
    WDSService wdsService;
    
    @BeforeSpecification
    void before() throws Exception{
        xFormCM = new XFormComponentManager(wdsFactory);
    }
    
    @Specification
    void getName() throws Exception {
        expect.that(xFormCM.getComponentManagerName()).endsWith("xform");
    }

    @Specification
    void deployFolder() throws Exception {
        URL url = XFormComponentManagerTest.class.getResource("/AbsenceRequest");
        
        InputStream pipaIS = XFormComponentManagerTest.class.getResourceAsStream("/AbsenceRequest/AbsenceRequest.pipa/AbsenceRequest/AbsenceRequest.pipa");
        final Item pipaItem = new Item("AbsenceRequest.pipa/AbsenceRequest/AbsenceRequest.pipa", "application/xml", copyToByteArray(pipaIS));
        
        InputStream aaIS = XFormComponentManagerTest.class.getResourceAsStream("/AbsenceRequest/AbsenceRequest.xform/AbsenceRequest/AbsenceApproval.xform");
        final Item aaItem = XFormsProcessor.processXForm("AbsenceRequest.xform/AbsenceRequest/AbsenceApproval.xform", new ByteArrayInputStream(copyToByteArray(aaIS)));
        
        InputStream aaxsdIS = XFormComponentManagerTest.class.getResourceAsStream("/AbsenceRequest/AbsenceRequest.xform/AbsenceRequest/AbsenceApproval.xform.xsd");
        final Item aaxsdItem = new Item("AbsenceRequest.xform/AbsenceRequest/AbsenceApproval.xform.xsd", "application/xml", copyToByteArray(aaxsdIS));
        
        InputStream arIS = XFormComponentManagerTest.class.getResourceAsStream("/AbsenceRequest/AbsenceRequest.xform/AbsenceRequest/AbsenceRequest.xform");
        final Item arItem = XFormsProcessor.processXForm("AbsenceRequest.xform/AbsenceRequest/AbsenceRequest.xform", new ByteArrayInputStream(copyToByteArray(arIS)));
        
        InputStream arxsdIS = XFormComponentManagerTest.class.getResourceAsStream("/AbsenceRequest/AbsenceRequest.xform/AbsenceRequest/AbsenceRequest.xform.xsd");
        final Item arxsdItem = new Item("AbsenceRequest.xform/AbsenceRequest/AbsenceRequest.xform.xsd", "application/xml", copyToByteArray(arxsdIS));
        
        InputStream notiIS = XFormComponentManagerTest.class.getResourceAsStream("/AbsenceRequest/AbsenceRequest.xform/AbsenceRequest/Notification.xform");
        final Item notiItem = XFormsProcessor.processXForm("AbsenceRequest.xform/AbsenceRequest/Notification.xform", new ByteArrayInputStream(copyToByteArray(notiIS)));
        
        InputStream notixsdIS = XFormComponentManagerTest.class.getResourceAsStream("/AbsenceRequest/AbsenceRequest.xform/AbsenceRequest/Notification.xform.xsd");
        final Item notixsdItem = new Item("AbsenceRequest.xform/AbsenceRequest/Notification.xform.xsd", "application/xml", copyToByteArray(notixsdIS));
        
        File base = new File(url.toURI());
        ComponentId compID = new ComponentId(new AssemblyId("a1"), "c1");
        expect.that(new Expectations() {
            {
                one(wdsFactory).getWDSService(); will(returnValue(wdsService));
                one(wdsService).retrieveItem("AbsenceRequest.pipa/AbsenceRequest/AbsenceRequest.pipa", "x");
                
                one(wdsService).retrieveItem("AbsenceRequest.xform/AbsenceRequest/AbsenceApproval.xform", "x");
                one(wdsService).retrieveItem("AbsenceRequest.xform/AbsenceRequest/AbsenceApproval.xform.xsd", "x");

                one(wdsService).retrieveItem("AbsenceRequest.xform/AbsenceRequest/AbsenceRequest.xform", "x");
                one(wdsService).retrieveItem("AbsenceRequest.xform/AbsenceRequest/AbsenceRequest.xform.xsd", "x");
                
                one(wdsService).retrieveItem("AbsenceRequest.xform/AbsenceRequest/Notification.xform", "x");
                one(wdsService).retrieveItem("AbsenceRequest.xform/AbsenceRequest/Notification.xform.xsd", "x");
                
                one(wdsService).storeItem(pipaItem, "x");
                one(wdsService).storeItem(aaItem, "x");
                one(wdsService).storeItem(aaxsdItem, "x");
                one(wdsService).storeItem(arItem, "x");
                one(wdsService).storeItem(arxsdItem, "x");
                one(wdsService).storeItem(notiItem, "x");
                one(wdsService).storeItem(notixsdItem, "x");
                
                one(wdsService).close();
            }
        });
        xFormCM.deploy(compID, base);
    }
    
    private static byte[] copyToByteArray(InputStream input) throws IOException {
        byte[] bytes = new byte[32768];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (true) {
            int bytesRead = input.read(bytes);
            if (bytesRead < 0)
                break;
            output.write(bytes, 0, bytesRead);
        }
        return output.toByteArray();
    }
}
