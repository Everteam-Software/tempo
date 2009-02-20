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


/**
 * This interface is to implement by a component manager that wishes to use the deployment service.
 * The lifecycle of a component is defined as:
 * <ul>
 * <li>deployed - a component is deployed with the deploy() call</li>
 * <li>initialized - a component is initialized and ready for serving request</li>
 * <li>started - a component is started; the start() call notifies that all other components are initialized</li>
 * <li>undeployed - a component is undeployed with the undeploy() call; actual resources that the component registered during
 * the deployment are returned by the deployment service for additional clean-up</li>
 * </ul>
 * 
 * When a component has multiple versions of over the time, there could be at most one active version that serves
 * as the default version of the component. Any requests that do not specify component version that the request wish to use
 * are routed to the default version.
 * <ul>
 * <li>activated - a component version is set to default</li>
 * <li>retired - a component version is set to non-default; the version is still accessible by specifying the version identifier</li>
 * </ul> 
 */
public interface ComponentManager extends Remote {

    /**
     * Return the component manager's name
     */
    public String getComponentManagerName();
    
    /**
     * 
     * Deploys a new assembly component.
     * <p>
     * If the activate parameter is set to true, the version specified by the component id is activated
     * after a successful deployment. 
     * <p>
     * In a clustered environment, this method is called on a single node (the coordinator).
     * The ComponentManager must validate the component for consistency and must take any necessary 
     * step in order to successfully deploy this component.
     * <p>
     * If the ComponentManager is unable to deploy the component, it should return ERROR-level 
     * messages with an appropriate description of the issue.
     *   
     * @param name
     * @param path
     * @param activate
     * @return
     */
    ComponentManagerResult deploy(ComponentId name, File path, boolean activate);

    /**
     * Initializes a component. Combined with the start(), initialize() works in a 2-phase pattern. On a initialize() call,
     * a component manager should get the component ready for serving requests from outside of the component. The
     * start() call is to notify the component manager that other components it may depend on are ready.
     * <p>
     * In a clustered environment, called on every node after deployment of a new component, or during
     * system startup to initialize the component.  This method should return only when the component is
     * available and ready for processing, provided its dependencies are also available and ready.   
     * However, the component should not yet initiate processing.    Processing should only be initiated 
     * after the start() method is called. 
     * 
     * @param name Component identifier
     * @param path Component root directory
     */
    void initialize(ComponentId name, File path);

    /**
     * Starts a component.
     * 
     * <p/>
     * Called after initialize() to start the execution of a component (if necessary).  When this method is called,
     * the component may initiate processing, such as polling messages from a queue, dispatching new requests or
     * generating events.
     */
    void start(ComponentId name);

    /**
     * Stops a component.
     * 
     * <p/>
     * Called before dispose() to stop the execution of a component (if necessary).  After this method returns,
     * the component should no longer initiate any new processing.   It may still process outstanding requests
     * until deactivate() is called and returns.
     */
    void stop(ComponentId name);

    /**
     * De-initializes a component.
     * <p/>
     * In a clustered environment, called on every node before undeployment.  This method effectively renders
     * the component unavailable for processing new requests.  The ComponentManager should release any 
     * transient resources allocated for the purpose of making the component active.
     */
    void dispose(ComponentId name);

    /**
     * Undeploys an assembly component.
     * <p>
     * In a clustered environment, this method is called on a single node (the coordinator).
     * This method must release any persistent resources previously allocated or used by the component.
     */
    void undeploy(ComponentId name, List<String> deployedResources);

    /**
     * Activates the version of the component specified in the ComponentId. To activate a version is to
     * set the version as the default one. This method retires any pre-activated versions if they are
     * different from the specified version.
     * 
     * @param name
     */
    void activate(ComponentId name);
    
    /**
     * Retires the version of the component specified in the given ComponentId. It does nothing if the given version
     * is already retired.
     * 
     * @param name
     */
    void retire(ComponentId name);
    
    /**
     * Notification of deployed component.
     * <p>
     * In a clustered environment, called on every node after deployment of a new component, or during 
     * system startup to notify the ComponentManager about existing deployed component. 
     * 
     * @param name Component identifier
     * @param path Component root directory
     * @param activate when set to true, the version is also activated
     */
    void deployed(ComponentId name, File path, boolean activate);

    /**
     * Notification of undeployed component.
     * <p>
     * In a clustered environment, called on every node when a component is undeployed.
     * <p>
     * The component artifacts may not be available anymore, hence the component directory is not 
     * provided to the ComponentManager. 
     * 
     * @param name Component identifier
     */
    void undeployed(ComponentId name);
    
    /**
     * Notification of activated component
     * 
     * @param name
     */
    void activated(ComponentId name);

    /**
     * Notification of retired component
     * 
     * @param name
     */
    void retired(ComponentId name);
}