package org.intalio.tempo.workflow.wds;

import org.intalio.tempo.workflow.wds.client.UIDGenerator;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UIDGeneratorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UIDGeneratorTest.class);
    }

    public void testUIDGenerator() throws Exception {
        UIDGenerator generator = new UIDGenerator();

        String uid1 = generator.generateUID();
        System.out.println(uid1);
        String uid2 = generator.generateUID();
        System.out.println(uid2);

        Assert.assertEquals(uid1.length(), uid2.length());
        Assert.assertFalse(uid1.equals(uid2));
    }
}
