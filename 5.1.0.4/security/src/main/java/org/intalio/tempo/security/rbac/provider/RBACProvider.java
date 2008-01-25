/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.rbac.provider;

import org.intalio.tempo.security.rbac.RBACAdmin;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.RBACRuntime;

/**
 * RBAC provider, a factory interface providing one or more concrete 
 * implementations of the three RBAC sub-systems:  administrative, querying
 * and runtime.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface RBACProvider
{

    /**
     * Return the RBAC administration functions.
     * <p>
     * An implementation MAY return <code>null</code> if these functions
     * are not available for this provider.
     */
    public RBACAdmin getAdmin()
        throws RBACException;
    

    /**
     * Return the RBAC query functions.
     * <p>
     * An implementation MAY return <code>null</code> if these functions
     * are not available for this provider.
     */
    public RBACQuery getQuery()
        throws RBACException;
    

    /**
     * Return the RBAC runtime functions.
     * <p>
     * An implementation MAY return <code>null</code> if these functions
     * are not available for this provider.
     */
    public RBACRuntime getRuntime()
        throws RBACException;
    
}
