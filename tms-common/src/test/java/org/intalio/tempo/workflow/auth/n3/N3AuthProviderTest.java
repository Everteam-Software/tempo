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

import junit.framework.TestCase;

public class N3AuthProviderTest extends TestCase {

    // TODO: this test needs update! The old token doesn't work any more.
    @SuppressWarnings("unused")
    private final static String SYSTEM_TEST_TOKEN = "VE9LRU4mJnVzZXI9PXRlc3Rcc3lzdGVtLXRlc3QmJmlzc3VlZD09MTEzNzQxOTg"
        + "xNTAwMyYmcm9sZXM9PXN5c3RlbVxzeXN0ZW0mJmZ1bGxOYW1lPT1Qcm9kdWN0IE1hbmFnZXIgIzEmJmVtYWlsPT1wcm9kL"
        + "W1hbmFnZXIxQGludGFsaW8uY29tJiZub25jZT09LTI4OTY1NDQxODc3OTI0MjY0MDUmJnRpbWVzdGFtcD09MTEzNzQxOTg"
        + "xNTAwMyYmZGlnZXN0PT1wVVc0aXFiMWd1ZnV5TEwxYXNZcit4MS8rRW89JiYmJlRPS0VO";
        
    public static void main(String[] args) {
        junit.textui.TestRunner.run(N3AuthProviderTest.class);
    }

    public void testN3AuthProvider() throws Exception {
//        IAuthProvider provider = new N3AuthProvider("/spring-beans.xml"); 
//        UserCredentials user = provider.authenticate(SYSTEM_TEST_TOKEN);
//        Assert.assertEquals("test\\system-test", user.getUserID());
    }
}
