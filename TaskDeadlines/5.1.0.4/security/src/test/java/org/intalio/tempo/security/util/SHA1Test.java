/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id: SHA1Test.java,v 1.1 2005/02/24 18:21:05 boisvert Exp $
 */

package org.intalio.tempo.security.util;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Test the SHA1 class.
 * 
 * @author <a href="mailto:boisvert@intalio.com">Alex Boisvert</a>
 * 
 */
public class SHA1Test
    extends TestCase
{
    
    public SHA1Test( String suite)
    {
        super( suite );
    }
    
    /**
     * Test string array generation / parsing  
     */
    public void testEncode() throws Exception {
        
        String[] text = {
            "abc", 
            "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq", 
            "" };
        String[] values = { 
            "qZk+NkcGgWq6PiVxeFDCbJzQ2J0=",
            "hJg+RBw70m66rkqh+VEp5eVGcPE=",
            "2jmj7l5rSw0yVb/vlWAYkK/YBwk=" };
        
        for ( int i=0; i<text.length; i++ ) {
            assertEquals( SHA1.encode( text[i] ), values[i] ); 
        }
        
        String hash1 = SHA1.encode( "test" );
        String hash2 = SHA1.encode( "test" );
        assertTrue( "Same hash", hash1.equals( hash2 ) );
    }
    
    
	public static void main( String[] args )
	{
		TestSuite suite = new TestSuite( SHA1Test.class);
		TestResult result = new TestResult();
		suite.run( result );
	}
	
}
