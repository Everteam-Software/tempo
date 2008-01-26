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

package org.intalio.tempo.workflow.wds;

import org.intalio.tempo.workflow.wds.client.UIDGenerator;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UIDGeneratorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UIDGeneratorTest.class);
    }

    public void testUIDGenerator() throws Exception {
        UIDGenerator generator = new UIDGenerator();

        String uid1 = generator.generateUID();
        System.out.println(uid1);
        String uid2 = generator.generateUID();
        System.out.println(uid2);

        Assert.assertEquals(uid1.length(), uid2.length());
        Assert.assertFalse(uid1.equals(uid2));
    }
}
