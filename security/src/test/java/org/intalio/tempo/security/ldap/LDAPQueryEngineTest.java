package org.intalio.tempo.security.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import junit.framework.TestCase;

import org.intalio.tempo.security.Property;

public class LDAPQueryEngineTest extends TestCase {
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
			
			return ++_count>_size;
		}

		
		public Object next() throws NamingException {
			
			return null;
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
			
			return null;
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
	    synchronized DirContext getRootContext() throws NamingException {
	        return new MockDirContext();
	    }

	    /**
	     * 
	     * @param branch
	     * @throws NamingException
	     */
	    synchronized DirContext getContext(String branch) throws NamingException {
	    	return new MockDirContext();
	    }
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		junit.textui.TestRunner.run(LDAPQueryEngineTest.class);
	}
	
	public void testQueryExist()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String id = "id";
		String field = "field";
		Map map = new HashMap();
		engine.queryExist(subject, cond, base, id, field, map);
	}
	
	public void testQueryExistRelation() throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String id = "id";
		String field = "field";
		String relBase = "relBase";
		Collection result = new ArrayList();
		Map map = new HashMap();
		engine.queryExistRelation(baseDN, id, field, relBase, result);
	}
	
	public void testQueryExtent()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		String sjbBase = "sjbBase";
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String id = "id";
		String field = "field";
		Collection result = new ArrayList();
		engine.queryExtent(sjbBase, id, result);
	}
	
	public void testQueryFields()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		Collection subjects = new ArrayList();
		String sjbBase = "sjbBase";
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String id = "id";
		String field = "field";
		Collection result = new ArrayList();
		engine.queryFields(subjects, base, id, field, result);
	}
	
	public void testQueryFields2()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		String sjbBase = "sjbBase";
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String id = "id";
		String field = "field";
		Collection result = new ArrayList();
		engine.queryFields(sjbBase, base, id, field, result);
	}
	
	public void testQueryFilteredFields()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		Collection subjects = new ArrayList();
		String sjbBase = "sjbBase";
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String fieldType = "fieldType";
		String id = "id";
		String field = "field";
		Collection result = new ArrayList();
		engine.queryFilteredFields(subjects, base, id, field, fieldType, result);
	}
	
	public void testQueryFilteredFields2()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		Collection subjects = new ArrayList();
		String sjbBase = "sjbBase";
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String fieldType = "fieldType";
		String id = "id";
		String field = "field";
		Collection result = new ArrayList();
		engine.queryFilteredFields(subject, base, id, field, fieldType, result);
	}
	
	public void testQueryInheritReference()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		Collection subjects = new ArrayList();
		String sjbBase = "sjbBase";
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String fieldType = "fieldType";
		String id = "id";
		String field = "field";
		Collection result = new ArrayList();
		result.add("task");
		engine.queryInheritReference(base, id, field, result);
	}
	
	public void testQueryInheritRelation()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		Collection subjects = new ArrayList();
		String sjbBase = "sjbBase";
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String fieldType = "fieldType";
		String id = "id";
		String field = "field";
		Collection result = new ArrayList();
		result.add("task");
		engine.queryInheritRelation(base, id, field, result);
	}
	
	public void testQueryNoRelation()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		Collection subjects = new ArrayList();
		String sjbBase = "sjbBase";
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String fieldType = "fieldType";
		String id = "id";
		String field = "field";
		Collection result = new ArrayList();
		engine.queryNoRelation(base, field, result);
	}
	
	public void testQueryProperties()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		Collection subjects = new ArrayList();
		String sjbBase = "sjbBase";
		String subject = "subject";
		String cond = "cond";
		String base = "base";
		String fieldType = "fieldType";
		String id = "id";
		String field = "field";
		Map<String, Property> result = new HashMap<String, Property>();
		Map<String, String> map = new HashMap<String, String>();
		engine.queryProperties(subject, base, id, map, result);
	}
	
	public void testQueryRelations()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		Collection subjects = new ArrayList();
		String sjbBase = "sjbBase";
		String subject = "subject";
		String relBase = "relBase";
		String cond = "cond";
		String base = "base";
		String fieldType = "fieldType";
		String id = "id";
		String rel = "rel";
		String field = "field";
		Collection<String> result = new ArrayList<String>();
		engine.queryRelations(subjects, sjbBase, id, relBase, rel, result);
	}
	
	public void testQueryRelations2()throws Exception{
		String baseDN = "baseDN";
		MockLDAPSecurityProvider provider = new MockLDAPSecurityProvider();
		provider.setPropertiesFile("src/test/resources/test.groups.properties");
		LDAPQueryEngine engine = new LDAPQueryEngine(provider, baseDN);
		
		Collection subjects = new ArrayList();
		String sjbBase = "sjbBase";
		String subject = "subject";
		String relBase = "relBase";
		String cond = "cond";
		String base = "base";
		String fieldType = "fieldType";
		String id = "id";
		String rel = "rel";
		String field = "field";
		Collection<String> result = new ArrayList<String>();
		engine.queryRelations(subject, sjbBase, id, relBase, rel, true, result);
	}
}
