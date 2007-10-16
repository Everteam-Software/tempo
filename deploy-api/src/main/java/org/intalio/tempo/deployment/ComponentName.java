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
 * Component name encapsulates both assembly and local component name.
 * <p>
 * This is an immutable object.
 */
public class ComponentName {

    private String _assemblyName;
    private String _componentName;
    
    public ComponentName(String assemblyName, String componentName) {
        if (assemblyName == null) throw new IllegalArgumentException("Assembly name cannot be null");
        if (componentName == null) throw new IllegalArgumentException("Component name cannot be null");
        _assemblyName = assemblyName;
        _componentName = componentName;
    }
    
    public String getAssemblyName() {
        return _assemblyName;
    }
    
    public String getComponentName() {
        return _componentName;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof ComponentName) {
            ComponentName other = (ComponentName) obj;
            return _componentName.equals(other._componentName)
                && _assemblyName.equals(other._assemblyName);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return _assemblyName.hashCode() ^ _componentName.hashCode();
    }
    
    public String toString() {
        return "{assembly="+_assemblyName+", component="+_componentName+"}";
    }
}

