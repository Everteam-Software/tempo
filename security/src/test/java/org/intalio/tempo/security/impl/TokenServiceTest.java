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

package org.intalio.tempo.security.impl;

import junit.framework.TestCase;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.rbac.RBACConstants;
import org.intalio.tempo.security.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Test the TokenHandler class.
 * 
 * @author <a href="mailto:boisvert@intalio.com">Alex Boisvert</a>
 * 
 */
public class TokenServiceTest
    extends TestCase
{
    protected transient Logger _log = LoggerFactory.getLogger( getClass() );

    /**
     * Get TokenService instance
     */
    public TokenService getTokenService()
    	throws Exception
    {
		TokenService service;
        
        ClassPathResource config = new ClassPathResource( "TokenServiceTest.xml" );
        XmlBeanFactory factory = new XmlBeanFactory( config );

        service = (TokenService) factory.getBean( "tokenService" );
		return service;
    }
    

	/**
	 * Test remote access to token service.  In particular, authenticateUser()
	 * and getTokenProperties(). 
	 */
	public void testTokenService()
		throws Exception
	{
		TokenService  service;
		String		  token, badToken;
		Property      prop;
		Property[]    props;

		_log.debug( "testTokenService()" );
		
		service = getTokenService();
		
		// authenticate
		token = service.authenticateUser( "exolab\\castor", "castor" );
		assertNotNull( token );
		assertTrue( token.length() > 0 );
		
		// negative authenticate test (invalid password)
		try {
			badToken = service.authenticateUser( "exolab\\castor", "BAD_PASSWORD" );
			throw new Exception( "Negative authenticate test failed: " + badToken );
		} catch ( AuthenticationException except ) {
			// expected exception
		}
		
		// check token properties
		props = service.getTokenProperties( token );
		prop = new Property( RBACConstants.PROPERTY_EMAIL, "castor@exolab.org" );
		_log.debug( "Properties: " + TokenTest.toString( props ) );
		TokenTest.assertProperty( prop, props );
		
	}



}
