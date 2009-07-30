package org.intalio.tempo.workflow.util.xml;

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;

public class OMMarshallerTest extends TestCase {
    private OMFactory _omFactory = OMAbstractFactory.getOMFactory();
    
    private class MockOMMarshaller extends OMMarshaller {
        protected MockOMMarshaller() {
            super(_omFactory, TaskXMLConstants.TASK_OM_NAMESPACE);
        }
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(OMMarshallerTest.class);

    }
    
    public void testConstructor()throws Exception{
        MockOMMarshaller marshaller =  new MockOMMarshaller();
        assertEquals(marshaller.getOMFactory(), _omFactory);
        assertEquals(marshaller.getOMNamespace(), TaskXMLConstants.TASK_OM_NAMESPACE);
    }
    
    public void testOMMarshaller()throws Exception{
        MockOMMarshaller marshaller = new MockOMMarshaller();
        OMElement root = marshaller.createElement("root");
        assertEquals(root.getLocalName(),"root");
        
        OMElement son = marshaller.createElement(root,"son");
        assertEquals(son.getLocalName(),"son");
        
        OMElement daughter = marshaller.createElement(root, "daughter", "Jenny");
        assertEquals(daughter.getLocalName(),"daughter");
        assertEquals(daughter.getText(),"Jenny");
    }
    

}
