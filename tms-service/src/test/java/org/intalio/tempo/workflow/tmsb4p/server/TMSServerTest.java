package org.intalio.tempo.workflow.tmsb4p.server;

import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.tms.server.Utils;

public class TMSServerTest extends TestCase{
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TMSServerTest.class);
    }
    
    public void testSetOutput() throws Exception {
        ITMSServer tms_server = Utils.createB4PTMSServer(prepareData());
        XmlObject taskData = XmlObject.Factory.parse("<part-data>Should be part data1</part-data>");
        tms_server.setOutput("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part1", taskData);
    }
    
    public void testGetOutput() throws Exception {
        ITMSServer tms_server = Utils.createB4PTMSServer(prepareData());
        String taskData = tms_server.getOutput("token1", "task2", null);
        assertEquals(taskData, "<part-data>Should be part data1</part-data>");
    }
    
    private HashMap<String, Task> prepareData(){
        HashMap<String, Task> tasks = new HashMap<String, Task>();
        Task task1 = new Task();
        task1.setOutputMessage("");
        tasks.put("10ac46be-cb99-4a6d-8449-18414d4d52e6", task1);
        
        Task task2 = new Task();
        task2.setOutputMessage("<part-data>Should be part data1</part-data>");
        tasks.put("task2", task2);
        return tasks;
    }
}
