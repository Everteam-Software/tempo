/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.util;

import java.util.HashMap;
import java.util.Map;
import org.intalio.tempo.security.Property;

/**
 * Utility methods for manipulating properties.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class PropertyUtils
{

   /**
    * Get a property.
    *
    * @param properties array of properties
    * @param name name of property
    */
   public static Property getProperty( Property[] properties, String name )
   {
       if ( properties == null ) {
            throw new IllegalArgumentException( "Argument 'properties' is null" );
       }
       if ( name == null ) {
            throw new IllegalArgumentException( "Argument 'name' is null" );
       }

       for ( int i=0; i<properties.length; i++ ) {
           if ( ( properties[i] != null ) && name.equals( properties[i].getName() ) ) {
               return properties[i];
           }
       }
       return null;
   }
   
   
   /**
    * Convert an array of properties into a Map with the property names
    * as key.
    *
    * @param properties array of properties
    */
   public static Map<String,Object> toMap( Property[] properties )
   {
       if ( properties == null ) {
            throw new IllegalArgumentException( "Argument 'properties' is null" );
       }
       
       Map<String,Object> map = new HashMap<String,Object>( properties.length );
       for ( int i=0; i<properties.length; i++ ) {
           map.put( properties[i].getName(), properties[i] );
       }
       return map;
   }
   

   /**
    * Get a property.
    *
    * @param properties map of properties
    * @param name name of property
    */
   public static Property getProperty( Map properties, String name )
   {
       if ( properties == null ) {
            throw new IllegalArgumentException( "Argument 'properties' is null" );
       }
       if ( name == null ) {
            throw new IllegalArgumentException( "Argument 'name' is null" );
       }
       return (Property) properties.get( name );
   }

}
