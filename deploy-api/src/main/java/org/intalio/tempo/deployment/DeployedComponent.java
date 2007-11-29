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

public class DeployedComponent {
    final ComponentName _componentName;
    final File _componentDir;
    final String _componentManagerName;

    public DeployedComponent(ComponentName componentName, File componentDir, String componentManagerName) {
        _componentName = componentName;
        _componentDir = componentDir;
        _componentManagerName = componentManagerName;
    }
    
    public ComponentName getComponentName() {
        return _componentName;
    }
    
    public File getComponentDir() {
        return _componentDir;
    }
    
    public String getComponentManagerName() {
        return _componentManagerName;
    }
}
