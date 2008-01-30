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

import java.io.Serializable;

/**
 * Deployed component.
 * <p>
 * This is an immutable data object returned when querying {@link DeploymentService#getDeployedAssemblies()} 
 */
public class DeployedComponent implements Serializable {
    private static final long serialVersionUID = 1L;

    final ComponentId _componentId;
    final String _componentDir;
    final String _componentManagerName;

    public DeployedComponent(ComponentId ComponentId, String componentDir, String componentManagerName) {
        _componentId = ComponentId;
        _componentDir = componentDir;
        _componentManagerName = componentManagerName;
    }
    
    public ComponentId getComponentId() {
        return _componentId;
    }
    
    public String getComponentDir() {
        return _componentDir;
    }
    
    public String getComponentManagerName() {
        return _componentManagerName;
    }
    
    public String toString() {
        return _componentId.toString();
    }

}
