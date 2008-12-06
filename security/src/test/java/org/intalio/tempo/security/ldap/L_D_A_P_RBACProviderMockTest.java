package org.intalio.tempo.security.ldap;

import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import junit.framework.TestCase;

import org.intalio.tempo.security.rbac.RBACQuery;



public class L_D_A_P_RBACProviderMockTest extends TestCase {
	class MockNamingEnumeration implements NamingEnumeration{
		private int _size;
		private int _count;
		
		public MockNamingEnumeration(int size){
			_size = size;
			_count = 0;
		}

		
		public void close() throws NamingException {
			
			
		}

		
		public boolean hasMore() throws NamingException {
			
			return _count < _size;
		}

		
		public Object next() throws NamingException {
			_count++;
			return new SearchResult("name=name","object",new BasicAttributes());
		}

		
		public boolean hasMoreElements() {
			
			return false;
		}

		
		public Object nextElement() {
			
			return null;
		}
		
	}
	class MockDirContext implements DirContext{

		
		public void bind(Name arg0, Object arg1, Attributes arg2)
				throws NamingException {
			
			
		}

		
		public void bind(String arg0, Object arg1, Attributes arg2)
				throws NamingException {
			
			
		}

		
		public DirContext createSubcontext(Name arg0, Attributes arg1)
				throws NamingException {
			
			return null;
		}

		
		public DirContext createSubcontext(String arg0, Attributes arg1)
				throws NamingException {
			
			return null;
		}

		
		public Attributes getAttributes(Name arg0) throws NamingException {
			
			return null;
		}

		
		public Attributes getAttributes(String arg0) throws NamingException {
			return new BasicAttributes();
		}

		
		public Attributes getAttributes(Name arg0, String[] arg1)
				throws NamingException {
			
			return null;
		}

		
		public Attributes getAttributes(String arg0, String[] arg1)
				throws NamingException {
			
			return null;
		}

		
		public DirContext getSchema(Name arg0) throws NamingException {
			
			return null;
		}

		
		public DirContext getSchema(String arg0) throws NamingException {
			
			return null;
		}

		
		public DirContext getSchemaClassDefinition(Name arg0)
				throws NamingException {
			
			return null;
		}

		
		public DirContext getSchemaClassDefinition(String arg0)
				throws NamingException {
			
			return null;
		}

		
		public void modifyAttributes(Name arg0, ModificationItem[] arg1)
				throws NamingException {
			
			
		}

		
		public void modifyAttributes(String arg0, ModificationItem[] arg1)
				throws NamingException {
			
			
		}

		
		public void modifyAttributes(Name arg0, int arg1, Attributes arg2)
				throws NamingException {
			
			
		}

		
		public void modifyAttributes(String arg0, int arg1, Attributes arg2)
				throws NamingException {
			
			
		}

		
		public void rebind(Name arg0, Object arg1, Attributes arg2)
				throws NamingException {
			
			
		}

		
		public void rebind(String arg0, Object arg1, Attributes arg2)
				throws NamingException {
			
			
		}

		
		public NamingEnumeration<SearchResult> search(Name arg0, Attributes arg1)
				throws NamingException {
			
			return null;
		}

		
		public NamingEnumeration<SearchResult> search(String arg0,
				Attributes arg1) throws NamingException {
			
			return null;
		}

		
		public NamingEnumeration<SearchResult> search(Name arg0,
				Attributes arg1, String[] arg2) throws NamingException {
			
			return null;
		}

		
		public NamingEnumeration<SearchResult> search(String arg0,
				Attributes arg1, String[] arg2) throws NamingException {
			
			return null;
		}

		
		public NamingEnumeration<SearchResult> search(Name arg0, String arg1,
				SearchControls arg2) throws NamingException {
			
			return null;
		}

		
		public NamingEnumeration<SearchResult> search(String arg0, String arg1,
				SearchControls arg2) throws NamingException {
			
			return new MockNamingEnumeration(1);
		}

		
		public NamingEnumeration<SearchResult> search(Name arg0, String arg1,
				Object[] arg2, SearchControls arg3) throws NamingException {
			
			return new MockNamingEnumeration(1);
		}

		
		public NamingEnumeration<SearchResult> search(String arg0, String arg1,
				Object[] arg2, SearchControls arg3) throws NamingException {
			
			return new MockNamingEnumeration(1);
		}

		
		public Object addToEnvironment(String arg0, Object arg1)
				throws NamingException {
			
			return null;
		}

		
		public void bind(Name arg0, Object arg1) throws NamingException {
			
			
		}

		
		public void bind(String arg0, Object arg1) throws NamingException {
			
			
		}

		
		public void close() throws NamingException {
			
			
		}

		
		public Name composeName(Name arg0, Name arg1) throws NamingException {
			
			return null;
		}

		
		public String composeName(String arg0, String arg1)
				throws NamingException {
			
			return null;
		}

		
		public Context createSubcontext(Name arg0) throws NamingException {
			
			return null;
		}

		
		public Context createSubcontext(String arg0) throws NamingException {
			
			return null;
		}

		
		public void destroySubcontext(Name arg0) throws NamingException {
			
			
		}

		
		public void destroySubcontext(String arg0) throws NamingException {
			
			
		}

		
		public Hashtable<?, ?> getEnvironment() throws NamingException {
			
			return null;
		}

		
		public String getNameInNamespace() throws NamingException {
			
			return null;
		}

		
		public NameParser getNameParser(Name arg0) throws NamingException {
			
			return null;
		}

		
		public NameParser getNameParser(String arg0) throws NamingException {
			
			return null;
		}

		
		public NamingEnumeration<NameClassPair> list(Name arg0)
				throws NamingException {
			
			return null;
		}

		
		public NamingEnumeration<NameClassPair> list(String arg0)
				throws NamingException {
			
			return new MockNamingEnumeration(2);
		}

		
		public NamingEnumeration<Binding> listBindings(Name arg0)
				throws NamingException {
			
			return null;
		}

		
		public NamingEnumeration<Binding> listBindings(String arg0)
				throws NamingException {
			
			return null;
		}

		
		public Object lookup(Name arg0) throws NamingException {
			
			return null;
		}

		
		public Object lookup(String arg0) throws NamingException {
			
			return null;
		}

		
		public Object lookupLink(Name arg0) throws NamingException {
			
			return null;
		}

		
		public Object lookupLink(String arg0) throws NamingException {
			
			return null;
		}

		
		public void rebind(Name arg0, Object arg1) throws NamingException {
			
			
		}

		
		public void rebind(String arg0, Object arg1) throws NamingException {
			
			
		}

		
		public Object removeFromEnvironment(String arg0) throws NamingException {
			
			return null;
		}

		
		public void rename(Name arg0, Name arg1) throws NamingException {
			
			
		}

		
		public void rename(String arg0, String arg1) throws NamingException {
			
			
		}

		
		public void unbind(Name arg0) throws NamingException {
			
			
		}

		
		public void unbind(String arg0) throws NamingException {
			
			
		}
		
	}
	class MockLDAPSecurityProvider extends LDAPSecurityProvider{
		/**
	     * Obtain the root _context of this _context
	     *  
	     * @throws NamingException
	     */
		
		private DirContext _context = new MockDirContext();
		
	    synchronized DirContext getRootContext() throws NamingException {
	        return _context;
	    }

	    /**
	     * 
	     * @param branch
	     * @throws NamingException
	     */
	    synchronized DirContext getContext(String branch) throws NamingException {
	    	return _context;
	    }
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(L_D_A_P_RBACProviderMockTest.class);
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
	
	public void testQuery()throws Exception{
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		
		LDAPRBACProvider rbacProvider = (LDAPRBACProvider)provider.getRBACProvider(null);
		RBACQuery query = rbacProvider.getQuery();
		query.ascendantRoles("role");
		query.assignedRoles("user");
		query.assignedUsers("role");
		query.authorizedRoles("user");
		query.authorizedUsers("role");
		query.descendantRoles("role");
		query.roleProperties("role");
		query.topRoles("intalio");
		query.userOperationsOnObject("user", "object");
		query.userProperties("user");
//		query.roleOperationsOnObject("role", "object");
	}
}
