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

package org.intalio.tempo.workflow.auth;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SimpleAuthProviderTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SimpleAuthProviderTest.class);
    }

    public void testSimpleAuthProvider() throws Exception {
        String token = "token";
        UserRoles credentials = new UserRoles("test/user", new String[]{});

        SimpleAuthProvider provider = new SimpleAuthProvider();
        try {
            provider.authenticate(token);
            Assert.fail("AuthException expected");
        } catch (AuthException e) {

        }

        provider.addUserToken(token, credentials);
        Assert.assertEquals(credentials, provider.authenticate(token));
    }
}
