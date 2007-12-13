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
 * Component identifier encapsulates assembly identifier and component name.
 * <p>
 * This is an immutable object.
 */
public class ComponentId implements Serializable {
    private static final long serialVersionUID = 1L;

    private AssemblyId _assemblyId;
    private String     _componentName;
    
    public ComponentId(AssemblyId assemblyId, String componentName) {
        if (assemblyId == null) throw new IllegalArgumentException("Assembly id cannot be null");
        if (componentName == null) throw new IllegalArgumentException("Component name cannot be null");
        _assemblyId = assemblyId;
        _componentName = componentName;
    }
    
    public AssemblyId getAssemblyId() {
        return _assemblyId;
    }
    
    public String getComponentName() {
        return _componentName;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof ComponentId) {
            ComponentId other = (ComponentId) obj;
            return _componentName.equals(other._componentName)
                && _assemblyId.equals(other._assemblyId);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return _componentName.hashCode() + _assemblyId.hashCode();
    }
    
    public String toString() {
        return "{"+_assemblyId.toString()+"}"+_componentName;
    }
}
