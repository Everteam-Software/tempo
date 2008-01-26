/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.simple;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;
import org.intalio.tempo.security.util.IdentifierUtils;

/**
 * Database containing realms, users, roles and permissions.
 * <p>
 * Note: The database is considered immutable and no attempt is made to 
 * synchronize access to it.  If you want to make changes, it is recommended
 * to create a new instance (i.e. from a file), change it and use the new instance 
 * after modification.
 *
 * @author <a href="boisvert@intalio.com">Alex Boisvert</a>
 */
public class SimpleDatabase
{

    /**
     * Castor mapping file
     */
    private static final String MAPPING_FILE =
        "/org/intalio/tempo/security/simple/SimpleDatabase.properties";


    /**
     * Loaded mapping
     */
    private static Mapping MAPPING;


    /**
     * Map of realms.  Tuples are { String, SimpleRealm }.
     */
    private HashMap<String,SimpleRealm> _realms;


    /**
     * Whether identifiers are case-sensitive.
     */
    private boolean _caseSensitive = false;
    
    
    /**
     * Realm separator character.
     */
    private char _separator = '\\';
    
    
    /**
     * Default realm.
     */
    private String _defaultRealm = "";
    
    
    /**
     * Default constructor used for marshalling.
     */
    public SimpleDatabase()
    {
        _realms = new HashMap<String,SimpleRealm>();
    }

	/**
	 * Get the default realm.
	 */
	public String getDefaultRealm()
	{
		return _defaultRealm;
	}


	/**
	 * Set the default realm.
	 */
	public void setDefaultRealm( String realm )
	{
		_defaultRealm = realm;
	}

	/**
	 * Set the default realm.
	 */
	public void setCaseSensitive( String value )
	{
		_caseSensitive = "true".equals( value.toLowerCase() );
	}


	/**
	 * Get a given realm.
	 */
	public SimpleRealm getRealm( String realm )
	{
		return (SimpleRealm) _realms.get( realm );
	}


	/**
	 * Get the realms
	 * 
	 * @return realms
	 */
	public String[] getRealms()
	{
		return (String[]) _realms.keySet().toArray( new String[ _realms.size() ] );		
	}

    /**
     * Get an enumeration of the users.
     */
    public Iterator getUsers()
    {
        ArrayList<SimpleUser>  users;
        SimpleRealm  realm;
        Iterator     iter;
        
        users = new ArrayList<SimpleUser>();
        iter = _realms.values().iterator();
        while ( iter.hasNext() ) {
            realm = (SimpleRealm) iter.next();
            users.addAll( realm.getUsers().values() );
        }
        return users.iterator();
    }

    
    /**
     * Get a given user.
     */
    public SimpleUser getUser( String user )
    {
        String       realm;
        SimpleRealm  simpleRealm;
        
        user = normalize( user );
        realm = IdentifierUtils.getRealm( user, _separator );
        
        simpleRealm = (SimpleRealm) _realms.get( realm );
        if ( simpleRealm == null ) {
            return null;
        }
        
        return simpleRealm.getUser( user );
    }

    
    
    /**
     * Get an enumeration of the roles.
     */
    public Iterator getRoles()
    {
        ArrayList<SimpleRole>  roles;
        SimpleRealm  realm;
        Iterator     iter;
        
        roles = new ArrayList<SimpleRole>();
        iter = _realms.values().iterator();
        while ( iter.hasNext() ) {
            realm = (SimpleRealm) iter.next();
            roles.addAll( realm.getRoles().values() );
        }
        return roles.iterator();
    }

    /**
     * Get a given role.
     */
    public SimpleRole getRole( String role )
    {
        String       realm;
        SimpleRealm  simpleRealm;
        
        role = normalize( role );
        realm = IdentifierUtils.getRealm( role, _separator );
        
        simpleRealm = (SimpleRealm) _realms.get( realm );
        if ( simpleRealm == null ) {
            return null;
        }
        
        return simpleRealm.getRole( role );
    }

    
    /**
     * Add a realm.
     */
    public void addRealm( SimpleRealm realm )
    {
        String id = realm.getIdentifier();
        
        if ( id == null ) {
        	throw new IllegalArgumentException( "Realm has no identifier" );
        }
        
        if ( ! _caseSensitive ) {
            id = id.toLowerCase();
        }

        _realms.put( id, realm );
    }

    
    /**
     * Prepare this object before it is used.
     */
    protected void prepare()
    {
        Iterator     iter;
        SimpleRealm  realm;
        
        iter = _realms.values().iterator();
        while ( iter.hasNext() ) {
            realm = (SimpleRealm) iter.next();
            realm.prepare( this );
        }

        iter = _realms.values().iterator();
        while ( iter.hasNext() ) {
            realm = (SimpleRealm) iter.next();
            realm.resolve( this );
        }
    }
    

    /**
     * Load a database from a file.
     */
    public static SimpleDatabase load( InputStream input )
        throws IOException
    {
        Unmarshaller     unmarshaller ;
        SimpleDatabase   database;

        synchronized ( SimpleDatabase.class ) {
            if ( MAPPING == null ) {
                MAPPING = new Mapping();
                try {
                    MAPPING.loadMapping( SimpleDatabase.class.getResource( MAPPING_FILE ) );
                } catch ( Exception except ) {
                    IOException ioe = new IOException( except.getMessage() );
                    ioe.initCause( except );
                    throw ioe;
                }
            }
        }

        try {
            unmarshaller = new Unmarshaller( MAPPING );
            database = (SimpleDatabase) unmarshaller.unmarshal( new InputSource( input ) );
        } catch ( Exception except ) {
            IOException ioe = new IOException( except.getMessage() );
            ioe.initCause( except );
            throw ioe;
        } finally {
            if ( input != null ) {
                input.close();
            }
        }

        database.prepare();

        return database;
    }

    

    /**
     * Normalize an identifier based on the database's configuration.
     */
    public String normalize( String identifier )
    {
       return IdentifierUtils.normalize( identifier, _defaultRealm, _caseSensitive, _separator );
    }
    

    /**
     * Normalize an identifier based on the database's configuration and a default realm.
     */
    public String normalize( String identifier, String defaultRealm )
    {
       return IdentifierUtils.normalize( identifier, defaultRealm, _caseSensitive, _separator );
    }

    
    /**
     * Normalize a set of identifiers based on the database's configuration.
     */
    public void normalize( String[] identifiers )
    {
       for ( int i=0; i<identifiers.length; i++ ) {
           identifiers[ i ] = normalize( identifiers[ i ] );
       }
    }
}
