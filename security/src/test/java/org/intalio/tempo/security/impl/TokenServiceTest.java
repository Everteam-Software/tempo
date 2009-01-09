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

import java.net.URI;
import java.rmi.RemoteException;

import junit.framework.TestCase;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.rbac.RBACConstants;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.security.util.PropertyUtils;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

import edu.yale.its.tp.cas.client.ProxyTicketValidator;

/**
 * Test the TokenHandler class.
 * 
 * @author <a href="mailto:boisvert@intalio.com">Alex Boisvert</a>
 * 
 */
@RunWith(InstinctRunner.class)
public class TokenServiceTest extends TestCase {
    protected transient Logger _log = LoggerFactory.getLogger(getClass());

    /**
     * Get TokenService instance
     */
    public TokenService getTokenService() throws Exception {
        TokenService service;

        ClassPathResource config = new ClassPathResource("TokenServiceTest.xml");
        XmlBeanFactory factory = new XmlBeanFactory(config);

        service = (TokenService) factory.getBean("tokenService");
        return service;
    }

    @Specification
    public void testCreateToken() throws Exception {
        assertUserPropertyIdentical("exolab\\castor");
        assertUserPropertyIdentical("tyredating\\pdv017@es.euromaster.com");
        assertUserPropertyIdentical("tyredating\\pdv017@es\\euromaster\\com");

    }

    private void assertUserPropertyIdentical(String user) throws AuthenticationException, RemoteException {
        TokenHandler tokenHandler = new TokenHandler();
        Property userProp = new Property(AuthenticationConstants.PROPERTY_USER, user);
        Property[] properties = new Property[] { userProp };
        String token = tokenHandler.createToken(properties);
        Property[] props = tokenHandler.parseToken(token);
        Property user2 = PropertyUtils.getProperty(props, AuthenticationConstants.PROPERTY_USER);
        _log.debug("Got back user:" + user2.toString());
        Assert.assertEquals(user, user2.getValue());
    }

    @Specification
    public void testTokenServiceWithManyRoles() throws Exception {
        TokenService service = getTokenService();

        String token = service.authenticateUser("proto\\ADK", "ADK");
        System.out.println(token.length());
        assertNotNull(token);

        System.out.println(token);

        assertTrue(token.length() > 0);
        // verify the token is not too big
        assertTrue(token.length() < 1000);
        // check we can create a valid URI with this token
        URI uri = URI.create("http://localhost:8080/init.xpl?token=" + token);
        assertNotNull(uri);
    }

    /**
     * Test remote access to token service. In particular, authenticateUser()
     * and getTokenProperties().
     */
    @Specification
    public void testTokenService() throws Exception {
        TokenService service;
        String token, badToken;
        Property prop;
        Property[] props;

        service = getTokenService();

        // authenticate
        token = service.authenticateUser("exolab\\castor", "castor");
        assertNotNull(token);
        assertTrue(token.length() > 0);

        // negative authenticate test (invalid password)
        try {
            badToken = service.authenticateUser("exolab\\castor", "BAD_PASSWORD");
            throw new Exception("Negative authenticate test failed: " + badToken);
        } catch (AuthenticationException except) {
            // expected exception
        }

        // authenticate with null password
        try {
            badToken = service.authenticateUser("exolab\\castor", (String) null);
            throw new Exception("Negative authenticate test failed: " + badToken);
        } catch (AuthenticationException except) {
            // expected exception
        }

        // check token properties
        props = service.getTokenProperties(token);
        prop = new Property(RBACConstants.PROPERTY_EMAIL, "castor@exolab.org");
        _log.debug("Properties: " + TokenTest.toString(props));
        TokenTest.assertProperty(prop, props);

        // authenticate with credential
        Property passwordProp = new Property(AuthenticationConstants.PROPERTY_PASSWORD, "castor");
        ;
        Property[] props2 = new Property[] { passwordProp };
        token = service.authenticateUser("exolab\\castor", props2);
        assertNotNull(token);
        assertTrue(token.length() > 0);

    }

    @Subject
    TokenServiceImpl serviceImpl;

    ProxyTicketValidator pv;

    final static ExpectThat expect = new ExpectThatImpl();

    @Specification
    public void testGetTokenFromTicket() throws Exception {
        // final ProxyTicketValidator pv;
        serviceImpl = (TokenServiceImpl) getTokenService();
        pv = serviceImpl.getProxyTicketValidator();
        expect.that(new Expectations() {
            {
                one(pv).setCasValidateUrl(null);
                one(pv).setService("serviceURL");
                one(pv).setServiceTicket("dummyServiceTicket");
                one(pv).validate();
                one(pv).isAuthenticationSuccesful();
                will(returnValue(true));
                one(pv).getUser();
                will(returnValue("exolab\\castor"));
            }
        });

        String token;
        token = serviceImpl.getTokenFromTicket("dummyServiceTicket", "serviceURL");
        assertTrue(token.length() > 0);
    }

}
