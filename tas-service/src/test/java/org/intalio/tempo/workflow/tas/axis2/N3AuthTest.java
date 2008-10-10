/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.workflow.tas.axis2;

import junit.framework.TestCase;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.workflow.tas.core.AuthCredentials;
import org.intalio.tempo.workflow.tas.core.N3AuthStrategy;
import org.intalio.tempo.workflow.tas.core.TaskAttachmentService;
import org.intalio.tempo.workflow.tas.core.TaskAttachmentServiceImpl;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

// TODO: update this test!
@RunWith(InstinctRunner.class)
public class N3AuthTest extends TestCase {

    @SuppressWarnings("unused")
    private final static Logger _logger = LoggerFactory.getLogger(TASAxis2BridgeTest.class);
    
    @SuppressWarnings("unused")
    private final static String SYSTEM_TEST_TOKEN = "VE9LRU4mJnVzZXI9PXRlc3Rcc3lzdGVtLXRlc3QmJmlzc3VlZD09MTEzNzQxOTg"
        + "xNTAwMyYmcm9sZXM9PXN5c3RlbVxzeXN0ZW0mJmZ1bGxOYW1lPT1Qcm9kdWN0IE1hbmFnZXIgIzEmJmVtYWlsPT1wcm9kL"
        + "W1hbmFnZXIxQGludGFsaW8uY29tJiZub25jZT09LTI4OTY1NDQxODc3OTI0MjY0MDUmJnRpbWVzdGFtcD09MTEzNzQxOTg"
        + "xNTAwMyYmZGlnZXN0PT1wVVc0aXFiMWd1ZnV5TEwxYXNZcit4MS8rRW89JiYmJlRPS0VO";
    
    @SuppressWarnings("unused")
    private TaskAttachmentService _service;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(N3AuthTest.class);
    }

    @Override
    protected void setUp()
            throws Exception {
        _service = new TaskAttachmentServiceImpl(new N3AuthStrategy(),
                new DummyStorageStrategy());
    }
    @Subject N3AuthStrategy  n3auth;
    final static ExpectThat expect = new ExpectThatImpl();
    
    @Specification
    public void testAuth() throws Exception {
//        String config = N3AuthTest.class.getClassLoader().getResource("securityConfig.xml").getFile();
//        String dir = new File(config).getParent();
//        System.setProperty(Constants.CONFIG_DIR_PROPERTY, dir);
//        String endpoint = System.getProperty("org.intalio.tempo.security.ws.endpoint");
//        TokenClient _client;
//        if (endpoint != null) {
//            _client = new TokenClient(endpoint);
//        } else {
//            _client = new TokenClientMock();
//        }
        
        AuthCredentials credentials = new AuthCredentials(SYSTEM_TEST_TOKEN);
        credentials.getAuthorizedUsers().add("test/system-test");
        credentials.getAuthorizedRoles().add("test/testrole");
        
        N3AuthStrategy n3auth = new FakeN3AuthStrategy();
        n3auth.setWsEndpoint("internal://");
        //n3auth = new N3AuthStrategy();
        final TokenService ts = ((FakeN3AuthStrategy)n3auth).connect2tokenService();
        expect.that(new Expectations(){{
            Property[] p = new Property[2];
            p[0] = new Property( AuthenticationConstants.PROPERTY_USER, "test/system-test");
            p[1] = new Property(AuthenticationConstants.PROPERTY_ROLES, "test/testrole");
           one(ts).getTokenProperties(SYSTEM_TEST_TOKEN);will(returnValue(p));
        }});       
        n3auth.authenticate(credentials);       
    }
    
    public void testUnauthorized() throws Exception {
//        try {
//            AuthCredentials credentials = new AuthCredentials(SYSTEM_TEST_TOKEN);
//            credentials.getAuthorizedUsers().add("test/system-test1");
//            
//            _service.add(credentials, new AttachmentMetadata(), new byte[0]);
//            
//            Assert.fail("AuthException expected but not thrown");
//        } catch (AuthException e) {
//            /* OK */
//        }
    }
    
    public void testBadToken() throws Exception {
//        try {
//            AuthCredentials credentials = new AuthCredentials("foo");
//            credentials.getAuthorizedUsers().add("test/system-test");
//            
//            _service.add(credentials, new AttachmentMetadata(), new byte[0]);
//            
//            Assert.fail("AuthException expected but not thrown");
//        } catch (AuthException e) {
//            /* OK */
//        }
    }    
}
