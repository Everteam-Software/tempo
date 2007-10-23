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
package org.intalio.tempo.workflow.tas.axis2;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.intalio.tempo.workflow.tas.core.TaskAttachmentService;

// TODO: update this test!
public class N3AuthTest extends TestCase {

    @SuppressWarnings("unused")
    private final static Logger _logger = Logger.getLogger(TASAxis2BridgeTest.class);
    
    @SuppressWarnings("unused")
    private final static String SYSTEM_TEST_TOKEN = "VE9LRU4mJnVzZXI9PXRlc3Rcc3lzdGVtLXRlc3QmJmlzc3VlZD09MTEzNzQxOTg"
        + "xNTAwMyYmcm9sZXM9PXN5c3RlbVxzeXN0ZW0mJmZ1bGxOYW1lPT1Qcm9kdWN0IE1hbmFnZXIgIzEmJmVtYWlsPT1wcm9kL"
        + "W1hbmFnZXIxQGludGFsaW8uY29tJiZub25jZT09LTI4OTY1NDQxODc3OTI0MjY0MDUmJnRpbWVzdGFtcD09MTEzNzQxOTg"
        + "xNTAwMyYmZGlnZXN0PT1wVVc0aXFiMWd1ZnV5TEwxYXNZcit4MS8rRW89JiYmJlRPS0VO";
    
    @SuppressWarnings("unused")
    private TaskAttachmentService _service;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(N3AuthTest.class);
    }

    @Override
    protected void setUp()
            throws Exception {
//        _service = new TaskAttachmentServiceImpl(new N3AuthStrategy(),
//                new DummyStorageStrategy());
    }

    public void testAuth() throws Exception {
//        AuthCredentials credentials = new AuthCredentials(SYSTEM_TEST_TOKEN);
//        credentials.getAuthorizedUsers().add("test/system-test");
//        
//        _service.add(credentials, new AttachmentMetadata(), new byte[0]);
    }
    
    public void testUnauthorized() throws Exception {
//        try {
//            AuthCredentials credentials = new AuthCredentials(SYSTEM_TEST_TOKEN);
//            credentials.getAuthorizedUsers().add("test/system-test1");
//            
//            _service.add(credentials, new AttachmentMetadata(), new byte[0]);
//            
//            Assert.fail("AuthException expected but not thrown");
//        } catch (AuthException e) {
//            /* OK */
//        }
    }
    
    public void testBadToken() throws Exception {
//        try {
//            AuthCredentials credentials = new AuthCredentials("foo");
//            credentials.getAuthorizedUsers().add("test/system-test");
//            
//            _service.add(credentials, new AttachmentMetadata(), new byte[0]);
//            
//            Assert.fail("AuthException expected but not thrown");
//        } catch (AuthException e) {
//            /* OK */
//        }
    }    
}
