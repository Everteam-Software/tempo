/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security;

import java.util.HashMap;

import javax.management.ObjectName;

import org.intalio.tempo.security.provider.SecurityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security Provider component
 * <p>
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class SecurityComponent
{

    /**
     * Provider-specific properties.
     */
    protected HashMap<String, Object> _props; 
    
    
    final static Logger _logger = LoggerFactory.getLogger( "tempo.security.SecurityComponent" );


    /**
     * Security provider.
     */
    protected SecurityProvider _provider;
    
    
    /**
     * Classname for the security provider.
     */
    protected String _clazz;


    /**
     * Realms federated by provider.
     */
    protected String[] _realms;        

    
    /**
     * Default realm
     */
    protected String _defaultRealm;        

    
    /**
     * Default no-arg constructor.
     */
    public SecurityComponent()
    {
        _props = new HashMap<String,Object>();    
    }

    
    /**
     * Set the security provider class.
     * 
     * @param clazz classname of provider
     */
    public void setProviderClass( String clazz )
    {
        _clazz = clazz;        
    }
    

    /**
     * Set the default realm.
     * 
     * @param realm default realm
     */
    public void setDefaultRealm( String realm )
    {
        _defaultRealm = realm;        
    }


    /**
     * Set a security provider-specific property
     * 
     * @param name property name
     * @param value property value
     */
    public void setProperty( String name, String value )
    {
        _props.put( name, value );        
    }


    /**
     * Get the JMX object name for the extension MBean for this component
     */
    public ObjectName getExtensionMBeanName()
    {
        return null;
    }


    /**
     * Initialize the component.
     */
    public void init()
        throws Exception
    {
        Class clazz;

        if ( _clazz == null ) {
            throw new Exception( "Parameter 'providerClass' not set." );            
        }
        
        try {
            clazz = Class.forName( _clazz );
            _provider = (SecurityProvider) clazz.newInstance();
            _provider.initialize( _props );            
            _realms = _provider.getRealms();

            for ( int i=0; i<_realms.length; i++ ) {
                // empty string treated as default realm
                if ( _realms[i].length() == 0 ) {
                    _realms[i] = "default";
                }
                // TODO
                // providerCtx.rebind( _realms[i], _provider );
            }


            // bind default realm to "default"
            if ( _defaultRealm != null ) {            
                for ( int i=0; i<_realms.length; i++ ) {
                    if ( _realms[i].equalsIgnoreCase( _defaultRealm ) ) {
                        // TODO
                        // providerCtx.rebind( "default", _provider );
                    }
                }
            }
                
        } catch ( Exception except ) {
            throw except;
        }
    }


    /**
     * Start the component.
     */
    public void start()
        throws Exception
    {
        // nothing
    }

    
    /**
     * Stop the component.
     */
    public void stop()
    {
        // nothing
    }


    /**
     * Shutdown the component.
     */
    public void shutDown()
        throws Exception
    {
        try {
            if ( _provider != null ) {
                _provider.dispose();
            }
        } catch ( Exception except ) {
            _logger.error( "Error during shutdown", except );
        }
    }

}
