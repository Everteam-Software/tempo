package org.intalio.tempo.workflow.task.xml;

import junit.framework.Assert;

import org.apache.axiom.om.OMElement;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;

import com.intalio.bpms.workflow.taskManagementServices20051109.Task;
import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;

public class UpdateTest {

	@Test
	public void testUpdate() throws Exception {
		XmlObject xmlObject = XmlObject.Factory.parse(this.getClass().getResource("/update.xml"));
		Assert.assertNotNull(xmlObject);
        System.out.println(xmlObject.toString());
        Task taskElement = Task.Factory.newInstance();
        TaskMetadata metadata = taskElement.addNewMetadata();
        
        metadata.set(new TaskUnmarshaller().expectElement(xmlObject, "taskMetadata"));
        System.out.println(taskElement.getMetadata().getTaskId());
	}
	
	@Test
	public void testUpdateWithUnmarshaller() throws Exception {
		XmlObject xmlObject = XmlObject.Factory.parse(this.getClass().getResource("/update.xml"));
		OMElement om = new XmlTooling().convertDocument(xmlObject);
		TaskMetadata tm = new TaskUnmarshaller().unmarshalPartialTask2(om);
		System.out.println(tm.getTaskId());
	}
}
