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

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RestrictedEntityTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(RestrictedEntityTest.class);
    }

    public void testRestrictedEntity() throws Exception {
        BaseRestrictedEntity bre = new BaseRestrictedEntity() {};
        
        Assert.assertTrue(bre.getUserOwners().isEmpty());
        Assert.assertTrue(bre.getRoleOwners().isEmpty());
        
        Collection<String> users = new ArrayList<String>();
        users.add("test/user1");
        users.add("test/user2");

        Collection<String> roles = new ArrayList<String>();
        roles.add("test/role1");
        roles.add("test/role2");
        roles.add("test/role3");

        bre.setRoleOwners(roles);
        bre.setUserOwners(users);
        
        IRestrictedEntity entity = bre;
        
        Assert.assertNotNull(entity.getUserOwners());
        Assert.assertNotNull(entity.getRoleOwners());
        
        entity.getUserOwners().add("test/user3");
        entity.getRoleOwners().add("test/role4");

        UserRoles user1 = new UserRoles("test/user2", new String[] {});
        UserRoles user2 = new UserRoles("test/user3", new String[] {"test/role2"});
        UserRoles user3 = new UserRoles("test/user4", new String[] {"test/role4"});
        UserRoles user4 = new UserRoles("test/user4", new String[] {"test/role5"});
        
        Assert.assertTrue(entity.isAvailableTo(user1));
        Assert.assertTrue(entity.isAvailableTo(user2));
        Assert.assertTrue(entity.isAvailableTo(user3));
        Assert.assertFalse(entity.isAvailableTo(user4));
    }

}
