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

package org.intalio.tempo.deployment;


/**
 * Deployment manager interface to manage component lifecycle.
 */
public interface DeploymentManager {

    /**
     * Deploy a new assembly component
     * <p>
     * In a clustered environment, this method is called on a single node.
     */
    void deploy(ComponentName name, String path);

    /**
     * Undeploy an assembly component
     * <p>
     * In a clustered environment, this method is called on a single node.
     */
    void undeploy(ComponentName name, String path);
    
    /**
     * Initialize a component.
     * <p>
     * In a clustered environment, called on every node after deployment.
     */
    void init(ComponentName name, String path);

    /**
     * Shutdown a component.
     * <p>
     * In a clustered environment, called on every node before undeployment.
     */
    void shutDown(ComponentName name);

    
    /**
     * Start a component.
     * <p>
     * Called after init() to start a component.
     */
    void start(ComponentName name);

    /**
     * Stop a component.
     */
    void stop(ComponentName name);
}

