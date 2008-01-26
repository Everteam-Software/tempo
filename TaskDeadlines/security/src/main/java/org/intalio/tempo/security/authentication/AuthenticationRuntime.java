/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.authentication;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.intalio.tempo.security.Property;

/**
 * Runtime services for authenticating users.
 * <p>
 * (The term user generally refers to a human being but may be extended to
 * include machines, devices, networks, agents or other logical entities.)
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface AuthenticationRuntime
    extends Remote
{

    /**
     * Authenticate a user.
     * <p>
     * This is valid only if the user exists.
     *
     * @param user identifier of the user
     * @param credentials plugin-specific set of credentials
     * @return true if user has been authenticated, false otherwise.
     */
    public boolean authenticate( String user, Property[] credentials )
        throws UserNotFoundException, AuthenticationException, RemoteException;

}
