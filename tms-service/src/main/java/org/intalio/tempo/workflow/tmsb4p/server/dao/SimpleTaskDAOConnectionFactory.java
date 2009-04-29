package org.intalio.tempo.workflow.tmsb4p.server.dao;

import java.util.HashMap;
import java.util.Map;

import org.intalio.tempo.workflow.taskb4p.Task;


public class SimpleTaskDAOConnectionFactory implements ITaskDAOConnectionFactory {

    private Map<String, Task> _tasks = new HashMap<String, Task>();

    public SimpleTaskDAOConnectionFactory(Map<String, Task> tasks) {
        _tasks = tasks;
    }

    public ITaskDAOConnection openConnection() {
        
        
        return new SimpleTaskDAOConnection(_tasks);
    }

}
