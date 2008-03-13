/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.token;


/**
 * TokenContext is a thread-local variable used to carry a security token as context information
 * instead of explicitly through APIs.
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class TokenContext
{
    private static ThreadLocal<String> _threadLocal = new ThreadLocal<String>();

    /**
     * Obtain thread-local security token.
     */
    public static String getToken() {
        return _threadLocal.get();
    }
    
    /**
     * Check if thread-local security token is available
     */
    public static boolean hasToken() {
        return (getToken() != null);
    }

    /**
     * Remove thread-local security token
     */
    public static void remove() {
        _threadLocal.remove();
    }
}
