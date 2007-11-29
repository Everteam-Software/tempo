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

package org.intalio.tempo.deployment.spi;

import java.io.File;
import java.util.List;

import org.intalio.tempo.deployment.ComponentName;
import org.intalio.tempo.deployment.DeploymentMessage;


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
    List<DeploymentMessage> deploy(ComponentName name, File path);

    /**
     * Undeploy an assembly component
     * <p>
     * In a clustered environment, this method is called on a single node (the coordinator)
     */
    List<DeploymentMessage> undeploy(ComponentName name, File path);
    
    /**
     * Activate a component.
     * <p>
     * In a clustered environment, called on every node after deployment.
     */
    List<DeploymentMessage> activate(ComponentName name, File path);

    /**
     * Deactivate a component.
     * <p/>
     * In a clustered environment, called on every node before undeployment.
     */
    List<DeploymentMessage> deactivate(ComponentName name);

    
    /**
     * Start a component.
     * <p/>
     * Called after activate() to start the execution of a component (if necessary).
     */
    List<DeploymentMessage> start(ComponentName name);

    /**
     * Stop a component.
     * <p/>
     * Called before deactivate to stop the execution of a component (if necessary).  This method should only return once the component has stopped
     * all current processing.
     */
    List<DeploymentMessage> stop(ComponentName name);
}

