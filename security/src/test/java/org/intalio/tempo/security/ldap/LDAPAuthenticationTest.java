/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 * $Id: LDAPAuthenticationTest.java,v 1.5 2005/02/24 18:20:22 boisvert Exp $
 */
package org.intalio.tempo.security.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationQuery;
import org.intalio.tempo.security.authentication.AuthenticationRuntime;
import org.intalio.tempo.security.authentication.provider.AuthenticationProvider;
import org.intalio.tempo.security.provider.SecurityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LDAPAuthenticationTest
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class LDAPAuthenticationTest extends TestCase {

    protected final static Logger LOG = LoggerFactory.getLogger("tempo.security.test");
    
    private final static String REALM = "intalio";
    
    private AbstractSuite    _suite;
    
    private SecurityProvider _security;
    
    private AuthenticationProvider _auth;
    
    private Map _configuration;

    /**
     * Constructor
     * @param arg0
     */
    public LDAPAuthenticationTest(String arg0) {
        super(arg0);
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new LDAPGroupSuite(LDAPAuthenticationTest.class));
        suite.addTest(new LDAPGroupSuiteActiveDirectory(LDAPAuthenticationTest.class));
        //suite.addTest(new LDAPRoleSuite(LDAPAuthenticationTest.class));
        return suite;
    }

    public void setSuite( AbstractSuite suite ) {
        this._suite = suite;
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        initAuthenticationProvider(REALM);
    }
    
    protected void initAuthenticationProvider(String realms) throws Exception {
        _configuration = _suite.getConfiguration();
        _security = new LDAPSecurityProvider();
        _security.initialize(_configuration);
        _auth = _security.getAuthenticationProvider(realms);
    }
    
    protected void tearDown() throws Exception {
        _security.dispose();
    }

    public String getName() {
        if ( _suite==null )
            return super.getName();
        return super.getName()+" with "+_suite.getName();
    }

    public String toString() {
        if ( _suite==null )
            return super.toString();
        return super.toString()+" with "+_suite.getClass().getName();
    }
    
    public void testQueryUserCredential() throws Exception {
        // disable this test for active directory
        if (_suite.getClass().getName().indexOf("ActiveDirectory")!=-1)
            return;

        String user = "jimm";
        Property[] props;
        Collection result;
        Collection<Property> control = new ArrayList<Property>();

        AuthenticationQuery query = _auth.getQuery();
    
        props = query.getUserCredentials(user);
        LOG.info(listArray(props));
        result = toCollection(props);
        control.add(new Property("password", "jimm"));
        assertTrue("Expected item not found", result.size()>=1);
        //assertTrue("Some expected items are missing.", result.containsAll(control));
    }
    
    public void testRuntimeAuthenticateSucceed() throws Exception {

        String user = "jimm";
        String pass = "jimm";
        Property[] props;

        AuthenticationRuntime runtime = _auth.getRuntime();
        props = new Property[1];
        props[0] = new Property(AuthenticationConstants.PROPERTY_PASSWORD, pass);
        assertTrue( "Authenication failed unexpectedly!", runtime.authenticate(user, props) );
    }

    public void testRuntimeAuthenticateNoUser() throws Exception {

        String user = "Wally";
        String pass = "jimm";
        Property[] props;

        AuthenticationRuntime runtime = _auth.getRuntime();
        props = new Property[1];
        props[0] = new Property(AuthenticationConstants.PROPERTY_PASSWORD, pass);
        assertTrue( "Authenication succeed unexpectedly!", !runtime.authenticate(user, props) );
    }

    public void testRuntimeAuthenticateWrongPass() throws Exception {

        String user = "jimm";
        String pass = "wrong pass";
        Property[] props;

        AuthenticationRuntime runtime = _auth.getRuntime();
        props = new Property[1];
        props[0] = new Property(AuthenticationConstants.PROPERTY_PASSWORD, pass);
        assertTrue( "Authenication succeed unexpectedly!", !runtime.authenticate(user, props) );
    }
    
    private Collection toCollection(Object[] array) {
        if (array==null||array.length==0)
            return Collections.EMPTY_LIST;

        Collection<Object> res = new ArrayList<Object>();
        for (int i = 0; i < array.length; i++) {
            res.add(array[i]);
        }
        return res;
    }
    
    private void listBytes(byte[] array, StringBuffer sb) {
        for (int i = 0; i < array.length; i++) {
            sb.append((char)array[i]);
        }
    }

    private String listArray(Property[] array) {
        if ( array==null )
            return "[]";
        if ( array.length==0 )
            return "[0]";
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        for (int i = 0; i < array.length; i++) {
            if (i!=0)
                sb.append(", ");
            sb.append("{");
            sb.append(array[i].getName());
            sb.append(", ");
            if (array[i].getValue() instanceof byte[])
                listBytes((byte[])array[i].getValue(), sb);
            else {
                sb.append(array[i].getValue().getClass().getName());
                sb.append("@");
                sb.append(array[i].getValue());
            }
            sb.append("}");
        }
        sb.append(']');
        return sb.toString();
    }    

}
