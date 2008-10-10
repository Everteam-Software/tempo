package org.intalio.tempo.security.rbac;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class RBACPermissionTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(RBACPermissionTest.class);
    }
    
    public void testConstructor()throws Exception {
        String operation = "operation";
        String object = "object";
        Exception iae = null;
        try{
            new RBACPermission(null, object);
        }catch(Exception e){
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'operation' is null");
        
        iae = null;
        try{
            new RBACPermission(operation, null);
        }catch(Exception e){
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'object' is null");
    }
    
    public void testGetterMethod()throws Exception {
        String operation = "operation";
        String object = "object";
        RBACPermission permission = new RBACPermission(operation, object);
        assertEquals(permission.getObject(), object);
        assertEquals(permission.getOperation(), operation);
    }
    
    public void testSetterMethod()throws Exception {
        String operation = "operation";
        String object = "object";
        RBACPermission permission = new RBACPermission(operation, object);
        permission.setObject("newObject");
        permission.setOperation("newOperation");
        assertEquals(permission.getObject(), "newObject");
        assertEquals(permission.getOperation(), "newOperation");
    }
    
    public void testEuqals()throws Exception {
        String operation = "operation";
        String object = "object";
        RBACPermission permission1 = new RBACPermission(operation, object);
        RBACPermission permission2 = new RBACPermission(operation, object);
        RBACPermission permission3 = new RBACPermission(operation, object);
        permission3.setObject("newObject");
        permission3.setOperation("newOperation");
        assertTrue(permission1.equals(permission2));
        assertFalse(permission1.equals(permission3));
        assertFalse(permission1.equals("object"));
        assertFalse(permission1.equals(null));
    }
    
    public void testHashCode() throws Exception{
        String operation = "operation";
        String object = "object";
        RBACPermission permission = new RBACPermission(operation, object);
       Map<RBACPermission, Object> map = new HashMap<RBACPermission, Object>(1);
       map.put(permission, "anything");
       assertEquals("anything",map.get(permission));
    }

}
