/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.auth;

import java.util.HashMap;
import java.util.Map;

import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.IAuthProvider;

public class SimpleAuthProvider implements IAuthProvider {

    private Map<String, UserRoles> _tokenMap = new HashMap<String, UserRoles>();

    public SimpleAuthProvider() {

    }

    public UserRoles authenticate(String participantToken) throws AuthException {
        if (participantToken == null) {
            throw new RequiredArgumentException("participantToken");
        }

        UserRoles credentials = _tokenMap.get(participantToken);
        if (credentials == null) {
            throw new AuthException("Failed to authenticate token: '" + participantToken + "'");
        }
        return credentials;
    }

    public void addUserToken(String token, UserRoles credentials) {
        if (token == null) {
            throw new RequiredArgumentException("token");
        }
        if (credentials == null) {
            throw new RequiredArgumentException("credentials");
        }

        _tokenMap.put(token, credentials);
    }
}
