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
import org.intalio.tempo.security.Property;

/**
 * Query services for reviewing RBAC element sets, properties and relations.
 * <p>
 * This interface seeks compliance with the NIST RBAC Proposed voluntary 
 * consensus standard DRAFT, dated 4/4/2003.  More information
 * can be found at http://csrc.ncsl.nist.gov/rbac/.
 * <p>
 * In addition to the core RBAC functions, this interface supports
 * the Hierarchical RBAC model.  Implementation of this interface
 * MUST support role hierarchies.
 * <p>
 * However, the interface do NOT offer the advanced functions 
 * <code>rolePermissions</code>, <code>userPermissions</code>,
 * and <code>sessionPermissions</code>
 * because they are inherently un-scalable.  The object set may
 * be so large that iterating over permissions becomes futile.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface RBACQuery
    extends Remote
{

    /**
     * Return the set of users assigned to a given role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param role identifier of the role
     * @return identifiers of the users assigned to the given role
     */
    public String[] assignedUsers( String role )
        throws RoleNotFoundException, RBACException, RemoteException;
    

    /**
     * Return the set of roles assigned to a given user.
     * <p>
     * This is valid only if the user exists.
     *
     * @param user identifier of the user
     * @return identifiers of the roles assigned to the given user
     */
    public String[] assignedRoles( String user )
        throws UserNotFoundException, RBACException, RemoteException;
    

    /**
     * Return the set of users assigned to a given role.
     * The set contains all operations granted directly to that role
     * or inherited by that role from other roles.
     * <p>
     * This is valid only if role exists.
     *
     * @param roles identifier of the role
     * @return permissions assigned to the given role
     */
    //
    // THIS FUNCITON IS NOT SUPPORTED BECAUSE IT IS
    // INHERENTLY UN-SCALABLE.  THE OBJECT SET MAY
    // BE SO LARGE THAT ITERATING BECOMES FUTILE.
    //
    // public Iterator rolePermissions( String role )
    //   throws RBACException;
    

    /**
     * Return the set of permissions a given user gets
     * through his/her assigned roles.
     * The set contains all operations obtained by the user either
     * directly or through his/her authorized roles.
     * <p>
     * This is valid only if the user exists.
     *
     * @param user identifier of the user
     * @return permissions assigned to the given user
     */
    //
    // THIS FUNCTION IS NOT SUPPORTED BECAUSE IT IS
    // INHERENTLY UN-SCALABLE.  THE OBJECT SET MAY
    // BE SO LARGE THAT ITERATING BECOMES FUTILE.
    //
    // public Iterator userPermissions( String user )
    //   throws RBACException;
    
    
    /**
     * Return the active roles associated with a session.
     * <p>
     * This is valid only if the session exists.
     *
     * @param session identifier of the session
     * @return roles assigned to the given user
     */
    //
    // THIS FUNCTION IS NOT SUPPORTED BECAUSE SESSION
    // MANAGEMENT IS LEFT TO A HIGHER-LEVEL ARCHITECTURAL
    // LAYER FOR FAILOVER AND CLUSTERING SUPPORT.
    //
    // public String[] sessionRoles( String session )
    //    throws SessionNotFoundException, RBACException, RemoteException;


    /**
     * Return the permissions assigned to the 
     * active roles of a session.
     * <p>
     * This is valid only if the session exists.
     *
     * @param session identifier of the session
     * @return permissions related to the session
     */
    //
    // THIS FUNCTION IS NOT SUPPORTED BECAUSE IT IS
    // INHERENTLY UN-SCALABLE.  THE OBJECT SET MAY
    // BE SO LARGE THAT ITERATING BECOMES FUTILE.
    //
    // public Iterator sessionPermissions( String session )
    //    throws RBACException;
    
    
    /**
     * Return the set of operations a given role is
     * permitted to perform on a given object.
     * <p>
     * This is valid only if the role exists and 
     * the object exists.
     *
     * @param role identifier of the role
     * @param object identifier of the object
     * @return operations permitted on the given object
     */
    public String[] roleOperationsOnObject( String role, String object )
        throws RoleNotFoundException, ObjectNotFoundException, 
               RBACException, RemoteException;


    /**
     * Return the set of operations a given user is permitted
     * to perform on a given object, obtained either directly
     * or through his/her assigned roles.
     * <p>
     * This is valid only if the user exists and 
     * the object exists.
     *
     * @param user identifier of the user
     * @param object identifier of the object
     * @return operations permitted on the given object
     */
    public String[] userOperationsOnObject( String user, String object )
        throws UserNotFoundException, ObjectNotFoundException,
               RBACException, RemoteException;

    
    //
    // HIERARCHICAL RBAC SECTION
    //
    

    /**
     * Return the set of users authorized to a given role.  A user is
     * authorized to a role if it has a direct or inherited assignment
     * with a role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param role identifier of the role
     * @return identifiers of the users authorized to the given role
     */
    public String[] authorizedUsers( String role )
        throws RoleNotFoundException, RBACException, RemoteException;
    

    /**
     * Return the set of roles authorized to a given user.  A user is
     * authorized to a role if it has a direct or inherited assignment
     * with a role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param user identifier of the user
     * @return identifiers of the roles authorized to the given user
     */
    public String[] authorizedRoles( String user )
        throws UserNotFoundException, RBACException, RemoteException;

    
    //
    // EXTENSIONS TO RBAC SPECIFICATION
    //


    /**
     * Return the set of top-level roles within a realm.
     * <p>
     * This is valid only if the realm exists.
     *
     * @param realm the specified realm
     * @return identifiers of the top-level roles within the realm
     */
    public String[] topRoles( String realm )
        throws RBACException, RemoteException;

    
    /**
     * Return the set of ascendant roles for a given role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param role role identifier
     * @return identifiers of the ascendant roles
     */
    public String[] ascendantRoles( String role )
        throws RoleNotFoundException, RBACException, RemoteException;

    
    /**
     * Return the set of descendant roles for a given role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param role role identifier
     * @return identifiers of the descendant roles
     */
    public String[] descendantRoles( String role )
        throws RoleNotFoundException, RBACException, RemoteException;

    
    /**
     * Return the set of properties assigned to a user.
     * <p>
     * This is valid only if the user exists.
     *
     * @param user identifier of the user
     * @return Plugin-specific set of properties of the user
     */
    public Property[] userProperties( String user )
        throws UserNotFoundException, RBACException, RemoteException;

    
    /**
     * Return the set of properties assigned to a role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param role identifier of the role
     * @return Plugin-specific set of properties of the role
     */
    public Property[] roleProperties( String role )
        throws RoleNotFoundException, RBACException, RemoteException;
    
}
