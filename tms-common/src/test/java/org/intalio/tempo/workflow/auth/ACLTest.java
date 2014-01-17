package org.intalio.tempo.workflow.auth;

import junit.framework.TestCase;

public class ACLTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ACLTest.class);
    }
    
    public void setUp() {
        System.setProperty("org.intalio.tempo.configDirectory",
                "src/test/resources/");
    }

    public void testACL() {
        ACL acl = new ACL();
        acl.setAction("testAction");        
        assertEquals(acl.getAction(), "testAction");
        
        UserRoles user1 = new UserRoles("test/user1", new String[] {"test/role1"});
        assertTrue(acl.isAuthorizedAction(user1, "testAction"));
        
        acl.getRoleOwners().add("test/role1");
        acl.getUserOwners().add("test/user1");
        assertTrue(acl.isAuthorizedAction(user1, "testAction"));
        
        UserRoles user2 = new UserRoles("test/user2", new String[] {"test/role2"});
        assertFalse(acl.isAuthorizedAction(user2, "testAction"));
    }
}
