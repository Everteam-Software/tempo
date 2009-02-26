package org.intalio.tempo.workflow.tmsb4p.server;

import org.intalio.tempo.workflow.taskb4p.Task;
import org.intalio.tempo.workflow.tms.TMSException;

public interface ITMSServer {
    public void create(Task task, String participantToken) throws TMSException;
}
