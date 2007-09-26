/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id: TokenTest.java,v 1.4 2005/03/29 22:09:07 ssahuc Exp $
 */

package org.intalio.tempo.security.impl;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.intalio.tempo.security.Property;

/**
 * Test the TokenHandler class.
 * 
 * @author <a href="mailto:boisvert@intalio.com">Alex Boisvert</a>
 * 
 */
public class TokenTest
    extends TestCase
{

	private final static boolean DEBUG = false;
	     
    public TokenTest( String suite)
    {
        super( suite );
    }
    
    /**
     * Test token creation / parsing 
     */
    public void testToken() throws Exception {
    	TokenHandler  handler;
    	String        token;
    	Property[]    props1, props2;
    	Property      p1, p2, p3;
    	
		handler = new TokenHandler();
		
		// no property
		props1 = new Property[] {};
		token = handler.createToken( props1 );
		props2 = handler.parseToken( token );
		assertTrue( props2.length == 0 );

		// one property		
		p1 = new Property( "name", "value" );
		props1 = new Property[] { p1 };
		token = handler.createToken( props1 );
		props2 = handler.parseToken( token );
		assertTrue( props2.length == 1 );
		assertProperty( p1, props2 );
		
		// two properties		
		p1 = new Property( "name1", "value1" );
		p2 = new Property( "name2", "value2=value2" );
		props1 = new Property[] { p1, p2 };
		token = handler.createToken( props1 );
		props2 = handler.parseToken( token );
		assertTrue( props2.length == 2 );
		assertProperty( p1, props2 );
		assertProperty( p2, props2 );


		// three properties		
		p1 = new Property( "name1", "value1" );
		p2 = new Property( "name2", "value2=value2" );
		p3 = new Property( "name3", "value3,value3,value3" );
		props1 = new Property[] { p1, p2, p3 };
		token = handler.createToken( props1 );
		if ( DEBUG ) System.out.println( "token: " + token );
		props2 = handler.parseToken( token );
		assertTrue( props2.length == 3 );
		assertProperty( p1, props2 );
		assertProperty( p2, props2 );
		assertProperty( p3, props2 );
    }


	/**
	 * Assert that a property has a given value within an array of properties.
	 * 
	 * @param expected expected property value
	 * @param properties array of properties
	 */
	public static void assertProperty( Property expected, Property[] properties )
	{
		for ( int i=0; i<properties.length; i++ ) {
			if ( properties[i].equals( expected ) ) {
				return;
			}
		}
		throw new AssertionFailedError( "Property not matched: " + expected.getName() );
	}


	/**
	 * Return a String representation for a Property array.
	 */
	public static String toString( Property[] props )
	{
		StringBuffer buf;
		
		buf = new StringBuffer();
		for ( int i=0; i<props.length; i++ ) {
			buf.append( props[ i ] );			
			buf.append( "\n" );
		}
		return buf.toString();
	}

}
