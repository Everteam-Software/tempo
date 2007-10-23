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

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;
import org.intalio.tempo.workflow.tas.core.TaskAttachmentServiceImpl;

public class TASAxis2BridgeTest extends TestCase {

    private final static Logger _logger = Logger.getLogger(TASAxis2BridgeTest.class);

    private TASAxis2Bridge _bridge;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TASAxis2BridgeTest.class);
    }

    @Override
    public void setUp()
            throws Exception {
        _bridge = new TASAxis2Bridge(new TaskAttachmentServiceImpl(new DummyAuthStrategy(),
                new DummyStorageStrategy()));
    }

    public void testLocalFileAddRequest()
            throws Exception {
//        OMElement documentElement = TestUtils.loadElementFromResource("/addRequestLocalFile.xml");
//        OMElement response = _bridge.add(documentElement);
//        _logger.debug(TestUtils.toPrettyXML(response));
    }

    public void testImmediateBase64AddRequest()
            throws Exception {
        OMElement documentElement = TestUtils.loadElementFromResource("/addRequestBase64.xml");
        OMElement response = _bridge.add(documentElement);
        _logger.debug(TestUtils.toPrettyXML(response));
    }

    public void testDeleteRequest() throws Exception {
        OMElement documentElement = TestUtils.loadElementFromResource("/deleteRequest.xml");
        OMElement response = _bridge.delete(documentElement);
        _logger.debug(TestUtils.toPrettyXML(response));
    }
}
