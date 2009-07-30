package org.intalio.tempo.workflow.fds.core;

import junit.framework.TestCase;

public class MessageFormatExceptionTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(MessageFormatExceptionTest.class);
    }
    
    public void testConstructor()throws Exception{
        MessageFormatException e = new MessageFormatException("message format not correct");
        assertEquals(e.getClass(), MessageFormatException.class);
        assertEquals(e.getMessage(), "message format not correct");
        
        Throwable throwable = new Exception();
        MessageFormatException e2 = new MessageFormatException("message", throwable);
        assertEquals(e2.getClass(), MessageFormatException.class);
        assertEquals(e2.getMessage(), "message");
        assertEquals(e2.getCause(),throwable);
    }

}
