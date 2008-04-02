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

import org.intalio.tempo.workflow.util.TaskEquality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthIdentifierSetTest extends TestCase {

    private static final Logger _logger = LoggerFactory.getLogger(AuthIdentifierSetTest.class);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AuthIdentifierSetTest.class);
    }

    public void testAuthIdentifierSet() throws Exception {
        AuthIdentifierSet set = new AuthIdentifierSet();

        Assert.assertTrue(set.isEmpty());
        Assert.assertTrue(set.size() == 0);
    }

    public void testAuthIdentifierSetAuthIdentifierSet() throws Exception {
        AuthIdentifierSet set1 = new AuthIdentifierSet();
        set1.add("test/user1");
        set1.add("test\\user2");
        set1.add("test.user3");

        AuthIdentifierSet set2 = new AuthIdentifierSet(set1);
        TaskEquality.areAuthIdSetEquals(set1, set2);
    }

    public void testAuthIdentifierSetStringArray() {
        String[] testIDs = { "test/user1", "test.user2", "test\\user3" };

        AuthIdentifierSet set1 = new AuthIdentifierSet();
        for (String testID : testIDs) {
            set1.add(testID);
        }

        AuthIdentifierSet set2 = new AuthIdentifierSet(testIDs);
        TaskEquality.areAuthIdSetEquals(set1, set2);
    }

    public void testEquals() throws Exception {
        TaskEquality.areAuthIdSetEquals(new AuthIdentifierSet(), new AuthIdentifierSet());

        AuthIdentifierSet set1 = new AuthIdentifierSet(new String[] { "test/user1", "test.user2", "test\\user3" });
        AuthIdentifierSet set2 = new AuthIdentifierSet(new String[] { "test\\user1", "test/user2", "test.user3" });
        TaskEquality.areAuthIdSetEquals(set1, set2);

        set1.add("test/user4");
        Assert.assertFalse(TaskEquality.areAuthIdSetEquals(set1, set2));

    }

    public void testHashCode() throws Exception {
        AuthIdentifierSet set1 = new AuthIdentifierSet(new String[] { "test/user1", "test.user2", "test\\user3" });
        AuthIdentifierSet set2 = new AuthIdentifierSet(new String[] { "test\\user1", "test/user2", "test.user3" });

        Assert.assertEquals(set1.hashCode(), set2.hashCode());

        set2.remove("test/user3");

        Assert.assertFalse(set2.hashCode() == set1.hashCode());
    }

    public void testToString() throws Exception {
        _logger.debug(new AuthIdentifierSet().toString());

        AuthIdentifierSet set1 = new AuthIdentifierSet(new String[] { "test/user1", "test.user2", "test\\user3" });
        _logger.debug(set1.toString());
    }

    public void testIsEmpty() {
        AuthIdentifierSet set = new AuthIdentifierSet();
        Assert.assertTrue(set.isEmpty());

        set.add("test/user1");
        Assert.assertFalse(set.isEmpty());

        set.clear();
        Assert.assertTrue(set.isEmpty());
    }

    public void testSize() {
        AuthIdentifierSet set = new AuthIdentifierSet();
        Assert.assertEquals(0, set.size());

        set.add("test/user1");
        set.add("test/user2");
        set.add("test/user3");
        Assert.assertEquals(3, set.size());

        set.clear();
        Assert.assertEquals(0, set.size());
    }

    public void testAdd() {
        AuthIdentifierSet set = new AuthIdentifierSet();

        set.add("test/user1");
        set.add("test\\user1");
        set.add("test.user1");
        Assert.assertEquals(1, set.size());

        set.add("test/user2");
        Assert.assertEquals(2, set.size());
    }

    public void testContains() {
        AuthIdentifierSet set = new AuthIdentifierSet();

        set.add("test/user1");
        set.add("test\\user2");

        Assert.assertTrue(set.contains("test.user1"));
        Assert.assertTrue(set.contains("test/user2"));
        Assert.assertFalse(set.contains("test/user3"));
    }

    public void testRemove() {
        AuthIdentifierSet set = new AuthIdentifierSet();

        set.add("test/user1");
        Assert.assertTrue(set.remove("test.user1"));
        Assert.assertTrue(set.isEmpty());
    }

    public void testClear() {
        AuthIdentifierSet set = new AuthIdentifierSet();

        for (int i = 0; i < 10; ++i) {
            set.add("test/user" + i);
        }
        Assert.assertEquals(10, set.size());

        set.clear();
        Assert.assertTrue(set.isEmpty());
    }

    public void testIterator() {
        AuthIdentifierSet set = new AuthIdentifierSet();

        for (int i = 0; i < 10; ++i) {
            set.add("test/user" + i);
        }

        int i = 0;
        for (String authID : set) {
            // I should've used SuppressWarnings("unused"), but see
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6294589
            if (authID == null) {
            }
            ++i;
        }

        Assert.assertEquals(10, i);
    }
}
