package org.intalio.tempo.workflow.tas.axis2;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AxiomXPathHelperTest extends TestCase {
    private final static Logger _logger = LoggerFactory.getLogger(AxiomXPathHelperTest.class);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AxiomXPathHelperTest.class);
    }

    public void testGetRequiredElement() throws Exception {
        OMElement root = TestUtils.loadElementFromResource("/AxiomXPathHelperTest.xml");
        AxiomXPathHelper _xpathHelper;
        Map<String, String> namespaceMap = new HashMap<String, String>();
        namespaceMap.put("tas", TASAxis2Bridge.TAS_XMLNS);
        _xpathHelper = new AxiomXPathHelper(namespaceMap);
        OMElement ele = _xpathHelper.getRequiredElement(root, "tas:authCredentials");
        assertNotNull(ele);
        try {
            ele = null;
            ele = _xpathHelper.getRequiredElement(root, "authCredentials");
            this.assertTrue(false);
        } catch (Exception e) {

        }
        assertNull(ele);
    }

    public void testGetElement() throws Exception {
        OMElement root = TestUtils.loadElementFromResource("/AxiomXPathHelperTest.xml");
        AxiomXPathHelper _xpathHelper;
        Map<String, String> namespaceMap = new HashMap<String, String>();
        namespaceMap.put("tas", TASAxis2Bridge.TAS_XMLNS);
        _xpathHelper = new AxiomXPathHelper(namespaceMap);
        OMElement ele = _xpathHelper.getElement(root, "tas:authCredentials");
        assertNotNull(ele);

        ele = null;
        ele = _xpathHelper.getElement(root, "xxx");

        assertNull(ele);
    }

    public void testGetRequiredElements() throws Exception {
        OMElement root = TestUtils.loadElementFromResource("/AxiomXPathHelperTest.xml");
        AxiomXPathHelper _xpathHelper;
        Map<String, String> namespaceMap = new HashMap<String, String>();
        namespaceMap.put("tas", TASAxis2Bridge.TAS_XMLNS);
        _xpathHelper = new AxiomXPathHelper(namespaceMap);
        OMElement ele = _xpathHelper.getRequiredElement(root, "tas:authCredentials");
        ele = _xpathHelper.getRequiredElement(ele, "tas:authorizedUsers");
        OMElement[] eles = _xpathHelper.getRequiredElements(ele, "tas:user");
        assertTrue(eles.length == 2);

        eles = null;
        eles = _xpathHelper.getRequiredElements(root, "tas:user1");

        assertTrue(eles.length == 0);
    }
    
    public void testGetRequiredStrings() throws Exception {
        OMElement root = TestUtils.loadElementFromResource("/AxiomXPathHelperTest.xml");
        AxiomXPathHelper _xpathHelper;
        Map<String, String> namespaceMap = new HashMap<String, String>();
        namespaceMap.put("tas", TASAxis2Bridge.TAS_XMLNS);
        _xpathHelper = new AxiomXPathHelper(namespaceMap);
        String ele = _xpathHelper.getRequiredString(root, "tas:authCredentials");
        _logger.debug(ele);
        assertNotNull(ele);
        
//        ele = null;
//        ele = _xpathHelper.getRequiredString(root, "//@attr");      
//        assertNotNull(ele);
//        
        ele = null;
        ele = _xpathHelper.getRequiredString(root, "//*[@attr='attr']");
        _logger.debug(ele);
        assertNotNull(ele);
        
        ele = null;
        try{
            ele = _xpathHelper.getRequiredString(root, "//tas:authCredentials1");
            assertTrue(false);
        }catch(Exception e){
            
        }
        assertNull(ele);
 
        ele = _xpathHelper.getRequiredString(root, "//tas:user");
        _logger.debug(ele);
        assertNotNull(ele);

    }

}
