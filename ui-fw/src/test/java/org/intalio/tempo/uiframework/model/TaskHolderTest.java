package org.intalio.tempo.uiframework.model;

import junit.framework.TestCase;

import org.intalio.tempo.workflow.task.PIPATask;

public class TaskHolderTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(TaskHolderTest.class);
    }
    
    public void testTaskHolder(){
        String formManagerURL = "http://www.intalio.com/";
        PIPATask task = new PIPATask("id", "formURL");
        
        String formManagerURL2 = "http://localhost:8080/";
        PIPATask task2 = new PIPATask("id2", "formURL2");
        
        TaskHolder<PIPATask> holder = new TaskHolder<PIPATask>(task, formManagerURL);
        
        assertEquals(holder.getTask(), task);
        assertEquals(holder.getFormManagerURL(), formManagerURL);
        
        holder.setTask(task2);
        holder.setFormManagerURL(formManagerURL2);
        assertEquals(holder.getTask(), task2);
        assertEquals(holder.getFormManagerURL(), formManagerURL2);
        
    }
    

}
