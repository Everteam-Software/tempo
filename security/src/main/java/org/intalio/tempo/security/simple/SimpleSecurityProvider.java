/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.simple;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.intalio.tempo.security.authentication.AuthenticationAdmin;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.authentication.AuthenticationQuery;
import org.intalio.tempo.security.authentication.AuthenticationRuntime;
import org.intalio.tempo.security.authentication.provider.AuthenticationProvider;
import org.intalio.tempo.security.provider.SecurityProvider;
import org.intalio.tempo.security.rbac.RBACAdmin;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.RBACRuntime;
import org.intalio.tempo.security.rbac.provider.RBACProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.SystemPropertyUtils;

/**
 * Simple Security provider
 *
 * @author <a href="boisvert@intalio.com">Alex Boisvert</a>
 */
public final class SimpleSecurityProvider
    implements SecurityProvider
{

    /**
     * RBAC providers: Map of { String, RBACProvider }.
     */
    private HashMap<String,RBACProvider> _rbacMap;

    /**
     * Authentication providders: Map of { String, AuthenticationProvider }.
     */
    private HashMap<String,AuthenticationProvider> _authMap;
    

    
    /** 
     * Package-accessible database of realms, users, roles and permissions.
     */
    private SimpleDatabase _database;
    
    
    /**
     * Configuration
     */
    private Map<String, Object> _config;


	/**
	 * Name
	 */    
	private String _name = "simple";


	/**
	 * Filename of user, role, permissions database
	 */	
	private String _filename;
	
    
	/**
	 * Time when we last checked for modified database.
	 */	
	private long _databaseLastCheck;


	/**
	 * Time when database was last modified.
	 */	
	private long _databaseLastModified;
	
	
	/**
	 * Amount of time between checks of modified database (in milliseconds).
	 */
	private long _databaseCheckPeriod = 5000L;


    /**
     * Log4J logger
     */
	static final Logger LOG = LoggerFactory.getLogger( "tempo.security.simple" );


    /**
     * Public no-arg constructor.
     */
    public SimpleSecurityProvider()
    {
        // empty
    }
    
        
    // implement SecurityProvider interface
    public void initialize( Object config )
        throws AuthenticationException, RBACException
    {
    	String period;

		try {		
        	_config = (Map<String, Object>) config;
		} catch ( Exception except ) {
			throw new RBACException( "Configuration object must be a 'java.util.Map'" );			
		}

        _filename = (String) _config.get( "configFile" );
        checkFilename();
		
		period = (String) _config.get( "refreshInterval" );
		if ( period != null ) {
            setRefreshInterval( Integer.parseInt( period ) );
		}

		reloadDatabase();
    }

    // Spring init method
    public void init()
        throws AuthenticationException, RBACException
    {
        checkFilename();
        
        reloadDatabase();
    }
    
    private void checkFilename()
    {
        if ( _filename == null ) {
            throw new IllegalStateException( "Missing configuration property 'configFile'");
        }
    }
    
    public void setConfigFile( String filename )
    {
        _filename = filename;
    }
    
    public void setRefreshInterval( int period )
    {
        _databaseCheckPeriod = period * 1000L;
        if ( _databaseCheckPeriod < 1000L ) {
            _databaseCheckPeriod = 1000L;
        }
    }
    

    private InputStream getConfigStream()
        throws IOException
    {
        String filename = SystemPropertyUtils.resolvePlaceholders(_filename);
        File file = new File(filename);
        if ( !file.exists() ) {
            ClassPathResource resource = new ClassPathResource( filename );
            return resource.getInputStream();
        }
        return new FileInputStream( filename );
    }
    
    // implement SecurityProvider interface
    public String getName()
    {
        return _name;
    }
    
    
	// implement SecurityProvider interface
	public void setName( String name ) {
		_name = name;
	}


    // implement SecurityProvider interface
    public String[] getRealms()
        throws AuthenticationException, RBACException
    {
        return _authMap.keySet().toArray( new String[ _authMap.size() ] );
    }

    
    // implement SecurityProvider interface
    public RBACProvider getRBACProvider( String realm )
        throws RBACException
    {
        if (!_rbacMap.containsKey(realm)) 
            throw new RBACException("Realm, "+realm+", is not supported by this Security Provider!");
        return (RBACProvider) _rbacMap.get( realm.toLowerCase() );
    }
    

    // implement SecurityProvider interface
    public AuthenticationProvider getAuthenticationProvider( String realm )
        throws AuthenticationException
    {
        return (AuthenticationProvider) _authMap.get( realm.toLowerCase() );
    }


	/**
	 * Get the user, role and permissions database.
	 */
	public synchronized SimpleDatabase getDatabase()
	{
		long  now;
		File  file;
		
		now = System.currentTimeMillis();
		if ( now > _databaseLastCheck + _databaseCheckPeriod ) {
			file = new File( _filename );
			if ( file.exists() ) {
				// reload config file only if changed
				if ( file.lastModified() != _databaseLastModified ) {
					reloadDatabase();
					_databaseLastModified = file.lastModified();
				}
			}
		}
		_databaseLastCheck = now;
		return _database;		
	}    


    // implement SecurityProvider interface
    public void dispose()
        throws RBACException
    {
        _rbacMap = null;
        _authMap = null;
        _database = null;
    }


	/**
	 * Reload user/role/permissions database from file. 
	 */
	private void reloadDatabase()
	{
		String     defaultRealm;
		String[]   realms;
		SimpleRBACProvider            rbac;
		SimpleAuthenticationProvider  auth;

		if ( LOG.isInfoEnabled() ) {
			LOG.info( "Reload security database " + _filename );
		}		
		try {
			_database = SimpleDatabase.load( getConfigStream() );
		} catch ( Exception except ) {
			LOG.error( "Error reloading security database " + _filename, except );
			throw new RuntimeException( except );	
		}

		realms = _database.getRealms();
		_rbacMap = new HashMap<String,RBACProvider>();
		_authMap = new HashMap<String,AuthenticationProvider>();
		for ( int i=0; i<realms.length; i++ ) {
			rbac = new SimpleRBACProvider( realms[i] );
			_rbacMap.put( realms[i].toLowerCase(), rbac );
			
			auth = new SimpleAuthenticationProvider( realms[i] );
			_authMap.put( realms[i].toLowerCase(), auth );
		}

		// bind default realm		        
		defaultRealm = _database.getDefaultRealm();
		rbac = new SimpleRBACProvider( defaultRealm );
		_rbacMap.put( "", rbac );
			
		auth = new SimpleAuthenticationProvider( defaultRealm );
		_authMap.put( "", auth );
	}
    
    
    // INNER CLASS
    class SimpleRBACProvider
        implements RBACProvider
    {
        
        private SimpleRBACQuery _query;
        private SimpleRBACRuntime _runtime;
        
        // implement RBACProvider interface
		SimpleRBACProvider( String realm )
        {
            _query = new SimpleRBACQuery( realm, SimpleSecurityProvider.this );
            _runtime = new SimpleRBACRuntime( realm, SimpleSecurityProvider.this );
        }

        
        // implement RBACProvider interface
        public RBACAdmin getAdmin()
            throws RBACException
        {
            // not supported
            return null;
        }
        
        
        // implement RBACProvider interface
        public RBACQuery getQuery() 
            throws RBACException
        {
            return _query;
        }
        
        
        // implement RBACProvider interface
        public RBACRuntime getRuntime() 
            throws RBACException
        {
            return _runtime;
        }
        
        
    } // class SimpleRBACProvider
    

    // INNER CLASS
    class SimpleAuthenticationProvider
        implements AuthenticationProvider
    {
        
        private SimpleAuthenticationQuery _query;
        private SimpleAuthenticationRuntime _runtime;

        
        // implement AuthenticationProvider interface
		SimpleAuthenticationProvider( String realm ) 
        {
            _query = new SimpleAuthenticationQuery( realm, SimpleSecurityProvider.this );
            _runtime = new SimpleAuthenticationRuntime( realm, SimpleSecurityProvider.this );
        }

        
        // implement AuthenticationProvider interface
        public AuthenticationAdmin getAdmin()
            throws AuthenticationException
        {
            // not supported
            return null;
        }
        
        
        // implement AuthenticationProvider interface
        public AuthenticationQuery getQuery()
            throws AuthenticationException
        {
            return _query;
        }
        

        // implement AuthenticationProvider interface
        public AuthenticationRuntime getRuntime() 
            throws AuthenticationException
        {
            return _runtime;
        }
        
        
    } // class SimpleAuthorizationProvider
    
}

