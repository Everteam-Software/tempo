package org.intalio.tempo.security;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class SecurityComponentTest extends TestCase {
   private static final Logger logger = LoggerFactory.getLogger(SecurityComponentTest.class);
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(SecurityComponentTest.class);
    }
    
    @Subject SecurityComponent sc;
   
    
    @BeforeSpecification
    public void setup() throws Exception{
      sc = new SecurityComponent();
        
    }
    
    @Specification
    public void test() throws Exception{
        sc.setDefaultRealm("__realm");
        sc.setProviderClass("org.intalio.tempo.security.DummySecurityProvider");
        sc.setProperty("test", "testvalue");
        sc.init();
        sc.start();
        sc.stop();
    }
    
    public void tearDown() throws Exception{
       sc.shutDown();
    }
}
