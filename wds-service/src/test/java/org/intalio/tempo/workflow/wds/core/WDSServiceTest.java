package org.intalio.tempo.workflow.wds.core;

import org.intalio.tempo.workflow.task.PIPATask;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class WDSServiceTest {
    final static ExpectThat expect = new ExpectThatImpl();

    @Subject(auto = false)
    WDSService wdsService;
    
    @BeforeSpecification
    void before() throws Exception {
        wdsService = new FackWDSServiceFactory().getWDSService();
    }
    
    @Specification
    void storeItemWhichExists() throws Exception {
        final Item item = WDSUtil.getSampleItem();
        expect.that(new Expectations() {
            {
                one(FackWDSServiceFactory.getJPAMock()).itemExists("AbscentRequest"); will(returnValue(true));
                one(FackWDSServiceFactory.getJPAMock()).deleteItem("AbscentRequest");
                one(FackWDSServiceFactory.getJPAMock()).storeItem(item);
                one(FackWDSServiceFactory.getJPAMock()).commit();
            }
        });
        wdsService.storeItem(item, "token1");
    }
    
    @Specification
    void storeItemWhichNotExists() throws Exception {
        final Item item = WDSUtil.getSampleItem();
        expect.that(new Expectations() {
            {
                one(FackWDSServiceFactory.getJPAMock()).itemExists("AbscentRequest"); will(returnValue(false));
                one(FackWDSServiceFactory.getJPAMock()).storeItem(item);
                one(FackWDSServiceFactory.getJPAMock()).commit();
            }
        });
        wdsService.storeItem(item, "token1");
    }
    
    @Specification
    void getNormalPIPATask() throws Exception {
        expect.that(new Expectations(){
            {
                one(FackWDSServiceFactory.getTMS()).getPipa("AbscentRequest");
                one(FackWDSServiceFactory.getTMS()).close();
            }
        });
        wdsService.getPipaTask("AbscentRequest", "token1");
    }
    
    @Specification(expectedException = NullPointerException.class, withMessage = "formURL")
    void getPIPATaskInvalidURL() throws Exception {
        wdsService.getPipaTask(null, "token1");
    }
    
    @Specification(expectedException = NullPointerException.class, withMessage = "participantToken")
    void getPIPATaskInvalidToken() throws Exception {
        wdsService.getPipaTask("AbscentRequest", null);
    }

    @Specification
    void storeNormalPIPATask() throws Exception {
        final PIPATask pipa = WDSUtil.getSamplePipa();
        expect.that(new Expectations(){
            {
                one(FackWDSServiceFactory.getTMS()).deletePipa(pipa.getFormURLAsString());
                one(FackWDSServiceFactory.getTMS()).storePipa(pipa);
                one(FackWDSServiceFactory.getTMS()).close();
            }
        });
        
        wdsService.storePipaTask(pipa, "token1");
    }
    
    @Specification(expectedException = NullPointerException.class, withMessage = "pipaTask")
    void storePIPATaskInvalidURL() throws Exception {
        wdsService.storePipaTask(null, "token1");
    }
    
    @Specification(expectedException = NullPointerException.class, withMessage = "participantToken")
    void storePIPATaskInvalidToken() throws Exception {
        PIPATask pipa = WDSUtil.getSamplePipa();
        wdsService.storePipaTask(pipa, null);
    }
    
    @Specification
    void deleteItem() throws Exception {
        expect.that(new Expectations(){
            {
                one(FackWDSServiceFactory.getJPAMock()).deleteItem("AbscentRequest");
                one(FackWDSServiceFactory.getJPAMock()).commit();
            }
        });
        
        wdsService.deleteItem("AbscentRequest", "Token1");
    }

    @Specification
    void deletePIPA() throws Exception {
        expect.that(new Expectations(){
            {
                one(FackWDSServiceFactory.getTMS()).deletePipa("/PIPAURL");
                one(FackWDSServiceFactory.getTMS()).close();
            }
        });
        
        wdsService.deletePIPA("token1", "/PIPAURL");
    }
    
    @Specification
    void retrieveItem() throws Exception {
        expect.that(new Expectations(){
            {
                one(FackWDSServiceFactory.getJPAMock()).retrieveItem("AbscentRequest");
            }
        });
        wdsService.retrieveItem("AbscentRequest", "token1");
    }
    
    @Specification
    void commit() throws Exception {
        expect.that(new Expectations(){
            {
               one(FackWDSServiceFactory.getJPAMock()).commit(); 
            }
        });
        wdsService.commit();
    }
    
    @Specification
    void close() throws Exception {
        expect.that(new Expectations(){
            {
                one(FackWDSServiceFactory.getJPAMock()).close();
            }
        });
        wdsService.close();
    }
}
