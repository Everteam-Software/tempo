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
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.auth.n3;

import junit.framework.Assert;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class N3AuthProviderTest {
    final static ExpectThat expect = new ExpectThatImpl();
    private final static String SYSTEM_TEST_TOKEN = "VE9LRU4mJnVzZXI9PWV4b2xhYlxjYXN0b3ImJmlzc3VlZD09MTIyMj"
                    + "MzMDc1MzYwNSYmcm9sZXM9PWV4b2xhYlxjb21taXR0ZXIsZXhvbGFiXHBhcnRpY2lwYW50JiZmdWxsTmFtZT09Q2FzdG9yIFd"
                    + "vcmthaG9saWMmJmVtYWlsPT1jYXN0b3JAZXhvbGFiLm9yZyYmbm9uY2U9PTE1Njg3MTk3NzMzMDYxNzkwNDQmJnRpbWVzdGFt"
                    + "cD09MTIyMjMzMDc1MzYwOCYmZGlnZXN0PT1BQWlmemVLYnE3czl0UTJ5NVp5N3dWUW5OaU09JiYmJlRPS0VO";

    @Subject
    N3AuthProvider ap;

    @Mock
    TokenClient tc;

    @BeforeSpecification
    void before() throws Exception {
        ap = new N3AuthProvider() {
            protected TokenClient getTokenClient() {
                return tc;
            }
        };
    }

    @Specification
    public void testN3AuthProvider() throws Exception {
        final Property[] properties = new Property[5];
        properties[0] = new Property("issued", "1222330753605");
        properties[1] = new Property("user", "exolab\\castor");
        properties[2] = new Property("fullName", "Castor Workaholic");
        properties[3] = new Property("email", "castor@exolab.org");
        properties[4] = new Property("roles", "exolab\\committer,exolab\\participant");
                
        expect.that(new Expectations() {
            {
                one(tc).getTokenProperties(SYSTEM_TEST_TOKEN); will(returnValue(properties));
                
               
               
            }
        });
        ap.setWsEndpoint("http://localhost:8080/axis2/services/TokenService");
        UserRoles user = ap.authenticate(SYSTEM_TEST_TOKEN);
        Assert.assertEquals("exolab\\castor", user.getUserID());
        
    }
    
    @Specification
    public void testN3AuthProviderForNonAdmin() throws Exception {
        final Property[] properties = new Property[5];
        properties[0] = new Property("issued", "1222330753605");
        properties[1] = new Property("user", "exolab\\castor");
        properties[2] = new Property("fullName", "Castor Workaholic");
        properties[3] = new Property("email", "castor@exolab.org");
        properties[4] = new Property("roles", "exolab\\committer,exolab\\participant");
        
        expect.that(new Expectations() {
            {
                one(tc).getTokenProperties(SYSTEM_TEST_TOKEN); will(returnValue(properties));
              
            }
        });
        ap.setWsEndpoint("http://localhost:8080/axis2/services/TokenService");
        UserRoles user = ap.authenticate(SYSTEM_TEST_TOKEN);
        Assert.assertEquals("exolab\\castor", user.getUserID());
        Assert.assertFalse(user.isWorkflowAdmin());
    }
         
    @Specification
    public void testN3AuthProviderForAdmin() throws Exception {
        final Property[] properties = new Property[6];
        properties[0] = new Property("issued", "1222330753605");
        properties[1] = new Property("user", "exolab\\castor");
        properties[2] = new Property("fullName", "Castor Workaholic");
        properties[3] = new Property("email", "castor@exolab.org");
        properties[4] = new Property("roles", "exolab\\committer,exolab\\participant");
        properties[5] = new Property("isWorkflowAdmin", true);
        
        expect.that(new Expectations() {
            {
            	one(tc).getTokenProperties(SYSTEM_TEST_TOKEN); will(returnValue(properties));
            }
        });
        ap.setWsEndpoint("http://localhost:8080/axis2/services/TokenService");
        UserRoles user = ap.authenticate(SYSTEM_TEST_TOKEN);
        Assert.assertEquals("exolab\\castor", user.getUserID());
        Assert.assertTrue(user.isWorkflowAdmin());
    }
}
