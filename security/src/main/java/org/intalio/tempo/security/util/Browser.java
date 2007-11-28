/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id: SimpleSpringTest.java,v 1.5 2005/03/29 22:09:07 ssahuc Exp $
 */

package org.intalio.tempo.security.util;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.impl.Realms;
import org.intalio.tempo.security.provider.SecurityProvider;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Simple user/role browser
 * 
 * @author <a href="mailto:boisvert@intalio.com">Alex Boisvert</a>
 * 
 */
public class Browser
{
    protected transient Logger _log = LoggerFactory.getLogger( getClass() );

    protected String _contextFilename;
    
    protected Realms _realms;
    
    /**
     * Get Realms instance
     */
    public void init()
    	throws Exception
    {
        Resource config = new FileSystemResource( _contextFilename );
        XmlBeanFactory factory = new XmlBeanFactory( config );
        _realms = (Realms) factory.getBean( "realms" );
        if ( _realms == null ) {
            throw new Exception( "Not bean named 'realms' in Spring context" );
        }
    }
    

	public void browseRoles()
		throws Exception
	{
        for (SecurityProvider s : _realms.getSecurityProviders()) {
            System.out.println( "Security Provider  name='" + s.getName() + "' class='" + s.getClass() + "'" );
            browseRoles(s);
        }
	}

    public void browseUsers()
        throws Exception
    {
        for (SecurityProvider s : _realms.getSecurityProviders()) {
            System.out.println( "Security Provider name='" + s.getName() + "' class='" + s.getClass() + "'" );
            browseUsers(s);
        }
    }

    public void displayUser( String user )
        throws Exception
    {
        String realm = IdentifierUtils.getRealm( user );
        SecurityProvider s = _realms.getSecurityProvider(  realm );
        if ( s == null ) {
            System.out.println( "Invalid user/realm: " + user );
            return;
        }
        displayUser( s.getRBACProvider( realm ).getQuery(), user );
    }

    public void displayRole( String role )
        throws Exception
    {
        String realm = IdentifierUtils.getRealm( role );
        SecurityProvider s = _realms.getSecurityProvider(  realm );
        if ( s == null ) {
            System.out.println( "Invalid role/realm: " + role );
            return;
        }
        displayRole( s.getRBACProvider( realm ).getQuery(), role );
    }

    void browseRoles( SecurityProvider provider ) throws Exception {
        String[] realms = provider.getRealms();
        for ( int i=0; i<realms.length; i++ ) {
            String realm = realms[i];
            System.out.println( "Realm '" + realm + "'" );
            browseRoles( realm, provider.getRBACProvider( realm ).getQuery() );
        }
    }

    void browseUsers( SecurityProvider provider ) throws Exception {
        String[] realms = provider.getRealms();
        for ( int i=0; i<realms.length; i++ ) {
            String realm = realms[i];
            System.out.println( "Realm '" + realm + "'" );
            browseUsers( realm, provider.getRBACProvider( realm ).getQuery() );
        }
    }

    void browseRoles( String realm, RBACQuery query ) throws Exception {
        String[] roles = query.topRoles( realm );
        for ( int i=0; i<roles.length; i++ ) {
            String role = roles[i];
            displayRole( query, role );            
        }
    }

    private void displayRole( RBACQuery query, String role ) throws Exception {
        System.out.println( "  Role '" + role + "'" );
        String[] subRoles = query.descendantRoles( role );
        for ( int j=0; j<subRoles.length; j++ ) {
            String subRole = subRoles[j];
            System.out.println( "    Inherits '" + subRole + "'" );
        }
    }
    
    private void displayUser( RBACQuery query, String user ) throws Exception {
        System.out.println( "  User '" + user + "'" );
        Property[] props = query.userProperties( user );
        for ( int j=0; j<props.length; j++ ) {
            Property prop = props[j];
            System.out.println( "    Property '" + prop.getName() + "' value '" + prop.getValue() + "'" );
        }
    }
    
    void browseUsers( String realm, RBACQuery query ) throws Exception {
        System.out.println( "Not implemented." );
    }
    
    public static void main( String[] args ) throws Exception {
        if ( args.length < 1 ) {
            System.out.println( "Usage: " );
            System.out.println( "-config [file]            configuration file" );
            System.out.println( "-password [password]      password (implies authentication)" );
            System.out.println( "-role [role]              role name (display or check if user has role)" );
            System.out.println( "-roles                    display all roles" );
            System.out.println( "-user                     username" );
            System.out.println( "-users                    display all users" );
            return;
        }
        Browser b = new Browser();
        
        boolean browseRoles = false;
        boolean browseUsers = false;
        String user = null;
        String password = null;
        String role = null;
        
        int arg = 0;
        while ( arg < args.length ) {
            if ( args[arg].equalsIgnoreCase( "-config" ) ) {
                arg++;
                b._contextFilename = args[arg];
                arg++;
            } else if ( args[arg].equalsIgnoreCase( "-roles" ) ) {
                arg++;
                browseRoles = true;
            } else if ( args[arg].equalsIgnoreCase( "-users" ) ) {
                arg++;
                browseUsers = true;
            } else if ( args[arg].equalsIgnoreCase( "-user" ) ) {
                arg++;
                user = args[arg];
                arg++;
            } else if ( args[arg].equalsIgnoreCase( "-role" ) ) {
                arg++;
                role = args[arg];
                arg++;
            } else if ( args[arg].equalsIgnoreCase( "-password" ) ) {
                arg++;
                password = args[arg];
                arg++;
            } else {
                System.out.println( "Invalid parameter: " + args[arg] );
                return;
            }
        }
        
        if (b._contextFilename == null ) {
            System.out.println( "Error:  No configuration file specified, please provide -config parameter." );
            return;
        }
        b.init();
        
        if (browseRoles) {
            b.browseRoles();
        }
        if (browseUsers) {
            b.browseUsers();
        }
        if (user != null && password != null) {
            b.authenticate(user, password);
        } else if (user != null && role != null) {
            b.checkRole(user, role);
        } else if (user == null && role != null) {
            b.displayRole(role);
        } else if (user != null && role == null) {
            b.displayUser( user );
        }
        
    }

    private void authenticate( String user, String password ) throws Exception {
        Property pwd = new Property( AuthenticationConstants.PROPERTY_PASSWORD, password );
        Property[] credentials = new Property[] { pwd };      
        if (_realms.authenticate( user, credentials )) {
            System.out.println( "Authentication succeeded." );
        } else {
            System.out.println( "Authentication failed." );
        }
    }


    private void checkRole( String user, String role ) throws Exception {
        String[] roles = _realms.authorizedRoles( user );
        System.out.println( "User has roles: " + StringArrayUtils.toCommaDelimited( roles ) );
        if ( StringArrayUtils.containsString( roles, role ) ) {
            System.out.println( "User '" + user + "' is authorized to role '" + role + "'." );
        } else {
            System.out.println( "User '" + user + "' is NOT authorized to role '" + role + "'." );
        }
    }

}
