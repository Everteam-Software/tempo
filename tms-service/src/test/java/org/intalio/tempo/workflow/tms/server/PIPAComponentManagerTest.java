package org.intalio.tempo.workflow.tms.server;

import java.io.InputStream;

import org.intalio.tempo.deployment.DeploymentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

public class PIPAComponentManagerTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(PIPAComponentManagerTest.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PIPAComponentManagerTest.class);
    }

    public void testCheckPipa() throws Exception {
        PIPAComponentManager pcm = new PIPAComponentManager(Utils.createTMSServer());
        InputStream is = this.getClass().getResourceAsStream("/AbsenceRequest.pipa");
        DeploymentMessage dm = pcm.checkPipa("token2", is, "pipatest");
        Assert.assertNull(dm);
    }

    public void testProcessPipa() throws Exception {
        PIPAComponentManager pcm = new PIPAComponentManager(Utils.createTMSServerJPA());
        InputStream is = this.getClass().getResourceAsStream("/AbsenceRequest.pipa");
        DeploymentMessage dm = pcm.processPipa("token2", is, "pipatest");
        Assert.assertNull(dm);
    }

    public void testDeploy() throws Exception {
        // PIPAComponentManager pcm = new
        // PIPAComponentManager(Utils.createTMSServer());
        // InputStream is =
        // this.getClass().getResourceAsStream("/AbsenceRequest.pipa");
        // DeploymentMessage dm = pcm.deploy(, base)("token2", is, "pipatest");
        // logger.info(dm.toString());
        // do nothing
    }
}
