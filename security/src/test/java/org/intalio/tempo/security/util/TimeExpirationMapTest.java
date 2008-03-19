/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id: TimeExpirationMapTest.java,v 1.1 2005/02/24 18:21:05 boisvert Exp $
 */

package org.intalio.tempo.security.util;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Test the TimeExpirationMap class.
 * 
 * @author <a href="mailto:boisvert@intalio.com">Alex Boisvert</a>
 * 
 */
public class TimeExpirationMapTest
    extends TestCase
{
    
    public TimeExpirationMapTest( String suite)
    {
        super( suite );
    }
    
    /**
     * Test expiration of elements  
     */
    public void testExpiration() throws Exception {
        TimeExpirationMap map = new TimeExpirationMap( 5000, 1000 );
        
        Long key1 = new Long( 1 );
        Long value1 = new Long( 11 );
        
        map.put( key1, value1 );
        assertTrue( "Key not found", map.get( key1 ) != null );
        assertTrue( "Incorrect value", map.get( key1 ).equals( value1 ) );
        
        try {
            Thread.sleep( 2000 );
        } catch ( InterruptedException except ) {
            // ignore
        }
        
        Long key2 = new Long( 2 );
        Long value2 = new Long( 22 );
        map.put( key2, value2 );

        assertTrue( "Key2 not found", map.get( key2 ) != null );
        assertTrue( "Incorrect value2", map.get( key2 ).equals( value2 ) );
        
        assertTrue( "Key1 not found after pause", map.get( key1 ) != null );
        assertTrue( "Incorrect value1 after pause", map.get( key1 ).equals( value1 ) );

        try {
            Thread.sleep( 4000 );
        } catch ( InterruptedException except ) {
            // ignore
        }
        
        assertTrue( "Key1 not expired", map.get( key1 ) == null );
        
        assertTrue( "Key2 not found after pause", map.get( key2 ) != null );
        assertTrue( "Incorrect value2 after pause", map.get( key2 ).equals( value2 ) );

        try {
            Thread.sleep( 2000 );
        } catch ( InterruptedException except ) {
            // ignore
        }
        
        assertTrue( "Key2 not expired", map.get( key2 ) == null );
        
        TimeExpirationMap map2 = new TimeExpirationMap(5000, 1000, 50, 0.75F);
        map.put("hello","3");
        assertEquals(map.put("hello", null),"3");
        assertNull(map.get("today"));
    }
    
    
	public static void main( String[] args )
	{
		TestSuite suite = new TestSuite( TimeExpirationMapTest.class);
		TestResult result = new TestResult();
		suite.run( result );
	}
	
}
