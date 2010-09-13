package org.intalio.tempo.workflow.util.xml;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public class OMUnMarshallerTest extends TestCase {
    private final static String namespaceURI = TaskXMLConstants.TASK_NAMESPACE;
    private final static String namespacePrefix = TaskXMLConstants.TASK_NAMESPACE_PREFIX;
    private OMFactory _omFactory = OMAbstractFactory.getOMFactory();
    
    private class MockOMUnMarshaller extends OMUnmarshaller{
        protected MockOMUnMarshaller(String namespaceURI, String namespacePrefix) {
            super(namespaceURI, namespacePrefix);
            // TODO Auto-generated constructor stub
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(OMUnMarshallerTest.class);
    }
    
    public void testConstructor(){
        Exception e = null;
        try{
            new MockOMUnMarshaller(namespaceURI, null);
        }catch(Exception rae){
            e = rae;
        }
        assertEquals(e.getClass(), RequiredArgumentException.class);
        assertEquals(e.getMessage(), "namespacePrefix may not be null");
        
        e = null;
        try{
            new MockOMUnMarshaller(null, namespacePrefix);
        }catch(Exception rae){
            e = rae;
        }
        assertEquals(e.getClass(), RequiredArgumentException.class);
        assertEquals(e.getMessage(), "namespaceURI may not be null");
    }

    public void testUnMarshaller() throws Exception {
        MockOMUnMarshaller unmarshaller = new MockOMUnMarshaller(namespaceURI, namespacePrefix);
        QName rootName = new QName(namespaceURI, "root", namespacePrefix);
        QName sonName = new QName(namespaceURI, "son", namespacePrefix);
        QName daughterName = new QName(namespaceURI, "daughter", namespacePrefix);
        QName boyName = new QName(namespaceURI, "boy", namespacePrefix);
        QName girlName = new QName(namespaceURI, "girl", namespacePrefix);
        
        OMElement root = _omFactory.createOMElement(rootName);
        OMElement son = _omFactory.createOMElement(sonName);
        OMElement daughter = _omFactory.createOMElement(daughterName);
        OMElement tom = _omFactory.createOMElement(boyName);
        OMElement jenny = _omFactory.createOMElement(girlName);
        tom.setText("Thomas");
        jenny.setText("Jennifer");
        root.addChild(son);
        root.addChild(daughter);
        root.addChild(tom);
        root.addChild(jenny);
        root.build();
        OMElementQueue queue = new OMElementQueue(root);
        
        OMElement requiredSon = unmarshaller.requireElement(queue, "son");
        assertEquals(requiredSon.getLocalName(),"son");
        
        OMElement expectedDaughter = unmarshaller.expectElement(queue, "daughter");
        assertEquals(expectedDaughter.getLocalName(),"daughter");
        
        String tomValue = unmarshaller.requireElementValue(queue,"boy");
        assertEquals(tomValue,"Thomas");
        
        String jennyValue = unmarshaller.expectElementValue(queue, "girl");
        assertEquals(jennyValue, "Jennifer");
    }
    
    public void testErrorHanlding() throws Exception{
        MockOMUnMarshaller unmarshaller = new MockOMUnMarshaller(namespaceURI, namespacePrefix);
        QName rootName = new QName(namespaceURI, "root", namespacePrefix);
        
        OMElement root = _omFactory.createOMElement(rootName);
        root.build();
        OMElementQueue queue = new OMElementQueue(root);
        
        Exception iife = null;
        try{
            unmarshaller.requireElement(queue, "son");
        }catch(Exception e){
            iife = e;
        }
        assertEquals(iife.getClass(), InvalidInputFormatException.class);
        
        OMElement expected = unmarshaller.expectElement(queue, "daughter");
        assertNull(expected);
        
        String value = unmarshaller.expectElementValue(queue, "daughter");
        assertNull(value);
        
        iife = null;
        try{
            unmarshaller.requireParameter(null, "name");
        }catch(Exception e){
            iife = e;
        }
        assertEquals(iife.getClass(), InvalidInputFormatException.class);
        
        iife = null;
        try{
            unmarshaller.forbidParameter("any", "name");
        }catch(Exception e){
            iife = e;
        }
        assertEquals(iife.getClass(), InvalidInputFormatException.class);
        
    }

}
