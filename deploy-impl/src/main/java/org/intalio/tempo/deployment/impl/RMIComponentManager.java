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
import java.rmi.Naming;
import java.util.List;

import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeploymentMessage;
import org.intalio.tempo.deployment.spi.ComponentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RMI adapter for ComponentManager 
 */
public class RMIComponentManager implements ComponentManager {
    private static final Logger LOG = LoggerFactory.getLogger(RMIComponentManager.class);
    
    private String _name;
    private String _rmiLookup;
    
    public RMIComponentManager(String componentManagerName, String rmiLookup) {
        _name = componentManagerName;
        _rmiLookup = rmiLookup;
    }
    
    public void setComponentManagerName(String name) {
        _name = name;
    }
    
    public String getComponentManagerName() {
        return _name;
    }

    public void setRMILookup(String rmiLookup) {
        _rmiLookup = rmiLookup;
    }
    
    public String getJNDILookup() {
        return _rmiLookup;
    }

    protected ComponentManager getComponentManager() {
        try {
            ComponentManager manager = (ComponentManager) Naming.lookup(_rmiLookup);
            if (manager == null) throw new IllegalArgumentException("ComponentManager not found: "+_rmiLookup);
            return manager;
        } catch (Exception except) {
            LOG.error(_("Error while looking up ComponentManager at {0}", _rmiLookup), except);
            throw new RuntimeException(except);
        }
    }
    
    public List<DeploymentMessage> deploy(ComponentId name, File path) {
        return getComponentManager().deploy(name, path);
    }

    public void undeploy(ComponentId name) {
        getComponentManager().undeploy(name);
    }

    public void activate(ComponentId name, File path) {
        getComponentManager().activate(name, path);
    }

    public void deactivate(ComponentId name) {
        getComponentManager().deactivate(name);
    }

    public void start(ComponentId name) {
        getComponentManager().start(name);
    }

    public void stop(ComponentId name) {
        getComponentManager().stop(name);
    }

}
