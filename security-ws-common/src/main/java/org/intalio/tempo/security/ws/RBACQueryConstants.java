package org.intalio.tempo.security.ws;

import static org.intalio.tempo.security.ws.Constants.RBACQUERY_NS;

import javax.xml.namespace.QName;

public class RBACQueryConstants {
        public static final String RBAC_PREFIX = "rbac";
    
		public static final QName GET_USER_PROPERTIES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getUserPropertiesResponse", RBAC_PREFIX);
	 	public static final QName GET_ROLE_PROPERTIES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getRolePropertiesResponse", RBAC_PREFIX);
	 	public static final QName ASSIGNED_USERS_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getAssignedUsersResponse", RBAC_PREFIX);
	    public static final QName ASSIGNED_ROLES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getAssignedRolesResponse", RBAC_PREFIX);
	    public static final QName ROLE_OPERATIONS_ON_OBJECT_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getRoleOperationsOnObjectResponse", RBAC_PREFIX);
	    public static final QName USER_OPERATIONS_ON_OBJECT_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getUserOperationsOnObjectResponse", RBAC_PREFIX);

	    public static final QName AUTHORIZED_USERS_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getAuthorizedUsersResponse", RBAC_PREFIX);
	    public static final QName AUTHORIZED_ROLES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getAuthorizedRolesResponse", RBAC_PREFIX);
	    public static final QName TOP_ROLES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getTopRolesResponse", RBAC_PREFIX);
	    public static final QName ASCENDANT_ROLES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getAscendantRolesResponse", RBAC_PREFIX);
	    public static final QName DESCENDANT_ROLES_RESPONSE =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "getDescendantRolesResponse", RBAC_PREFIX);
	    public static final QName REMOTE_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "RemoteFault", RBAC_PREFIX);
	    public static final QName USER_NOT_FOUND_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "UserNotFoundFault", RBAC_PREFIX);
	    public static final QName ROLE_NOT_FOUND_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "RoleNotFoundFault", RBAC_PREFIX);
	    public static final QName OBJECT_NOT_FOUND_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "ObjectNotFoundFault", RBAC_PREFIX);
	    public static final QName RBAC_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "RBACFault", RBAC_PREFIX);
	    public static final QName ILLEGAL_ARGUMENT_EXCEPTION =
	        new QName(RBACQUERY_NS.getNamespaceURI(), "IllegalArgumentFault", RBAC_PREFIX);
	    
	    
	    public static final QName USER = new QName(RBACQUERY_NS.getNamespaceURI(), "user", RBAC_PREFIX);
	    public static final QName ROLE = new QName(RBACQUERY_NS.getNamespaceURI(), "role", RBAC_PREFIX);
	    public static final QName PROPERTY = new QName(RBACQUERY_NS.getNamespaceURI(), "property", RBAC_PREFIX);
	    public static final QName NAME = new QName(RBACQUERY_NS.getNamespaceURI(), "name", RBAC_PREFIX);
	    public static final QName VALUE = new QName(RBACQUERY_NS.getNamespaceURI(), "value", RBAC_PREFIX);
	    public static final QName OBJECT = new QName(RBACQUERY_NS.getNamespaceURI(), "object", RBAC_PREFIX);
	    public static final QName OPERATION = new QName(RBACQUERY_NS.getNamespaceURI(), "operation", RBAC_PREFIX);
	    public static final QName REALM = new QName(RBACQUERY_NS.getNamespaceURI(), "realm", RBAC_PREFIX);
}
