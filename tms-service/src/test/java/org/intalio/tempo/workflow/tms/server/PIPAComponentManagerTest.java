package org.intalio.tempo.workflow.tms.server;

import java.io.File;
import java.io.InputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeploymentMessage;
import org.intalio.tempo.deployment.spi.ComponentManagerResult;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class PIPAComponentManagerTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(PIPAComponentManagerTest.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PIPAComponentManagerTest.class);
    }
    @Specification
    public void testCheckPipa() throws Exception {
        PIPAComponentManager pcm = new PIPAComponentManager(Utils.createTMSServer());
        InputStream is = this.getClass().getResourceAsStream("/AbsenceRequest.pipa");
        DeploymentMessage dm = pcm.checkPipa("token2", is, "pipatest");
        Assert.assertNull(dm);
        
        InputStream is2 = this.getClass().getResourceAsStream("/errorPIPARequest.pipa");
        DeploymentMessage dm2 = pcm.checkPipa("token2", is2, "pipatest");
        Assert.assertNotNull(dm2);
    }
    @Subject PIPAComponentManager pcm;
    @Mock ComponentId cid;
    final static ExpectThat expect = new ExpectThatImpl();   
    
    @Specification
    public void testProcessPipa() throws Exception {
//        final JPATaskDaoConnectionFactory mockTdf = Mocker.mock(JPATaskDaoConnectionFactory.class);
//        ITMSServer server = new TMSServer(Utils.getMeASimpleAuthProvider(), mockTdf, Utils.getMeADefaultPermissionHandler()){
//            protected ServiceClient getServiceClient()throws AxisFault{           
//                return new MockServiceClient();
//            };
//        };       
//        pcm = new PIPAComponentManager(server);
        
//        expect.that(new Expectations(){{
//            ITaskDAOConnection tdc;
//            atLeast(1).of(mockTdf).openConnection(); will(returnValue(tdc=Mocker.mock(ITaskDAOConnection.class)));
//            one(tdc).deletePipaTask("http://www.intalio.com/AbsenceRequest/AbsenceRequest.xform");
//            //ignoring(tdc.storePipaTask(null));
//            one(tdc).commit();
//        }});       
        
        PIPAComponentManager pcm = new PIPAComponentManager(Utils.createTMSServerJPA());
        InputStream is = this.getClass().getResourceAsStream("/AbsenceRequest.pipa");
        DeploymentMessage dm = pcm.processPipa("token2", is, "pipatest");
        if (dm != null)
            logger.error("dm:"+dm.toString());
        Assert.assertNull(dm);
    }


    @Specification
    public void testDeploy() throws Exception { 
         PIPAComponentManager pcm = new PIPAComponentManager(Utils.createTMSServerJPA());
         expect.that(new Expectations(){{
             
         }});
         //InputStream is = this.getClass().getResourceAsStream("/AbsenceRequest.pipa");
         ComponentManagerResult dm = pcm.deploy(cid, new File("./target/test-classes/PIPARequest/"));
         logger.info(dm.toString());
         dm = pcm.deploy(cid, new File("./target/test-classes/errorPIPARequest/"));
         logger.info(dm.toString());
    }
    
    @Specification
    public void testOthers() throws Exception { 
         PIPAComponentManager pcm = new PIPAComponentManager(Utils.createTMSServerJPA());
         expect.that(new Expectations(){{
             
         }});
         pcm.activate(cid, new File("./"));
         pcm.deactivate(cid);
         pcm.undeploy(cid, null);
         pcm.start(cid);
         pcm.stop(cid);
    }
}
