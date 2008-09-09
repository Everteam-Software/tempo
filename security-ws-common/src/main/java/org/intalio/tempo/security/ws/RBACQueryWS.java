/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.security.ws;

import java.rmi.RemoteException;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.rbac.ObjectNotFoundException;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.RoleNotFoundException;
import org.intalio.tempo.security.rbac.UserNotFoundException;
import org.intalio.tempo.security.rbac.provider.RBACProvider;
import org.intalio.tempo.security.util.IdentifierUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RBACQueryWS extends BaseWS {
    private static final Logger LOG = LoggerFactory.getLogger(RBACQueryConstants.class);

    /**
     * Return the set of users assigned to a given role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param role identifier of the role
     * @return identifiers of the users assigned to the given role
     */
    
    public OMElement getAssignedUsers(OMElement requestEl) throws AxisFault {
    	
        OMParser request = new OMParser(requestEl);
        
        
        String[] users= null;
        	try {
        		String role = request.getRequiredString(RBACQueryConstants.ROLE);
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(IdentifierUtils.getRealm(role));
				
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				
				users=usersRBACquery.assignedUsers(role);
			
			} catch (RoleNotFoundException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRoleNotFoundExceptionResponse(e));
				
			} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			} catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
        	
       

        return RBACQueryResponseMarshaller.getAssignedUsersResponse(users);
    }

    /**
     * Return the set of roles assigned to a given user.
     * <p>
     * This is valid only if the user exists.
     *
     * @param user identifier of the user
     * @return identifiers of the roles assigned to the given user
     */
    public OMElement getAssignedRoles(OMElement requestEl) throws AxisFault {
    	
        OMParser request = new OMParser(requestEl);
        
        
        String[] users= null;
        	try {
        		String user = request.getRequiredString(RBACQueryConstants.USER);
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(IdentifierUtils.getRealm(user));
				
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				
				users=usersRBACquery.assignedRoles(user);
				
        	}catch (UserNotFoundException e) {
        		throw new Fault(e,RBACQueryResponseMarshaller.getUserNotFoundExceptionResponse(e));
        	} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			} catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
        	
       

        return RBACQueryResponseMarshaller.getAssignedRolesResponse(users);
    }
    
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
     * @throws AxisFault 
     */
    public OMElement getRoleOperationsOnObject( OMElement requestEl ) throws AxisFault{
        OMParser request = new OMParser(requestEl);
        
        String[] operations= null;
        	try {
        		String role = request.getRequiredString(RBACQueryConstants.ROLE);
        		String object = request.getRequiredString(RBACQueryConstants.OBJECT);
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(IdentifierUtils.getRealm(role));
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				operations=usersRBACquery.roleOperationsOnObject(role, object);
			} catch (RoleNotFoundException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRoleNotFoundExceptionResponse(e));
			} catch (ObjectNotFoundException e) {
								throw new Fault(e,RBACQueryResponseMarshaller.getObjectNotFoundExceptionResponse(e));
			} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			} catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
    	return RBACQueryResponseMarshaller.getRoleOperationsOnObjectResponse(operations);
    }
    
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
    public OMElement getUserOperationsOnObject( OMElement requestEl )throws AxisFault{

        OMParser request = new OMParser(requestEl);
        

        String[] operations= null;
        	try {
				String user = request.getRequiredString(RBACQueryConstants.USER);
				String object = request.getRequiredString(RBACQueryConstants.OBJECT);
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(IdentifierUtils.getRealm(user));
				
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				
				operations=usersRBACquery.userOperationsOnObject(user, object);
				
        	} catch (UserNotFoundException e) {
        		throw new Fault(e,RBACQueryResponseMarshaller.getUserNotFoundExceptionResponse(e));
        		
			} catch (ObjectNotFoundException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getObjectNotFoundExceptionResponse(e));

			} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			} catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
    	return RBACQueryResponseMarshaller.getUserOperationsOnObjectResponse(operations);
    }
    
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
    
    public OMElement getAuthorizedUsers( OMElement requestEl )throws AxisFault{

        OMParser request = new OMParser(requestEl);
        
        

        String[] users= null;
        	try {
        		String role = request.getRequiredString(RBACQueryConstants.ROLE);
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(IdentifierUtils.getRealm(role));
				
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				
				users=usersRBACquery.authorizedUsers(role);
				
			} catch (UserNotFoundException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getUserNotFoundExceptionResponse(e));
			} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			}catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
    	return RBACQueryResponseMarshaller.getAuthorizedUsersResponse(users);
    }
    
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


    public OMElement getAuthorizedRoles( OMElement requestEl )throws AxisFault{

        OMParser request = new OMParser(requestEl);
        
        

        String[] roles= null;
        	try {
        		String user = request.getRequiredString(RBACQueryConstants.USER);
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(IdentifierUtils.getRealm(user));
				
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				
				roles=usersRBACquery.authorizedRoles(user);
				
			} catch (UserNotFoundException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getUserNotFoundExceptionResponse(e));
			} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			} catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
    	return RBACQueryResponseMarshaller.getAuthorizedRolesResponse(roles);
    }
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

    public OMElement getTopRoles( OMElement requestEl )throws AxisFault{

        OMParser request = new OMParser(requestEl);
        
        

        String[] roles= null;
        	try {
        		String realm = request.getRequiredString(RBACQueryConstants.REALM);
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(realm);
				
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				
				roles=usersRBACquery.topRoles(realm);
				
        	} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			} catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
    	return RBACQueryResponseMarshaller.getTopRolesResponse(roles);
    }

    
    
	/**
     * Return the set of ascendant roles for a given role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param role role identifier
     * @return identifiers of the ascendant roles
     */


    public OMElement getAscendantRoles( OMElement requestEl )throws AxisFault{
    	
        OMParser request = new OMParser(requestEl);
        String[] roles= null;
        	try {
        		String role = request.getRequiredString(RBACQueryConstants.ROLE);
        	        
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(IdentifierUtils.getRealm(role));
				
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				
				roles=usersRBACquery.ascendantRoles(role);
				
			} catch (RoleNotFoundException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRoleNotFoundExceptionResponse(e));
			} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			} catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
    	return RBACQueryResponseMarshaller.getAscendantRolesResponse(roles);
    }
    

	/**
     * Return the set of descendant roles for a given role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param role role identifier
     * @return identifiers of the descendant roles
     */
    public OMElement getDescendantRoles( OMElement requestEl )throws AxisFault{

        OMParser request = new OMParser(requestEl);
       
        

        String[] roles= null;
        	try { 
        		String role = request.getRequiredString(RBACQueryConstants.ROLE);
        	
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(IdentifierUtils.getRealm(role));
				
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				
				roles=usersRBACquery.descendantRoles(role);
				
        	}catch (RoleNotFoundException e) {
        		throw new Fault(e,RBACQueryResponseMarshaller.getRoleNotFoundExceptionResponse(e));
        	} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			} catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
    	return RBACQueryResponseMarshaller.getDescendantRolesResponse(roles);
    }
    
    /**
     * Return the set of properties assigned to a user.
     * <p>
     * This is valid only if the user exists.
     *
     * @param user identifier of the user
     * @return Plugin-specific set of properties of the user
     */

	public OMElement getUserProperties(OMElement requestEl) throws AxisFault{
        OMParser request = new OMParser(requestEl);
        
        
        Property[] properties= null;
        	try {
        		String user = request.getRequiredString(RBACQueryConstants.USER);
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(IdentifierUtils.getRealm(user));
				
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				
				properties=usersRBACquery.userProperties(user);
				
				
        	} catch (UserNotFoundException e) {
        		throw new Fault(e,RBACQueryResponseMarshaller.getUserNotFoundExceptionResponse(e));
        		
        	} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			} catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
			
        return RBACQueryResponseMarshaller.getUserPropertiesResponse(properties);
    }

	/**
     * Return the set of properties assigned to a role.
     * <p>
     * This is valid only if the role exists.
     *
     * @param role identifier of the role
     * @return Plugin-specific set of properties of the role
     */
    
	public OMElement getRoleProperties(OMElement requestEl) throws AxisFault {
    	
        OMParser request = new OMParser(requestEl);
        Property[] properties= null;
        	try {
        		String role = request.getRequiredString(RBACQueryConstants.ROLE);
				RBACProvider usersRBACProvider = _securityProvider.getRBACProvider(IdentifierUtils.getRealm(role));
				RBACQuery usersRBACquery=usersRBACProvider.getQuery();
				properties=usersRBACquery.roleProperties(role);
        	}catch (RoleNotFoundException e) {
        		
        		throw new Fault(e,RBACQueryResponseMarshaller.getRoleNotFoundExceptionResponse(e));
			} catch (RemoteException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRemoteExceptionResponse(e));
				
			} catch (RBACException e) {
				throw new Fault(e,RBACQueryResponseMarshaller.getRBACExceptionResponse(e));
			} catch (IllegalArgumentException e){
				throw new Fault(e,RBACQueryResponseMarshaller.getIllegalArgumentException(e));
			}
        return RBACQueryResponseMarshaller.getRolePropertiesResponse(properties);
    }
    
}