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
import junit.framework.TestCase;

import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;

public class N3AuthProviderLiveTest extends TestCase {

    private final static String SYSTEM_TEST_TOKEN = "VE9LRU4mJnVzZXI9PWV4b2xhYlxjYXN0b3ImJmlzc3VlZD09MTIyMj"
        + "MzMDc1MzYwNSYmcm9sZXM9PWV4b2xhYlxjb21taXR0ZXIsZXhvbGFiXHBhcnRpY2lwYW50JiZmdWxsTmFtZT09Q2FzdG9yIFd"
        + "vcmthaG9saWMmJmVtYWlsPT1jYXN0b3JAZXhvbGFiLm9yZyYmbm9uY2U9PTE1Njg3MTk3NzMzMDYxNzkwNDQmJnRpbWVzdGFt"
        + "cD09MTIyMjMzMDc1MzYwOCYmZGlnZXN0PT1BQWlmemVLYnE3czl0UTJ5NVp5N3dWUW5OaU09JiYmJlRPS0VO";
        
    public static void main(String[] args) {
        junit.textui.TestRunner.run(N3AuthProviderLiveTest.class);
    }

    public void testN3AuthProvider() throws Exception {
        N3AuthProvider n3 = new N3AuthProvider();
        n3.setWsEndpoint("http://localhost:8080/axis2/services/TokenService");
        
        IAuthProvider provider = n3;
        UserRoles user = provider.authenticate(SYSTEM_TEST_TOKEN);
        Assert.assertEquals("exolab\\castor", user.getUserID());
    }
}
