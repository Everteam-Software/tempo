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
 */
package org.intalio.tempo.workflow.tas.core;

import junit.framework.Assert;
import junit.framework.TestCase;


public class WDSStorageStrategyTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(WDSStorageStrategyTest.class);
    }
    
    
    public void testFilter() throws Exception {
    	Assert.assertNotSame("http://localhost:8080/wds",TASUtil.filterLocalhost("http://localhost:8080/wds"));
    	Assert.assertSame("http://127.0.0.1:8080/wds",TASUtil.filterLocalhost("http://127.0.0.1:8080/wds"));
    	try { TASUtil.filterLocalhost("httll:asfdv"); Assert.fail(); } catch (Exception e) { }
    	try { TASUtil.filterLocalhost("htp:localhost"); Assert.fail(); } catch (Exception e) { }
    }

    public void testSanitize() throws Exception {
        assertSanitize("c:\\foo", "foo");
        assertSanitize("ok", "ok" );
        assertSanitize("myDocument.doc", "myDocument.doc" );
        assertSanitize("/some/unix/path/myDocument.doc", "myDocument.doc" );
        assertSanitize("c:\\some\\windows\\path\\my Document.doc", "my%20Document.doc" );
        
        assertSanitize("./foo", "foo" );
        assertSanitize("\\foo", "foo" );
        assertSanitize("\\foo foo", "foo%20foo" );
        // @note(alex) we have encoding problems on release.intalio.com
        // assertSanitize("ééé", "%C3%A9%C3%A9%C3%A9" );
    }
    
    void assertSanitize(String filename, String expected) throws Exception {
        System.out.println("original: " + filename);
        String sanitized = TASUtil.sanitize(filename);
        System.out.println("sanitized: " + sanitized);
        if (sanitized.trim().length() < 1) throw new Exception("Filename is empty: " + sanitized);
        if (sanitized.contains(" ")) throw new Exception("Filename still contains spaces: " + sanitized);
        if (sanitized.contains("\\")) throw new Exception("Filename still contains backslash: " + sanitized);
        if (sanitized.contains("/")) throw new Exception("Filename still contains slash: " + sanitized);
        if (sanitized.contains(":")) throw new Exception("Filename still contains colon: " + sanitized);
        assertEquals(expected, sanitized);
    }
    
}