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

import java.io.File;


/**
 * Component manager interface to manage component deployment and lifecycle.
 */
public interface ComponentManager {

    /**
     * Return the component manager's name
     */
    public String getComponentManagerName();
    
    /**
     * Deploy a new assembly component
     * <p>
     * In a clustered environment, this method is called on a single node (the coordinator)
     */
    void deploy(ComponentName name, File path);

    /**
     * Undeploy an assembly component
     * <p>
     * In a clustered environment, this method is called on a single node (the coordinator)
     */
    void undeploy(ComponentName name, File path);
    
    /**
     * Initialize a component.
     * <p>
     * In a clustered environment, called on every node after deployment.
     */
    void init(ComponentName name, File path);

    /**
     * Shutdown a component.
     * <p>
     * In a clustered environment, called on every node before undeployment.
     */
    void shutdown(ComponentName name);

    
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

