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
import java.util.List;

/**
 * Deployed assembly
 */
public class DeployedAssembly {
    final String _assemblyName;
    final File _assemblyDir;
    final List<DeployedComponent> _components;
    final boolean _activated;

    public DeployedAssembly(String assemblyName, File assemblyDir, List<DeployedComponent> components, 
                            boolean activated)
    {
        _assemblyName = assemblyName;
        _assemblyDir = assemblyDir;
        _components = components;
        _activated = activated;
    }
    
    public String getAssemblyName() {
        return _assemblyName;
    }
    
    public File getAssemblyDir() {
        return _assemblyDir;
    }
    
    public List<DeployedComponent> getDeployedComponents() {
        return _components;
    }
    
    public boolean isActivated() {
        return _activated;
    }
}
