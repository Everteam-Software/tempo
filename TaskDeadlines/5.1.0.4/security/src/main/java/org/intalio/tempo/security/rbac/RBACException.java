/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.rbac;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Base exception for all RBAC operations.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class RBACException
    extends Exception
{

    /**
     * The underlying exception.
     */
    private Exception _except;


    /**
     * Construct a new RBACException exception wrapping an underlying exception
     * and providing a message.
     *
     * @param message The exception message
     * @param except The underlying exception
     */
    public RBACException( String message, Exception except )
    {
        super( message );
        _except = except;
    }


    /**
     * Construct a new RBACException exception with a message.
     *
     * @param message The exception message
     */
    public RBACException( String message )
    {
        super( message );
        _except = null;
    }


    /**
     * Construct a new RBACException exception wrapping an underlying exception.
     *
     * @param except The underlying exception
     */
    public RBACException( Exception except )
    {
        this( null, except );
    }


    /**
     * Returns the underlying exception, if this exception wraps another exception.
     *
     * @return The underlying exception, or null
     */
    public Exception getException()
    {
        return _except;
    }


    public void printStackTrace()
    {
        if ( _except == null )
            super.printStackTrace();
        else
            _except.printStackTrace();
    }


    public void printStackTrace( PrintStream stream )
    {
        if ( _except == null )
            super.printStackTrace( stream );
        else
            _except.printStackTrace( stream );
    }


    public void printStackTrace( PrintWriter writer )
    {
        if ( _except == null )
            super.printStackTrace( writer );
        else
            _except.printStackTrace( writer );
    }

    /**
     * Return a detail message for this exception.
     *
     * <p>If there is an embedded exception, and if the NestedException
     * has no detail message of its own, this method will return
     * the detail message from the embedded exception.</p>
     *
     * @return The error or warning message.
     */
    public String getMessage()
    {
        String message = super.getMessage();

        if (message == null && _except != null) {
            return _except.getMessage();
        } else {
            return message;
        }
    }

    /**
     * Override toString to pick up any embedded exception.
     *
     * @return A string representation of this exception.
     */
    public String toString()
    {
        if (_except != null) {
            return _except.toString();
        } else {
            return super.toString();
        }
    }

}
