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
 * Callback interface for the deployment service.
 */
public interface DeploymentServiceCallback {

    /**
     * Notify the deployment service the given ComponentManager is available
     */
    void available(ComponentManager manager);
    
    /**
     * Notify the deployment service the the given ComponentManager is now unavailable
     */
    void unavailable(ComponentManager manager);
    
}

