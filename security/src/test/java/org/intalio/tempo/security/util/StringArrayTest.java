/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id: StringArrayTest.java,v 1.1 2003/12/05 19:12:48 boisvert Exp $
 */

package org.intalio.tempo.security.util;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Test the TokenHandler class.
 * 
 * @author <a href="mailto:boisvert@intalio.com">Alex Boisvert</a>
 * 
 */
public class StringArrayTest
    extends TestCase
{
    
    public StringArrayTest( String suite)
    {
        super( suite );
    }
    
    /**
     * Test string array generation / parsing  
     */
    public void testParsing() throws Exception {
		String[] array;
		String[] parsed;
    	String   delimited;

		// empty array
		array = new String[] {};
		delimited = StringArrayUtils.toCommaDelimited( array );
		parsed = StringArrayUtils.parseCommaDelimited( delimited );
		assertTrue( parsed.length == 0 );

		// one element		
		array = new String[] { "one" };
		delimited = StringArrayUtils.toCommaDelimited( array );
		parsed = StringArrayUtils.parseCommaDelimited( delimited );
		assertTrue( parsed.length == 1 );
		assertTrue( parsed[0].equals( array[0] ) );

		// two elements    	
    	array = new String[] { "one", "two" };
    	delimited = StringArrayUtils.toCommaDelimited( array );
		parsed = StringArrayUtils.parseCommaDelimited( delimited );
		assertTrue( parsed.length == 2 );
		assertTrue( parsed[0].equals( array[0] ) );
		assertTrue( parsed[1].equals( array[1] ) );
		
		assertTrue(StringArrayUtils.containsString(array,"one"));
		assertFalse(StringArrayUtils.containsString(array,"three"));
		
		try {
		    assertTrue(StringArrayUtils.containsString(array,null));
		    fail("expected exception");
		} catch (IllegalArgumentException e) {
		    
		}
		
		assertEquals(StringArrayUtils.addCommaDelimited("one","two"), "one,two");
    }
    
    
	public static void main( String[] args )
	{
		TestSuite suite = new TestSuite( StringArrayTest.class);
		TestResult result = new TestResult();
		suite.run( result );
	}
	
}
