/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.authentication;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Base exception for authentication exceptions.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class AuthenticationException
    extends Exception
{
    private static final long serialVersionUID = -5475604802250316628L;
    
    /**
     * The underlying exception.
     */
    private Exception _except;


    /**
     * Construct a new AuthenticationException exception wrapping an underlying exception
     * and providing a message.
     *
     * @param message The exception message
     * @param except The underlying exception
     */
    public AuthenticationException( String message, Exception except )
    {
        super( message );
        _except = except;
    }


    /**
     * Construct a new AuthenticationException exception with a message.
     *
     * @param message The exception message
     */
    public AuthenticationException( String message )
    {
        super( message );
        _except = null;
    }


    /**
     * Construct a new AuthenticationException exception wrapping an underlying exception.
     *
     * @param except The underlying exception
     */
    public AuthenticationException( Exception except )
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
        }
        return message;
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
        }
        return super.toString();
    }

}
