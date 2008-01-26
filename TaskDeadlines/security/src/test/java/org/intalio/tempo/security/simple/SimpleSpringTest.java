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

package org.intalio.tempo.security.simple;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.impl.Realms;

/**
 * Test SimpleProvider with a Spring-based configuration.
 * 
 * @author <a href="mailto:boisvert@intalio.com">Alex Boisvert</a>
 * 
 */
public class SimpleSpringTest
    extends TestCase
{
    protected transient Logger _log = Logger.getLogger( getClass() );

    /**
     * Get Realms instance
     */
    public Realms getRealms()
    	throws Exception
    {
		Realms realms;
        
        ClassPathResource config = new ClassPathResource( "SimpleSpringTest.xml" );
        XmlBeanFactory factory = new XmlBeanFactory( config );

        realms = (Realms) factory.getBean( "realms" );
		return realms;
    }
    

	/**
	 * Test remote access to token service.  In particular, authenticateUser()
	 * and getTokenProperties(). 
	 */
	public void testSimple()
		throws Exception
	{
		Realms        realms;
        String        user;
        Property[]    credentials;
        Property      password;

		_log.debug( "testSimple()" );
		
        realms = getRealms();
        
        // test positive login as "castor"
        user = "exolab\\castor";
        password = new Property( AuthenticationConstants.PROPERTY_PASSWORD, "castor" );
        credentials = new Property[] { password };      
        assertTrue( realms.authenticate( user, credentials ) );

        // test negative login as "castor" - invalid password
        user = "exolab\\castor";
        password = new Property( AuthenticationConstants.PROPERTY_PASSWORD, "xxx" );
        credentials = new Property[] { password };      
        assertFalse( realms.authenticate( user, credentials ) );
	}

}
