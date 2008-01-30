/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id: SimpleSpringTest.java,v 1.5 2005/03/29 22:09:07 ssahuc Exp $
 */

package org.intalio.tempo.deployment.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeploymentMessage;
import org.intalio.tempo.deployment.spi.ComponentManager;

/**
 * Mock ComponentManager used for testing DeployService 
 */
public class MockComponentManager implements ComponentManager {
    
    private String _name;
    
    private Map<ComponentId, Component> _components = new HashMap<ComponentId, Component>();

    boolean _failDeployment;
    boolean _failActivate;
    boolean _failStart;
    boolean _failStop;
    boolean _failDeactivate;
    boolean _failUndeploy;

    public MockComponentManager(String name) {
        _name = name;
    }
    
    public String getComponentManagerName() {
        return _name;
    }

    public List<DeploymentMessage> deploy(ComponentId name, File path) {
        List<DeploymentMessage> messages = new ArrayList<DeploymentMessage>();
        
        if (_failDeployment) throw new RuntimeException("Deployment force failed");
        _components.put(name, new Component(name, path));
        
        return messages;  
    }

    public void activate(ComponentId name, File path) {
        if (_failActivate) throw new RuntimeException("Activate force failed");

        Component c = _components.get(name);
        if (c == null) throw new RuntimeException("Component "+name+" not deployed");
        c._activated = true;
    }

    public void deactivate(ComponentId name) {
        if (_failDeactivate) throw new RuntimeException("Deactivate force failed");

        Component c = _components.get(name);
        if (c == null) throw new RuntimeException("Component "+name+" not deployed");
        c._activated = false;
    }

    public void start(ComponentId name) {
        if (_failStart) throw new RuntimeException("Start force failed");

        Component c = _components.get(name);
        if (c == null) throw new RuntimeException("Component "+name+" not deployed");
        c._started = true;
    }

    public void stop(ComponentId name) {
        if (_failStop) throw new RuntimeException("Stop force failed");

        Component c = _components.get(name);
        if (c == null) throw new RuntimeException("Component "+name+" not deployed");
        c._started = false;
    }

    public void undeploy(ComponentId name) {
        if (_failUndeploy) throw new RuntimeException("Undeploy force failed");
        _components.remove(name);
    }

    public boolean isDeployed(ComponentId name) {
        return _components.containsKey(name);
    }
    
    class Component {
        ComponentId _name;
        File _path;
        boolean _activated;
        boolean _started;
        
        Component(ComponentId name, File path) {
            _name = name;
            _path = path;
        }
    }
}
