package org.intalio.tempo.workflow.tas.axis2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class ExceptionTest extends TestCase {
    private final static Logger logger = LoggerFactory.getLogger(ExceptionTest.class);
    public void testAll(){
        try {
            throw new InvalidMessageFormatException();
        }
        catch(InvalidMessageFormatException e){
            
        }
        
        try {
            throw new InvalidMessageFormatException("test");
        }
        catch(InvalidMessageFormatException e){
            try {
                throw new InvalidMessageFormatException("test", e);
                }
            catch(InvalidMessageFormatException ee){
                try {
                    throw new InvalidMessageFormatException(ee);
                    }
                catch(InvalidMessageFormatException eee){
                    logger.info(eee.getMessage());
                    
                }
                
            }            
        }
        
        
        
    }
}
