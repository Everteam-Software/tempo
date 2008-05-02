/**
 * Copyright (c) 2005-2008 Intalio inc.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.intalio.tempo.deployment.DeploymentService;
import org.intalio.tempo.deployment.spi.DeploymentServiceCallback;
import org.intalio.tempo.registry.Registry;
import org.intalio.tempo.registry.RegistryFactory;

/**
 * Utility class to lookup DeploymentService.
 */
public class DeploymentServiceLookup {
    public static final String DEFAULT_PROPERTY_FILE     = "${org.intalio.tempo.configDirectory}/tempo-deploy.properties";
    public static final String DEFAULT_DEPLOYMENT_SERVICE_NAME  = "DeploymentService";
    public static final String DEFAULT_DEPLOYMENT_CALLBACK_NAME = "DeploymentServiceCallback";

    public String  propertyFile = DEFAULT_PROPERTY_FILE;
    public String  deploymentServiceName  = DEFAULT_DEPLOYMENT_SERVICE_NAME;
    public String  deploymentCallbackName = DEFAULT_DEPLOYMENT_CALLBACK_NAME;

    private Registry _registry;
    
    /**
     * Default constructor.
     */
    public DeploymentServiceLookup() {
    }
    
    /**
     * Load configuration from properties file.
     */
    public void loadProperties() {
        RegistryFactory registryFactory = new RegistryFactory();
        registryFactory.init();
        _registry = registryFactory.getRegistry();
        
        String configFile = resolveSystemProperties(propertyFile);
        try {
            Properties props = new Properties();
            if (new File(configFile).exists())
                props.load(new FileInputStream(configFile));
            deploymentServiceName = (String) props.getProperty("deployment.service.name", DEFAULT_DEPLOYMENT_SERVICE_NAME);
            deploymentCallbackName = (String) props.getProperty("deployment.callback.name", DEFAULT_DEPLOYMENT_CALLBACK_NAME);
        } catch (IOException except) {
            throw new RuntimeException(except);
        }
    }

    /**
     * Lookup DeploymentService via JNDI
     */
    public DeploymentService lookupDeploymentService() {
        return lookup(deploymentServiceName);
    }
    
    /**
     * Lookup DeploymentServiceCallback via JNDI
     */
    public DeploymentServiceCallback lookupDeploymentCallback() {
        return lookup(deploymentCallbackName);
    }
    
    /**
     * Lookup an object via JNDI
     */
    @SuppressWarnings("unchecked")
    private <T> T lookup(String name) {
        return (T) _registry.lookup(name, getClass().getClassLoader());
    }

    private static String resolveSystemProperties(String str) {
        int fromIndex = 0;
        while (true) {
            int start = str.indexOf("${", fromIndex);
            if (start < 0) break;
            int end = str.indexOf("}", start);
            if (end < 0) break;
            String replace = str.substring(start, end+1);
            String key = str.substring(start+2, end);
            String value = System.getProperty(key);
            if (value != null) {
                str = str.replace(replace, value);
            } else {
                fromIndex = start+2;
            }
        }
        return str;
    }
    
}
