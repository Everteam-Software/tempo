package org.intalio.tempo.security.token;

import junit.framework.TestCase;

public class TokenContextTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(TokenContextTest.class);
    }
    
    public void testStaticMethod(){
        String token = TokenContext.getToken();
        boolean hasToken = TokenContext.hasToken();
        TokenContext.remove();
        assertEquals(null != token, hasToken);
    }

}
