package org.intalio.tempo.workflow.task.traits;


public interface ITaskWithPriority {

    //Default priority for tasks.
    Integer NORMAL_PRIORITY = new Integer(15);

    Integer getPriority();

    void setPriority(Integer priority);

}