package org.intalio.tempo.workflow.tas.axis2;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.intalio.tempo.workflow.tas.core.TaskAttachmentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TASAxis2SingleInstanceFacadeTest extends TestCase {
    private final static Logger _logger = LoggerFactory.getLogger(TASAxis2SingleInstanceFacadeTest.class);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TASAxis2SingleInstanceFacadeTest.class);
    }
    
    public void testAll() throws Exception{
        TASAxis2Bridge _bridge = new TASAxis2Bridge(new TaskAttachmentServiceImpl(new DummyAuthStrategy(),
                        new DummyStorageStrategy()));
        TASAxis2SingleInstanceFacade f = new TASAxis2SingleInstanceFacade();
        f.setAxis2TASBridge(_bridge);
        OMElement documentElement = TestUtils.loadElementFromResource("/addRequestBase64.xml");
        OMElement response = f.add(documentElement);
        _logger.debug(TestUtils.toPrettyXML(response));
        
        response = null;
        documentElement = TestUtils.loadElementFromResource("/deleteRequest.xml");
        response = _bridge.delete(documentElement);
        _logger.debug(TestUtils.toPrettyXML(response));
    }
}
