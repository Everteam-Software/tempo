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
import java.io.Serializable;
import java.util.List;

/**
 * Deployed assembly.
 * <p>
 * This is an immutable data object returned when querying {@link DeploymentService#getDeployedAssemblies()} 
 */
public class DeployedAssembly implements Serializable {
    private static final long serialVersionUID = 1L;
    
    final AssemblyId _aid;
    final File _assemblyDir;
    final List<DeployedComponent> _components;

    public DeployedAssembly(AssemblyId assemblyId, File assemblyDir, List<DeployedComponent> components)
    {
        _aid = assemblyId;
        _assemblyDir = assemblyDir;
        _components = components;
    }
    
    public AssemblyId getAssemblyId() {
        return _aid;
    }
    
    public File getAssemblyDir() {
        return _assemblyDir;
    }
    
    public List<DeployedComponent> getDeployedComponents() {
        return _components;
    }

    public String toString() {
        return _aid.toString();
    }
}
