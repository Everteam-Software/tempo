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
 * Exception indicating that the desired function cannot be executed
 * due to access control restrictions.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class AccessControlException 
    extends RBACException
{
    private static final long serialVersionUID = 2341392138588878142L;


    /**
     * Construct a new AccessControlException exception wrapping an underlying exception
     * and providing a message.
     *
     * @param message The exception message
     * @param except The underlying exception
     */
    public AccessControlException(String message, Exception except)
    {
        super( message, except );
    }


    /**
     * Construct a new AccessControlException exception with a message.
     *
     * @param message The exception message
     */
    public AccessControlException(String message)
    {
        super( message );
    }


    /**
     * Construct a new AccessControlException exception wrapping an underlying exception.
     *
     * @param except The underlying exception
     */
    public AccessControlException(Exception except)
    {
        super( except );
    }
    
}
