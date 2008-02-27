package org.intalio.tempo.workflow.task.traits;

import java.util.Date;

public interface ITaskWithDeadline {

    Date getDeadline();

    void setDeadline(Date deadline);

}