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

import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.auth.AuthIdentifierNormalizer;

public class AuthIdentifierNormalizerTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AuthIdentifierNormalizerTest.class);
    }

    public void testAuthIdentifierNormalizer() throws Exception {
        String idWithForwardSlash = "test/test";
        String idWithDot = "test.test";
        String idWithBackslash = "test\\test";

        Assert.assertEquals(idWithBackslash, AuthIdentifierNormalizer.normalizeAuthIdentifier(idWithForwardSlash));
        Assert.assertEquals(idWithBackslash, AuthIdentifierNormalizer.normalizeAuthIdentifier(idWithDot));
        Assert.assertEquals(idWithBackslash, AuthIdentifierNormalizer.normalizeAuthIdentifier(idWithBackslash));

        String[] multipleIDs = {idWithForwardSlash, idWithDot, idWithBackslash};
        String[] normalizedIDs = AuthIdentifierNormalizer.normalizeAuthIdentifiers(multipleIDs);

        for (String normalizedID : normalizedIDs) {
            Assert.assertEquals(idWithBackslash, normalizedID);
        }

        try {
            AuthIdentifierNormalizer.normalizeAuthIdentifier(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }

        try {
            AuthIdentifierNormalizer.normalizeAuthIdentifiers(new String[] {"test/test", null, "test\\test"});
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {

        }
    }
}
