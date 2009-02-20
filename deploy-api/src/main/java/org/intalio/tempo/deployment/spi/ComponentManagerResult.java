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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.intalio.tempo.deployment.DeploymentMessage;

public class ComponentManagerResult implements Serializable {
    
    private static final long serialVersionUID = -8809416379298699150L;
    
    final static List<String> EMPTY_LIST = new ArrayList<String>(0); 
    private List<DeploymentMessage> messages;
    private List<String> deployedResources;

    public ComponentManagerResult(List<DeploymentMessage> messages, List<String> deployedResources) {
        this.messages = messages;
        this.deployedResources = deployedResources;
    }
    
    public ComponentManagerResult(List<DeploymentMessage> messages) {
        this.messages = messages;
        this.deployedResources = EMPTY_LIST;
    }

    public List<DeploymentMessage> getMessages() {
        return messages;
    }

    public List<String> getDeployedResources() {
        return deployedResources;
    }
}
