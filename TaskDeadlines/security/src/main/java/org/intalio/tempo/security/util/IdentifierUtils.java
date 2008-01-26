/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.util;

/**
 * Utility methods for manipulating identifiers.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class IdentifierUtils
{
    
    /**
     * Valid realm separator characters.
     */
    public static final String REALM_SEPARATORS = "\\:|/";

    
    /**
     * Get the realm from identifier, based on standard realm separators.
     * <p>
     * Example:  If the identifier is "marketing\john", this method
     * returns "marketing".
     *
     * @param id identifier
     * @return realm 
     */
    public static String getRealm( String id )
    {
        return getRealm( id, REALM_SEPARATORS );
    }
    
    
    /**
     * Get the realm from identifier, based on a given list of 
     * realm separators.
     * <p>
     * Example:  If the identifier is "marketing\john", this method
     * returns "marketing" (assuming that "\" is a separator).
     *
     * @param id identifier
     * @param separators list of realm separators
     * @return realm 
     */
    public static String getRealm( String id, String separators )
    {
        if ( id == null ) {
            throw new IllegalArgumentException( "Argument 'id' is null" );
        }
        if ( separators == null ) {
            throw new IllegalArgumentException( "Argument 'separators' is null" );
        }
        int len = id.length();
        for ( int i=0; i<len; i++ ) {
            char c = id.charAt( i );
            if ( separators.indexOf( c ) != -1 ) {
                return id.substring( 0, i );
            }
        }
        // no separator found, no realm
        return "";
    }

    
    /**
     * Get the realm from identifier, based on a given 
     * realm separator.
     * <p>
     * Example:  If the identifier is "marketing\john", this method
     * returns "marketing" (assuming that "\" is the separator).
     *
     * @param id identifier
     * @param separator
     * @return realm 
     */
    public static String getRealm( String id, char separator )
    {
        if ( id == null ) {
            throw new IllegalArgumentException( "Argument 'id' is null" );
        }
        int len = id.length();
        for ( int i=0; i<len; i++ ) {
            char c = id.charAt( i );
            if ( c == separator ) {
                return id.substring( 0, i );
            }
        }
        // no separator found, no realm
        return "";
    }

    
    /**
     * Strip the realm prefix from a user or role identifier.
     * <p>
     * Example:  If the identifier is "marketing\john", this method
     * returns "john".
     *
     * @param id identifier
     * @return identifier stripped from any realm prefix 
     */
    public static String stripRealm( String id )
    {
        return stripRealm( id, REALM_SEPARATORS );
    }
    

    /**
     * Strip the realm prefix from an identifier, given a set of 
     * realm separators.
     * <p>
     * Example:  If the identifier is "marketing\john", this method
     * returns "john" (assuming that "\" is a separator).
     *
     * @param id identifier
     * @return identifier stripped from its realm prefix 
     */
    public static String stripRealm( String id, String separators )
    {
        if ( id == null ) {
            throw new IllegalArgumentException( "Argument 'id' is null" );
        }
        if ( separators == null ) {
            throw new IllegalArgumentException( "Argument 'separators' is null" );
        }
        int len = id.length();
        for ( int i=0; i<len; i++ ) {
            char c = id.charAt( i );
            if ( separators.indexOf( c ) != -1 ) {
                return id.substring( i+1 );
            }
        }
        // no separator found, no realm
        return id;
    }

    
    /**
     * Strip the realm prefix from an identifier, given a 
     * realm character separator.
     * <p>
     * Example:  If the identifier is "marketing\john", this method
     * returns "john" (assuming that "\" is the separator).
     *
     * @param id identifier
     * @return identifier stripped from its realm prefix 
     */
    public static String stripRealm( String id, char separator )
    {
        if ( id == null ) {
            throw new IllegalArgumentException( "Argument 'id' is null" );
        }
        int len = id.length();
        for ( int i=0; i<len; i++ ) {
            char c = id.charAt( i );
            if ( c == separator ) {
                return id.substring( i+1 );
            }
        }
        // no separator found, no realm
        return id;
    }


	/**
	 * Return the index of the realm separator character, 
	 * or -1 if none is found.
	 * 
	 * @param id identifiers
	 * @param separators realm separators
	 * @return position of separator character
	 */
	public static int indexOfSeparator( String id, String separators )
	{
		if ( id == null ) {
			throw new IllegalArgumentException( "Argument 'id' is null" );
		}
		if ( separators == null ) {
			throw new IllegalArgumentException( "Argument 'separators' is null" );
		}
		int len = id.length();
		for ( int i=0; i<len; i++ ) {
			char c = id.charAt( i );
			if ( separators.indexOf( c ) != -1 ) {
				return i;
			}
		}
		// no separator found
		return -1;
	}


    /**
     * Normalize an identifier by ensuring that it contains
     * a realm prefix and, if necessary, converting it to lowercase if the
     * identifier is case-insensitive.
     *
     * @param id identifier
     * @param defaultRealm realm to prefix if identifier doesn't have one
     * @param caseSensitive true if identifier is case-sensitive
     * @param separator realm separator
     * @return normalized identifier
     */
    public static String normalize( String id, String defaultRealm, 
                                    boolean caseSensitive, char separator )
    {
        int           pos;
        StringBuffer  buf;
        
        if ( id == null ) {
        	throw new IllegalArgumentException( "Argument 'id' is null" );
        }
        pos = indexOfSeparator( id, REALM_SEPARATORS );
        if ( pos == -1 ) {
            // no realm
            buf = new StringBuffer( defaultRealm );
            buf.append( separator );
            buf.append( id );
            id = buf.toString();
        } else if ( pos == 0 ){
        	// empty realm, use default & normalize separator
            buf = new StringBuffer( defaultRealm );
            buf.append( separator );
            buf.append( id.substring( pos+1 ) );
            id = buf.toString();
        } else {
        	// realm specified, just normalize the separator
			buf = new StringBuffer( id.substring( 0, pos ) );
			buf.append( separator );
			buf.append( id.substring( pos+1 ) );
			id = buf.toString();
        }

        if ( ! caseSensitive ) {
            id = id.toLowerCase();
        }
        return id;
    }
    
}
