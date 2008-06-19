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

import java.util.ArrayList;
import java.util.List;

import org.intalio.tempo.deployment.DeploymentMessage;

public class ComponentManagerResult {
    final static List<String> EMPTY_LIST = new ArrayList<String>(0); 
    private List<DeploymentMessage> messages;
    private List<String> deployedObjects;

    public ComponentManagerResult(List<DeploymentMessage> messages, List<String> deployedObjects) {
        this.messages = messages;
        this.deployedObjects = deployedObjects;
    }
    
    public ComponentManagerResult(List<DeploymentMessage> messages) {
        this.messages = messages;
        this.deployedObjects = EMPTY_LIST;
    }

    public List<DeploymentMessage> getMessages() {
        return messages;
    }

    public List<String> getDeployedObjects() {
        return deployedObjects;
    }
}
