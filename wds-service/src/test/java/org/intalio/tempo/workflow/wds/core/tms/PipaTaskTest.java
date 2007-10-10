/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.workflow.wds.core.tms;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.intalio.tempo.workflow.wds.core.tms.PipaTask;

public class PipaTaskTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PipaTaskTest.class);
    }

    public void testPipaTaskValidation() throws Exception {
        PipaTask task1 = new PipaTask();
        task1.setId("abc");
        task1.setFormNamespace("urn:ns");
        task1.setFormURL("http://localhost/");
        task1.setProcessEndpoint("http://localhost/process");
        task1.setInitSoapAction("initProcess");

        Assert.assertTrue(task1.isValid());

        PipaTask task2 = new PipaTask();

        Assert.assertFalse(task2.isValid());

        String[] unnormalizedUsers = {"abc/abc", "def\\def", "ghi.ghi"};
        String[] normalizedUsers = {"abc\\abc", "def\\def", "ghi\\ghi"};
        task1.setUserOwners(unnormalizedUsers);

        for (int i = 0; i < normalizedUsers.length; ++i) {
            String normalizedUser = normalizedUsers[i];
            String actualUser = task1.getUserOwners()[i];

            Assert.assertEquals(normalizedUser, actualUser);
        }

        String[] unnormalizedRoles = {"jkl/jkl", "mno\\mno", "pqr.pqr"};
        String[] normalizedRoles = {"jkl\\jkl", "mno\\mno", "pqr\\pqr"};
        task1.setRoleOwners(unnormalizedRoles);

        for (int i = 0; i < normalizedRoles.length; ++i) {
            String normalizedRole = normalizedRoles[i];
            String actualRole = task1.getRoleOwners()[i];

            Assert.assertEquals(normalizedRole, actualRole);
        }

        System.out.println(task1);
        System.out.println(task2);
    }
}
