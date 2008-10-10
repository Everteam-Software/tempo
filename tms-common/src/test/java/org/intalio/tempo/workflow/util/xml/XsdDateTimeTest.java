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

package org.intalio.tempo.workflow.util.xml;

import junit.framework.TestCase;
import java.util.Date;

public class XsdDateTimeTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(XsdDateTimeTest.class);
    }

    public void testConstructor() throws Exception {
        Date now = new Date();
        XsdDateTime dt = new XsdDateTime(now.getTime());
        XsdDateTime dt2 = new XsdDateTime(now);
        assertEquals(dt, dt2);

        assertEquals(dt.toString(), dt2.toString());

        Exception iae = null;
        try {
            XsdDateTime dt3 = new XsdDateTime("badformat");
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
    }
}
