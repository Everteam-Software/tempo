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

import javax.naming.Context;
import javax.naming.InitialContext;

import org.intalio.tempo.deployment.DeploymentService;
import org.intalio.tempo.deployment.spi.DeploymentServiceCallback;
import org.springframework.util.SystemPropertyUtils;

/**
 * Utility class to lookup DeploymentService.
 */
public class DeploymentServiceLookup {
    public static final String DEFAULT_PROPERTY_FILE     = "${org.intalio.tempo.configDirectory}/tempo-deploy.properties";
    public static final String DEFAULT_DEPLOYMENT_SERVICE_NAME  = "java:/DeploymentService";
    public static final String DEFAULT_DEPLOYMENT_CALLBACK_NAME = "java:/DeploymentServiceCallback";

    public String  propertyFile = DEFAULT_PROPERTY_FILE;
    public String  deploymentServiceName  = DEFAULT_DEPLOYMENT_SERVICE_NAME;
    public String  deploymentCallbackName = DEFAULT_DEPLOYMENT_CALLBACK_NAME;
    
    /**
     * Default constructor.
     */
    public DeploymentServiceLookup() {
    }
    
    /**
     * Load configuration from properties file.
     */
    public void loadProperties() {
        String configFile = SystemPropertyUtils.resolvePlaceholders(propertyFile);
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
        try {
            Context context = new InitialContext();
            Object proxiedObject = context.lookup(name);
            RemoteProxy proxy = new RemoteProxy(proxiedObject, getClass().getClassLoader(), proxiedObject.getClass().getClassLoader());
            return (T) proxy.newProxyInstance();
        } catch (Exception except) {
            throw new RuntimeException(except);
        }
    }
    
}
