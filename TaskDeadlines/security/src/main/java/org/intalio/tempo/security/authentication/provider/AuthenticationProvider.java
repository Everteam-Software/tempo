/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.authentication.provider;

import org.intalio.tempo.security.authentication.AuthenticationAdmin;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.authentication.AuthenticationQuery;
import org.intalio.tempo.security.authentication.AuthenticationRuntime;

/**
 * Authentication provider, a factory interface providing one or more concrete 
 * implementation of the three authentication sub-systems: administrative, querying
 * and runtime.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface AuthenticationProvider
{

    /**
     * Return the authentication administration functions.
     * <p>
     * An implementation MAY return <code>null</code> if these functions
     * are not available for this provider.
     */
    public AuthenticationAdmin getAdmin()
        throws AuthenticationException;
    

    /**
     * Return the authentication query functions.
     * <p>
     * An implementation MAY return <code>null</code> if these functions
     * are not available for this provider.
     */
    public AuthenticationQuery getQuery()
        throws AuthenticationException;
    

    /**
     * Return the authentication runtime functions.
     * <p>
     * An implementation MAY return <code>null</code> if these functions
     * are not available for this provider.
     */
    public AuthenticationRuntime getRuntime()
        throws AuthenticationException;
    
}
