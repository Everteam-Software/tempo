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
 * Deployment message, contains the description and detail of deployment operation.
 */
public class DeploymentMessage implements Serializable {
    private static final long serialVersionUID = 8929159867749507536L;
    
    private String _description;
    private Level _level;
    private String _resource;
    private String _location;
    private ComponentId _cid;
    private String _componentManagerName;

    public enum Level { INFO, WARNING, ERROR }
    
    public DeploymentMessage(Level level, String description)
    {
        _level = level;
        _description = description;
    }
    
    public String getDescription() {
        return _description;
    }
    
    public Level getLevel() {
        return _level;
    }

    public boolean isInfo() {
        return Level.INFO.equals(_level);
    }

    public boolean isWarning() {
        return Level.WARNING.equals(_level);
    }

    public boolean isError() {
        return Level.ERROR.equals(_level);
    }

    public String getResource() {
        return _resource;
    }
    
    public void setResource(String resource) {
        _resource = resource;
    }
    
    public String getLocation() {
        return _location;
    }

    public void setLocation(String location) {
        _location = location;
    }
    
    public ComponentId getComponentId() {
        return _cid;
    }
    
    public void setComponentId(ComponentId cid) {
        _cid = cid;
    }
    
    public String getComponentManagerName() {
        return _componentManagerName;
    }

    public void setComponentManagerName(String componentManagerName) {
        _componentManagerName = componentManagerName;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        buf.append(_level.name());
        buf.append(",");
        buf.append(_description);
        if (_resource != null) {
            buf.append(",");
            buf.append(_resource);
        }
        if (_location != null) {
            buf.append(",");
            buf.append(_location);
        }
        if (_cid != null) {
            buf.append(",");
            buf.append(_cid);
        }
        if (_componentManagerName != null) {
            buf.append(",");
            buf.append(_componentManagerName);
        }
        buf.append("]");
        return buf.toString();
    }
}
