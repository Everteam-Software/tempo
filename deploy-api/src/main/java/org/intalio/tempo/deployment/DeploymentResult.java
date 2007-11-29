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

import java.util.ArrayList;
import java.util.List;

/**
 * Deployment result
 */
public class DeploymentResult {

    private boolean _success;
    private List<DeploymentMessage> _messages;
    private String _assemblyName;
    
    public DeploymentResult(String assemblyName, boolean success, List<DeploymentMessage> messages) {
        _assemblyName = assemblyName;
        _success = success;
        _messages = messages;
    }

    public DeploymentResult(String assemblyName, boolean success, DeploymentMessage message) {
        _assemblyName = assemblyName;
        _success = success;
        _messages = new ArrayList<DeploymentMessage>();
        _messages.add(message);
    }

    public String getAssemblyName() {
        return _assemblyName;
    }
    
    public boolean isSuccessful() {
        return _success;
    }
    
    public List<DeploymentMessage> getMessages() {
        return _messages;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("DeploymentResult: ");
        buf.append(_assemblyName);
        if (_success) buf.append(" -> Success\n");
        else buf.append(" -> Failed\n");
        for (DeploymentMessage m : _messages) {
            buf.append(m.toString()+"\n");
        }
        return buf.toString();
    }
}
