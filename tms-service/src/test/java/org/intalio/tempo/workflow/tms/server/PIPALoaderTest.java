package org.intalio.tempo.workflow.tms.server;

import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

public class PIPALoaderTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PIPALoaderTest.class);
    }

    public void testParsePipa() throws Exception {
        Properties prop = new Properties();
        prop.load(this.getClass().getResourceAsStream("/AbsenceRequest.pipa"));
        Assert.assertNotNull(PIPALoader.parsePipa(prop));
    }

}
