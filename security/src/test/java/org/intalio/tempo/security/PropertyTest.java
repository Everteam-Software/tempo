package org.intalio.tempo.security;

import java.util.Properties;

import org.intalio.tempo.security.util.PropertyUtilsTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class PropertyTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(PropertyTest.class);
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PropertyTest.class);
    }
    
    public void testAll(){
        Property p1 = new Property();
        p1.setName("test1");
        p1.setValue("test2");
        
        Property p2 = new Property("test1", "test2");
        assertTrue(p1.equals(p2));
        p2.setValue("test1");
        p2.setName("hah");
        assertFalse(p1.equals(p2));
        
        logger.debug("hash code " +p1.hashCode());
        
    }
}
