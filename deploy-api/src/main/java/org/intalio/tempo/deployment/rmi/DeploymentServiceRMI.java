/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.deployment.rmi;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import org.intalio.tempo.deployment.DeploymentService;
import org.intalio.tempo.deployment.spi.DeploymentServiceCallback;
import org.springframework.util.SystemPropertyUtils;

/**
 * RMI configuration and lookup for DeploymentService.
 */
public class DeploymentServiceRMI {
    public static final String DEFAULT_PROPERTY_FILE     = "file:${org.intalio.tempo.configDirectory}/tempo-deploy.properties";
    public static final String DEFAULT_DEPLOYMENT_SERVICE_NAME  = "DeploymentService";
    public static final String DEFAULT_DEPLOYMENT_CALLBACK_NAME = "DeploymentServiceCallback";
    public static final int    DEFAULT_RMI_PORT      = 1099;

    public String  propertyFile = DEFAULT_PROPERTY_FILE;
    public int     rmiPort      = DEFAULT_RMI_PORT;
    public String  deploymentServiceName  = DEFAULT_DEPLOYMENT_SERVICE_NAME;
    public String  deploymentCallbackName = DEFAULT_DEPLOYMENT_CALLBACK_NAME;

    /**
     * Default constructor.
     */
    public DeploymentServiceRMI() {
    }
    
    /**
     * Load configuration from properties file.
     */
    public void loadProperties() {
        String configFile = SystemPropertyUtils.resolvePlaceholders(propertyFile);
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(configFile));
            rmiPort = Integer.parseInt((String) props.getProperty("rmi.port", Integer.toString(DEFAULT_RMI_PORT)));
            deploymentServiceName = (String) props.getProperty("deployment.service.name", DEFAULT_DEPLOYMENT_SERVICE_NAME);
            deploymentCallbackName = (String) props.getProperty("deployment.callback.name", DEFAULT_DEPLOYMENT_CALLBACK_NAME);
        } catch (IOException except) {
            throw new RuntimeException(except);
        }
    }

    /**
     * Lookup DeploymentService in RMI Registry.
     */
    public DeploymentService lookupDeploymentService() {
        return lookup(deploymentServiceName);
    }
    
    /**
     * Lookup DeploymentService in RMI Registry.
     */
    public DeploymentServiceCallback lookupDeploymentCallback() {
        return lookup(deploymentCallbackName);
    }
    
    /**
     * Lookup an object in RMI Registry.
     */
    @SuppressWarnings("unchecked")
    private <T> T lookup(String name) {
        try {
            Registry registry = LocateRegistry.getRegistry(rmiPort);
            return (T) registry.lookup(name);
        } catch (Exception except) {
            throw new RuntimeException(except);
        }
    }
    
}
