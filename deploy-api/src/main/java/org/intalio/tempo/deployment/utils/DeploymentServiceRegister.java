/**
 * Copyright (c) 2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.deployment.utils;

import java.util.Timer;
import java.util.TimerTask;

import org.intalio.tempo.deployment.spi.ComponentManager;
import org.intalio.tempo.deployment.spi.DeploymentServiceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to lookup DeploymentService.
 */
public class DeploymentServiceRegister {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentServiceRegister.class);

    private Timer _timer;
    private DeploymentServiceLookup _lookup;
    private ComponentManager _manager;

    private boolean _debug = System.getProperty("org.intalio.tempo.deployment.utils.DeploymentServiceRegister", "no_debug").equalsIgnoreCase("debug");
    
    public DeploymentServiceRegister(ComponentManager manager) {
        _manager = manager;
    }
    
    public String getName() {
        return _manager.getComponentManagerName();
    }
    
    public void init() {
        _lookup = new DeploymentServiceLookup();
        _lookup.loadProperties();
        _timer = new Timer("DeploymentServiceRegister Timer for "+getName(), true);
        _timer.schedule(new InitTask(), 1000, 1000);
    }
    
    public void destroy() {
        try {
            DeploymentServiceCallback callback = _lookup.lookupDeploymentCallback();
            if (callback != null) callback.unavailable(_manager);
        } catch (Exception exception) {
            LOG.debug("DeploymentServiceCallback not available during shutdown of "+getName());
            return;
        }
    }
    
    class InitTask extends TimerTask {
        public void run() {
            try {
                DeploymentServiceCallback callback = _lookup.lookupDeploymentCallback();
                if (callback == null) throw new RuntimeException("DeploymentServiceCallback not yet available");
                callback.available(_manager);
                _timer.cancel();
                LOG.debug("Registered ComponentManager: "+getName());
            } catch (Exception e) {
                if (_debug) 
                    LOG.debug("DeploymentServiceCallback not yet available (ComponentManager="+getName()+"): " + e.toString(), e);
                return;
            }
        }
    }
    
}
