/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.util;

import java.util.ArrayList;

/**
 * Utility class to manipulate String arrays.
 */
public class StringArrayUtils
{

	/**
	 * Convert an array of string elements into a comma-delimited string.
	 * <p>
	 * Example:  ["cat","dog","kangaroo"] -> "cat,dog,kangaroo"
	 * 
	 * @param array array of string elements
	 * @return comma-delimited elements as string
	 */
	public static String toCommaDelimited( String[] array )
	{
		StringBuffer buf;
		
		buf = new StringBuffer();
		for ( int i=0;i<array.length; i++ ) {
			buf.append( array[i] );
			if ( i < array.length-1 ) buf.append( "," );
		}
		return buf.toString();		
	}
	 
    /**
     * Add a value to an existiing comma-delimited string
     */
    public static String addCommaDelimited( String existing, String value ) {
        return existing + "," + value;
    }
    

	/**
	 * Parse a comma-delimited string into a string array.
	 */
	public static String[] parseCommaDelimited( String delimited )
	{
		ArrayList<String> list;
		int           pos;
		StringBuffer  buf;
		String        item;
		
		list = new ArrayList<String>();
		buf = new StringBuffer( delimited );
		while ( buf.length() > 0 ) {
			for ( pos=0; pos<buf.length(); pos++ ) {
				if ( buf.charAt( pos ) == ',' ) break;
			}
			item = buf.substring( 0, pos );
			list.add( item );
			buf = buf.delete( 0, pos+1 );
		}
		
		return (String[]) list.toArray( new String[ list.size() ] );		
	}
	
	
	/**
	 * Return true if a string array contains a given string.
	 */
	public static boolean containsString( String[] list, String item )
	{
		if ( item == null )
			throw new IllegalArgumentException( "Argument 'item' is null" );
		
		for ( int i=0; i<list.length; i++ ) {
		    if ( item.equals( list[i]) ) 
		        return true;
		}
		
		return false;
	}


}
