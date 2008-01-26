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
 * Exception indicating that a session doesn't exist.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class ObjectNotFoundException
    extends RBACException
{

    /**
     * Construct a new SessionNotFoundException exception wrapping an underlying exception
     * and providing a message.
     *
     * @param message The exception message
     * @param except The underlying exception
     */
    public ObjectNotFoundException(String message, Exception except)
    {
        super( message, except );
    }


    /**
     * Construct a new SessionNotFoundException exception with a message.
     *
     * @param message The exception message
     */
    public ObjectNotFoundException(String message)
    {
        super( message );
    }


    /**
     * Construct a new SessionNotFoundException exception wrapping an underlying exception.
     *
     * @param except The underlying exception
     */
    public ObjectNotFoundException(Exception except)
    {
        super( except );
    }
    
}
