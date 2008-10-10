package org.intalio.tempo.security.rbac;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import junit.framework.TestCase;

public class AllExceptionTest extends TestCase {

    public void testAccessControlException() {
        Exception e = new Exception();
        String message = "exception message";
        AccessControlException ace1 = new AccessControlException(message, e);
        AccessControlException ace2 = new AccessControlException(message);
        AccessControlException ace3 = new AccessControlException(e);

        assertEquals(ace1.getClass(), AccessControlException.class);
        assertEquals(ace2.getClass(), AccessControlException.class);
        assertEquals(ace3.getClass(), AccessControlException.class);

        assertEquals(ace1.getMessage(), message);
        assertEquals(ace2.getMessage(), message);
        assertEquals(ace3.getMessage(), null);

        assertEquals(ace1.getException(), e);
        assertEquals(ace2.getException(), null);
        assertEquals(ace3.getException(), e);
    }

    public void testObjectNotFoundException() {
        Exception e = new Exception();
        String message = "exception message";
        ObjectNotFoundException ace1 = new ObjectNotFoundException(message, e);
        ObjectNotFoundException ace2 = new ObjectNotFoundException(message);
        ObjectNotFoundException ace3 = new ObjectNotFoundException(e);

        assertEquals(ace1.getClass(), ObjectNotFoundException.class);
        assertEquals(ace2.getClass(), ObjectNotFoundException.class);
        assertEquals(ace3.getClass(), ObjectNotFoundException.class);

        assertEquals(ace1.getMessage(), message);
        assertEquals(ace2.getMessage(), message);
        assertEquals(ace3.getMessage(), null);

        assertEquals(ace1.getException(), e);
        assertEquals(ace2.getException(), null);
        assertEquals(ace3.getException(), e);
    }

    public void testRBACException() throws Exception {
        Exception e = new Exception();
        String message = "exception message";
        RBACException ace1 = new RBACException(message, e);
        RBACException ace2 = new RBACException(message);
        RBACException ace3 = new RBACException(e);

        assertEquals(ace1.getClass(), RBACException.class);
        assertEquals(ace2.getClass(), RBACException.class);
        assertEquals(ace3.getClass(), RBACException.class);

        assertEquals(ace1.getMessage(), message);
        assertEquals(ace2.getMessage(), message);
        assertEquals(ace3.getMessage(), null);

        assertEquals(ace1.getException(), e);
        assertEquals(ace2.getException(), null);
        assertEquals(ace3.getException(), e);

        ace1.printStackTrace();
        ace2.printStackTrace();
        ace3.printStackTrace();

        ace1.toString();
        ace2.toString();
        ace3.toString();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos);
        ace1.printStackTrace(ps);
        ace2.printStackTrace(ps);
        ace3.printStackTrace(ps);

        PrintWriter pw = new PrintWriter(bos);
        ace1.printStackTrace(pw);
        ace2.printStackTrace(pw);
        ace3.printStackTrace(pw);
    }

    public void testRoleNotFoundException() {
        Exception e = new Exception();
        String message = "exception message";
        RoleNotFoundException ace1 = new RoleNotFoundException(message, e);
        RoleNotFoundException ace2 = new RoleNotFoundException(message);
        RoleNotFoundException ace3 = new RoleNotFoundException(e);

        assertEquals(ace1.getClass(), RoleNotFoundException.class);
        assertEquals(ace2.getClass(), RoleNotFoundException.class);
        assertEquals(ace3.getClass(), RoleNotFoundException.class);

        assertEquals(ace1.getMessage(), message);
        assertEquals(ace2.getMessage(), message);
        assertEquals(ace3.getMessage(), null);

        assertEquals(ace1.getException(), e);
        assertEquals(ace2.getException(), null);
        assertEquals(ace3.getException(), e);
    }

    public void testSessionNotFoundException() {
        Exception e = new Exception();
        String message = "exception message";
        SessionNotFoundException ace1 = new SessionNotFoundException(message, e);
        SessionNotFoundException ace2 = new SessionNotFoundException(message);
        SessionNotFoundException ace3 = new SessionNotFoundException(e);

        assertEquals(ace1.getClass(), SessionNotFoundException.class);
        assertEquals(ace2.getClass(), SessionNotFoundException.class);
        assertEquals(ace3.getClass(), SessionNotFoundException.class);

        assertEquals(ace1.getMessage(), message);
        assertEquals(ace2.getMessage(), message);
        assertEquals(ace3.getMessage(), null);

        assertEquals(ace1.getException(), e);
        assertEquals(ace2.getException(), null);
        assertEquals(ace3.getException(), e);
    }

    public void testUserNotFoundException() {
        Exception e = new Exception();
        String message = "exception message";
        UserNotFoundException ace1 = new UserNotFoundException(message, e);
        UserNotFoundException ace2 = new UserNotFoundException(message);
        UserNotFoundException ace3 = new UserNotFoundException(e);

        assertEquals(ace1.getClass(), UserNotFoundException.class);
        assertEquals(ace2.getClass(), UserNotFoundException.class);
        assertEquals(ace3.getClass(), UserNotFoundException.class);

        assertEquals(ace1.getMessage(), message);
        assertEquals(ace2.getMessage(), message);
        assertEquals(ace3.getMessage(), null);

        assertEquals(ace1.getException(), e);
        assertEquals(ace2.getException(), null);
        assertEquals(ace3.getException(), e);
    }

    @SuppressWarnings("static-access")
    public void testRBACConstants() {
        RBACConstants constant = new RBACConstants();
        assertEquals(RBACConstants.PROPERTY_DESCRIPTION, constant.PROPERTY_DESCRIPTION);
        assertEquals(RBACConstants.PROPERTY_EMAIL, constant.PROPERTY_EMAIL);
        assertEquals(RBACConstants.PROPERTY_FULL_NAME, constant.PROPERTY_FULL_NAME);
    }

}
