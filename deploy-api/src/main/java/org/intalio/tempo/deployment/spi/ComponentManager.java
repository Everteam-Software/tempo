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
import java.rmi.Remote;
import java.util.List;

import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeploymentMessage;


/**
 * Component manager interface to manage component deployment and lifecycle.
 */
public interface ComponentManager extends Remote {

    /**
     * Return the component manager's name
     */
    public String getComponentManagerName();
    
    /**
     * Deploy a new assembly component
     * <p>
     * In a clustered environment, this method is called on a single node (the coordinator).
     * The ComponentManager must validate the component for consistency and must make any necessary 
     * internal state change(s) to existing components in order to successfully deploy this component.
     * This might entail retiring or disabling previously deployed components that may conflict or be 
     * superceeded with this new component.
     * <p>
     * The notions of conflict or superceedance are specific to the component type, and may apply 
     * to component interface, endpoint(s), or rely on naming convention to determine the replacement
     * behavior.
     * <p>
     * If the ComponentManager is unable to safely resolve conflicts, it should return ERROR-level 
     * messages with an appropriate description of the issue.  
     */
    List<DeploymentMessage> deploy(ComponentId name, File path);

    /**
     * Undeploy an assembly component.
     * <p>
     * In a clustered environment, this method is called on a single node (the coordinator).
     * This method must release any persistent resources previously allocated or used by the component.
     */
    void undeploy(ComponentId name);
    
    /**
     * Activate a component.
     * <p>
     * In a clustered environment, called on every node after deployment of a new component, or during
     * system startup to activate the component.  This method should return only when the component is
     * available and ready for processing, provided its dependencies are also available and ready.   
     * However, the component should not yet initiate processing.    Processing should only be initiated 
     * after the start() method is called. 
     * 
     * @param name Component identifier
     */
    void activate(ComponentId name, File path);

    /**
     * Deactivate a component.
     * <p/>
     * In a clustered environment, called on every node before undeployment.  This method effectively renders
     * the component unavailable for processing and should release any transient resources allocated for the 
     * purpose of the component.
     */
    void deactivate(ComponentId name);

    
    /**
     * Start a component.
     * <p/>
     * Called after activate() to start the execution of a component (if necessary).  When this method is called,
     * the component may initiate processing, such as polling messages from a queue, dispatching new requests or
     * generating events.
     */
    void start(ComponentId name);

    /**
     * Stop a component.
     * <p/>
     * Called before deactivate to stop the execution of a component (if necessary).  After this method returns,
     * the component should no longer initiate any new processing.   It may still process outstanding requests
     * until deactivate() is called and returns.
     */
    void stop(ComponentId name);
}
