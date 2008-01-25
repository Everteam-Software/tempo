/**
 * Copyright (C) 2005, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.util;

import java.util.HashMap;

/**
 * A map whose values are retained only for a given duration.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class TimeExpirationMap
{

    /**
     * Expiration time (ms)
     */
    private int _expiration;
    
    /**
     * Expiration interval (ms)
     */
    private int _interval;
    
    /**
     * Time when last expiration occured 
     */
    private long _lastExpiration;
    
    /**
     * Underlying map.
     */
    private HashMap<Object,TimedValue> _map;
    
    
    /**
     * Construct an empty <tt>TimeExpirationMap</tt> with the specified initial
     * expiration time and interval check time.
     * 
     * @param expiration retain time for values (ms)
     * @param interval time interval (ms) to check for expired values
     */
    public TimeExpirationMap( int expiration, int interval ) 
    {
        _expiration = expiration;
        _interval = interval;
        _map = new HashMap<Object,TimedValue>();
        _lastExpiration = System.currentTimeMillis();
    }


    /**
     * Construct an empty <tt>TimeExpirationMap</tt> with the specified initial
     * capacity and load factor. 
     * 
     * @param expiration retain time for values (ms)
     * @param interval time interval (ms) to check for expired values
     * @param initialCapacity initial capacity.
     * @param loadFactor      load factor.
     */
    public TimeExpirationMap( int expiration, int interval, 
        int initialCapacity, float loadFactor )
    {
        _expiration = expiration;
        _interval = interval;
        _map = new HashMap<Object,TimedValue>( initialCapacity, loadFactor );
    }

    
    /**
     * Returns the value to which the specified key is mapped in this hash map,
     * or <tt>null</tt> if the map contains no mapping for this key.
     */
    public synchronized Object get( Object key )
    {
        TimedValue value = (TimedValue) _map.get( key );
        if ( value == null ) {
            return null;
        }
        long now = System.currentTimeMillis();
        if ( now > _lastExpiration + _interval ) {
            expire( now );
        }
        if ( value._time + _expiration < now ) {
            // value expired
            _map.remove( key );
            return null;
        }
        return value._value;
    }


    /**
     * Place an entry in the map
     */
    public synchronized Object put( Object key, Object value ) {
        long now = System.currentTimeMillis();
        if ( now > _lastExpiration + _interval ) {
            expire( now );
        }
        TimedValue newValue = new TimedValue();
        newValue._time = now;
        newValue._value = value;
        TimedValue existing = (TimedValue) _map.put( key, newValue );
        if ( existing != null ) {
            _map.put( key, existing );
            return existing._value;
        }
        return null; 
    }
    
    /**
     * Expire entries
     */
    private void expire( long now )
    {
        Object[] keys = _map.keySet().toArray();
        for ( int i=0; i<keys.length; i++ ) {
            TimedValue value = (TimedValue) _map.get( keys[i] );
            if ( value._time + _expiration < now ) {
                _map.remove( keys[i] );
            }
        }
    }
    
    /**
     * INNER CLASS
     */
    private class TimedValue
    {
        Object _value;
        long _time;
    }
    
}
