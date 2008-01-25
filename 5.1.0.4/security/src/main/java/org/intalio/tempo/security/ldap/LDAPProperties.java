/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 * $Id: LDAPProperties.java,v 1.5 2004/05/07 03:27:37 yip Exp $
 */
package org.intalio.tempo.security.ldap;

/**
 * LDAPProperties
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface LDAPProperties {

    /**
     * The supported realm(s) for the directory. The properties 
     * list name of the realm, and the base DN separated by a 
     * colon (':')
     * For example: 'airrus:dc=airrus,dc=com'.
     */
    public String SECURITY_LDAP_REALM           ="security.ldap.realm";
    
    /**
     * The user's sub-_context
     * For example: 'ou=People', if people belongs to 
     * 'ou=People'
     */
    public String SECURITY_LDAP_USER_BASE       ="security.ldap.user.base";

    /**
     * The syntax of the security principal used for authenication.
     * Either 'dn' or 'url' is supported. If 'dn' is specified (or
     * this properties is not specified), distingished name format 
     * is used. For example, 
     *    cn=Directory Manager,dc=airrus,dc=com 
     * is a principal in distingished name format. If 'url' is 
     * specified, url syntax is used. For example, 
     *    admin@airrus.com
     */
    public String SECURITY_LDAP_PRINCIPAL_SYNTAX= "security.ldap.principal.syntax";

    /**
     * The role or group's sub-_context
     * For example: 'ou=Roles', if roles belongs to
     * 'ou=Roles'.
     */
    public String SECURITY_LDAP_ROLE_BASE       ="security.ldap.role.base";
    
    /**
     * An LDAP attribute name that identifies an user
     * For example: 'uid'.
     */
    public String SECURITY_LDAP_USER_ID         ="security.ldap.user.id";

    /**
     * The LDAP attribute name that identifies a role
     * For example: 'cn'.
     */
    public String SECURITY_LDAP_ROLE_ID         ="security.ldap.role.id";

    /**
     * The LDAP attribute names of an user. (mutliple keys)
     * Format: 'alias:ldap property'
     */
    public String SECURITY_LDAP_USER_PROP       ="security.ldap.user.prop";

    /**
     * The LDAP atrribute names of a role or group. (mutliple keys)
     * Format: 'alias:ldap property'
     */
    public String SECURITY_LDAP_ROLE_PROP       ="security.ldap.role.prop";

    /**
     * The LDAP attribute name of an user object that references the user's roles
     * For example, 'nsRoleDN'.
     */
    public String SECURITY_LDAP_USER_ROLES      ="security.ldap.user.roles";

    /**
     * The LDAP attribute name of a role (or group) that references the role's 
     * users.
     * For example, 'member'.
     */
    public String SECURITY_LDAP_ROLE_USERS      ="security.ldap.role.users";
 
    /**
     * The attribute name of an user's calculated effective roles.
     * If Role is used, specify 'nsRole' or a corresponding attribute
     * of the particular server. For Group with Active Directory, 
     * specify 'memberOf'. If unsure, leaves it out
     */
    public String SECURITY_LDAP_USER_ALLROLES   ="security.ldap.user.allroles";
    
    /**
     * The LDAP attribute name that references a role's descendants or members.
     * If this attribute is specified, leave security.ldap.role.ascen out.
     * For example, if Group is used for access control, specify 'member'
     * or a corresponding attribute of the particular server, and leave out
     * security.ldap.role.ascen
     */
    public String SECURITY_LDAP_ROLE_DESCEN     ="security.ldap.role.descen";

    /**
     * The LDAP attribute name that references a role's ascendants
     * If this attribute is specified, leave 'security.ldap.role.descen' out
     * For example, if standard Role is used for access control, specify
     * 'nsRoleDN' or a corresponding attribute of the particular server, 
     * and leave out security.ldap.role.descen properties.
     */
    public String SECURITY_LDAP_ROLE_ASCEN      ="security.ldap.role.ascen";
    
    /**
     * The attribute name of an role's calculated effective ascendents.
     * If Role is used, specify 'nsRole' or a corresponding attribute
     * of the particular server. For Group with Active Directory, 
     * specify 'memberOf'. If unsure, leaves it out
     */
    public String SECURITY_LDAP_ROLE_ALLROLES   ="security.ldap.user.allroles";

    /**
     * The LDAP attribute name of a role that references permission objects
     * For example: 'permissions'.
     */
    public String SECURITY_LDAP_ROLE_PERMS      ="security.ldap.role.perms";
    
    /**
     * The LDAP attribute of permission object that reference the permission's 
     * roles or groups
     * For example: 'nsRoleDN'.
     */
    public String SECURITY_LDAP_PERM_ROLES      ="security.ldap.perm.roles";

    /**
     * The LDAP attribute of permission object that references the permission's 
     * protected resources.
     * For example: 'resource'.
     */
    public String SECURITY_LDAP_PERM_OBJECTS    ="security.ldap.perm.objects";
    
    /**
     * The LDAP attribute name that identifies a permission object
     * For example, 'cn'.
     */
    public String SECURITY_LDAP_PERM_ID         ="security.ldap.perm.id";
    
    /**
     * The permissions' sub-_context
     * For example, 'ou=Permission'.
     */
    public String SECURITY_LDAP_PERM_BASE       ="security.ldap.perm.base";
    
    /**
     * The LDAP attributes names of an user's credentials or passwords 
     * of the user. (mutliple keys)
     * For example, 'userPassword:password'.
     */   
    public String SECURITY_LDAP_USER_CREDENTIAL ="security.ldap.user.credential";
}
