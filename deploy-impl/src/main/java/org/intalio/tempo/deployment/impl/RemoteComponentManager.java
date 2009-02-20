/**
 * Copyright (C) 2003-2007, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 */

package org.intalio.tempo.deployment.impl;

import static org.intalio.tempo.deployment.impl.LocalizedMessages._;

import java.io.File;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.spi.ComponentManager;
import org.intalio.tempo.deployment.spi.ComponentManagerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remote adapter for ComponentManager 
 */
public class RemoteComponentManager implements ComponentManager {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteComponentManager.class);
    
    private String _name;
    private String _jndiLookup;
    
    public RemoteComponentManager(String componentManagerName, String jndiLookup) {
        _name = componentManagerName;
        _jndiLookup = jndiLookup;
    }
    
    public void setComponentManagerName(String name) {
        _name = name;
    }
    
    public String getComponentManagerName() {
        return _name;
    }

    public void setJNDILookup(String jndiLookup) {
        _jndiLookup = jndiLookup;
    }
    
    public String getJNDILookup() {
        return _jndiLookup;
    }

    protected ComponentManager getComponentManager() {
        try {
            Context context = new InitialContext();
            ComponentManager manager = (ComponentManager) context.lookup(_jndiLookup);
            if (manager == null) throw new IllegalArgumentException("ComponentManager not found: "+_jndiLookup);
            return manager;
        } catch (Exception except) {
            LOG.error(_("Error while looking up ComponentManager at {0}", _jndiLookup), except);
            throw new RuntimeException(except);
        }
    }
    
    public void undeploy(ComponentId name, List<String> deployedObjects) {
        getComponentManager().undeploy(name, deployedObjects);
    }

    public void initialize(ComponentId name, File path) {
        getComponentManager().initialize(name, path);
    }

    public void dispose(ComponentId name) {
        getComponentManager().dispose(name);
    }

    public void start(ComponentId name) {
        getComponentManager().start(name);
    }

    public void stop(ComponentId name) {
        getComponentManager().stop(name);
    }

    public void deployed(ComponentId name, File path, boolean activate) {
        getComponentManager().deployed(name, path, activate);
    }

    public void undeployed(ComponentId name) {
        getComponentManager().undeployed(name);
    }

	public void activate(ComponentId name) {
		getComponentManager().activate(name);
	}

	public void activated(ComponentId name) {
		getComponentManager().activated(name);
	}

	public ComponentManagerResult deploy(ComponentId name, File path, boolean activate) {
		return getComponentManager().deploy(name, path, activate);
	}

	public void retire(ComponentId name) {
		getComponentManager().retire(name);
	}

	public void retired(ComponentId name) {
		getComponentManager().retired(name);
	}
}
