package org.intalio.tempo.workflow.fds.dispatchers;

import junit.framework.TestCase;

import org.intalio.tempo.workflow.fds.dispatches.InvalidInputFormatException;

public class InvalidInputFormatExceptionTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(InvalidInputFormatExceptionTest.class);
    }
    
    public void testMessageConstructor()throws Exception{
        InvalidInputFormatException e = new InvalidInputFormatException("message invalid input format");
        assertNotNull(e);
        assertEquals(e.getClass(), InvalidInputFormatException.class);
        assertEquals(e.getMessage(),"message invalid input format");
    }
    
    public void testMessageCauseConstructor()throws Exception{
        Throwable cause  = new Exception();
        InvalidInputFormatException e = new InvalidInputFormatException("message invalid input format", cause);
        assertNotNull(e);
        assertEquals(e.getClass(), InvalidInputFormatException.class);
        assertEquals(e.getMessage(),"message invalid input format");
        assertEquals(e.getCause(), cause);
    }
    
    public void testCauseConstructor()throws Exception{
        Throwable cause  = new Exception();
        InvalidInputFormatException e = new InvalidInputFormatException(cause);
        assertNotNull(e);
        assertEquals(e.getClass(), InvalidInputFormatException.class);
        assertEquals(e.getCause(), cause);
    }
}