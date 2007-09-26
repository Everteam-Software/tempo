/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.rbac;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Runtime services for making access control decisions.
 * <p>
 * This interface attempts to comply with the NIST RBAC Proposed voluntary 
 * consensus standard DRAFT, dated 4/4/2003.  More information
 * can be found at http://csrc.ncsl.nist.gov/rbac/.
 * <p>
 * In addition to the core RBAC functions, this interface supports
 * the Hierarchical RBAC model.  Implementation of this interface
 * MUST support role hierarchies.
 * <p>
 * To support failover and clustering, the creation and management of 
 * RBAC sessions is left to a higher-level architectural layer.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface RBACRuntime
    extends Remote
{

    /**
     * Returns a boolean value meaning whether the user is allowed or not
     * to perform a given operation on a given object, given a list of
     * active roles.
     * <p>
     * This is valid if the user exists, the roles exists and the roles 
     * are assigned to the user.
     *
     * @param user user identifier
     * @param roles List of active roles
     * @param operation identifier of the operation to perform
     * @param object identifier of the target object
     * @return true if user is allowed to perform operation on object
     */
    public boolean checkAccess( String user, String[] roles, String operation, String object )
        throws RBACException, RemoteException;

}
