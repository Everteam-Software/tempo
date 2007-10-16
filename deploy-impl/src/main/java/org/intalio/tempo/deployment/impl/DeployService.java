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
package org.intalio.tempo.deployment.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.intalio.tempo.deployment.DeploymentManager;
import org.intalio.tempo.deployment.DeploymentManagerMediator;
import org.springframework.util.SystemPropertyUtils;

/**
 * Deployment service
 */
public class DeployService {
	private static final Logger LOG = Logger.getLogger(DeployService.class);

	private static final String DEFAULT_DEPLOY_DIR = "${org.intalio.tempo.configDirectory}/deploy";
	
    /**
     * Mapping of [componentType] to [serviceName]  
     */
    private Map<String, String> _types = new HashMap<String, String>();
    
    /**
     * Mapping of [serviceName] to [DeploymentManager]  
     */
    private Map<String, DeploymentManager> _managers = new HashMap<String, DeploymentManager>();

    /**
     * Mapping of [serviceName] to [DeploymentManagerFactory]  
     */
    private Map<String, DeploymentManagerMediator> _factories = new HashMap<String, DeploymentManagerMediator>();

    private int _pollPeriod = 3000; // milliseconds
    
    private String _deployDir = SystemPropertyUtils.resolvePlaceholders(DEFAULT_DEPLOY_DIR);
    
    public DeployService() {
    	
    }
    
    public String getDeployDirectory() {
    	return _deployDir;
    }
    
    public void setDeployDirectory(String path) {
    	_deployDir = SystemPropertyUtils.resolvePlaceholders(path);
    }
    
    public int getPollPeriod() {
    	return _pollPeriod;
    }

    public void setPollPeriod(int pollPeriod) {
    	_pollPeriod = pollPeriod;
    }
    
    public void init() {
    }

    public void start() {
    	
    }
    
    public void stop() {
    	
    }
    
    public void deployZip(String assemblyName, ZipInputStream zip) {
    }
    
    public void deployPath(String path) {
    	
    }
    
    public void undeployPath(String path) {
    	
    }

    public void undeployName(String name) {
    	
    }
}
