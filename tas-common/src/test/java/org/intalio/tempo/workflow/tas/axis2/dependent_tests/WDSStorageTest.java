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
package org.intalio.tempo.workflow.tas.axis2.dependent_tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.intalio.tempo.workflow.tas.axis2.DummyAuthStrategy;
import org.intalio.tempo.workflow.tas.axis2.TASAxis2BridgeTest;
import org.intalio.tempo.workflow.tas.core.AttachmentMetadata;
import org.intalio.tempo.workflow.tas.core.AuthCredentials;
import org.intalio.tempo.workflow.tas.core.TaskAttachmentService;
import org.intalio.tempo.workflow.tas.core.TaskAttachmentServiceImpl;
import org.intalio.tempo.workflow.tas.core.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tas.core.WDSStorageStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WDSStorageTest extends TestCase {

    private static final Logger _logger = LoggerFactory.getLogger(TASAxis2BridgeTest.class);

    private static final String _WDS_ENDPOINT = "http://localhost:8080/wds/";
    
    private TaskAttachmentService _service;
    private AuthCredentials _credentials = new AuthCredentials("token");
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(WDSStorageTest.class);
    }

    @Override
    protected void setUp()
            throws Exception {
        _service = new TaskAttachmentServiceImpl(new DummyAuthStrategy(),
                new WDSStorageStrategy(_WDS_ENDPOINT));
    }

    public void testWDSStorage() throws Exception {
        
        AttachmentMetadata metadata = new AttachmentMetadata();
        metadata.setMimeType("text/plain");
        metadata.setFilename("hello.txt");
        
        String url = _service.add(_credentials, metadata, "Hello world!".getBytes("UTF-8"));
        
        _logger.debug("URL: " + url);
        
        GetMethod getMethod = new GetMethod(url);
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(getMethod);
        String storedInfo = new String(getMethod.getResponseBody(), "UTF-8");
     
        _logger.debug("Stored info: '" + storedInfo + "'");
        
        _service.delete(_credentials, url);
    }

    public void testBadDelete() throws Exception {
        try {
            _service.delete(_credentials, "http://localhost:8080/badurl");
            Assert.fail("UnavailableAttachmentException expected, but not thrown.");
        } catch (UnavailableAttachmentException e) {
            /* OK */
        }
    }
}
