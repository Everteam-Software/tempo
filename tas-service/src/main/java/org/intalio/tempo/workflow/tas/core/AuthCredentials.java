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
 */
package org.intalio.tempo.workflow.tas.core;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class encapsulates all security credentials TAS invoker must provide with TAS requests.
 * 
 * @author Iwan Memruk
 * @version $Revision: 1021 $
 */
public class AuthCredentials {

    /**
     * The participant token.
     */
    private String _participantToken;

    /**
     * All users which are allowed to manage the target secure item.
     */
    private Collection<String> _authorizedUsers = new ArrayList<String>();

    /**
     * All roles which are allowed to manage the target secure item.
     */
    private Collection<String> _authorizedRoles = new ArrayList<String>();

    /**
     * Instance constructor.
     */
    public AuthCredentials(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Parameter 'token' is null");
        }
        _participantToken = token;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Auth credentials:\n");

        builder.append("Participant token: ");
        builder.append(_participantToken);
        builder.append('\n');
        builder.append("Authorized users:\n");
        for (String user : _authorizedUsers) {
            builder.append(user);
            builder.append('\n');
        }
        builder.append("Authorized roles:\n");
        for (String role : _authorizedRoles) {
            builder.append(role);
            builder.append('\n');
        }
        return builder.toString();
    }

    /**
     * Return the participant token.
     */
    public String getParticipantToken() {
        return _participantToken;
    }

    /**
     * Returns the modifiable <code>Collection</code> of all users which are allowed to manage the target secure item.
     */
    public Collection<String> getAuthorizedUsers() {
        return _authorizedUsers;
    }

    /**
     * Returns the modifiable <code>Collection</code> of all roles which are allowed to manage the target secure item.
     */
    public Collection<String> getAuthorizedRoles() {
        return _authorizedRoles;
    }
}
