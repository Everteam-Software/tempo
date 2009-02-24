/**
 * Copyright (C) 2008, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
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
import org.intalio.tempo.deployment.spi.ComponentManagerResult;

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
    boolean _failRetire;

    public MockComponentManager() {
    }

    public MockComponentManager(String name) {
        _name = name;
    }
    
    public void setName(String name) {
		this._name = name;
	}

	public String getComponentManagerName() {
        return _name;
    }

    public ComponentManagerResult deploy(ComponentId name, File path, boolean activate) {
        List<DeploymentMessage> messages = new ArrayList<DeploymentMessage>();
        if (_failDeployment) throw new RuntimeException("Deployment force-failed");
        return new ComponentManagerResult(messages);  
    }

    public void initialize(ComponentId name, File path) {
        if (_failActivate) throw new RuntimeException("Activate force failed");

        Component c = _components.get(name);
        if (c == null) throw new RuntimeException("Component "+name+" not deployed");
        c._activated = true;
    }

    public void dispose(ComponentId name) {
        if (_failDeactivate) throw new RuntimeException("Deactivate force-failed");

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

    public void undeploy(ComponentId name, List<String> deployedObjects) {
        if (_failUndeploy) throw new RuntimeException("Undeploy force failed");
    }

    public boolean isDeployed(ComponentId name) {
        return _components.containsKey(name);
    }
    
    public void deployed(ComponentId name, File path, boolean activate) {
        _components.put(name, new Component(name, path));
    }

    public void undeployed(ComponentId name) {
        _components.remove(name);
    }

    public boolean isActivated(ComponentId name) {
        Component c = _components.get(name);
        if (c == null) throw new RuntimeException("Component "+name+" not deployed");
        return c._activated;
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

	public void activate(ComponentId name) {
        if (_failActivate) throw new RuntimeException("Activate force-failed");
	}

	public void retire(ComponentId name) {
        if (_failRetire) throw new RuntimeException("Retire force-failed");
	}

	public void activated(ComponentId name) {
        if (_failActivate) throw new RuntimeException("Activated force-failed");
	}

	public void retired(ComponentId name) {
        if (_failRetire) throw new RuntimeException("Retired force-failed");
	}
}