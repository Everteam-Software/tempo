package org.intalio.tempo.security.ws;

import static org.intalio.tempo.security.ws.Constants.RBACQUERY_NS;

import javax.xml.namespace.QName;

public class RBACQueryConstants {
		public static final QName GET_USER_PROPERTIES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getUserPropertiesResponse");
	 	public static final QName GET_ROLE_PROPERTIES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getRolePropertiesResponse");
	 	public static final QName ASSIGNED_USERS_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getAssignedUsersResponse");
	    public static final QName ASSIGNED_ROLES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getAssignedRolesResponse");
	    public static final QName ROLE_OPERATIONS_ON_OBJECT_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getRoleOperationsOnObjectResponse");
	    public static final QName USER_OPERATIONS_ON_OBJECT_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getUserOperationsOnObjectResponse");

	    public static final QName AUTHORIZED_USERS_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getAuthorizedUsersResponse");
	    public static final QName AUTHORIZED_ROLES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getAuthorizedRolesResponse");
	    public static final QName TOP_ROLES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getTopRolesResponse");
	    public static final QName ASCENDANT_ROLES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getAscendantRolesResponse");
	    public static final QName DESCENDANT_ROLES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getDescendantRolesResponse");
	    public static final QName REMOTE_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "RemoteFault");
	    public static final QName USER_NOT_FOUND_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "UserNotFoundFault");
	    public static final QName ROLE_NOT_FOUND_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "RoleNotFoundFault");
	    public static final QName OBJECT_NOT_FOUND_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "ObjectNotFoundFault");
	    public static final QName RBAC_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "RBACFault");
	    public static final QName ILLEGAL_ARGUMENT_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "IllegalArgumentFault");
	    
	    
	    public static final QName USER = new QName(RBACQUERY_NS.getNamespaceURI(), "user");
	    public static final QName ROLE = new QName(RBACQUERY_NS.getNamespaceURI(), "role");
	    public static final QName PROPERTY = new QName(RBACQUERY_NS.getNamespaceURI(), "property");
	    public static final QName NAME = new QName(RBACQUERY_NS.getNamespaceURI(), "name");
	    public static final QName VALUE = new QName(RBACQUERY_NS.getNamespaceURI(), "value");
	    public static final QName OBJECT = new QName(RBACQUERY_NS.getNamespaceURI(), "object");
	    public static final QName OPERATION = new QName(RBACQUERY_NS.getNamespaceURI(), "operation");
	    public static final QName REALM = new QName(RBACQUERY_NS.getNamespaceURI(), "realm");
}
