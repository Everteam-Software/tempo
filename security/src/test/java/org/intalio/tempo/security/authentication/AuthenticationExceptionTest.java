package org.intalio.tempo.security.authentication;

import java.io.PrintStream;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationExceptionTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationException.class);
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AuthenticationExceptionTest.class);
    }
    
    public void testAll(){
        AuthenticationException e = new AuthenticationException("test");
        logger.info(e.toString());
        logger.info(e.getMessage());       
        e.printStackTrace(System.out);
        e.printStackTrace(new PrintWriter(System.out));
        
        
        try{
        AuthenticationException ee = new  AuthenticationException("This is an authentication exception", e);
        logger.info(ee.toString());
        logger.info(ee.getMessage());       
        ee.printStackTrace(System.out);
        ee.printStackTrace(new PrintWriter(System.out));
        throw ee;
        }catch(Exception eee){
            try{
                throw new AuthenticationException(eee);
            }catch(AuthenticationException e4){
                logger.info(e4.getMessage());
                logger.info(e4.getException().getMessage());
                logger.info(e4.toString());                     
                e4.printStackTrace(System.out);
                e4.printStackTrace(new PrintWriter(System.out));
            }
        }
        
 
 
    }
    
}
