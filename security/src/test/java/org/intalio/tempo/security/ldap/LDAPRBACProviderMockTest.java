package org.intalio.tempo.security.ldap;

import java.io.FileInputStream;
import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.intalio.tempo.security.ldap.LDAPSecurityProviderTest.MockLDAPSecurityProvider;
import org.intalio.tempo.security.rbac.provider.RBACProvider;

import junit.framework.TestCase;

public class LDAPRBACProviderMockTest extends TestCase {
	class MockLDAPSecurityProvider extends LDAPSecurityProvider{
		/**
	     * Obtain the root _context of this _context
	     *  
	     * @throws NamingException
	     */
	    synchronized DirContext getRootContext() throws NamingException {
	        return null;
	    }

	    /**
	     * 
	     * @param branch
	     * @throws NamingException
	     */
	    synchronized DirContext getContext(String branch) throws NamingException {
	    	return null;
	    }
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(LDAPRBACProviderMockTest.class);
	}
	
	public void testGetter()throws Exception{
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		
		LDAPRBACProvider rbacProvider = (LDAPRBACProvider)provider.getRBACProvider(null);
		assertEquals("LDAP RBAC Provider",rbacProvider.getName());
		assertNotNull(rbacProvider.getQuery());
		Exception ex = null;
		try{
			rbacProvider.getAdmin();
		}catch(Exception e){
			ex = e;
		}
		assertEquals(ex.getClass(), RuntimeException.class);
		
		ex = null;
		try{
			rbacProvider.getRuntime();
		}catch(Exception e){
			ex = e;
		}
		assertEquals(ex.getClass(), RuntimeException.class);
	}
	
	public void testDispose()throws Exception{
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		
		LDAPRBACProvider rbacProvider = (LDAPRBACProvider)provider.getRBACProvider(null);
		rbacProvider.dispose();
	}
	
	public void testInitialize()throws Exception{
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		
		LDAPRBACProvider rbacProvider = (LDAPRBACProvider)provider.getRBACProvider(null);
		Properties config = new Properties();
        config.load( new FileInputStream( "src/test/resources/test.groups.properties" ) );
        
		rbacProvider.initialize(config);
	}

}
