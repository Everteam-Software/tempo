package org.intalio.tempo.uiframework.forms;

import junit.framework.TestCase;

public class FormManagerBrokerTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(FormManagerBrokerTest.class);
    }
    
    public void testBroker(){
        FormManagerBroker broker = FormManagerBroker.getInstance();
        
        FormManager formManager = new GenericFormManager();
        broker.setFormManager(formManager);
        assertEquals(formManager, broker.getFormManager());
    }

}
