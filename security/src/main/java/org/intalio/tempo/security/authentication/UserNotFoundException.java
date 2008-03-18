/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.authentication;

/**
 * Exception indicating that a user doesn't exist.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class UserNotFoundException
    extends AuthenticationException
{
    private static final long serialVersionUID = -8666152567536290006L;


    /**
     * Construct a new UserNotFoundException exception wrapping an underlying exception
     * and providing a message.
     *
     * @param message The exception message
     * @param except The underlying exception
     */
    public UserNotFoundException( String message, Exception except )
    {
        super( message, except );
    }


    /**
     * Construct a new UserNotFoundException exception with a message.
     *
     * @param message The exception message
     */
    public UserNotFoundException( String message )
    {
        super( message );
    }


    /**
     * Construct a new UserNotFoundException exception wrapping an underlying exception.
     *
     * @param except The underlying exception
     */
    public UserNotFoundException( Exception except )
    {
        super( except );
    }
    
}
