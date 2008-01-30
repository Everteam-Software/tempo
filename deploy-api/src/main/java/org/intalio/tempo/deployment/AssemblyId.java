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
 * Assembly identifier encapsulates assembly name and assembly version identifier.
 * <p>
 * This is an immutable object.
 */
public class AssemblyId implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int NO_VERSION = 0;
    
    private String _assemblyName;
    private int    _version;
    
    public AssemblyId(String assemblyName) {
        this(assemblyName, NO_VERSION);
    }

    public AssemblyId(String assemblyName, int version) {
        if (assemblyName == null) throw new IllegalArgumentException("Assembly name cannot be null");
        _assemblyName = assemblyName;
        _version = version;
    }
    
    public String getAssemblyName() {
        return _assemblyName;
    }
    
    public int getAssemblyVersion() {
        return _version;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof AssemblyId) {
            AssemblyId other = (AssemblyId) obj;
            return _assemblyName.equals(other._assemblyName)
                && _version == other._version;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return _assemblyName.hashCode() + _version;
    }
    
    public String toString() {
        if (_version == NO_VERSION) return  _assemblyName;
        else return _assemblyName+"-"+_version;
    }
}
