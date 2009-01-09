/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id: SimpleTest.java,v 1.9 2005/02/24 18:20:38 boisvert Exp $
 */

package org.intalio.tempo.security.simple;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.authentication.AuthenticationQuery;
import org.intalio.tempo.security.authentication.AuthenticationRuntime;
import org.intalio.tempo.security.authentication.UserNotFoundException;

import org.intalio.tempo.security.authentication.provider.AuthenticationProvider;

import org.intalio.tempo.security.provider.SecurityProvider;

import org.intalio.tempo.security.rbac.RBACConstants;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.RBACRuntime;

import org.intalio.tempo.security.rbac.provider.RBACProvider;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import java.util.HashMap;

public class SimpleTest
    extends TestCase
{
    
    public SimpleTest( String suite)
    {
        super( suite );
    }


	SecurityProvider getProvider()
		throws Exception
	{
		SimpleSecurityProvider  provider;
		HashMap<String,String>  props;
		
		props = new HashMap<String,String>();
		props.put( "configFile", "testSimpleSecurity.xml" );
		
		provider = new SimpleSecurityProvider();
		provider.initialize( props );
		
		return provider;
	}

    public void setUp()
        throws Exception
    {
        // nothing
    }

    public void tearDown() 
        throws Exception
    {
        // nothing
    }


	/**
	 * Test provider factory.
	 */
    public void testProvider()
        throws Exception
    {
		SecurityProvider  provider;
		String[]          realms;
		
		provider = getProvider();
		realms = provider.getRealms();
		assertSameValues( new String[] { "intalio", "exolab", "proto", "" },
						  realms );
						      	
						      	
		assertNotNull( provider.getName() );

		assertNotNull( provider.getRBACProvider( "intalio" ) );						      	
		assertNotNull( provider.getAuthenticationProvider( "intalio" ) );						      	

		assertNotNull( provider.getRBACProvider( "exolab" ) );						      	
		assertNotNull( provider.getAuthenticationProvider( "exolab" ) );						      	
    }


	/**
	 * Test authentication functions.
	 */
	public void testAuthentication()
		throws Exception
	{
		SecurityProvider        provider;
		AuthenticationProvider  auth;
		
		provider = getProvider();
		
        // ****
		// **** test runtime interface
		// ****
		AuthenticationRuntime  runtime;
		Property[]			   credentials;
		Property 			   password;
		AuthenticationException caught;
		
		auth = provider.getAuthenticationProvider( "exolab" );
		runtime = auth.getRuntime();		

		// test positive login as "castor"		
		password = new Property( AuthenticationConstants.PROPERTY_PASSWORD,
								 "castor" );
		credentials = new Property[] { password };		
		assertTrue( runtime.authenticate( "exolab\\castor", credentials ) );

		// test negative login as "castor" - invalid password	
		password = new Property( AuthenticationConstants.PROPERTY_PASSWORD,
								 "xxx" );
		credentials = new Property[] { password };		
		assertFalse( runtime.authenticate( "exolab\\castor", credentials ) );

		// test negative login as "castor" - no password provided
		credentials = new Property[] {};		
		assertFalse( runtime.authenticate( "exolab\\castor", credentials ) );

		// test unknown user triggers exception		
		password = new Property( AuthenticationConstants.PROPERTY_PASSWORD,
								 "xxx" );
		credentials = new Property[] { password };
		caught = null;
		try {		
			assertFalse( runtime.authenticate( "exolab\\unknown", credentials ) );
		} catch ( UserNotFoundException except ) {
			caught = except;	
		}
		assertNotNull( caught );

		// test default realm
		auth = provider.getAuthenticationProvider( "" );
		runtime = auth.getRuntime();		
				
		password = new Property( AuthenticationConstants.PROPERTY_PASSWORD,
								 "eng2" );
		credentials = new Property[] { password };		
		assertTrue( runtime.authenticate( "eng2", credentials ) );
		assertTrue( runtime.authenticate( "\\eng2", credentials ) );

		// test case-insensitive login		
		assertTrue( runtime.authenticate( "Eng2", credentials ) );
		assertTrue( runtime.authenticate( "ENG2", credentials ) );
		assertTrue( runtime.authenticate( "intalio\\ENG2", credentials ) );
		assertTrue( runtime.authenticate( "INTALIO\\ENG2", credentials ) );
		assertTrue( runtime.authenticate( "INTALIO:ENG2", credentials ) );

		// ****
		// **** test query interface
		// ****
		AuthenticationQuery query;
		
		auth = provider.getAuthenticationProvider( "exolab" );
		query = auth.getQuery();
		
		// try password retrieval
		credentials = query.getUserCredentials( "exolab\\castor" );
		password = new Property( AuthenticationConstants.PROPERTY_PASSWORD,
								 "castor" );
		assertProperty( password, credentials );

		// test unknown user triggers exception		
		password = new Property( AuthenticationConstants.PROPERTY_PASSWORD,
								 "xxx" );
		credentials = new Property[] { password };
		caught = null;
		try {		
			credentials = query.getUserCredentials( "exolab\\unknown" );
		} catch ( UserNotFoundException except ) {
			caught = except;	
		}
		assertNotNull( caught );
	}
	
	
	/**
	 * Test RBAC functions.
	 */
	public void testRBAC()
		throws Exception
	{
		SecurityProvider        provider;
		RBACProvider            rbac;
		
		provider = getProvider();
		
		// ****
		// **** test runtime interface
		// ****
		RBACRuntime  runtime;
		Property[]   props;
		Property     name;
		Property     description;
		Property     email;
		String[]     roles;
		RBACException caught;

		rbac = provider.getRBACProvider( "intalio" );
		runtime = rbac.getRuntime();
		
		// check direct permission
		roles = new String[] { "eng" };		
		assertTrue( runtime.checkAccess( "eng1", roles, "read", "code" ) );
		
		// test inherited role permission
		roles = new String[] { "eng" };		
		assertTrue( runtime.checkAccess( "eng-manager1", roles, "read", "code" ) );

		// test multiple active roles
		roles = new String[] { "eng", "eng-manager" };		
		assertTrue( runtime.checkAccess( "eng-manager1", roles, "approve", "specification" ) );

		// negative test:  invalid operation
		roles = new String[] { "eng" };		
		assertFalse( runtime.checkAccess( "eng-manager1", roles, "xxx", "code" ) );
		
		// negative test:  invalid object
		roles = new String[] { "eng" };		
		assertFalse( runtime.checkAccess( "eng-manager1", roles, "read", "xxx" ) );

		// negative test:  not inherited permission
		roles = new String[] { "prod-manager" };		
		assertFalse( runtime.checkAccess( "prod-manager1", roles, "read", "code" ) );

		// negative test:  role not active for permission
		roles = new String[] { "eng" };		
		assertFalse( runtime.checkAccess( "eng-manager1", roles, "perf-review", "report" ) );

		// negative test:  role not active for permission
		roles = new String[] { "eng", "manager" };		
		assertFalse( runtime.checkAccess( "eng-manager1", roles, "approve", "specification" ) );

		// negative test:  role not active for permission
		roles = new String[] { "eng" };		
		assertFalse( runtime.checkAccess( "eng-manager1", roles, "perf-review", "report" ) );

		// negative test:  has object access but not operation
		roles = new String[] { "eng" };		
		assertFalse( runtime.checkAccess( "eng1", roles, "review", "specification" ) );

		// negative test:  has operation but not object access
		roles = new String[] { "eng" };		
		assertFalse( runtime.checkAccess( "eng1", roles, "write", "specification" ) );

		// negative test: invalid role
		caught = null;
		try {		
			roles = new String[] { "eng" };		
			assertFalse( runtime.checkAccess( "xxx", roles, "read", "code" ) );
		} catch ( org.intalio.tempo.security.rbac.UserNotFoundException except ) {
			caught = except;	
		}
		assertNotNull( caught );
		
		// negative test:  user not authorized to role
		caught = null;
		try {		
			roles = new String[] { "manager" };		
			assertFalse( runtime.checkAccess( "eng1", roles, "perf-review", "report" ) );
		} catch ( RBACException except ) {
			caught = except;	
		}
		assertNotNull( caught );
		
		// ****
		// **** test runtime interface
		// ****
		RBACQuery  query;
		String[]   users;
		String[]   operations;
		
		rbac = provider.getRBACProvider( "exolab" );
		query = rbac.getQuery();
		
		// check top roles
		roles = query.topRoles( "exolab" );
		assertSameValues( new String[] { "exolab\\committer" },
						  roles );
		
		// check assigned roles
		roles = query.assignedRoles( "exolab\\castor" );
		assertSameValues( new String[] { "exolab\\committer" },
						  roles );
		
		// check authorized roles
		roles = query.authorizedRoles( "exolab\\castor" );
		assertSameValues( new String[] { "exolab\\committer", "exolab\\participant" },
						  roles );
		
		users = query.assignedUsers( "exolab\\committer" );
		assertSameValues( new String[] { "exolab\\castor", "exolab\\tyrex" },
						  users );

		users = query.assignedUsers( "exolab\\participant" );
		assertSameValues( new String[] { "exolab\\anonymous" },
						  users );

		users = query.authorizedUsers( "exolab\\committer" );
		assertSameValues( new String[] { "exolab\\castor", "exolab\\tyrex" },
						  users );

		users = query.authorizedUsers( "exolab\\participant" );
		assertSameValues( new String[] { "exolab\\castor", "exolab\\tyrex", "exolab\\anonymous" },
						  users );
						  
	    roles = query.descendantRoles( "exolab\\committer" );
		assertSameValues( new String[] { "exolab\\participant" },
						  roles );
	    
		roles = query.descendantRoles( "exolab\\participant" );
		assertSameValues( new String[] {},
						  roles );
		
		operations = query.roleOperationsOnObject( "exolab\\participant", "cvs" );
		assertSameValues( new String[] { "checkout" },
						  operations );

		operations = query.roleOperationsOnObject( "exolab\\committer", "cvs" );
		assertSameValues( new String[] { "checkout", "commit" },
						  operations );

		operations = query.userOperationsOnObject( "exolab\\castor", "cvs" );
		assertSameValues( new String[] { "checkout", "commit" },
						  operations );


		// check user properties		
		name = new Property( RBACConstants.PROPERTY_FULL_NAME,
			 				 "Castor Workaholic" );
		email = new Property( RBACConstants.PROPERTY_EMAIL,
							  "castor@exolab.org" );
		props = query.userProperties( "exolab\\castor" );
		assertProperty( name, props );
		assertProperty( email, props );
								  
		// check role properties		
		description = new Property( RBACConstants.PROPERTY_DESCRIPTION,
									"Community Participant" );
		props = query.roleProperties( "exolab\\participant" );
		assertProperty( description, props );
	}


	/**
	 * Assert that two string arrays have the same containing values.
	 * 
	 * @param expected expected values
	 * @param actual actual values
	 */
	public static void assertSameValues( String[] expected, String[] actual )
	{
		HashMap<String,String>  map;
		boolean  pass;
		StringBuffer buf;
		
		pass = true;
		
		if ( expected == null ) {
			throw new IllegalArgumentException( "Argument 'expected' is null" );
		}
		if ( actual == null ) {
			throw new IllegalArgumentException( "Argument 'actual' is null" );
		}
		if ( expected.length != actual.length ) {
			pass = false;			
		}
		map = new HashMap<String,String>();
		for ( int i=0; i<expected.length; i++ ) {
			map.put( expected[ i ], expected[ i ] );			
		}
		for ( int i=0; i<actual.length; i++ ) {
			if ( map.remove( actual[ i ] ) == null ) {
				pass = false;
			}			
		}
		
		if ( map.size() != 0 ) {
			// leftover elements
			pass = false;
		}
		
		if ( ! pass ) {
			buf = new StringBuffer();
			buf.append( "String arrays different: " );
			buf.append( "\nExpected:\n" );
			for ( int i=0; i<expected.length; i++ ) {
				buf.append( expected[ i ] );			
				buf.append( "\n" );
			}
			buf.append( "\nActual:\n" );
			for ( int i=0; i<actual.length; i++ ) {
				buf.append( actual[ i ] );			
				buf.append( "\n" );
			}
			throw new AssertionFailedError( buf.toString() );		
		}				
	}


	/**
	 * Assert that a property has a given value within an array of properties.
	 * 
	 * @param expected expected property value
	 * @param properties array of properties
	 */
	public static boolean assertProperty( Property expected, Property[] properties )
	{
		for ( int i=0; i<properties.length; i++ ) {
			if ( properties[i].equals( expected ) ) {
				return true;
			}
		}
		throw new AssertionFailedError( "Property not matched: " + expected.getName() );
	}
	

	/**
	 * Assert that a condition is false.
	 */
	static public void assertFalse( boolean b )
	{
		assertTrue( ! b );	
	}    


	public static void main( String[] args )
	{
		TestSuite suite = new TestSuite( SimpleTest.class);
		TestResult result = new TestResult();
		suite.run( result );
	}
}
