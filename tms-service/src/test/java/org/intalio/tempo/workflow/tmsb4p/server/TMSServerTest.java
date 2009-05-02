package org.intalio.tempo.workflow.tmsb4p.server;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.tms.server.Utils;

public class TMSServerTest extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TMSServerTest.class);
    }

    public void testSetOutput() throws Exception {
        ITMSServer tms_server = Utils.createB4PTMSServer(prepareData());
        XmlObject taskData = XmlObject.Factory.parse("<part-data>Should be part data1</part-data>");
        XmlObject taskData2 = XmlObject.Factory.parse("<part-data>Should be part data2</part-data>");
        tms_server.setOutput("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part1", taskData);
        tms_server.setOutput("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part2", taskData2);
        String output = tms_server.getOutput("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part1");
        String output2 = tms_server.getOutput("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part2");
        assertEquals(output, "<part-data>Should be part data1</part-data>");
        assertEquals(output2, "<part-data>Should be part data2</part-data>");
    }

    public void testGetOutput() throws Exception {
        ITMSServer tms_server = Utils.createB4PTMSServer(prepareData());
        String taskData = tms_server.getOutput("token1", "task2", null);
        assertEquals(taskData, "<part-data>Should be part data1</part-data>");
    }

    public void testDeleteOutput() throws Exception {
        ITMSServer tms_server = Utils.createB4PTMSServer(prepareData());
        XmlObject taskData = XmlObject.Factory.parse("<part-data>Should be part data1</part-data>");
        tms_server.setOutput("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part1", taskData);
        tms_server.deleteOutput("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6");
        String output = tms_server.getOutput("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part1");
        assertNull(output);
    }

    public void testSetFault() throws Exception {
        ITMSServer tms_server = Utils.createB4PTMSServer(prepareData());
        XmlObject faultData = XmlObject.Factory.parse("<fault-data>Should be fault data1, part1</fault-data>");
        XmlObject faultData2 = XmlObject.Factory.parse("<fault-data>Should be fault data2, part2</fault-data>");
        tms_server.setFault("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part1", faultData);
        tms_server.setFault("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part2", faultData2);
        Map fault = tms_server.getFault("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6");
        assertEquals(fault.get("part1").toString(), "<fault-data>Should be fault data1, part1</fault-data>");
        assertEquals(fault.get("part2").toString(), "<fault-data>Should be fault data2, part2</fault-data>");
    }

    public void testDeleteFault() throws Exception {
        ITMSServer tms_server = Utils.createB4PTMSServer(prepareData());
        XmlObject faultData = XmlObject.Factory.parse("<fault-data>Should be fault data1, part1</fault-data>");
        XmlObject faultData2 = XmlObject.Factory.parse("<fault-data>Should be fault data2, part2</fault-data>");
        tms_server.setFault("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part1", faultData);
        tms_server.setFault("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6", "part2", faultData2);
        tms_server.deleteFault("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6");
        Map faultMsg = tms_server.getFault("token1", "10ac46be-cb99-4a6d-8449-18414d4d52e6");
        assertNull(faultMsg);
    }

    private HashMap<String, Task> prepareData() {
        HashMap<String, Task> tasks = new HashMap<String, Task>();
        Task task1 = new Task();
        task1.setOutputMessage("");
        task1.setActualOwner("test\\user1");
        tasks.put("10ac46be-cb99-4a6d-8449-18414d4d52e6", task1);

        Task task2 = new Task();
        task2.setActualOwner("test\\user1");
        task2.setOutputMessage("<part-data>Should be part data1</part-data>");
        tasks.put("task2", task2);
        return tasks;
    }
}
