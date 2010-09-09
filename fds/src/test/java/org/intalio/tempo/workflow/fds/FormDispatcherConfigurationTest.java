package org.intalio.tempo.workflow.fds;

import junit.framework.TestCase;

public class FormDispatcherConfigurationTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(FormDispatcherConfigurationTest.class);
    }
    
    public void testConfigFile()throws Exception{
        String prop = System.getProperty(FormDispatcherConfiguration.CONFIG_DIR_PROPERTY);
        System.setProperty(FormDispatcherConfiguration.CONFIG_DIR_PROPERTY, "src/test/resources");
        FormDispatcherConfiguration fdc = FormDispatcherConfiguration.getInstance();
        String pxeBaseUrl = fdc.getPxeBaseUrl();
        assertEquals(pxeBaseUrl,"http://localhost:8080/ode/processes");
        
        String fdsUrl = fdc.getFdsUrl();
        assertEquals(fdsUrl,"http://localhost:8080/fds/workflow/ib4p");
        
        String wfUrl = fdc.getWorkflowProcessesRelativeUrl();
        assertEquals(wfUrl, "/workflow/ib4p");
        
        String tmsUrl = fdc.getTmsUrl();
        assertEquals(tmsUrl,"http://localhost:8080/axis2/services/TaskManagementServices");
        
        int timeout = fdc.getHttpTimeout();
        assertEquals(timeout, 5000);
        
        if(null == prop){
            System.clearProperty(FormDispatcherConfiguration.CONFIG_DIR_PROPERTY);
        }else{
            System.setProperty(FormDispatcherConfiguration.CONFIG_DIR_PROPERTY, prop);
        }
    }
}
