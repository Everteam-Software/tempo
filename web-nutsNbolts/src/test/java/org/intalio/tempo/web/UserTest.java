package org.intalio.tempo.web;

import junit.framework.TestCase;

import org.junit.Test;

public class UserTest extends TestCase{

    private User user;
    
    protected void setUp() throws Exception {
        user = new User("test1", new String[] {"test/role1", "test/role2"}, "token1");
    }
    
    @Test
    public void testGetRoles() throws Exception {
        String[] roles = user.getRoles();
        assertEquals(roles[0], "test/role1");
    }
    
    @Test
    public void testHasRole() throws Exception {
        assertTrue(user.hasRole("test/role1"));
        assertFalse(user.hasRole("test/role3"));
    }
    
    @Test
    public void testHasOneRoleOf() throws Exception {
        String[] roles = new String[] {"test/role3", "test/role2"};
        assertTrue(user.hasOneRoleOf(roles));
        roles = new String[] {"test/role3", "test/role4"};
        assertFalse(user.hasOneRoleOf(roles));
    }
}
