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
public class SessionNotFoundException
    extends RBACException
{
    private static final long serialVersionUID = 6724653992937312749L;


    /**
     * Construct a new SessionNotFoundException exception wrapping an underlying exception
     * and providing a message.
     *
     * @param message The exception message
     * @param except The underlying exception
     */
    public SessionNotFoundException(String message, Exception except)
    {
        super( message, except );
    }


    /**
     * Construct a new SessionNotFoundException exception with a message.
     *
     * @param message The exception message
     */
    public SessionNotFoundException(String message)
    {
        super( message );
    }


    /**
     * Construct a new SessionNotFoundException exception wrapping an underlying exception.
     *
     * @param except The underlying exception
     */
    public SessionNotFoundException(Exception except)
    {
        super( except );
    }
    
}
