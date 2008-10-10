package org.intalio.tempo.security.simple;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.rbac.RBACException;

public class SimpleSecurityProviderTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(SimpleSecurityProviderTest.class);
    }
    
    public void testInitialize(){
        SimpleSecurityProvider provider = new SimpleSecurityProvider();
        Exception actualE = null;
        try{
            provider.initialize("anything");
        }catch(Exception e){
            actualE = e;
        }
        Exception expectedE = new RBACException( "Configuration object must be a 'java.util.Map'");
        assertEquals(actualE.getClass(), expectedE.getClass());
        assertEquals(actualE.getMessage(), expectedE.getMessage());
        
        Map<String, String> config = new HashMap<String, String>();
        Exception actualE2 = null;
        try {
            provider.initialize(config);
        } catch (Exception e) {
            actualE2 = e;
        }
        Exception expectedE2 = new IllegalStateException( "Missing configuration property 'configFile'");
        assertEquals(expectedE2.getClass(), actualE2.getClass());
        assertEquals(expectedE2.getMessage(), actualE2.getMessage());
        
        config.put("configFile", "testSimpleSecurity.xml");
        config.put("refreshInterval", "1");
        try{
            provider.initialize(config);
        }catch(Exception e){
            assertEquals(true, false);
        }
        
        try{
            provider.dispose();
        }catch(Exception e){
            assertTrue(false);
        }
        assertNull(provider.getDatabase());
    }

}
