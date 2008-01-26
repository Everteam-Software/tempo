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

public class UserCredentialsTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UserCredentialsTest.class);
    }

public void testUserCredentials() throws Exception {
        String userId = "test\\user1";
        String[] roleArray = new String[] {"test\\role1", "test\\role2", "test\\role3"};
        UserRoles user1 = new UserRoles(userId, roleArray);

        Assert.assertEquals(userId, user1.getUserID());

        for (String roleID : roleArray) {
            Assert.assertTrue(user1.getAssignedRoles().contains(roleID));
        }
    }
}
