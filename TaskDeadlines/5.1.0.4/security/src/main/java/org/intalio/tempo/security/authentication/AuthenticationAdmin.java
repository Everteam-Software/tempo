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
 * Administrative services for the creation and maintaince of authentication
 * credentials.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface AuthenticationAdmin
    extends Remote
{

    /**
     * Set a user's credentials..
     * <p>
     * This is valid only if the user exists.
     *
     * @param user identifier of the user
     * @param credentials Plugin-specific set of credentials for the user
     */
    public void setUserCredentials( String user, Property[] credentials )
        throws AuthenticationException, RemoteException;
   
}
