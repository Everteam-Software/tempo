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
 * Administrative services for the creation and maintaince of RBAC 
 * element sets and relations.
 * <p>
 * This interface is compliant with the NIST RBAC Proposed voluntary 
 * consensus standard DRAFT, dated 4/4/2003.  More information
 * can be found at http://csrc.ncsl.nist.gov/rbac/.
 * <p>
 * In addition to the core RBAC functions, this interface supports
 * the Hierarchical RBAC model.  Implementation of this interface
 * MUST support role hierarchies.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface RBACAdmin
    extends Remote
{

    /**
     * Create a new user.
     * <p>
     * This is valid only if the new user is not already a member of
     * the user list. 
     *
     * @param user Unique identifier for the user
     * @param properties Plugin-specific set of additional properties for the user
     */
    public void addUser( String user, Property[] properties )
        throws RBACException, RemoteException;
   
    
    /**
     * Deletes an existing user.
     * <p>
     * This is valid only if the user is a member of the user list.  All
     * relationships with the user are lost, such as role associations.
     *
     * @param user Unique identifier for the user
     */
    public void deleteUser( String user )
        throws RBACException, RemoteException;


    /**
     * Create a new role.
     * <p>
     * This is valid only if the new role is not already a member of
     * the role list. 
     *
     * @param role Unique identifier for the role
     * @param properties Plugin-specific set of additional properties for the role
     */
    public void addRole( String role, Property[] properties )
        throws RoleNotFoundException, RBACException, RemoteException;

    
    /**
     * Deletes an existing role.
     * <p>
     * This is valid only if the role is a member of the role list.  All
     * relationships with the role are lost, such as permission associations.
     *
     * @param role Unique identifier for the role
     */
    public void deleteRole( String role )
        throws RoleNotFoundException, RBACException, RemoteException;

    
    /**
     * Assign a user to a role.
     * <p>
     * This is valid only if both the user and role exist.
     *
     * @param user Unique identifier for the user
     * @param role Unique identifier for the role
     */
    public void assignUser( String user, String role )
        throws UserNotFoundException, RoleNotFoundException, RBACException, RemoteException;
    

    /**
     * Delete the assignment of a user to a role.
     * <p>
     * This is valid only if both the user and role exist, and
     * the user is assigned to the role.
     *
     * @param user Unique identifier for the user
     * @param role Unique identifier for the role
     */
    public void deassignUser( String user, String role )
        throws UserNotFoundException, RoleNotFoundException, RBACException, RemoteException;


    /**
     * Grant a role the permission to perform an operation
     * on an object.
     * <p>
     * This is valid only if and only if the pair (operation, object)
     * represents a permission and the role exists.
     *
     * @param role Unique identifier for the role
     * @param operation Unique identifier of the operation
     * @param object Unique identifier for the object
     */
    public void grantPermission( String role, String operation, String object )
        throws RoleNotFoundException, RBACException, RemoteException;


    /**
     * Revoke the permission to perform an operation on an object
     * from the set of permissions assigned to a role.
     * <p>
     * This is valid if and only if the pair (operation, object)
     * represents a permission, the role exists and the permission
     * is assigned to that role.
     *
     * @param role Unique identifier for the role
     * @param operation Unique identifier of the operation
     * @param object Unique identifier for the object
     */
    public void revokePermission( String role, String operation, String object )
        throws RoleNotFoundException, RBACException, RemoteException;


    //
    // HIERARCHICAL RBAC SECTION
    //
    
    /**
     * Establish a new immediate inheritance relationship between existing
     * roles.
     * <p>
     * This is valid only if the ascendant and descendant roles exists and the
     * descendant role is not already a descendant of the ascendant (directly or
     * indirectly) to avoid cycle creation.
     * <p>
     * Terminology clarification:  The "descendant" role inherits the permissions 
     * of the "ascendant".
     *
     * @param ascendant identifier of the ascendant role
     * @param descendant identifier of the descendant role
     */
    public void addInheritance( String ascendant, String descendant )
        throws RoleNotFoundException, RBACException, RemoteException;
    

    /**
     * Deletes an existing immediate inheritance relationship between 
     * existing roles.
     * <p>
     * This is valid only if the ascendant and descendant roles exists
     * and the descendant role is an immediate descendant of the ascendant.
     *
     * @param ascendant identifier of the ascendant role
     * @param descendant identifier of the descendant role
     */
    public void deleteInheritance( String ascendant, String descendant )
        throws RoleNotFoundException, RBACException, RemoteException;
    

    /**
     * Create a new role and inserts it in the role hierarchy as an
     * immediate ascendant of an existing role (the descendant).
     * <p>
     * This is valid only if the new role is not already a member of
     * the role list, the descendant role exists and the new ascendant
     * role is not already an ascendant of the descendant (directly or
     * indirectly) to avoid cycle creation.
     *
     * @param ascendant identifier for the new ascendant role
     * @param properties Plugin-specific set of additional properties for the role
     * @param descendant identifier of the descendant role
     */
    public void addAscendant( String ascendant, Property[] properties, String descendant )
        throws RoleNotFoundException, RBACException, RemoteException;
    

    /**
     * Create a new role and inserts it in the role hierarchy as an
     * immediate descendant of an existing role (the ascendant).
     * <p>
     * This is valid only if the new role is not already a member of
     * the role list, the ascendant role exists and the new descendant 
     * role is not already a descendant of the ascendant (directly or
     * indirectly) to avoid cycle creation.
     *
     * @param descendant identifier for the new descendant role
     * @param properties Plugin-specific set of additional properties for the role
     * @param ascendant identifier of the ascendant role
     */
    public void addDescendant( String descendant, Property[] properties, String ascendant )
        throws RoleNotFoundException, RBACException, RemoteException;

    
    //
    // EXTENSIONS TO RBAC SPECIFICATION
    //

    /**
     * Set properties assigned to a user.
     * <p>
     * This is valid only if the user exists.
     *
     * @param user identifier of the user
     * @param properties Plugin-specific set of properties for the user
     */
    public void setUserProperties( String user, Property[] properties )
        throws UserNotFoundException, RBACException, RemoteException;

    
    /**
     * Set properties assigned to a role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param role identifier of the role
     * @param properties Plugin-specific set of properties for the role
     */
    public void setRoleProperties( String role, Property[] properties )
        throws RoleNotFoundException, RBACException, RemoteException;
    
}

