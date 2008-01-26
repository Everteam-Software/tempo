/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.rbac;

/**
 * Exception indicating that a role doesn't exist.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class RoleNotFoundException
    extends RBACException
{

    /**
     * Construct a new RoleNotFoundException exception wrapping an underlying exception
     * and providing a message.
     *
     * @param message The exception message
     * @param except The underlying exception
     */
    public RoleNotFoundException( String message, Exception except )
    {
        super( message, except );
    }


    /**
     * Construct a new RoleNotFoundException exception with a message.
     *
     * @param message The exception message
     */
    public RoleNotFoundException( String message )
    {
        super( message );
    }


    /**
     * Construct a new RoleNotFoundException exception wrapping an underlying exception.
     *
     * @param except The underlying exception
     */
    public RoleNotFoundException( Exception except )
    {
        super( except );
    }

}
